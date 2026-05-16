-- 这是M1代码 — 预约/订单模型扩展：支付态、流程节点、关联入场/离场缴费单（与 N2/N3/N4 流水一致）
-- 可重复执行
-- 这是我cursor给父亲写的
SET NAMES utf8mb4;

SET @s = (SELECT IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'chewei_yuyue' AND COLUMN_NAME = 'yuyue_zhifu_zhuangtai') > 0,
  'SELECT 1',
  'ALTER TABLE `chewei_yuyue` ADD COLUMN `yuyue_zhifu_zhuangtai` varchar(32) NOT NULL DEFAULT ''无需预付'' COMMENT ''M1 预约侧支付状态：未支付/已支付/无需预付'' AFTER `zhuangtai`'
));
PREPARE _stmt FROM @s;
EXECUTE _stmt;
DEALLOCATE PREPARE _stmt;

SET @s = (SELECT IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'chewei_yuyue' AND COLUMN_NAME = 'liucheng_jiedian') > 0,
  'SELECT 1',
  'ALTER TABLE `chewei_yuyue` ADD COLUMN `liucheng_jiedian` varchar(64) NOT NULL DEFAULT ''已预约待入场'' COMMENT ''M1 与入场离场支付一致的状态机节点'' AFTER `yuyue_zhifu_zhuangtai`'
));
PREPARE _stmt FROM @s;
EXECUTE _stmt;
DEALLOCATE PREPARE _stmt;

SET @s = (SELECT IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'chewei_yuyue' AND COLUMN_NAME = 'chezijinchang_id') > 0,
  'SELECT 1',
  'ALTER TABLE `chewei_yuyue` ADD COLUMN `chezijinchang_id` bigint(20) DEFAULT NULL COMMENT ''M1 关联入场单 chezijinchang.id'' AFTER `liucheng_jiedian`'
));
PREPARE _stmt FROM @s;
EXECUTE _stmt;
DEALLOCATE PREPARE _stmt;

SET @s = (SELECT IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'chewei_yuyue' AND COLUMN_NAME = 'tingchejiaofei_id') > 0,
  'SELECT 1',
  'ALTER TABLE `chewei_yuyue` ADD COLUMN `tingchejiaofei_id` bigint(20) DEFAULT NULL COMMENT ''M1 关联离场/停车费单 tingchejiaofei.id'' AFTER `chezijinchang_id`'
));
PREPARE _stmt FROM @s;
EXECUTE _stmt;
DEALLOCATE PREPARE _stmt;
