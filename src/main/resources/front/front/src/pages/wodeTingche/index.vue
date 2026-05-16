<template>
  <!-- 这是我cursor给父亲写的 — P1-09/P1-25 我的停车（Tab：待入场 / 停车中 / 待支付 / 待补缴） -->
  <div class="tech-feature-wrap tech-page-panel wode-tingche-page">
    <h2 class="wode-tingche-title">我的停车</h2>
    <p class="wode-tingche-desc">查看预约、在场、待支付与待补缴记录；待补缴请至 N7 补缴页完成支付后再离场结算。</p>

    <el-alert
      v-if="!hasToken"
      title="请先登录后查看我的停车"
      type="warning"
      :closable="false"
      show-icon
      class="wode-tingche-alert"
    />

    <el-tabs v-model="activeTab" @tab-click="onTabClick">
      <el-tab-pane label="待入场" name="daiRuchang">
        <el-button type="primary" size="small" native-type="button" :loading="loadingYuyue" @click="loadDaiRuchang">刷新</el-button>
        <div v-loading="loadingYuyue" class="wode-tingche-list">
          <el-empty v-if="!loadingYuyue && daiRuchangList.length === 0" description="暂无待入场预约" />
          <el-card v-for="row in daiRuchangList" :key="'y-' + row.id" shadow="hover" class="wode-tingche-card">
            <div class="wode-tingche-card-head">
              <span class="wode-tingche-tag">预约 #{{ row.id }}</span>
              <span class="wode-tingche-status">{{ row.liuchengJiedian || row.zhuangtai }}</span>
            </div>
            <ul class="wode-tingche-meta">
              <li><span>停车场</span><b>{{ row.tingchechangmingcheng || '—' }}</b></li>
              <li v-if="row.quyu"><span>区域</span><b>{{ row.quyu }}</b></li>
              <li v-if="row.cheweibianhao"><span>车位</span><b>{{ row.cheweibianhao }}</b></li>
              <li><span>时段</span><b>{{ row.kaishiShijian }} ~ {{ row.jieshuShijian }}</b></li>
            </ul>
            <div class="wode-tingche-actions">
              <el-button type="primary" size="small" @click="goRuchang(row)">去入场</el-button>
            </div>
          </el-card>
        </div>
        <el-pagination
          v-if="yuyueTotal > yuyueLimit"
          class="wode-tingche-pager"
          background
          layout="prev, pager, next"
          :current-page="yuyuePage"
          :page-size="yuyueLimit"
          :total="yuyueTotal"
          @current-change="onYuyuePageChange"
        />
      </el-tab-pane>

      <el-tab-pane label="停车中" name="zaiChang">
        <el-button type="primary" size="small" native-type="button" :loading="loadingSummary" @click="loadSummary">刷新</el-button>
        <div v-loading="loadingSummary" class="wode-tingche-list">
          <el-empty v-if="!loadingSummary && zaiChangList.length === 0" description="暂无在场车辆" />
          <el-card v-for="row in zaiChangList" :key="'r-' + row.id" shadow="hover" class="wode-tingche-card">
            <div class="wode-tingche-card-head">
              <span class="wode-tingche-tag">入场 #{{ row.id }}</span>
              <span class="wode-tingche-status">停车中</span>
            </div>
            <ul class="wode-tingche-meta">
              <li><span>停车场</span><b>{{ row.tingchechangmingcheng || '—' }}</b></li>
              <li v-if="row.quyu"><span>区域</span><b>{{ row.quyu }}</b></li>
              <li v-if="row.cheweibianhao"><span>车位</span><b>{{ row.cheweibianhao }}</b></li>
              <li v-if="row.chepaihao"><span>车牌</span><b>{{ row.chepaihao }}</b></li>
              <li><span>进场时间</span><b>{{ row.jinchangshijian || '—' }}</b></li>
            </ul>
            <div class="wode-tingche-actions">
              <el-button type="warning" size="small" @click="goLichang(row)">离场</el-button>
            </div>
          </el-card>
        </div>
      </el-tab-pane>

      <el-tab-pane label="待补缴" name="daiBujiao">
        <el-button type="primary" size="small" native-type="button" :loading="loadingBujiao" @click="loadDaiBujiao">刷新</el-button>
        <div v-loading="loadingBujiao" class="wode-tingche-list">
          <el-empty v-if="!loadingBujiao && daiBujiaoList.length === 0" description="暂无待支付补缴单" />
          <el-card v-for="row in daiBujiaoList" :key="'b-' + row.id" shadow="hover" class="wode-tingche-card">
            <div class="wode-tingche-card-head">
              <span class="wode-tingche-tag">{{ row.danhao || ('补缴 #' + row.id) }}</span>
              <span class="wode-tingche-status wode-tingche-status-warn">{{ row.zhuangtai || '待支付' }}</span>
            </div>
            <ul class="wode-tingche-meta">
              <li v-if="row.leixing"><span>类型</span><b>{{ row.leixing }}</b></li>
              <li><span>金额</span><b class="wode-tingche-fee">{{ row.jine != null ? row.jine + ' 元' : '—' }}</b></li>
              <li v-if="row.chezijinchangId"><span>入场单</span><b>#{{ row.chezijinchangId }}</b></li>
              <li v-if="row.tingchechangmingcheng"><span>停车场</span><b>{{ row.tingchechangmingcheng }}</b></li>
              <li v-if="row.quyu"><span>区域</span><b>{{ row.quyu }}</b></li>
              <li v-if="row.chepaihao"><span>车牌</span><b>{{ row.chepaihao }}</b></li>
              <li v-if="row.yuanyin"><span>说明</span><b>{{ row.yuanyin }}</b></li>
            </ul>
            <div class="wode-tingche-actions">
              <el-button type="warning" size="small" @click="goBujiaoPay(row)">去补缴</el-button>
              <el-button v-if="row.chezijinchangId" type="text" size="small" @click="goLichangAfterBujiao(row)">补缴后离场</el-button>
            </div>
          </el-card>
        </div>
      </el-tab-pane>

      <el-tab-pane label="待支付" name="daiZhifu">
        <el-button type="primary" size="small" native-type="button" :loading="loadingSummary" @click="loadSummary">刷新</el-button>
        <div v-loading="loadingSummary" class="wode-tingche-list">
          <el-empty v-if="!loadingSummary && daiZhifuList.length === 0" description="暂无待支付缴费单" />
          <el-card v-for="row in daiZhifuList" :key="'f-' + row.id" shadow="hover" class="wode-tingche-card">
            <div class="wode-tingche-card-head">
              <span class="wode-tingche-tag">{{ row.dingdanhao || ('缴费 #' + row.id) }}</span>
              <span class="wode-tingche-status">{{ row.ispay || '未支付' }}</span>
            </div>
            <ul class="wode-tingche-meta">
              <li><span>停车场</span><b>{{ row.tingchechangmingcheng || '—' }}</b></li>
              <li v-if="row.quyu"><span>区域</span><b>{{ row.quyu }}</b></li>
              <li v-if="row.chepaihao"><span>车牌</span><b>{{ row.chepaihao }}</b></li>
              <li><span>停车时长</span><b>{{ row.bencitingcheshizhang != null ? row.bencitingcheshizhang + ' 小时' : '—' }}</b></li>
              <li><span>费用</span><b class="wode-tingche-fee">{{ row.bencitingchefeiyong != null ? row.bencitingchefeiyong + ' 元' : '—' }}</b></li>
              <li><span>离场时间</span><b>{{ row.lichangshijian || '—' }}</b></li>
            </ul>
            <div class="wode-tingche-actions">
              <el-button type="success" size="small" @click="goPay(row)">缴费详情 / 支付</el-button>
            </div>
          </el-card>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
