-- 这是我cursor给父亲写的 — 十条逻辑关联演示数据（覆盖预约/入场/离场/支付/补缴/统计/异常）
-- 场景：演示停车场 B区，用户 demo2026，可测 N8/N9/N10/M6/M7
SET NAMES utf8mb4;
USE springboot673i609q;

-- ① 演示用户（前端登录：demo2026 / 123456）
INSERT INTO `yonghu` (`id`, `addtime`, `yonghuzhanghao`, `xingming`, `mima`, `xingbie`, `shouji`, `chepaihao`, `touxiang`)
SELECT 900001, NOW(), 'demo2026', '张明', '123456', '男', '13800138000', '粤B88888', 'upload/yonghu_touxiang_demo.jpg'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `yonghu` WHERE `yonghuzhanghao` = 'demo2026');

-- ② 车场区域信息
INSERT INTO `cheweixinxi` (`id`, `addtime`, `tingchechangmingcheng`, `quyu`, `cheweitupian`, `cheweishuliang`, `xiaoshidanjia`, `cheweixiangqing`)
SELECT 29, NOW(), '演示停车场', 'B区', 'upload/cheweixinxi_demo.jpg', 10, 6,
       '毕业设计演示用车场：支持预约、入场、离场计费、补缴与运营报表。'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `cheweixinxi` WHERE `tingchechangmingcheng` = '演示停车场' AND `quyu` = 'B区');

-- ③④⑤ 三个车位（空闲 / 已入场 / 待结算）
INSERT INTO `chewei` (`id`, `tingchechangmingcheng`, `quyu`, `cheweibianhao`, `zhuangtai`, `cheweixinxi_id`, `beizhu`, `banben`, `wangge_hang`, `wangge_lie`)
SELECT 4, '演示停车场', 'B区', 'DEMO-B01', '已预约未入场', 29, '演示-待入场预约', 0, 1, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `chewei` WHERE `cheweibianhao` = 'DEMO-B01' AND `tingchechangmingcheng` = '演示停车场');

INSERT INTO `chewei` (`id`, `tingchechangmingcheng`, `quyu`, `cheweibianhao`, `zhuangtai`, `cheweixinxi_id`, `beizhu`, `banben`, `wangge_hang`, `wangge_lie`)
SELECT 5, '演示停车场', 'B区', 'DEMO-B02', '已入场', 29, '演示-在场车辆', 1, 1, 2
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `chewei` WHERE `cheweibianhao` = 'DEMO-B02' AND `tingchechangmingcheng` = '演示停车场');

INSERT INTO `chewei` (`id`, `tingchechangmingcheng`, `quyu`, `cheweibianhao`, `zhuangtai`, `cheweixinxi_id`, `beizhu`, `banben`, `wangge_hang`, `wangge_lie`)
SELECT 6, '演示停车场', 'B区', 'DEMO-B03', '已离场待结算', 29, '演示-待支付离场', 0, 2, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `chewei` WHERE `cheweibianhao` = 'DEMO-B03' AND `tingchechangmingcheng` = '演示停车场');

-- M5 计费规则（演示停车场）
INSERT INTO `chewei_jifei_guize` (`guize_mingcheng`, `tingchechangmingcheng`, `quyu`, `jifei_moshi`, `meixiaoshi_danjia`, `shouxiaoshi_yuan`, `jieti_danjia`, `fengding_yuan`, `zuixiao_jifei_xiaoshi`, `qiyong`, `beizhu`)
SELECT '演示B区-首小时封顶', '演示停车场', 'B区', '首小时', 6, 12, 6, 60, 1, '是', '演示数据：首12元，后续6元/时，封顶60'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `chewei_jifei_guize` WHERE `tingchechangmingcheng` = '演示停车场' AND `quyu` = 'B区');

-- ⑥ 有效预约未入场（N10 异常-未入场）
INSERT INTO `chewei_yuyue` (`id`, `chewei_id`, `tingchechangmingcheng`, `quyu`, `kaishi_shijian`, `jieshu_shijian`, `zhuangtai`, `yuyue_zhifu_zhuangtai`, `liucheng_jiedian`, `addtime`)
SELECT 2, 4, '演示停车场', 'B区', DATE_ADD(NOW(), INTERVAL 2 HOUR), DATE_ADD(NOW(), INTERVAL 5 HOUR), '有效', '已支付', '已预约待入场', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `chewei_yuyue` WHERE `id` = 2);

-- ⑦ 已取消预约（N8 超时统计）
INSERT INTO `chewei_yuyue` (`id`, `chewei_id`, `tingchechangmingcheng`, `quyu`, `kaishi_shijian`, `jieshu_shijian`, `zhuangtai`, `yuyue_zhifu_zhuangtai`, `liucheng_jiedian`, `addtime`)
SELECT 3, 4, '演示停车场', 'B区', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), '已取消', '无需预付', '已取消', DATE_SUB(NOW(), INTERVAL 2 DAY)
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `chewei_yuyue` WHERE `id` = 3);

