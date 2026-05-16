-- 这是我cursor给父亲写的 — 100 套逻辑关联演示数据（覆盖 N1~N10 / M1~M7 全部主要状态）
-- 用户端登录：batch100_001 ~ batch100_100，密码均为 123456
-- 可重复执行：先清理 BATCH100 / batch100_ 前缀数据再插入
SET NAMES utf8mb4;
USE springboot673i609q;

-- ========== 清理旧批次（仅删本脚本生成的数据）==========
DELETE b FROM `tingche_bujiao` b WHERE b.`danhao` LIKE 'BATCH100-B-%';
DELETE f FROM `tingchejiaofei` f WHERE f.`dingdanhao` LIKE 'BATCH100-F-%';
DELETE y FROM `chewei_yuyue` y WHERE y.`id` BETWEEN 1101 AND 1200;
DELETE e FROM `chezijinchang` e WHERE e.`id` BETWEEN 1101 AND 1200;
DELETE c FROM `chewei` c WHERE c.`cheweibianhao` LIKE 'B100-%';
DELETE u FROM `yonghu` u WHERE u.`yonghuzhanghao` LIKE 'batch100_%';

-- ========== 存储过程：生成 100 套 ==========
DROP PROCEDURE IF EXISTS `proc_seed_batch100`;
DELIMITER $$
CREATE PROCEDURE `proc_seed_batch100`()
BEGIN
  DECLARE i INT DEFAULT 1;
  DECLARE st INT;
  DECLARE v_uid BIGINT;
  DECLARE v_cw BIGINT;
  DECLARE v_yy BIGINT;
  DECLARE v_rc BIGINT;
  DECLARE v_fee BIGINT;
  DECLARE v_lot VARCHAR(200);
  DECLARE v_quyu VARCHAR(200);
  DECLARE v_cxi BIGINT;
  DECLARE v_price INT;
  DECLARE v_user VARCHAR(64);
  DECLARE v_name VARCHAR(64);
  DECLARE v_plate VARCHAR(32);
  DECLARE v_code VARCHAR(32);
  DECLARE v_hours DOUBLE;
  DECLARE v_fee_amt DOUBLE;
  DECLARE v_day_off INT;
  DECLARE v_jin DATETIME;
  DECLARE v_li DATETIME;
  DECLARE v_kaishi DATETIME;
  DECLARE v_jieshu DATETIME;

  WHILE i <= 100 DO
    SET st = (i - 1) % 20;
    SET v_uid = 910000 + i;
    SET v_cw = 1100 + i;
    SET v_yy = 1100 + i;
    SET v_rc = 1100 + i;
    SET v_fee = 1100 + i;
    SET v_user = CONCAT('batch100_', LPAD(i, 3, '0'));
    SET v_name = CONCAT('测试车主', i);
    SET v_plate = CONCAT('粤T', LPAD(10000 + i, 5, '0'));
    SET v_code = CONCAT('B100-', LPAD(i, 3, '0'));
    SET v_day_off = i % 7;
    SET v_hours = 1 + (i % 8);
    SET v_fee_amt = ROUND(v_hours * 6 + (i % 5) * 3, 2);

    -- 车场分布：9 个车场轮换
    IF (i % 9) = 0 THEN
      SET v_lot = '演示停车场';
      SET v_quyu = 'B区';
      SET v_cxi = 29;
      SET v_price = 6;
    ELSE
      SET v_lot = CONCAT('停车场名称', (i % 9));
      SET v_quyu = ELT(1 + (i % 3), 'A区', 'B区', 'C区');
      SET v_cxi = 20 + (i % 9);
      SET v_price = 5 + (i % 6);
    END IF;

    -- 用户
    INSERT INTO `yonghu` (`id`, `addtime`, `yonghuzhanghao`, `xingming`, `mima`, `xingbie`, `shouji`, `chepaihao`)
    VALUES (v_uid, NOW(), v_user, v_name, '123456',
            IF(i % 2 = 0, '男', '女'),
            CONCAT('139', LPAD(10000000 + i, 8, '0')),
            v_plate);

    -- 按 20 种场景分支
    CASE st
      -- 0 空闲（仅车位）
      WHEN 0 THEN
        INSERT INTO `chewei` (`id`, `tingchechangmingcheng`, `quyu`, `cheweibianhao`, `zhuangtai`, `cheweixinxi_id`, `beizhu`, `banben`, `wangge_hang`, `wangge_lie`)
        VALUES (v_cw, v_lot, v_quyu, v_code, '空闲', v_cxi, CONCAT('BATCH100-', i, '-空闲'), 0, 1 + (i % 5), 1 + (i % 8));

      -- 1 未入场预约-已支付（N10 未入场，开始时间已过）
      WHEN 1 THEN
        INSERT INTO `chewei` (`id`, `tingchechangmingcheng`, `quyu`, `cheweibianhao`, `zhuangtai`, `cheweixinxi_id`, `beizhu`, `banben`, `wangge_hang`, `wangge_lie`)
        VALUES (v_cw, v_lot, v_quyu, v_code, '已预约未入场', v_cxi, CONCAT('BATCH100-', i), 0, 1 + (i % 5), 1 + (i % 8));
        SET v_kaishi = DATE_SUB(NOW(), INTERVAL 3 HOUR);
        SET v_jieshu = DATE_ADD(NOW(), INTERVAL 2 HOUR);
        INSERT INTO `chewei_yuyue` (`id`, `chewei_id`, `tingchechangmingcheng`, `quyu`, `kaishi_shijian`, `jieshu_shijian`, `zhuangtai`, `yuyue_zhifu_zhuangtai`, `liucheng_jiedian`, `addtime`)
        VALUES (v_yy, v_cw, v_lot, v_quyu, v_kaishi, v_jieshu, '有效', '已支付', '已预约待入场', DATE_SUB(NOW(), INTERVAL 1 DAY));

      -- 2 未入场预约-未来时段（N4 余位/预约）
      WHEN 2 THEN
        INSERT INTO `chewei` (`id`, `tingchechangmingcheng`, `quyu`, `cheweibianhao`, `zhuangtai`, `cheweixinxi_id`, `beizhu`, `banben`, `wangge_hang`, `wangge_lie`)
        VALUES (v_cw, v_lot, v_quyu, v_code, '已预约未入场', v_cxi, CONCAT('BATCH100-', i), 0, 1 + (i % 5), 1 + (i % 8));
        SET v_kaishi = DATE_ADD(NOW(), INTERVAL 1 DAY);
        SET v_jieshu = DATE_ADD(NOW(), INTERVAL 1 DAY) + INTERVAL 4 HOUR;
        INSERT INTO `chewei_yuyue` (`id`, `chewei_id`, `tingchechangmingcheng`, `quyu`, `kaishi_shijian`, `jieshu_shijian`, `zhuangtai`, `yuyue_zhifu_zhuangtai`, `liucheng_jiedian`, `addtime`)
        VALUES (v_yy, v_cw, v_lot, v_quyu, v_kaishi, v_jieshu, '有效', '无需预付', '已预约待入场', NOW());

      -- 3 预约已取消，车位恢复空闲
      WHEN 3 THEN
        INSERT INTO `chewei` (`id`, `tingchechangmingcheng`, `quyu`, `cheweibianhao`, `zhuangtai`, `cheweixinxi_id`, `beizhu`, `banben`, `wangge_hang`, `wangge_lie`)
        VALUES (v_cw, v_lot, v_quyu, v_code, '空闲', v_cxi, CONCAT('BATCH100-', i), 0, 1 + (i % 5), 1 + (i % 8));
        SET v_kaishi = DATE_SUB(NOW(), INTERVAL 2 DAY);
        SET v_jieshu = DATE_SUB(NOW(), INTERVAL 1 DAY);
        INSERT INTO `chewei_yuyue` (`id`, `chewei_id`, `tingchechangmingcheng`, `quyu`, `kaishi_shijian`, `jieshu_shijian`, `zhuangtai`, `yuyue_zhifu_zhuangtai`, `liucheng_jiedian`, `addtime`)
        VALUES (v_yy, v_cw, v_lot, v_quyu, v_kaishi, v_jieshu, '已取消', '无需预付', '已取消', DATE_SUB(NOW(), INTERVAL 2 DAY));

      -- 4 超时自动取消（N6/N8）
      WHEN 4 THEN
        INSERT INTO `chewei` (`id`, `tingchechangmingcheng`, `quyu`, `cheweibianhao`, `zhuangtai`, `cheweixinxi_id`, `beizhu`, `banben`, `wangge_hang`, `wangge_lie`)
        VALUES (v_cw, v_lot, v_quyu, v_code, '空闲', v_cxi, CONCAT('BATCH100-', i), 0, 1 + (i % 5), 1 + (i % 8));
        SET v_kaishi = DATE_SUB(NOW(), INTERVAL 3 DAY);
        SET v_jieshu = DATE_SUB(NOW(), INTERVAL 3 DAY) + INTERVAL 2 HOUR;
        INSERT INTO `chewei_yuyue` (`id`, `chewei_id`, `tingchechangmingcheng`, `quyu`, `kaishi_shijian`, `jieshu_shijian`, `zhuangtai`, `yuyue_zhifu_zhuangtai`, `liucheng_jiedian`, `addtime`)
        VALUES (v_yy, v_cw, v_lot, v_quyu, v_kaishi, v_jieshu, '已取消', '已支付', '超时自动取消', DATE_SUB(NOW(), INTERVAL 3 DAY));

      -- 5 超时未入场违约待付（N10 weiYue）
      WHEN 5 THEN
        INSERT INTO `chewei` (`id`, `tingchechangmingcheng`, `quyu`, `cheweibianhao`, `zhuangtai`, `cheweixinxi_id`, `beizhu`, `banben`, `wangge_hang`, `wangge_lie`)
        VALUES (v_cw, v_lot, v_quyu, v_code, '已预约未入场', v_cxi, CONCAT('BATCH100-', i), 0, 1 + (i % 5), 1 + (i % 8));
        SET v_kaishi = DATE_SUB(NOW(), INTERVAL 1 DAY);
        SET v_jieshu = DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 3 HOUR;
        INSERT INTO `tingchejiaofei` (`id`, `addtime`, `dingdanhao`, `tingchechangmingcheng`, `quyu`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `lichangshijian`, `bencitingcheshizhang`, `bencitingchefeiyong`, `chewei_id`, `ispay`)
        VALUES (v_fee, DATE_SUB(NOW(), INTERVAL 1 DAY), CONCAT('BATCH100-F-', LPAD(i, 3, '0')), v_lot, v_quyu, v_price, v_user, v_name,
                CONCAT('139', LPAD(10000000 + i, 8, '0')), v_plate, v_kaishi, v_kaishi, 0, 20.0, v_cw, '未支付');
        INSERT INTO `chewei_yuyue` (`id`, `chewei_id`, `tingchechangmingcheng`, `quyu`, `kaishi_shijian`, `jieshu_shijian`, `zhuangtai`, `yuyue_zhifu_zhuangtai`, `liucheng_jiedian`, `tingchejiaofei_id`, `addtime`)
        VALUES (v_yy, v_cw, v_lot, v_quyu, v_kaishi, v_jieshu, '有效', '未支付', '超时未入场待付违约', v_fee, DATE_SUB(NOW(), INTERVAL 1 DAY));
        UPDATE `chewei` SET `tingchejiaofei_id` = v_fee WHERE `id` = v_cw;

      -- 6 直接入场（无预约）已入场
      WHEN 6 THEN
        SET v_jin = DATE_SUB(NOW(), INTERVAL v_hours HOUR);
        INSERT INTO `chezijinchang` (`id`, `addtime`, `tingchechangmingcheng`, `quyu`, `cheweishuliang`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `chewei_id`)
        VALUES (v_rc, NOW(), v_lot, v_quyu, 1, v_price, v_user, v_name, CONCAT('139', LPAD(10000000 + i, 8, '0')), v_plate, v_jin, v_cw);
        INSERT INTO `chewei` (`id`, `tingchechangmingcheng`, `quyu`, `cheweibianhao`, `zhuangtai`, `cheweixinxi_id`, `chezijinchang_id`, `beizhu`, `banben`, `wangge_hang`, `wangge_lie`)
        VALUES (v_cw, v_lot, v_quyu, v_code, '已入场', v_cxi, v_rc, CONCAT('BATCH100-', i), 1, 1 + (i % 5), 1 + (i % 8));

      -- 7 预约后已入场（M1 已入场）
      WHEN 7 THEN
        SET v_kaishi = DATE_SUB(NOW(), INTERVAL 5 HOUR);
        SET v_jieshu = DATE_ADD(NOW(), INTERVAL 3 HOUR);
        SET v_jin = DATE_SUB(NOW(), INTERVAL 4 HOUR);
        INSERT INTO `chezijinchang` (`id`, `addtime`, `tingchechangmingcheng`, `quyu`, `cheweishuliang`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `chewei_id`)
        VALUES (v_rc, NOW(), v_lot, v_quyu, 1, v_price, v_user, v_name, CONCAT('139', LPAD(10000000 + i, 8, '0')), v_plate, v_jin, v_cw);
        INSERT INTO `chewei` (`id`, `tingchechangmingcheng`, `quyu`, `cheweibianhao`, `zhuangtai`, `cheweixinxi_id`, `chezijinchang_id`, `beizhu`, `banben`, `wangge_hang`, `wangge_lie`)
        VALUES (v_cw, v_lot, v_quyu, v_code, '已入场', v_cxi, v_rc, CONCAT('BATCH100-', i), 1, 1 + (i % 5), 1 + (i % 8));
        INSERT INTO `chewei_yuyue` (`id`, `chewei_id`, `tingchechangmingcheng`, `quyu`, `kaishi_shijian`, `jieshu_shijian`, `zhuangtai`, `yuyue_zhifu_zhuangtai`, `liucheng_jiedian`, `chezijinchang_id`, `addtime`)
        VALUES (v_yy, v_cw, v_lot, v_quyu, v_kaishi, v_jieshu, '有效', '已支付', '已入场', v_rc, DATE_SUB(NOW(), INTERVAL 1 DAY));

      -- 8 超时未续费（N10 chaoshiWeiXufei）
      WHEN 8 THEN
        SET v_kaishi = DATE_SUB(NOW(), INTERVAL 8 HOUR);
        SET v_jieshu = DATE_SUB(NOW(), INTERVAL 2 HOUR);
        SET v_jin = DATE_SUB(NOW(), INTERVAL 7 HOUR);
        INSERT INTO `chezijinchang` (`id`, `addtime`, `tingchechangmingcheng`, `quyu`, `cheweishuliang`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `chewei_id`)
        VALUES (v_rc, NOW(), v_lot, v_quyu, 1, v_price, v_user, v_name, CONCAT('139', LPAD(10000000 + i, 8, '0')), v_plate, v_jin, v_cw);
        INSERT INTO `chewei` (`id`, `tingchechangmingcheng`, `quyu`, `cheweibianhao`, `zhuangtai`, `cheweixinxi_id`, `chezijinchang_id`, `beizhu`, `banben`, `wangge_hang`, `wangge_lie`)
        VALUES (v_cw, v_lot, v_quyu, v_code, '已入场', v_cxi, v_rc, CONCAT('BATCH100-', i), 1, 1 + (i % 5), 1 + (i % 8));
        INSERT INTO `chewei_yuyue` (`id`, `chewei_id`, `tingchechangmingcheng`, `quyu`, `kaishi_shijian`, `jieshu_shijian`, `zhuangtai`, `yuyue_zhifu_zhuangtai`, `liucheng_jiedian`, `chezijinchang_id`, `addtime`)
        VALUES (v_yy, v_cw, v_lot, v_quyu, v_kaishi, v_jieshu, '有效', '已支付', '已入场', v_rc, DATE_SUB(NOW(), INTERVAL 1 DAY));

      -- 9 已离场待结算-未支付（N10 weiZhifu）
      WHEN 9 THEN
        SET v_jin = DATE_SUB(NOW(), INTERVAL (v_hours + 2) HOUR);
        SET v_li = DATE_SUB(NOW(), INTERVAL 1 HOUR);
        INSERT INTO `chezijinchang` (`id`, `addtime`, `tingchechangmingcheng`, `quyu`, `cheweishuliang`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `chewei_id`)
        VALUES (v_rc, DATE_SUB(NOW(), INTERVAL 1 DAY), v_lot, v_quyu, 1, v_price, v_user, v_name, CONCAT('139', LPAD(10000000 + i, 8, '0')), v_plate, v_jin, v_cw);
        INSERT INTO `tingchejiaofei` (`id`, `addtime`, `dingdanhao`, `tingchechangmingcheng`, `quyu`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `lichangshijian`, `bencitingcheshizhang`, `bencitingchefeiyong`, `crossuserid`, `crossrefid`, `chewei_id`, `ispay`)
        VALUES (v_fee, NOW(), CONCAT('BATCH100-F-', LPAD(i, 3, '0')), v_lot, v_quyu, v_price, v_user, v_name,
                CONCAT('139', LPAD(10000000 + i, 8, '0')), v_plate, v_jin, v_li, v_hours, v_fee_amt, v_uid, v_rc, v_cw, '未支付');
        INSERT INTO `chewei` (`id`, `tingchechangmingcheng`, `quyu`, `cheweibianhao`, `zhuangtai`, `cheweixinxi_id`, `chezijinchang_id`, `tingchejiaofei_id`, `beizhu`, `banben`, `wangge_hang`, `wangge_lie`)
        VALUES (v_cw, v_lot, v_quyu, v_code, '已离场待结算', v_cxi, v_rc, v_fee, CONCAT('BATCH100-', i), 0, 1 + (i % 5), 1 + (i % 8));

      -- 10 待结算 + 超时补缴待支付（N7/N10）
      WHEN 10 THEN
        SET v_jin = DATE_SUB(NOW(), INTERVAL 6 HOUR);
        SET v_li = DATE_SUB(NOW(), INTERVAL 30 MINUTE);
        INSERT INTO `chezijinchang` (`id`, `addtime`, `tingchechangmingcheng`, `quyu`, `cheweishuliang`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `chewei_id`)
        VALUES (v_rc, DATE_SUB(NOW(), INTERVAL 1 DAY), v_lot, v_quyu, 1, v_price, v_user, v_name, CONCAT('139', LPAD(10000000 + i, 8, '0')), v_plate, v_jin, v_cw);
        INSERT INTO `tingchejiaofei` (`id`, `addtime`, `dingdanhao`, `tingchechangmingcheng`, `quyu`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `lichangshijian`, `bencitingcheshizhang`, `bencitingchefeiyong`, `crossuserid`, `crossrefid`, `chewei_id`, `ispay`)
        VALUES (v_fee, NOW(), CONCAT('BATCH100-F-', LPAD(i, 3, '0')), v_lot, v_quyu, v_price, v_user, v_name,
                CONCAT('139', LPAD(10000000 + i, 8, '0')), v_plate, v_jin, v_li, v_hours, v_fee_amt, v_uid, v_rc, v_cw, '未支付');
        INSERT INTO `tingche_bujiao` (`danhao`, `leixing`, `chezijinchang_id`, `yonghuzhanghao`, `xingming`, `chepaihao`, `jine`, `yuanyin`, `zhuangtai`, `beizhu`, `addtime`)
        VALUES (CONCAT('BATCH100-B-', LPAD(i, 3, '0')), '超时补缴', v_rc, v_user, v_name, v_plate, 25.0, '超时续费补缴', '待支付', CONCAT('BATCH100-', i), NOW());
        INSERT INTO `chewei` (`id`, `tingchechangmingcheng`, `quyu`, `cheweibianhao`, `zhuangtai`, `cheweixinxi_id`, `chezijinchang_id`, `tingchejiaofei_id`, `beizhu`, `banben`, `wangge_hang`, `wangge_lie`)
        VALUES (v_cw, v_lot, v_quyu, v_code, '已离场待结算', v_cxi, v_rc, v_fee, CONCAT('BATCH100-', i), 0, 1 + (i % 5), 1 + (i % 8));

      -- 11 待结算 + 续费待支付
      WHEN 11 THEN
        SET v_jin = DATE_SUB(NOW(), INTERVAL 5 HOUR);
        SET v_li = DATE_SUB(NOW(), INTERVAL 20 MINUTE);
        INSERT INTO `chezijinchang` (`id`, `addtime`, `tingchechangmingcheng`, `quyu`, `cheweishuliang`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `chewei_id`)
        VALUES (v_rc, NOW(), v_lot, v_quyu, 1, v_price, v_user, v_name, CONCAT('139', LPAD(10000000 + i, 8, '0')), v_plate, v_jin, v_cw);
        INSERT INTO `tingchejiaofei` (`id`, `addtime`, `dingdanhao`, `tingchechangmingcheng`, `quyu`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `lichangshijian`, `bencitingcheshizhang`, `bencitingchefeiyong`, `crossuserid`, `crossrefid`, `chewei_id`, `ispay`)
        VALUES (v_fee, NOW(), CONCAT('BATCH100-F-', LPAD(i, 3, '0')), v_lot, v_quyu, v_price, v_user, v_name,
                CONCAT('139', LPAD(10000000 + i, 8, '0')), v_plate, v_jin, v_li, v_hours, v_fee_amt, v_uid, v_rc, v_cw, '未支付');
        INSERT INTO `tingche_bujiao` (`danhao`, `leixing`, `chezijinchang_id`, `yonghuzhanghao`, `xingming`, `chepaihao`, `jine`, `yuanyin`, `zhuangtai`, `beizhu`, `addtime`)
        VALUES (CONCAT('BATCH100-B-', LPAD(i, 3, '0')), '续费', v_rc, v_user, v_name, v_plate, 12.0, '延长停车', '待支付', CONCAT('BATCH100-', i), NOW());
        INSERT INTO `chewei` (`id`, `tingchechangmingcheng`, `quyu`, `cheweibianhao`, `zhuangtai`, `cheweixinxi_id`, `chezijinchang_id`, `tingchejiaofei_id`, `beizhu`, `banben`, `wangge_hang`, `wangge_lie`)
        VALUES (v_cw, v_lot, v_quyu, v_code, '已离场待结算', v_cxi, v_rc, v_fee, CONCAT('BATCH100-', i), 0, 1 + (i % 5), 1 + (i % 8));

      -- 12 待结算 + 管理员调整待支付
      WHEN 12 THEN
        SET v_jin = DATE_SUB(NOW(), INTERVAL 4 HOUR);
        SET v_li = DATE_SUB(NOW(), INTERVAL 10 MINUTE);
        INSERT INTO `chezijinchang` (`id`, `addtime`, `tingchechangmingcheng`, `quyu`, `cheweishuliang`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `chewei_id`)
        VALUES (v_rc, NOW(), v_lot, v_quyu, 1, v_price, v_user, v_name, CONCAT('139', LPAD(10000000 + i, 8, '0')), v_plate, v_jin, v_cw);
        INSERT INTO `tingchejiaofei` (`id`, `addtime`, `dingdanhao`, `tingchechangmingcheng`, `quyu`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `lichangshijian`, `bencitingcheshizhang`, `bencitingchefeiyong`, `crossuserid`, `crossrefid`, `chewei_id`, `ispay`)
        VALUES (v_fee, NOW(), CONCAT('BATCH100-F-', LPAD(i, 3, '0')), v_lot, v_quyu, v_price, v_user, v_name,
                CONCAT('139', LPAD(10000000 + i, 8, '0')), v_plate, v_jin, v_li, v_hours, v_fee_amt, v_uid, v_rc, v_cw, '未支付');
        INSERT INTO `tingche_bujiao` (`danhao`, `leixing`, `chezijinchang_id`, `yonghuzhanghao`, `xingming`, `chepaihao`, `jine`, `yuanyin`, `zhuangtai`, `guanliyuan_zhanghao`, `beizhu`, `addtime`)
        VALUES (CONCAT('BATCH100-B-', LPAD(i, 3, '0')), '管理员调整', v_rc, v_user, v_name, v_plate, 8.0, '人工调价', '待支付', 'admin', CONCAT('BATCH100-', i), NOW());
        INSERT INTO `chewei` (`id`, `tingchechangmingcheng`, `quyu`, `cheweibianhao`, `zhuangtai`, `cheweixinxi_id`, `chezijinchang_id`, `tingchejiaofei_id`, `beizhu`, `banben`, `wangge_hang`, `wangge_lie`)
        VALUES (v_cw, v_lot, v_quyu, v_code, '已离场待结算', v_cxi, v_rc, v_fee, CONCAT('BATCH100-', i), 0, 1 + (i % 5), 1 + (i % 8));

      -- 13 已结算-完整链路已支付（N8/N9 收入）
      WHEN 13 THEN
        SET v_jin = DATE_SUB(DATE_SUB(NOW(), INTERVAL v_day_off DAY), INTERVAL (v_hours + 1) HOUR);
        SET v_li = DATE_SUB(NOW(), INTERVAL v_day_off DAY);
        INSERT INTO `chezijinchang` (`id`, `addtime`, `tingchechangmingcheng`, `quyu`, `cheweishuliang`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `chewei_id`)
        VALUES (v_rc, DATE_SUB(NOW(), INTERVAL v_day_off DAY), v_lot, v_quyu, 1, v_price, v_user, v_name, CONCAT('139', LPAD(10000000 + i, 8, '0')), v_plate, v_jin, v_cw);
        INSERT INTO `tingchejiaofei` (`id`, `addtime`, `dingdanhao`, `tingchechangmingcheng`, `quyu`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `lichangshijian`, `bencitingcheshizhang`, `bencitingchefeiyong`, `crossuserid`, `crossrefid`, `chewei_id`, `ispay`)
        VALUES (v_fee, DATE_SUB(NOW(), INTERVAL v_day_off DAY), CONCAT('BATCH100-F-', LPAD(i, 3, '0')), v_lot, v_quyu, v_price, v_user, v_name,
                CONCAT('139', LPAD(10000000 + i, 8, '0')), v_plate, v_jin, v_li, v_hours, v_fee_amt, v_uid, v_rc, v_cw, '已支付');
        SET v_kaishi = DATE_SUB(v_jin, INTERVAL 1 HOUR);
        SET v_jieshu = DATE_ADD(v_li, INTERVAL 1 HOUR);
        INSERT INTO `chewei_yuyue` (`id`, `chewei_id`, `tingchechangmingcheng`, `quyu`, `kaishi_shijian`, `jieshu_shijian`, `zhuangtai`, `yuyue_zhifu_zhuangtai`, `liucheng_jiedian`, `chezijinchang_id`, `tingchejiaofei_id`, `addtime`)
        VALUES (v_yy, v_cw, v_lot, v_quyu, v_kaishi, v_jieshu, '有效', '已支付', '已完成', v_rc, v_fee, DATE_SUB(NOW(), INTERVAL v_day_off DAY));
        INSERT INTO `chewei` (`id`, `tingchechangmingcheng`, `quyu`, `cheweibianhao`, `zhuangtai`, `cheweixinxi_id`, `chezijinchang_id`, `tingchejiaofei_id`, `beizhu`, `banben`, `wangge_hang`, `wangge_lie`)
        VALUES (v_cw, v_lot, v_quyu, v_code, '已结算', v_cxi, v_rc, v_fee, CONCAT('BATCH100-', i), 0, 1 + (i % 5), 1 + (i % 8));

      -- 14 已结算 + 补缴单已支付（独立）
      WHEN 14 THEN
        SET v_jin = DATE_SUB(DATE_SUB(NOW(), INTERVAL v_day_off DAY), INTERVAL 5 HOUR);
        SET v_li = DATE_SUB(DATE_SUB(NOW(), INTERVAL v_day_off DAY), INTERVAL 1 HOUR);
        INSERT INTO `chezijinchang` (`id`, `addtime`, `tingchechangmingcheng`, `quyu`, `cheweishuliang`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `chewei_id`)
        VALUES (v_rc, DATE_SUB(NOW(), INTERVAL v_day_off DAY), v_lot, v_quyu, 1, v_price, v_user, v_name, CONCAT('139', LPAD(10000000 + i, 8, '0')), v_plate, v_jin, v_cw);
        INSERT INTO `tingchejiaofei` (`id`, `addtime`, `dingdanhao`, `tingchechangmingcheng`, `quyu`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `lichangshijian`, `bencitingcheshizhang`, `bencitingchefeiyong`, `crossuserid`, `crossrefid`, `chewei_id`, `ispay`)
        VALUES (v_fee, DATE_SUB(NOW(), INTERVAL v_day_off DAY), CONCAT('BATCH100-F-', LPAD(i, 3, '0')), v_lot, v_quyu, v_price, v_user, v_name,
                CONCAT('139', LPAD(10000000 + i, 8, '0')), v_plate, v_jin, v_li, 4.0, 36.0, v_uid, v_rc, v_cw, '已支付');
        INSERT INTO `tingche_bujiao` (`danhao`, `leixing`, `chezijinchang_id`, `tingchejiaofei_id`, `yonghuzhanghao`, `xingming`, `chepaihao`, `jine`, `yuanyin`, `zhuangtai`, `zhifu_shijian`, `addtime`)
        VALUES (CONCAT('BATCH100-B-', LPAD(i, 3, '0')), '超时补缴', v_rc, NULL, v_user, v_name, v_plate, 18.0, '超时补缴已付', '已支付', DATE_SUB(NOW(), INTERVAL v_day_off DAY), DATE_SUB(NOW(), INTERVAL v_day_off DAY));
        INSERT INTO `chewei` (`id`, `tingchechangmingcheng`, `quyu`, `cheweibianhao`, `zhuangtai`, `cheweixinxi_id`, `chezijinchang_id`, `tingchejiaofei_id`, `beizhu`, `banben`, `wangge_hang`, `wangge_lie`)
        VALUES (v_cw, v_lot, v_quyu, v_code, '已结算', v_cxi, v_rc, v_fee, CONCAT('BATCH100-', i), 0, 1 + (i % 5), 1 + (i % 8));

      -- 15 已结算 + 补缴已并入离场单
      WHEN 15 THEN
        SET v_jin = DATE_SUB(DATE_SUB(NOW(), INTERVAL v_day_off DAY), INTERVAL 6 HOUR);
        SET v_li = DATE_SUB(DATE_SUB(NOW(), INTERVAL v_day_off DAY), INTERVAL 2 HOUR);
        INSERT INTO `chezijinchang` (`id`, `addtime`, `tingchechangmingcheng`, `quyu`, `cheweishuliang`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `chewei_id`)
        VALUES (v_rc, DATE_SUB(NOW(), INTERVAL v_day_off DAY), v_lot, v_quyu, 1, v_price, v_user, v_name, CONCAT('139', LPAD(10000000 + i, 8, '0')), v_plate, v_jin, v_cw);
        INSERT INTO `tingchejiaofei` (`id`, `addtime`, `dingdanhao`, `tingchechangmingcheng`, `quyu`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `lichangshijian`, `bencitingcheshizhang`, `bencitingchefeiyong`, `crossuserid`, `crossrefid`, `chewei_id`, `ispay`)
        VALUES (v_fee, DATE_SUB(NOW(), INTERVAL v_day_off DAY), CONCAT('BATCH100-F-', LPAD(i, 3, '0')), v_lot, v_quyu, v_price, v_user, v_name,
                CONCAT('139', LPAD(10000000 + i, 8, '0')), v_plate, v_jin, v_li, 4.0, 48.0, v_uid, v_rc, v_cw, '已支付');
        INSERT INTO `tingche_bujiao` (`danhao`, `leixing`, `chezijinchang_id`, `tingchejiaofei_id`, `yonghuzhanghao`, `xingming`, `chepaihao`, `jine`, `yuanyin`, `zhuangtai`, `zhifu_shijian`, `addtime`)
        VALUES (CONCAT('BATCH100-B-', LPAD(i, 3, '0')), '管理员调整', v_rc, v_fee, v_user, v_name, v_plate, 10.0, '并入离场单', '已并入离场单', DATE_SUB(NOW(), INTERVAL v_day_off DAY), DATE_SUB(NOW(), INTERVAL v_day_off DAY));
        INSERT INTO `chewei` (`id`, `tingchechangmingcheng`, `quyu`, `cheweibianhao`, `zhuangtai`, `cheweixinxi_id`, `chezijinchang_id`, `tingchejiaofei_id`, `beizhu`, `banben`, `wangge_hang`, `wangge_lie`)
        VALUES (v_cw, v_lot, v_quyu, v_code, '已结算', v_cxi, v_rc, v_fee, CONCAT('BATCH100-', i), 0, 1 + (i % 5), 1 + (i % 8));

      -- 16 无预约直接离场已付（walk-in 完成）
      WHEN 16 THEN
        SET v_jin = DATE_SUB(DATE_SUB(NOW(), INTERVAL v_day_off DAY), INTERVAL 3 HOUR);
        SET v_li = DATE_SUB(NOW(), INTERVAL v_day_off DAY);
        INSERT INTO `chezijinchang` (`id`, `addtime`, `tingchechangmingcheng`, `quyu`, `cheweishuliang`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `chewei_id`)
        VALUES (v_rc, DATE_SUB(NOW(), INTERVAL v_day_off DAY), v_lot, v_quyu, 1, v_price, v_user, v_name, CONCAT('139', LPAD(10000000 + i, 8, '0')), v_plate, v_jin, v_cw);
        INSERT INTO `tingchejiaofei` (`id`, `addtime`, `dingdanhao`, `tingchechangmingcheng`, `quyu`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `lichangshijian`, `bencitingcheshizhang`, `bencitingchefeiyong`, `crossuserid`, `crossrefid`, `chewei_id`, `ispay`)
        VALUES (v_fee, DATE_SUB(NOW(), INTERVAL v_day_off DAY), CONCAT('BATCH100-F-', LPAD(i, 3, '0')), v_lot, v_quyu, v_price, v_user, v_name,
                CONCAT('139', LPAD(10000000 + i, 8, '0')), v_plate, v_jin, v_li, 3.0, 24.0, v_uid, v_rc, v_cw, '已支付');
        INSERT INTO `chewei` (`id`, `tingchechangmingcheng`, `quyu`, `cheweibianhao`, `zhuangtai`, `cheweixinxi_id`, `chezijinchang_id`, `tingchejiaofei_id`, `beizhu`, `banben`, `wangge_hang`, `wangge_lie`)
        VALUES (v_cw, v_lot, v_quyu, v_code, '已结算', v_cxi, v_rc, v_fee, CONCAT('BATCH100-', i), 0, 1 + (i % 5), 1 + (i % 8));

      -- 17 已离场待支付停车费（M1 节点）
      WHEN 17 THEN
        SET v_kaishi = DATE_SUB(NOW(), INTERVAL 10 HOUR);
        SET v_jieshu = DATE_ADD(NOW(), INTERVAL 2 HOUR);
        SET v_jin = DATE_SUB(NOW(), INTERVAL 9 HOUR);
        SET v_li = DATE_SUB(NOW(), INTERVAL 2 HOUR);
        INSERT INTO `chezijinchang` (`id`, `addtime`, `tingchechangmingcheng`, `quyu`, `cheweishuliang`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `chewei_id`)
        VALUES (v_rc, DATE_SUB(NOW(), INTERVAL 1 DAY), v_lot, v_quyu, 1, v_price, v_user, v_name, CONCAT('139', LPAD(10000000 + i, 8, '0')), v_plate, v_jin, v_cw);
        INSERT INTO `tingchejiaofei` (`id`, `addtime`, `dingdanhao`, `tingchechangmingcheng`, `quyu`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `lichangshijian`, `bencitingcheshizhang`, `bencitingchefeiyong`, `crossuserid`, `crossrefid`, `chewei_id`, `ispay`)
        VALUES (v_fee, NOW(), CONCAT('BATCH100-F-', LPAD(i, 3, '0')), v_lot, v_quyu, v_price, v_user, v_name,
                CONCAT('139', LPAD(10000000 + i, 8, '0')), v_plate, v_jin, v_li, 7.0, 52.0, v_uid, v_rc, v_cw, '未支付');
        INSERT INTO `chewei_yuyue` (`id`, `chewei_id`, `tingchechangmingcheng`, `quyu`, `kaishi_shijian`, `jieshu_shijian`, `zhuangtai`, `yuyue_zhifu_zhuangtai`, `liucheng_jiedian`, `chezijinchang_id`, `tingchejiaofei_id`, `addtime`)
        VALUES (v_yy, v_cw, v_lot, v_quyu, v_kaishi, v_jieshu, '有效', '已支付', '已离场待支付停车费', v_rc, v_fee, DATE_SUB(NOW(), INTERVAL 1 DAY));
        INSERT INTO `chewei` (`id`, `tingchechangmingcheng`, `quyu`, `cheweibianhao`, `zhuangtai`, `cheweixinxi_id`, `chezijinchang_id`, `tingchejiaofei_id`, `beizhu`, `banben`, `wangge_hang`, `wangge_lie`)
        VALUES (v_cw, v_lot, v_quyu, v_code, '已离场待结算', v_cxi, v_rc, v_fee, CONCAT('BATCH100-', i), 0, 1 + (i % 5), 1 + (i % 8));

      -- 18 未入场-预约未支付预付
      WHEN 18 THEN
        INSERT INTO `chewei` (`id`, `tingchechangmingcheng`, `quyu`, `cheweibianhao`, `zhuangtai`, `cheweixinxi_id`, `beizhu`, `banben`, `wangge_hang`, `wangge_lie`)
        VALUES (v_cw, v_lot, v_quyu, v_code, '已预约未入场', v_cxi, CONCAT('BATCH100-', i), 0, 1 + (i % 5), 1 + (i % 8));
        SET v_kaishi = DATE_ADD(NOW(), INTERVAL 3 HOUR);
        SET v_jieshu = DATE_ADD(NOW(), INTERVAL 6 HOUR);
        INSERT INTO `chewei_yuyue` (`id`, `chewei_id`, `tingchechangmingcheng`, `quyu`, `kaishi_shijian`, `jieshu_shijian`, `zhuangtai`, `yuyue_zhifu_zhuangtai`, `liucheng_jiedian`, `addtime`)
        VALUES (v_yy, v_cw, v_lot, v_quyu, v_kaishi, v_jieshu, '有效', '未支付', '已预约待入场', NOW());

      -- 19 补缴已作废
      WHEN 19 THEN
        SET v_jin = DATE_SUB(NOW(), INTERVAL 2 DAY);
        INSERT INTO `chezijinchang` (`id`, `addtime`, `tingchechangmingcheng`, `quyu`, `cheweishuliang`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `chewei_id`)
        VALUES (v_rc, DATE_SUB(NOW(), INTERVAL 2 DAY), v_lot, v_quyu, 1, v_price, v_user, v_name, CONCAT('139', LPAD(10000000 + i, 8, '0')), v_plate, v_jin, v_cw);
        INSERT INTO `tingche_bujiao` (`danhao`, `leixing`, `chezijinchang_id`, `yonghuzhanghao`, `xingming`, `chepaihao`, `jine`, `yuanyin`, `zhuangtai`, `guanliyuan_zhanghao`, `beizhu`, `addtime`)
        VALUES (CONCAT('BATCH100-B-', LPAD(i, 3, '0')), '管理员调整', v_rc, v_user, v_name, v_plate, 5.0, '误开单作废', '已作废', 'admin', CONCAT('BATCH100-', i), DATE_SUB(NOW(), INTERVAL 2 DAY));
        INSERT INTO `chewei` (`id`, `tingchechangmingcheng`, `quyu`, `cheweibianhao`, `zhuangtai`, `cheweixinxi_id`, `chezijinchang_id`, `beizhu`, `banben`, `wangge_hang`, `wangge_lie`)
        VALUES (v_cw, v_lot, v_quyu, v_code, '空闲', v_cxi, v_rc, CONCAT('BATCH100-', i), 0, 1 + (i % 5), 1 + (i % 8));

      ELSE
        INSERT INTO `chewei` (`id`, `tingchechangmingcheng`, `quyu`, `cheweibianhao`, `zhuangtai`, `cheweixinxi_id`, `beizhu`, `banben`, `wangge_hang`, `wangge_lie`)
        VALUES (v_cw, v_lot, v_quyu, v_code, '空闲', v_cxi, CONCAT('BATCH100-', i), 0, 1 + (i % 5), 1 + (i % 8));
    END CASE;

    SET i = i + 1;
  END WHILE;