// 这是我cursor给父亲写的 — P1-09 我的停车
const LIUCHENG_DAIRUCHANG = '已预约待入场'

export default {
  data() {
    return {
      activeTab: 'daiRuchang',
      hasToken: !!localStorage.getItem('frontToken'),
      loadingYuyue: false,
      loadingSummary: false,
      loadingBujiao: false,
      daiRuchangList: [],
      zaiChangList: [],
      daiZhifuList: [],
      daiBujiaoList: [],
      yuyuePage: 1,
      yuyueLimit: 10,
      yuyueTotal: 0,
      summaryLoaded: false
    }
  },
  created() {
    this.syncTabFromRoute()
    this.refreshActiveTab()
  },
  watch: {
    '$route'(to) {
      if (!to.path || to.path.indexOf('wodeTingche') < 0) {
        return
      }
      this.syncTabFromRoute()
      this.refreshActiveTab()
    }
  },
  methods: {
    // 这是我cursor给父亲写的 — P1-20 支持 ?tab=daiZhifu 打开待支付并刷新
    syncTabFromRoute() {
      const tab = (this.$route.query.tab || '').trim()
      if (tab === 'daiZhifu' || tab === 'zaiChang' || tab === 'daiRuchang' || tab === 'daiBujiao') {
        this.activeTab = tab
      }
    },
    refreshActiveTab() {
      if (!this.hasToken) {
        return
      }
      if (this.activeTab === 'daiRuchang') {
        this.loadDaiRuchang()
      } else if (this.activeTab === 'daiBujiao') {
        this.loadDaiBujiao()
      } else {
        this.loadSummary()
      }
    },
    onTabClick(tab) {
      if (!this.hasToken) return
      if (tab.name === 'daiRuchang') {
        this.loadDaiRuchang()
      } else if (tab.name === 'daiBujiao') {
        this.loadDaiBujiao()
      } else {
        this.loadSummary()
      }
    },
    onYuyuePageChange(p) {
      this.yuyuePage = p
      this.loadDaiRuchang()
    },
    /** P1-07 我的预约列表 */
    loadDaiRuchang() {
      if (!this.hasToken) return
      this.loadingYuyue = true
      this.$http.get('chewei/n4/yuyue/myPage', {
        params: {
          page: this.yuyuePage,
          limit: this.yuyueLimit,
          zhuangtai: '有效'
        }
      }).then(res => {
        if (res.data && res.data.code === 0) {
          const page = res.data.data || {}
          const raw = page.list || []
          this.daiRuchangList = raw.filter(y => {
            const jn = (y.liuchengJiedian || '').trim()
            return jn === LIUCHENG_DAIRUCHANG && !y.chezijinchangId
          })
          this.yuyueTotal = page.total != null ? page.total : raw.length
        } else {
          this.$message.error((res.data && res.data.msg) || '加载预约失败')
        }
      }).catch(err => this.onHttpFail(err)).finally(() => {
        this.loadingYuyue = false
      })
    },
    /** 这是我cursor给父亲写的 — P1-25 待补缴：优先 N7 按用户+待支付；失败时回退 P1-08 汇总 */
    loadDaiBujiao() {
      if (!this.hasToken) return
      this.loadingBujiao = true
      this.$http.get('chewei/n7/bujiao/list', { params: { zhuangtai: '待支付' } }).then(res => {
        if (res.data && res.data.code === 0) {
          this.daiBujiaoList = res.data.data || []
          return
        }
        return this.fillDaiBujiaoFromSummary((res.data && res.data.msg) || 'N7 列表加载失败')
      }).catch(() => {
        return this.fillDaiBujiaoFromSummary('N7 列表请求失败')
      }).finally(() => {
        this.loadingBujiao = false
      })
    },
    fillDaiBujiaoFromSummary(warnMsg) {
      return this.$http.get('chewei/my/parkingSummary').then(res => {
        if (res.data && res.data.code === 0) {
          const d = res.data.data || {}
          this.daiBujiaoList = d.daiBujiaoList || []
          this.zaiChangList = d.zaiChangRuchangList || this.zaiChangList
          this.daiZhifuList = d.daiZhifuJiaofeiList || this.daiZhifuList
          this.summaryLoaded = true
          if (warnMsg) this.$message.warning(warnMsg + '，已使用汇总接口数据')
        } else {
          this.$message.error((res.data && res.data.msg) || '加载待补缴失败')
        }
      }).catch(err => this.onHttpFail(err))
    },
    /** P1-08 我的停车汇总（停车中 / 待支付 / 待补缴） */
    loadSummary() {
      if (!this.hasToken) return
      this.loadingSummary = true
      this.$http.get('chewei/my/parkingSummary').then(res => {
        if (res.data && res.data.code === 0) {
          const d = res.data.data || {}
          this.zaiChangList = d.zaiChangRuchangList || []
          this.daiZhifuList = d.daiZhifuJiaofeiList || []
          if (d.daiBujiaoList) {
            this.daiBujiaoList = d.daiBujiaoList
          }
          this.summaryLoaded = true
        } else {
          this.$message.error((res.data && res.data.msg) || '加载汇总失败')
        }
      }).catch(err => this.onHttpFail(err)).finally(() => {
        this.loadingSummary = false
      })
    },
    goRuchang(row) {
      if (!row || !row.id) return
      this.$router.push({ path: '/index/m2TingcheLi', query: { yuyueId: String(row.id) } })
    },
    goLichang(row) {
      if (!row || !row.id) return
      this.$router.push({ path: '/index/m2TingcheLi', query: { chezijinchangId: String(row.id) } })
    },
    goPay(row) {
      if (!row || !row.id) return
      this.$router.push({ path: '/index/tingchejiaofeiDetail', query: { id: String(row.id) } })
    },
    goBujiaoPay(row) {
      const query = { fromM2: '0' }
      if (row && row.chezijinchangId) {
        query.chezijinchangId = String(row.chezijinchangId)
      }
      this.$router.push({ path: '/index/n7Bujiao', query })
    },
    goLichangAfterBujiao(row) {
      if (!row || !row.chezijinchangId) return
      this.$router.push({
        path: '/index/m2TingcheLi',
        query: { chezijinchangId: String(row.chezijinchangId) }
      })
    },
    onHttpFail(err) {
      const body = err && err.body
      if (body && body.code === 401) {
        this.$message.warning(body.msg || '请先登录')
        return
      }
      this.$message.error((body && body.msg) || '请求失败')
    }
  }
}
</script>

