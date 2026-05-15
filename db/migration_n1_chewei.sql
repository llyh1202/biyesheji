-- N1 车位级主数据：单车位编号，隶属车场/区域
-- 这是我cursor给父亲写的
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `chewei` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tingchechangmingcheng` varchar(200) DEFAULT NULL COMMENT '停车场名称',
  `quyu` varchar(200) NOT NULL DEFAULT '' COMMENT '区域',
  `cheweibianhao` varchar(100) NOT NULL COMMENT '车位编号，如A-01',
  `zhuangtai` varchar(50) DEFAULT '空闲' COMMENT '空闲/已预约/占用等',
  `cheweixinxi_id` bigint(20) DEFAULT NULL COMMENT '关联车位信息(区域配置)ID，可选',
  `beizhu` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lot_area_code` (`tingchechangmingcheng`(80),`quyu`(80),`cheweibianhao`(80))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车位编号主数据';

INSERT IGNORE INTO `chewei` (`tingchechangmingcheng`,`quyu`,`cheweibianhao`,`zhuangtai`,`beizhu`) VALUES
('停车场名称1','A区','A-01','空闲','示例'),
('停车场名称1','A区','A-02','空闲',NULL),
('停车场名称2','A区','B-01','空闲',NULL);