END$$
DELIMITER ;

CALL `proc_seed_batch100`();
DROP PROCEDURE IF EXISTS `proc_seed_batch100`;

-- 汇总视图校验用：今日额外入场（增强 N8 今日 KPI）
INSERT INTO `chezijinchang` (`id`, `addtime`, `tingchechangmingcheng`, `quyu`, `cheweishuliang`, `xiaoshidanjia`, `yonghuzhanghao`, `xingming`, `shouji`, `chepaihao`, `jinchangshijian`, `chewei_id`)
SELECT 1201, NOW(), '演示停车场', 'B区', 1, 6, 'batch100_050', '测试车主50', '13910000050', '粤T10050', DATE_SUB(NOW(), INTERVAL 1 HOUR), 1150
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `chezijinchang` WHERE `id` = 1201);

SELECT 'batch100_users' AS metric, COUNT(*) AS cnt FROM `yonghu` WHERE `yonghuzhanghao` LIKE 'batch100_%'
UNION ALL SELECT 'batch100_chewei', COUNT(*) FROM `chewei` WHERE `cheweibianhao` LIKE 'B100-%'
UNION ALL SELECT 'batch100_yuyue', COUNT(*) FROM `chewei_yuyue` WHERE `id` BETWEEN 1101 AND 1200
UNION ALL SELECT 'batch100_ruchang', COUNT(*) FROM `chezijinchang` WHERE `id` BETWEEN 1101 AND 1201
UNION ALL SELECT 'batch100_jiaofei', COUNT(*) FROM `tingchejiaofei` WHERE `dingdanhao` LIKE 'BATCH100-F-%'
UNION ALL SELECT 'batch100_bujiao', COUNT(*) FROM `tingche_bujiao` WHERE `danhao` LIKE 'BATCH100-B-%';

SELECT `zhuangtai`, COUNT(*) AS cnt FROM `chewei` WHERE `cheweibianhao` LIKE 'B100-%' GROUP BY `zhuangtai` ORDER BY cnt DESC;
SELECT `liucheng_jiedian`, COUNT(*) AS cnt FROM `chewei_yuyue` WHERE `id` BETWEEN 1101 AND 1200 GROUP BY `liucheng_jiedian`;
SELECT `zhuangtai`, COUNT(*) AS cnt FROM `tingche_bujiao` WHERE `danhao` LIKE 'BATCH100-B-%' GROUP BY `zhuangtai`;
