# 后端阶段说明

已完成内容：

- 基础目录分层：`controller`、`service`、`mapper`、`entity`、`dto`、`vo`、`common`、`config`、`utils`。
- 数据库脚本：`src/main/resources/schema.sql`，包含用户、分类、菜品、购物车、订单相关表。
- 用户接口：
  - `POST /api/auth/register`
  - `POST /api/auth/login`
  - `GET /api/auth/me`
  - `PUT /api/users/me`
- 分类与菜品接口：
  - `GET /api/categories`
  - `POST /api/admin/categories`
  - `PUT /api/admin/categories/{id}`
  - `DELETE /api/admin/categories/{id}`
  - `GET /api/dishes`
  - `GET /api/dishes/{id}`
  - `GET /api/dishes/top`
  - `GET /api/dishes/recommend`
  - `POST /api/admin/dishes`
  - `PUT /api/admin/dishes/{id}`
  - `PATCH /api/admin/dishes/{id}/status`
  - `DELETE /api/admin/dishes/{id}`
  - `POST /api/upload/image`
- 购物车接口：
  - `GET /api/cart`
  - `POST /api/cart/items`
  - `PUT /api/cart/items/{id}`
  - `DELETE /api/cart/items/{id}`
  - `DELETE /api/cart`
- 订单接口：
  - `POST /api/orders`
  - `GET /api/orders/my`
  - `GET /api/orders/{id}`
  - `PATCH /api/orders/{id}/cancel`
  - `GET /api/admin/orders`
  - `GET /api/admin/orders/{id}`
  - `PATCH /api/admin/orders/{id}/status`
- 密码加密：PBKDF2-SHA256。
- Token：HMAC-SHA256 JWT。
- 管理员接口权限：需要 `ADMIN` token。
- 图片上传目录：`backend/uploads/dishes/`，访问路径为 `/uploads/dishes/...`。
- 跨域：允许 `localhost:5173` 和 `127.0.0.1:5173`。
- 后端端口：`8090`。

第三阶段业务规则：

- 用户下单时从购物车生成订单，订单明细保存菜品名称和价格快照。
- 下单会扣减库存、增加销量并清空购物车。
- 只有 `PENDING` 订单允许用户取消，取消后恢复库存并回退销量。
- 管理员订单状态按 `PENDING -> ACCEPTED -> COOKING -> DELIVERING -> COMPLETED` 顺序流转。

第四阶段订单状态闭环：

- 新增 `order_status_log` 表，记录订单每次状态变化、操作人、操作角色和时间。
- 用户提交订单会写入 `PENDING` 日志，用户取消订单会写入 `CANCELLED` 日志。
- 管理员推进订单状态时会写入真实状态日志，订单详情时间轴优先读取这些日志。
- 历史订单如果没有状态日志，详情页会根据订单当前状态和创建 / 更新时间生成兼容时间轴。
- 管理员订单页支持关键词、状态、开始日期、结束日期筛选，并可进入订单详情查看明细和时间轴。

第四阶段联调建议：

1. 客户下单后查看订单详情，确认时间轴出现“提交订单”时间。
2. 管理员进入订单管理，按顺序推进状态到 `COMPLETED`。
3. 客户刷新订单详情，确认每个已发生节点都有真实时间。
4. 尝试跳级修改订单状态，确认后端返回“订单状态必须按流程流转”。
5. 取消 `PENDING` 订单，确认时间轴显示取消节点，并恢复库存。

本地使用：

1. 创建 MySQL 数据库 `takeout_system`。
2. 执行 `src/main/resources/schema.sql`。
3. 确认 `src/main/resources/application.yaml` 里的数据库用户名和密码正确。
4. 启动后端。

接口地址示例：

```text
http://localhost:8090/api/auth/login
```

演示管理员账号：

```text
username: admin
password: admin123
```
