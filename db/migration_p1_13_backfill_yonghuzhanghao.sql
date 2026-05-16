-- 这是我cursor给父亲写的 — P1-13：历史数据回填 yonghuzhanghao（P1-06 之前预约/入场/缴费单）
-- 可重复执行（仅更新仍为 NULL 或空串的行）
SET NAMES utf8mb4;

-- 1) 预约单 ← 已关联入场单的用户账号
UPDATE chewei_yuyue y
INNER JOIN chezijinchang c ON c.id = y.chezijinchang_id
SET y.yonghuzhanghao = c.yonghuzhanghao
WHERE (y.yonghuzhanghao IS NULL OR TRIM(y.yonghuzhanghao) = '')
  AND c.yonghuzhanghao IS NOT NULL AND TRIM(c.yonghuzhanghao) <> '';

-- 2) 入场单 ← 关联缴费单（crossrefid = 入场单 id）
UPDATE chezijinchang c
INNER JOIN tingchejiaofei t ON t.crossrefid = c.id
SET c.yonghuzhanghao = t.yonghuzhanghao
WHERE (c.yonghuzhanghao IS NULL OR TRIM(c.yonghuzhanghao) = '')
  AND t.yonghuzhanghao IS NOT NULL AND TRIM(t.yonghuzhanghao) <> '';

-- 3) 预约单 ← 回填后的入场单（步骤 2 之后新关联）
UPDATE chewei_yuyue y
INNER JOIN chezijinchang c ON c.id = y.chezijinchang_id
SET y.yonghuzhanghao = c.yonghuzhanghao
WHERE (y.yonghuzhanghao IS NULL OR TRIM(y.yonghuzhanghao) = '')
  AND c.yonghuzhanghao IS NOT NULL AND TRIM(c.yonghuzhanghao) <> '';

-- 4) 缴费单 ← 入场单
UPDATE tingchejiaofei t
INNER JOIN chezijinchang c ON c.id = t.crossrefid
SET t.yonghuzhanghao = c.yonghuzhanghao
WHERE (t.yonghuzhanghao IS NULL OR TRIM(t.yonghuzhanghao) = '')
  AND c.yonghuzhanghao IS NOT NULL AND TRIM(c.yonghuzhanghao) <> '';