-- ⑧ 在场入场单（DEMO-B02）
INSERT INTO `chezijinchang` (`id`, `addtime`, `tingchechangmingcheng`, `quyu`, `cheweishuliang`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `chewei_id`)
SELECT 48, NOW(), '演示停车场', 'B区', 1, 6, 'demo2026', '张明', '13800138000', '粤B88888', DATE_SUB(NOW(), INTERVAL 3 HOUR), 5
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `chezijinchang` WHERE `id` = 48);

-- ⑨ 已支付离场单（近7日收入趋势 / N8 KPI）
INSERT INTO `tingchejiaofei` (`id`, `addtime`, `dingdanhao`, `tingchechangmingcheng`, `quyu`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `lichangshijian`, `bencitingcheshizhang`, `bencitingchefeiyong`, `chewei_id`, `ispay`)
SELECT 54, DATE_SUB(NOW(), INTERVAL 1 DAY), 'DEMO-PAY-001', '演示停车场', 'B区', 6, 'demo2026', '张明', '13800138000', '粤B88888',
       DATE_SUB(NOW(), INTERVAL 1 DAY) - INTERVAL 4 HOUR, DATE_SUB(NOW(), INTERVAL 1 DAY), 4.0, 30.0, 5, '已支付'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `tingchejiaofei` WHERE `dingdanhao` = 'DEMO-PAY-001');

-- ⑩ 未支付离场单 + 关联入场（N10 异常-未支付离场）
INSERT INTO `chezijinchang` (`id`, `addtime`, `tingchechangmingcheng`, `quyu`, `cheweishuliang`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `chewei_id`)
SELECT 49, DATE_SUB(NOW(), INTERVAL 6 HOUR), '演示停车场', 'B区', 1, 6, 'demo2026', '张明', '13800138000', '粤B88888', DATE_SUB(NOW(), INTERVAL 6 HOUR), 6
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `chezijinchang` WHERE `id` = 49);

INSERT INTO `tingchejiaofei` (`id`, `addtime`, `dingdanhao`, `tingchechangmingcheng`, `quyu`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `lichangshijian`, `bencitingcheshizhang`, `bencitingchefeiyong`, `crossuserid`, `crossrefid`, `chewei_id`, `ispay`)
SELECT 55, NOW(), 'DEMO-UNPAY-001', '演示停车场', 'B区', 6, 'demo2026', '张明', '13800138000', '粤B88888',
       DATE_SUB(NOW(), INTERVAL 6 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR), 5.0, 42.0, 900001, 49, 6, '未支付'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `tingchejiaofei` WHERE `dingdanhao` = 'DEMO-UNPAY-001');

-- 关联回填（车位 / 预约 / 补缴演示）
UPDATE `chewei` SET `chezijinchang_id` = 48, `tingchejiaofei_id` = NULL WHERE `id` = 5;
UPDATE `chewei` SET `chezijinchang_id` = 49, `tingchejiaofei_id` = 55 WHERE `id` = 6;

UPDATE `chewei_yuyue` SET `chezijinchang_id` = NULL, `tingchejiaofei_id` = NULL WHERE `id` = 2;

INSERT INTO `tingche_bujiao` (`danhao`, `leixing`, `chezijinchang_id`, `tingchejiaofei_id`, `yonghuzhanghao`, `xingming`, `chepaihao`, `jine`, `yuanyin`, `zhuangtai`, `beizhu`, `addtime`)
SELECT 'DEMO-BJ-001', '超时补缴', 48, NULL, 'demo2026', '张明', '粤B88888', 15.0, '演示超时补缴单', '待支付', 'N7/N10 测试', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `tingche_bujiao` WHERE `danhao` = 'DEMO-BJ-001');

-- 额外：昨日第二笔已支付（让近7日趋势更明显）
INSERT INTO `tingchejiaofei` (`addtime`, `dingdanhao`, `tingchechangmingcheng`, `quyu`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `lichangshijian`, `bencitingcheshizhang`, `bencitingchefeiyong`, `chewei_id`, `ispay`)
SELECT DATE_SUB(NOW(), INTERVAL 2 DAY), 'DEMO-PAY-002', '演示停车场', 'B区', 6, 'demo2026', '张明', '13800138000', '粤B88888',
       DATE_SUB(NOW(), INTERVAL 2 DAY) - INTERVAL 3 HOUR, DATE_SUB(NOW(), INTERVAL 2 DAY), 3.0, 18.0, 4, '已支付'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `tingchejiaofei` WHERE `dingdanhao` = 'DEMO-PAY-002');
