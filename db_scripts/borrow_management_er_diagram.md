# 实训室借用与耗材管理模块 - 数据库表关系图

## 1. 数据库配置

- **数据库类型**: MySQL
- **数据库名称**: lab_db
- **连接信息**: jdbc:mysql://127.0.0.1:3307/lab_db

## 2. 模块概述

本次设计包含两个核心模块：
1. **实训室借用管理模块** - 处理实训室的借用申请、审批、查询等功能
2. **耗材借用管理模块** - 处理耗材的库存管理、借用登记、归还登记等功能

## 3. 表关系概览

### 3.1 核心表关系图（ASCII）

```
┌───────────────┐     ┌─────────────────────┐
│   lab_info    │     │ lab_borrow_apply    │
├───────────────┤     ├─────────────────────┤
│ lab_id (PK)   │────▶│ apply_id (PK)       │
│ lab_name      │     │ lab_id (FK)         │
│ capacity      │     │ borrow_department   │
│ location      │     │ borrower_name       │
│ status        │     │ start_time          │
└───────────────┘     │ end_time            │
       ▲              │ status              │
       │              └─────────────────────┘
       │
       ▼
┌───────────────────────┐     ┌───────────────┐
│ lab_equipment_config  │     │   consumable   │
├───────────────────────┤     ├───────────────┤
│ config_id (PK)        │     │ consumable_id (PK) │
│ lab_id (FK)           │────▶│ consumable_name    │
│ equipment_name        │     │ spec_model         │
│ spec_model            │     │ unit               │
│ quantity              │     │ current_stock      │
└───────────────────────┘     │ lab_id (FK)        │
                               └───────────────┘
                                     ▲
                                     │
                                     │
                               ┌─────────────────────┐
                               │ consumable_borrow_  │
                               │       record        │
                               ├─────────────────────┤
                               │ record_id (PK)      │
                               │ consumable_id (FK)  │
                               │ borrower_name       │
                               │ borrow_quantity     │
                               │ borrow_date         │
                               │ expected_return_date│
                               │ return_quantity     │
                               │ status              │
                               └─────────────────────┘
```

## 4. 表关系详细说明

### 4.1 实训室借用管理模块

#### 4.1.1 1:N 关系

| 主表 | 子表 | 外键 | 关系说明 |
|------|------|------|----------|
| `lab_info` | `lab_borrow_apply` | `lab_id` | 一个实训室可以有多个借用申请，一个借用申请只对应一个实训室 |
| `lab_info` | `lab_equipment_config` | `lab_id` | 一个实训室可以配置多种设备，一种设备配置只属于一个实训室 |

#### 4.1.2 外键约束

| 外键名称 | 所在表 | 外键字段 | 参考表 | 参考字段 |
|----------|--------|----------|--------|----------|
| `fk_borrow_apply_lab` | `lab_borrow_apply` | `lab_id` | `lab_info` | `lab_id` |
| `fk_equipment_config_lab` | `lab_equipment_config` | `lab_id` | `lab_info` | `lab_id` |

### 4.2 耗材借用管理模块

#### 4.2.1 1:N 关系

| 主表 | 子表 | 外键 | 关系说明 |
|------|------|------|----------|
| `lab_info` | `consumable` | `lab_id` | 一个实训室可以存放多种耗材，一种耗材只存放在一个实训室 |
| `consumable` | `consumable_borrow_record` | `consumable_id` | 一种耗材可以有多个借用记录，一个借用记录只对应一种耗材 |

#### 4.2.2 外键约束

| 外键名称 | 所在表 | 外键字段 | 参考表 | 参考字段 |
|----------|--------|----------|--------|----------|
| `fk_consumable_lab` | `consumable` | `lab_id` | `lab_info` | `lab_id` |
| `fk_borrow_record_consumable` | `consumable_borrow_record` | `consumable_id` | `consumable` | `consumable_id` |

## 5. 视图与表的关系

### 5.1 实训室借用统计视图 (`lab_borrow_stat`)

该视图统计各实训室的借用情况，包括总借用次数、已完成次数、逾期次数等：

```sql
SELECT
  l.lab_id,
  l.lab_name,
  COUNT(ba.apply_id) AS total_borrow_times,
  COUNT(CASE WHEN ba.status = '已完成' THEN 1 END) AS completed_times,
  COUNT(CASE WHEN ba.status = '逾期未归还' THEN 1 END) AS overdue_times,
  COUNT(CASE WHEN ba.status = '待审批' THEN 1 END) AS pending_approval_times,
  SUM(CASE WHEN ba.status IN ('已完成', '逾期未归还') THEN TIMESTAMPDIFF(HOUR, ba.start_time, ba.end_time) ELSE 0 END) AS total_borrow_hours
FROM
  lab_info l
LEFT JOIN lab_borrow_apply ba ON l.lab_id = ba.lab_id
GROUP BY
  l.lab_id, l.lab_name;
```

