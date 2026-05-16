-- 这是N6代码 — 超时策略规则表
-- 这是我cursor给父亲写的
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `chewei_chaoshi_guize` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `guize_mingcheng` varchar(100) NOT NULL,
  `tingchechangmingcheng` varchar(200) DEFAULT NULL,
  `quyu` varchar(200) DEFAULT NULL,
  `yuyue_baoliu_fenzhong` int(11) NOT NULL DEFAULT 30,
  `jifei_kuanxian_fenzhong` int(11) NOT NULL DEFAULT 15,
  `weiruchang_koufei_yuan` double DEFAULT 0,
  `qiyong` varchar(8) NOT NULL DEFAULT '是',
  `beizhu` varchar(500) DEFAULT NULL,
  `addtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_lot_quyu` (`tingchechangmingcheng`, `quyu`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `chewei_chaoshi_guize` (`guize_mingcheng`, `tingchechangmingcheng`, `quyu`, `yuyue_baoliu_fenzhong`, `jifei_kuanxian_fenzhong`, `weiruchang_koufei_yuan`, `qiyong`, `beizhu`)
SELECT 'global_default', NULL, NULL, 30, 15, 0, '是', 'N6 default hold 30min grace 15min'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `chewei_chaoshi_guize` WHERE `tingchechangmingcheng` IS NULL AND (`quyu` IS NULL OR `quyu` = '') LIMIT 1);
