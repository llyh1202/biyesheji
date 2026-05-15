-- 这是N4代码 — 车位时段预约（余位校验）
-- 这是我cursor给父亲写的
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `chewei_yuyue` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `chewei_id` bigint(20) NOT NULL COMMENT '车位 chewei.id',
  `tingchechangmingcheng` varchar(200) DEFAULT NULL COMMENT '停车场名称（冗余）',
  `quyu` varchar(200) DEFAULT NULL COMMENT '区域（冗余）',
  `kaishi_shijian` datetime NOT NULL COMMENT '预约开始',
  `jieshu_shijian` datetime NOT NULL COMMENT '预约结束',
  `zhuangtai` varchar(32) NOT NULL DEFAULT '有效' COMMENT '有效/已取消',
  `addtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_chewei_time` (`chewei_id`, `kaishi_shijian`, `jieshu_shijian`),
  KEY `idx_lot_quyu` (`tingchechangmingcheng`, `quyu`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='N4 车位时段预约';