### 5.2 耗材借用统计视图 (`consumable_borrow_stat`)

该视图统计各耗材的借用情况，包括总借用次数、总借用量、归还率等：

```sql
SELECT
  c.consumable_id,
  c.consumable_name,
  c.spec_model,
  c.unit,
  c.current_stock,
  c.min_stock,
  COUNT(cbr.record_id) AS total_borrow_times,
  SUM(cbr.borrow_quantity) AS total_borrow_quantity,
  SUM(cbr.return_quantity) AS total_return_quantity,
  CASE WHEN COUNT(cbr.record_id) > 0 THEN 
    ROUND(SUM(cbr.return_quantity) / SUM(cbr.borrow_quantity) * 100, 2)
  ELSE 0 END AS return_rate,
  SUM(cbr.borrow_quantity) - SUM(cbr.return_quantity) AS total_unreturned_quantity,
  l.lab_name AS storage_location
FROM
  consumable c
LEFT JOIN consumable_borrow_record cbr ON c.consumable_id = cbr.consumable_id
LEFT JOIN lab_info l ON c.lab_id = l.lab_id
GROUP BY
  c.consumable_id, c.consumable_name, c.spec_model, c.unit, c.current_stock, c.min_stock, l.lab_name;
```

### 5.3 空实训室查询视图 (`available_lab_view`)

该视图用于快速查询空实训室信息，包含实训室基本信息和设备配置：

```sql
SELECT
  l.lab_id,
  l.lab_name,
  l.capacity,
  l.location,
  l.status AS lab_status,
  GROUP_CONCAT(DISTINCT ec.equipment_name SEPARATOR ', ') AS core_equipment,
  GROUP_CONCAT(DISTINCT ec.spec_model SEPARATOR ', ') AS equipment_config
FROM
  lab_info l
LEFT JOIN lab_equipment_config ec ON l.lab_id = ec.lab_id AND ec.status = '正常'
GROUP BY
  l.lab_id, l.lab_name, l.capacity, l.location, l.status;
```

## 6. 业务流程与数据流向

### 6.1 实训室借用流程

1. **借用申请**
   - 用户选择实训室（关联 `lab_info`）
   - 填写借用信息（`lab_borrow_apply`）
   - 系统生成借用申请记录，状态为"待审批"

2. **申请审批**
   - 审批人查看待审批申请
   - 审批通过：状态更新为"已审批"
   - 审批驳回：状态更新为"已驳回"

3. **借用执行**
   - 开始借用：系统记录开始时间
   - 结束借用：系统记录结束时间，状态更新为"已完成"
   - 逾期未归还：系统自动更新状态为"逾期未归还"

4. **空实训室查询**
   - 用户输入查询条件（日期、时间段、规格）
   - 系统通过 `available_lab_view` 结合 `lab_borrow_apply` 查询空闲实训室
   - 返回符合条件的空实训室列表

### 6.2 耗材借用流程

1. **耗材入库**
   - 管理员录入耗材信息（`consumable`）
   - 设置初始库存和最低库存阈值

2. **耗材借用**
   - 用户选择耗材（关联 `consumable`）
   - 填写借用信息（`consumable_borrow_record`）
   - 系统扣减耗材当前库存
   - 生成借用记录，状态为"未归还"

3. **耗材归还**
   - 用户归还耗材，关联借用记录
   - 填写归还信息（实际归还数量、损耗情况）
   - 系统根据实际归还数量恢复库存
   - 更新借用记录状态（"部分未归还"/"已归还"）

4. **库存管理**
   - 系统实时监控耗材库存
   - 当库存低于最低阈值时，发送库存不足提醒

## 7. 数据完整性约束

### 7.1 主键约束

| 表名 | 主键字段 |
|------|----------|
| `lab_borrow_apply` | `apply_id` |
| `lab_equipment_config` | `config_id` |
| `consumable` | `consumable_id` |
| `consumable_borrow_record` | `record_id` |

### 7.2 外键约束

