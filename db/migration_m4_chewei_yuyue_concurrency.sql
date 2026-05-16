-- 这是M4代码 — 预约并发：车位乐观锁版本号 + 防重复预约唯一约束
-- 这是我cursor给父亲写的
SET NAMES utf8mb4;

SET @s = (SELECT IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'chewei' AND COLUMN_NAME = 'banben') > 0,
  'SELECT 1',
  'ALTER TABLE `chewei` ADD COLUMN `banben` int(11) NOT NULL DEFAULT 0 COMMENT ''M4 optimistic lock version'' AFTER `beizhu`'
));
PREPARE _stmt FROM @s;
EXECUTE _stmt;
DEALLOCATE PREPARE _stmt;

-- 同一车位、同一时段、有效预约不可重复插入（防双击/并发重复单）
SET @idx = (SELECT COUNT(*) FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'chewei_yuyue' AND INDEX_NAME = 'uk_chewei_slot_valid');
SET @sql = IF(@idx > 0, 'SELECT 1',
  'ALTER TABLE `chewei_yuyue` ADD UNIQUE KEY `uk_chewei_slot_valid` (`chewei_id`, `kaishi_shijian`, `jieshu_shijian`, `zhuangtai`)');
PREPARE _stmt2 FROM @sql;
EXECUTE _stmt2;
DEALLOCATE PREPARE _stmt2;
