-- 这是N5代码 — 车位可视化可选：简易平面图栅格坐标（非 GIS）
-- 可重复执行：列已存在则跳过，避免 1060 Duplicate column name
-- 这是我cursor给父亲写的
SET NAMES utf8mb4;

SET @s = (SELECT IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'chewei' AND COLUMN_NAME = 'wangge_hang') > 0,
  'SELECT 1',
  'ALTER TABLE `chewei` ADD COLUMN `wangge_hang` int(11) DEFAULT NULL COMMENT ''N5 栅格行(可选)'' AFTER `beizhu`'
));
PREPARE _stmt FROM @s;
EXECUTE _stmt;
DEALLOCATE PREPARE _stmt;

SET @s = (SELECT IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'chewei' AND COLUMN_NAME = 'wangge_lie') > 0,
  'SELECT 1',
  'ALTER TABLE `chewei` ADD COLUMN `wangge_lie` int(11) DEFAULT NULL COMMENT ''N5 栅格列(可选)'' AFTER `wangge_hang`'
));
PREPARE _stmt FROM @s;
EXECUTE _stmt;
DEALLOCATE PREPARE _stmt;
