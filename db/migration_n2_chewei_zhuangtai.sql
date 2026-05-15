-- 这是N2代码 — N2 车位占用状态机：与入场单、离场/缴费订单关联
-- 可重复执行：列已存在则跳过，避免 1060 Duplicate column name
-- 这是我cursor给父亲写的
SET NAMES utf8mb4;

SET @s = (SELECT IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'chewei' AND COLUMN_NAME = 'chezijinchang_id') > 0,
  'SELECT 1',
  'ALTER TABLE `chewei` ADD COLUMN `chezijinchang_id` bigint(20) DEFAULT NULL COMMENT ''当前入场单chezijinchang.id'' AFTER `cheweixinxi_id`'
));
PREPARE _stmt FROM @s;
EXECUTE _stmt;
DEALLOCATE PREPARE _stmt;

SET @s = (SELECT IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'chewei' AND COLUMN_NAME = 'tingchejiaofei_id') > 0,
  'SELECT 1',
  'ALTER TABLE `chewei` ADD COLUMN `tingchejiaofei_id` bigint(20) DEFAULT NULL COMMENT ''当前离场/缴费订单tingchejiaofei.id'' AFTER `chezijinchang_id`'
));
PREPARE _stmt FROM @s;
EXECUTE _stmt;
DEALLOCATE PREPARE _stmt;

SET @s = (SELECT IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'chezijinchang' AND COLUMN_NAME = 'chewei_id') > 0,
  'SELECT 1',
  'ALTER TABLE `chezijinchang` ADD COLUMN `chewei_id` bigint(20) DEFAULT NULL COMMENT ''关联车位主数据chewei.id'' AFTER `jinchangshijian`'
));
PREPARE _stmt FROM @s;
EXECUTE _stmt;
DEALLOCATE PREPARE _stmt;

SET @s = (SELECT IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'tingchejiaofei' AND COLUMN_NAME = 'chewei_id') > 0,
  'SELECT 1',
  'ALTER TABLE `tingchejiaofei` ADD COLUMN `chewei_id` bigint(20) DEFAULT NULL COMMENT ''关联车位主数据chewei.id'' AFTER `crossrefid`'
));
PREPARE _stmt FROM @s;
EXECUTE _stmt;
DEALLOCATE PREPARE _stmt;