| 外键名称 | 所在表 | 外键字段 | 参考表 | 参考字段 |
|----------|--------|----------|--------|----------|
| `fk_borrow_apply_lab` | `lab_borrow_apply` | `lab_id` | `lab_info` | `lab_id` |
| `fk_equipment_config_lab` | `lab_equipment_config` | `lab_id` | `lab_info` | `lab_id` |
| `fk_consumable_lab` | `consumable` | `lab_id` | `lab_info` | `lab_id` |
| `fk_borrow_record_consumable` | `consumable_borrow_record` | `consumable_id` | `consumable` | `consumable_id` |

### 7.3 状态枚举值约束

#### 实训室借用申请状态
- `待审批` - 申请已提交，等待审批
- `已审批` - 申请已通过审批
- `已驳回` - 申请未通过审批
- `已完成` - 借用已完成并归还
- `逾期未归还` - 借用超过约定时间未归还

#### 耗材借用状态
- `未归还` - 耗材已借用但未归还
- `部分未归还` - 部分耗材已归还
- `已归还` - 所有耗材已归还

#### 损耗情况枚举值
- `完好` - 耗材归还时状态完好
- `部分损耗` - 耗材归还时部分损坏
- `全部损耗` - 耗材归还时完全损坏

## 8. 索引优化

为了提高查询性能，在以下字段上创建了索引：

### 8.1 实训室借用管理模块

| 表名 | 索引字段 | 索引类型 |
|------|----------|----------|
| `lab_borrow_apply` | `lab_id` | 普通索引 |
| `lab_borrow_apply` | `borrower_name` | 普通索引 |
| `lab_borrow_apply` | `borrow_department` | 普通索引 |
| `lab_borrow_apply` | `status` | 普通索引 |
| `lab_borrow_apply` | `start_time` | 普通索引 |
| `lab_borrow_apply` | `end_time` | 普通索引 |
| `lab_equipment_config` | `lab_id` | 普通索引 |
| `lab_equipment_config` | `equipment_name` | 普通索引 |

### 8.2 耗材借用管理模块

| 表名 | 索引字段 | 索引类型 |
|------|----------|----------|
| `consumable` | `consumable_name` | 普通索引 |
| `consumable` | `lab_id` | 普通索引 |
| `consumable` | `status` | 普通索引 |
| `consumable_borrow_record` | `consumable_id` | 普通索引 |
| `consumable_borrow_record` | `borrower_name` | 普通索引 |
| `consumable_borrow_record` | `borrow_department` | 普通索引 |
| `consumable_borrow_record` | `borrow_date` | 普通索引 |
| `consumable_borrow_record` | `status` | 普通索引 |

## 9. 模块间集成关系

### 9.1 与固定资产管理模块的集成

实训室借用管理模块与之前设计的固定资产管理模块通过 `lab_info` 表进行集成：

```
┌─────────────────────┐     ┌───────────────┐     ┌───────────────┐
│ lab_borrow_apply    │     │   lab_info    │     │     asset     │
├─────────────────────┤     ├───────────────┤     ├───────────────┤
│ lab_id (FK)         │────▶│ lab_id (PK)   │◀────│ asset_id (PK) │
└─────────────────────┘     └───────────────┘     └───────────────┘
```

这种集成方式确保了：
1. 实训室信息的一致性
2. 固定资产与实训室的关联性
3. 借用管理与资产管理的协同

## 10. 查询示例

### 10.1 查询指定时间段内的空实训室

```sql
SELECT
  *
FROM
  available_lab_view
WHERE
  lab_id NOT IN (
    SELECT
      lab_id
    FROM
      lab_borrow_apply
    WHERE
      status IN ('已审批', '待审批')
      AND start_time < '2026-01-10 18:00:00'
      AND end_time > '2026-01-10 14:00:00'
  )
  AND capacity >= 30
ORDER BY
  lab_id;
```

### 10.2 查询耗材借用统计信息

```sql
SELECT
  *
FROM
  consumable_borrow_stat
WHERE
  current_stock < min_stock
ORDER BY
  total_unreturned_quantity DESC;
```

### 10.3 查询实训室借用记录

```sql
SELECT
  ba.apply_id,
  l.lab_name,
  ba.borrow_department,
  ba.borrower_name,
  ba.start_time,
  ba.end_time,
  ba.status,
  ba.purpose
FROM
  lab_borrow_apply ba
JOIN lab_info l ON ba.lab_id = l.lab_id
WHERE
  ba.borrow_date BETWEEN '2026-01-01' AND '2026-01-31'
  AND ba.borrow_department = '计算机科学系'
ORDER BY
  ba.start_time DESC;
```