<style scoped>
.wode-tingche-page {
  width: 80%;
  padding: 20px;
  margin: 10px auto;
}
.wode-tingche-title {
  margin: 0 0 8px;
  font-size: 20px;
  color: #0f172a;
}
.wode-tingche-desc {
  color: #64748b;
  margin: 0 0 16px;
  line-height: 1.6;
  font-size: 14px;
}
.wode-tingche-alert {
  margin-bottom: 16px;
}
.wode-tingche-list {
  margin-top: 16px;
  min-height: 120px;
}
.wode-tingche-card {
  margin-bottom: 12px;
  border-radius: 10px;
}
.wode-tingche-card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.wode-tingche-tag {
  font-weight: 600;
  color: #0f172a;
}
.wode-tingche-status {
  font-size: 13px;
  color: #10b981;
}
.wode-tingche-status-warn {
  color: #d97706;
}
.wode-tingche-meta {
  list-style: none;
  padding: 0;
  margin: 0 0 12px;
  font-size: 14px;
  line-height: 1.9;
  color: #475569;
}
.wode-tingche-meta li span {
  display: inline-block;
  width: 72px;
  color: #94a3b8;
}
.wode-tingche-fee {
  color: #dc2626;
}
.wode-tingche-actions {
  text-align: right;
}
.wode-tingche-pager {
  margin-top: 16px;
  text-align: center;
}
</style>
