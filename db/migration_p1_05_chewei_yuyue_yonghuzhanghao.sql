-- 这是我cursor给父亲写的 — P1-05：chewei_yuyue 绑定用户账号（我的停车查询）
-- 可重复执行
SET NAMES utf8mb4;

SET @s = (SELECT IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'chewei_yuyue' AND COLUMN_NAME = 'yonghuzhanghao') > 0,
  'SELECT 1',
  'ALTER TABLE `chewei_yuyue` ADD COLUMN `yonghuzhanghao` varchar(200) DEFAULT NULL COMMENT ''P1-05 预约用户账号（yonghu.yonghuzhanghao）'' AFTER `zhuangtai`'
));
PREPARE _stmt FROM @s;
EXECUTE _stmt;
DEALLOCATE PREPARE _stmt;

SET @idx = (SELECT COUNT(*) FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'chewei_yuyue' AND INDEX_NAME = 'idx_yonghuzhanghao');
SET @sql = IF(@idx > 0, 'SELECT 1',
  'ALTER TABLE `chewei_yuyue` ADD KEY `idx_yonghuzhanghao` (`yonghuzhanghao`)');
PREPARE _stmt2 FROM @sql;
EXECUTE _stmt2;
DEALLOCATE PREPARE _stmt2;
