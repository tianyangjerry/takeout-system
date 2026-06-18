CREATE TABLE IF NOT EXISTS `user` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  phone VARCHAR(20),
  role VARCHAR(20) NOT NULL,
  address VARCHAR(255),
  avatar VARCHAR(255),
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  sort_order INT DEFAULT 0,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS dish (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  category_id BIGINT NOT NULL,
  name VARCHAR(100) NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  stock INT NOT NULL DEFAULT 0,
  image_url VARCHAR(255),
  description TEXT,
  status TINYINT DEFAULT 1,
  sales INT DEFAULT 0,
  rating DECIMAL(3,2) DEFAULT 5.00,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cart (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL UNIQUE,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cart_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  cart_id BIGINT NOT NULL,
  dish_id BIGINT NOT NULL,
  quantity INT NOT NULL DEFAULT 1,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_cart_dish (cart_id, dish_id),
  KEY idx_cart_item_cart_id (cart_id),
  KEY idx_cart_item_dish_id (dish_id)
);

CREATE TABLE IF NOT EXISTS orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_no VARCHAR(40) NOT NULL UNIQUE,
  user_id BIGINT NOT NULL,
  total_amount DECIMAL(10,2) NOT NULL,
  receiver_name VARCHAR(50) NOT NULL,
  receiver_phone VARCHAR(20) NOT NULL,
  receiver_address VARCHAR(255) NOT NULL,
  remark VARCHAR(255),
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  cancel_reason VARCHAR(255),
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_orders_user_id (user_id),
  KEY idx_orders_status (status),
  KEY idx_orders_created_time (created_time)
);

CREATE TABLE IF NOT EXISTS order_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  dish_id BIGINT NOT NULL,
  dish_name VARCHAR(100) NOT NULL,
  dish_price DECIMAL(10,2) NOT NULL,
  quantity INT NOT NULL,
  subtotal DECIMAL(10,2) NOT NULL,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  KEY idx_order_item_order_id (order_id),
  KEY idx_order_item_dish_id (dish_id)
);

CREATE TABLE IF NOT EXISTS order_status_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  status VARCHAR(20) NOT NULL,
  status_text VARCHAR(30) NOT NULL,
  operator_id BIGINT,
  operator_role VARCHAR(20),
  remark VARCHAR(255),
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  KEY idx_order_status_log_order_id (order_id),
  KEY idx_order_status_log_status (status)
);

-- 演示管理员账号：admin / admin123
-- 密码使用 PBKDF2-SHA256 加密，和 PasswordUtil 的校验格式一致。
INSERT IGNORE INTO `user` (username, password, phone, role, address, avatar)
VALUES (
  'admin',
  'pbkdf2_sha256$120000$YWRtaW4tZGVtby1zYWx0LTE$p5Y4SxGUvVkBvRtJRduO3cPBoob1Bn6H-FB9G5sa6aI',
  '13800000000',
  'ADMIN',
  '校园食堂管理处',
  NULL
);

INSERT IGNORE INTO category (id, name, sort_order)
VALUES
  (1, '盖浇饭', 1),
  (2, '面食', 2),
  (3, '饮品', 3),
  (4, '小吃', 4);

INSERT IGNORE INTO dish (id, category_id, name, price, stock, image_url, description, status, sales, rating)
VALUES
  (1, 1, '黄焖鸡米饭', 18.00, 32, '/uploads/dishes/dish-1-huangmenji.jpg', '鸡肉鲜嫩，汤汁浓郁，适合午餐高峰快速点餐。', 1, 156, 4.80),
  (2, 1, '香辣鸡排饭', 16.00, 26, '/uploads/dishes/dish-2-jipai-rice.jpg', '香辣鸡排搭配米饭和时蔬，校园人气套餐。', 1, 98, 4.90),
  (3, 2, '番茄牛肉面', 17.00, 20, '/uploads/dishes/dish-3-beef-noodle.jpg', '番茄汤底酸甜开胃，牛肉分量充足。', 1, 88, 4.70),
  (4, 3, '珍珠奶茶', 10.00, 45, '/uploads/dishes/dish-4-milk-tea.jpg', '经典珍珠奶茶，甜度适中。', 1, 120, 4.60),
  (5, 4, '炸鸡腿套餐', 15.00, 18, '/uploads/dishes/dish-5-fried-chicken.jpg', '外酥里嫩，搭配小食更满足。', 1, 76, 4.50);

UPDATE dish SET image_url = '/uploads/dishes/dish-1-huangmenji.jpg' WHERE id = 1;
UPDATE dish SET image_url = '/uploads/dishes/dish-2-jipai-rice.jpg' WHERE id = 2;
UPDATE dish SET image_url = '/uploads/dishes/dish-3-beef-noodle.jpg' WHERE id = 3;
UPDATE dish SET image_url = '/uploads/dishes/dish-4-milk-tea.jpg' WHERE id = 4;
UPDATE dish SET image_url = '/uploads/dishes/dish-5-fried-chicken.jpg' WHERE id = 5;
