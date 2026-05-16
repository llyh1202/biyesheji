-- 这是M5代码 — 停车费用计算规则（首小时/阶梯/封顶），与 N6 宽限期、N3 离场结算共用
-- 这是我cursor给父亲写的
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `chewei_jifei_guize` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `guize_mingcheng` varchar(100) NOT NULL,
  `tingchechangmingcheng` varchar(200) DEFAULT NULL,
  `quyu` varchar(200) DEFAULT NULL,
  `jifei_moshi` varchar(32) NOT NULL DEFAULT '纯小时' COMMENT '纯小时/首小时/阶梯',
  `meixiaoshi_danjia` int(11) DEFAULT NULL COMMENT '每小时单价，空则取入场单单价',
  `shouxiaoshi_yuan` double DEFAULT NULL COMMENT '首小时费用(首小时/阶梯)',
  `jieti_danjia` int(11) DEFAULT NULL COMMENT '后续每小时单价(阶梯)',
  `fengding_yuan` double DEFAULT NULL COMMENT '封顶金额，0或空不封顶',
  `zuixiao_jifei_xiaoshi` double NOT NULL DEFAULT 1 COMMENT '最小计费小时(向上取整后)',
  `qiyong` varchar(8) NOT NULL DEFAULT '是',
  `beizhu` varchar(500) DEFAULT NULL,
  `addtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_lot_quyu` (`tingchechangmingcheng`, `quyu`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `chewei_jifei_guize` (`guize_mingcheng`, `tingchechangmingcheng`, `quyu`, `jifei_moshi`, `meixiaoshi_danjia`, `shouxiaoshi_yuan`, `jieti_danjia`, `fengding_yuan`, `zuixiao_jifei_xiaoshi`, `qiyong`, `beizhu`)
SELECT 'global_hourly_default', NULL, NULL, '纯小时', NULL, NULL, NULL, NULL, 1, '是', 'M5 default: ceil hours * hourly price'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `chewei_jifei_guize` WHERE `tingchechangmingcheng` IS NULL AND (`quyu` IS NULL OR `quyu` = '') LIMIT 1);
