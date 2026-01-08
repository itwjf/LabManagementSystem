# 固定资产管理模块 - 数据库表关系图

## 1. 数据库配置

- **数据库类型**: MySQL
- **数据库名称**: lab_db
- **连接信息**: jdbc:mysql://127.0.0.1:3307/lab_db

## 2. 表关系概览

固定资产管理模块包含7个主要对象（5张表 + 2个视图），它们之间通过外键建立了清晰的关联关系。

### 2.1 核心表关系图（ASCII）

```
┌───────────────┐     ┌───────────────┐     ┌───────────────┐
│   lab_info    │     │  asset_type   │     │     asset     │
├───────────────┤     ├───────────────┤     ├───────────────┤
│ lab_id (PK)   │────▶│ type_id (PK)  │────▶│ asset_id (PK) │
│ lab_name      │     │ type_name     │     │ asset_name    │
│ location      │     │ description   │     │ type_id (FK)  │
│ capacity      │     └───────────────┘     │ lab_id (FK)   │
│ manager       │                            │ spec_model    │
│ contact       │                            │ unit_price    │
└───────────────┘                            │ total_quantity│
                                             │ available_quantity │
                                             │ status        │
                                             └───────────────┘
                                                   ▲
                                                   │
┌─────────────────────┐     ┌─────────────────────┐│
│  asset_out_record   │     │  asset_in_record    ││
├─────────────────────┤     ├─────────────────────┤│
│ record_id (PK)      │     │ record_id (PK)      ││
│ lab_id (FK)         │────▶│ lab_id (FK)         ││
│ asset_id (FK)       │────▶│ asset_id (FK)       │┘
│ quantity            │     │ quantity            │
│ out_reason          │     │ unit_price          │
│ out_date            │     │ total_amount        │
│ handler             │     │ in_date             │
│ approval_status     │     │ handler             │
└─────────────────────┘     └─────────────────────┘
```

## 3. 表关系详细说明

### 3.1 1:1 关系

固定资产管理模块中主要以一对多关系为主，没有严格的1:1关系。

### 3.2 1:N 关系

| 主表 | 子表 | 外键 | 关系说明 |
|------|------|------|----------|
| `lab_info` | `asset` | `lab_id` | 一个实训室可以拥有多个固定资产，一个固定资产只能属于一个实训室 |
| `asset_type` | `asset` | `type_id` | 一种资产类型可以包含多个固定资产，一个固定资产只能属于一种类型 |
| `asset` | `asset_in_record` | `asset_id` | 一个固定资产可以有多个入库记录，一个入库记录只对应一个固定资产 |
| `asset` | `asset_out_record` | `asset_id` | 一个固定资产可以有多个出库记录，一个出库记录只对应一个固定资产 |
| `lab_info` | `asset_in_record` | `lab_id` | 一个实训室可以有多个入库记录，一个入库记录只对应一个实训室 |
| `lab_info` | `asset_out_record` | `lab_id` | 一个实训室可以有多个出库记录，一个出库记录只对应一个实训室 |

### 3.3 N:M 关系

固定资产管理模块中没有直接的多对多关系，所有复杂关系都通过中间表或外键约束来实现。

## 4. 外键约束列表

| 外键名称 | 所在表 | 外键字段 | 参考表 | 参考字段 |
|----------|--------|----------|--------|----------|
| `fk_asset_type` | `asset` | `type_id` | `asset_type` | `type_id` |
| `fk_asset_lab` | `asset` | `lab_id` | `lab_info` | `lab_id` |
| `fk_in_record_lab` | `asset_in_record` | `lab_id` | `lab_info` | `lab_id` |
| `fk_in_record_asset` | `asset_in_record` | `asset_id` | `asset` | `asset_id` |
| `fk_out_record_lab` | `asset_out_record` | `lab_id` | `lab_info` | `lab_id` |
| `fk_out_record_asset` | `asset_out_record` | `asset_id` | `asset` | `asset_id` |

## 5. 视图与表的关系

### 5.1 `asset_account` 视图

该视图是 `asset`、`asset_type` 和 `lab_info` 表的连接查询结果，用于快速查询各实训室的资产台账：

