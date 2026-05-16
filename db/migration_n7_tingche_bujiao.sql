-- 这是N7代码 — 续费/超时补缴单（管理员调整 + 用户补缴，离场结算时并入主缴费单）
-- 这是我cursor给父亲写的
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `tingche_bujiao` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `danhao` varchar(64) NOT NULL,
  `leixing` varchar(32) NOT NULL COMMENT '超时补缴/管理员调整/续费',
  `chezijinchang_id` bigint(20) NOT NULL,
  `tingchejiaofei_id` bigint(20) DEFAULT NULL COMMENT '并入离场主单后回填',
  `yonghuzhanghao` varchar(200) DEFAULT NULL,
  `xingming` varchar(200) DEFAULT NULL,
  `chepaihao` varchar(64) DEFAULT NULL,
  `jine` double NOT NULL DEFAULT 0,
  `yuanyin` varchar(500) DEFAULT NULL,
  `zhuangtai` varchar(32) NOT NULL DEFAULT '待支付',
  `guanliyuan_zhanghao` varchar(200) DEFAULT NULL,
  `beizhu` varchar(500) DEFAULT NULL,
  `zhifu_shijian` datetime DEFAULT NULL,
  `addtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_ruchang` (`chezijinchang_id`),
  KEY `idx_user` (`yonghuzhanghao`),
  KEY `idx_zhuangtai` (`zhuangtai`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
