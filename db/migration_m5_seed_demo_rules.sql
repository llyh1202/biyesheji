-- 这是我cursor给父亲写的 — M5 示例计费规则（配合 cheweixinxi 停车场名称1/2）
SET NAMES utf8mb4;

INSERT INTO `chewei_jifei_guize` (`guize_mingcheng`, `tingchechangmingcheng`, `quyu`, `jifei_moshi`, `meixiaoshi_danjia`, `shouxiaoshi_yuan`, `jieti_danjia`, `fengding_yuan`, `zuixiao_jifei_xiaoshi`, `qiyong`, `beizhu`)
SELECT '停车场1-A区-首小时封顶', '停车场名称1', 'A区', '首小时', 5, 10, 5, 50, 1, '是', '首小时10元，后续5元/时，单日封顶50元'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `chewei_jifei_guize` WHERE `tingchechangmingcheng` = '停车场名称1' AND `quyu` = 'A区' LIMIT 1
);

INSERT INTO `chewei_jifei_guize` (`guize_mingcheng`, `tingchechangmingcheng`, `quyu`, `jifei_moshi`, `meixiaoshi_danjia`, `shouxiaoshi_yuan`, `jieti_danjia`, `fengding_yuan`, `zuixiao_jifei_xiaoshi`, `qiyong`, `beizhu`)
SELECT '停车场2-A区-纯小时', '停车场名称2', 'A区', '纯小时', 8, NULL, NULL, NULL, 1, '是', '纯小时8元，无封顶'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `chewei_jifei_guize` WHERE `tingchechangmingcheng` = '停车场名称2' AND `quyu` = 'A区' LIMIT 1
);
