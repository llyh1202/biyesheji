<template>
  <!-- 这是我cursor给父亲写的 — N6 超时策略（前台查看与手动扫描） -->
  <div :style='{"width":"80%","padding":"20px","margin":"10px auto","background":"#fff"}'>
    <h2 :style='{"margin":"0 0 12px","fontSize":"20px"}'>超时策略（N6）</h2>
    <p :style='{"color":"#666","lineHeight":"1.6","marginBottom":"16px"}'>
      预约开始后超过「保留时长」仍未入场，系统将自动取消预约并释放车位；若规则配置了违约金，会生成违约缴费单。
      离场计费时会扣减「计费宽限期」分钟数。
    </p>
    <el-button type="warning" :loading="scanning" @click="runScan">立即执行超时扫描</el-button>
    <el-alert v-if="scanResult" :title="scanSummary" type="info" :closable="false" show-icon :style='{"margin":"16px 0"}' />
    <el-table v-loading="loading" :data="rules" border :style='{"marginTop":"16px"}'>
      <el-table-column prop="guizeMingcheng" label="规则名称" />
      <el-table-column label="停车场">
        <template slot-scope="s">{{ s.row.tingchechangmingcheng || '全局' }}</template>
      </el-table-column>
      <el-table-column prop="yuyueBaoliuFenzhong" label="保留(分)" width="90" />
      <el-table-column prop="jifeiKuanxianFenzhong" label="宽限(分)" width="90" />
      <el-table-column prop="weiruchangKoufeiYuan" label="违约(元)" width="90" />
      <el-table-column prop="qiyong" label="启用" width="70" />
    </el-table>
    <p :style='{"marginTop":"12px","color":"#909399","fontSize":"13px"}'>规则维护请登录管理端：车位信息管理 → 超时策略</p>
  </div>
</template>

<script>
export default {
  data() {
    return {
      loading: false,
      scanning: false,
      rules: [],
      scanResult: null
    }
  },
  computed: {
    scanSummary() {
      if (!this.scanResult) return ''
      const d = this.scanResult
      return `扫描 ${d.scanned} 条，取消 ${d.cancelled} 条，违约单 ${d.penaltyOrders} 条`
    }
  },
  created() {
    this.loadRules()
  },
  methods: {
    loadRules() {
      this.loading = true
      this.$http.get('chewei/n6/guize/list').then(res => {
        if (res.data && res.data.code === 0) {
          this.rules = res.data.data || []
        } else {
          this.$message.error((res.data && res.data.msg) || '加载失败')
        }
      }).catch(() => {
        this.$message.error('无法加载 N6 规则，请确认后端已启动且已执行 migration_n6')
      }).finally(() => {
        this.loading = false
      })
    },
    runScan() {
      this.scanning = true
      this.$http.get('chewei/n6/timeout/run').then(res => {
        if (res.data && res.data.code === 0) {
          this.scanResult = res.data.data
          this.$message.success('扫描完成')
        } else {
          this.$message.error((res.data && res.data.msg) || '扫描失败')
        }
      }).catch(() => {
        this.$message.error('扫描请求失败')
      }).finally(() => {
        this.scanning = false
      })
    }
  }
}
</script>
