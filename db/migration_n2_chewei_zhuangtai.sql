-- N2 车位占用状态机：与入场单、离场/缴费订单关联
-- 这是我cursor给父亲写的
SET NAMES utf8mb4;

ALTER TABLE `chewei`
  ADD COLUMN `chezijinchang_id` bigint(20) DEFAULT NULL COMMENT '当前入场单chezijinchang.id' AFTER `cheweixinxi_id`,
  ADD COLUMN `tingchejiaofei_id` bigint(20) DEFAULT NULL COMMENT '当前离场/缴费订单tingchejiaofei.id' AFTER `chezijinchang_id`;

ALTER TABLE `chezijinchang`
  ADD COLUMN `chewei_id` bigint(20) DEFAULT NULL COMMENT '关联车位主数据chewei.id' AFTER `jinchangshijian`;

ALTER TABLE `tingchejiaofei`
  ADD COLUMN `chewei_id` bigint(20) DEFAULT NULL COMMENT '关联车位主数据chewei.id' AFTER `crossrefid`;
