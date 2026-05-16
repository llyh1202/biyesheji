-- 这是M7代码 — 统计只读视图（报表/看板查询用）
-- 这是我cursor给父亲写的
USE springboot673i609q;

CREATE OR REPLACE VIEW v_stat_chewei_dim AS
SELECT
  CASE WHEN TRIM(IFNULL(tingchechangmingcheng, '')) = '' THEN '未命名车场' ELSE TRIM(tingchechangmingcheng) END AS tingchechangmingcheng,
  IFNULL(quyu, '') AS quyu,
  COUNT(*) AS chewei_count,
  SUM(CASE WHEN zhuangtai = '空闲' THEN 1 ELSE 0 END) AS free_count
FROM chewei
GROUP BY 1, 2;

CREATE OR REPLACE VIEW v_stat_chewei_global AS
SELECT
  COUNT(*) AS chewei_total,
  SUM(CASE WHEN zhuangtai = '空闲' THEN 1 ELSE 0 END) AS chewei_free
FROM chewei;

CREATE OR REPLACE VIEW v_stat_revenue_daily AS
SELECT
  DATE(t.lichangshijian) AS stat_date,
  CASE WHEN TRIM(IFNULL(t.tingchechangmingcheng, '')) = '' THEN '未命名车场' ELSE TRIM(t.tingchechangmingcheng) END AS tingchechangmingcheng,
  IFNULL(t.quyu, '') AS quyu,
  COUNT(*) AS lichang_count,
  SUM(CASE WHEN t.ispay = '已支付' THEN IFNULL(t.bencitingchefeiyong, 0) ELSE 0 END) AS parking_revenue,
  SUM(
    CASE
      WHEN IFNULL(t.bencitingcheshizhang, 0) > 0 THEN t.bencitingcheshizhang
      WHEN t.jinchangshijian IS NOT NULL AND t.lichangshijian IS NOT NULL
        THEN TIMESTAMPDIFF(SECOND, t.jinchangshijian, t.lichangshijian) / 3600.0
      ELSE 0
    END
  ) AS duration_sum_hours,
  SUM(
    CASE
      WHEN IFNULL(t.bencitingcheshizhang, 0) > 0 OR (t.jinchangshijian IS NOT NULL AND t.lichangshijian IS NOT NULL) THEN 1
      ELSE 0
    END
  ) AS duration_cnt
FROM tingchejiaofei t
WHERE t.lichangshijian IS NOT NULL
GROUP BY 1, 2, 3;