```sql
SELECT
  a.asset_id,
  a.asset_name,
  t.type_name,
  a.spec_model,
  a.unit,
  a.unit_price,
  a.total_quantity,
  a.available_quantity,
  a.total_value,
  a.purchase_date,
  a.status,
  l.lab_name,
  a.update_time AS last_update_time
FROM
  asset a
JOIN asset_type t ON a.type_id = t.type_id
JOIN lab_info l ON a.lab_id = l.lab_id;
```

### 5.2 `asset_change_stat` 视图

该视图是 `lab_info`、`asset_type`、`asset`、`asset_in_record` 和 `asset_out_record` 表的联合查询结果，用于固定资产变动统计：

```sql
SELECT
  l.lab_name,
  t.type_name,
  SUM(CASE WHEN r.in_date >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY) THEN r.quantity ELSE 0 END) AS month_add_quantity,
  SUM(CASE WHEN r.in_date >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY) THEN r.total_amount ELSE 0 END) AS month_add_value,
  SUM(CASE WHEN o.out_date >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY) THEN o.quantity ELSE 0 END) AS month_reduce_quantity,
  SUM(CASE WHEN o.out_date >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY) THEN (o.quantity * a.unit_price) ELSE 0 END) AS month_reduce_value
FROM
  lab_info l
LEFT JOIN asset a ON l.lab_id = a.lab_id
LEFT JOIN asset_type t ON a.type_id = t.type_id
LEFT JOIN asset_in_record r ON a.asset_id = r.asset_id
LEFT JOIN asset_out_record o ON a.asset_id = o.asset_id
GROUP BY
  l.lab_name, t.type_name;
```

## 6. 业务流程与数据流向

### 6.1 资产入库流程

1. 选择实训室（关联 `lab_info`）
2. 选择或创建资产类型（关联 `asset_type`）
3. 创建或更新固定资产记录（`asset`）
4. 生成入库记录（`asset_in_record`）
5. 更新固定资产的总数量、可用数量和总价值

### 6.2 资产出库流程

1. 选择实训室（关联 `lab_info`）
2. 选择要出库的资产（关联 `asset`）
3. 生成出库记录（`asset_out_record`）
4. 检查审批状态（如果需要）
5. 更新固定资产的可用数量
6. 根据出库原因更新资产状态

### 6.3 台账与统计流程

1. 通过 `asset_account` 视图查询各实训室资产台账
2. 通过 `asset_change_stat` 视图获取资产变动统计数据
3. 支持按实训室、资产类型、时间范围筛选数据

## 7. 数据完整性约束

### 7.1 主键约束

| 表名 | 主键字段 |
|------|----------|
| `lab_info` | `lab_id` |
| `asset_type` | `type_id` |
| `asset` | `asset_id` |
| `asset_in_record` | `record_id` |
| `asset_out_record` | `record_id` |

### 7.2 唯一约束

| 表名 | 唯一约束字段 |
|------|--------------|
| `lab_info` | `lab_name` |
| `asset_type` | `type_name` |

### 7.3 非空约束

各表的核心业务字段都设置了非空约束，确保数据完整性。

### 7.4 外键约束

外键约束确保了表之间的引用完整性，防止无效数据的插入。

## 8. 索引优化

为了提高查询性能，在以下字段上创建了索引：

| 表名 | 索引字段 | 索引类型 |
|------|----------|----------|
| `asset` | `type_id` | 普通索引 |
| `asset` | `lab_id` | 普通索引 |
| `asset` | `status` | 普通索引 |
| `asset_in_record` | `lab_id` | 普通索引 |
| `asset_in_record` | `asset_id` | 普通索引 |
| `asset_in_record` | `in_date` | 普通索引 |
| `asset_out_record` | `lab_id` | 普通索引 |
| `asset_out_record` | `asset_id` | 普通索引 |
| `asset_out_record` | `out_date` | 普通索引 |
| `asset_out_record` | `approval_status` | 普通索引 |

## 9. 数据字典

详细的数据字典请参考 `asset_management_tables.sql` 文件中的字段注释。
