<template>
  <!-- 这是我cursor给父亲写的 — P1-11 M2 停车闭环步骤向导 -->
  <div class="tech-feature-wrap tech-page-panel m2-wizard-page">
    <h2 class="m2-wizard-title">停车业务闭环（M2）</h2>
    <p class="m2-wizard-desc">按步骤完成：选预约 → 确认信息 → 入场 → 离场试算 → 支付。可从「我的停车」或预约成功页自动带入预约单。</p>

    <el-alert
      v-if="!hasToken"
      title="入场、离场、结算须先登录；未登录将跳转登录页。"
      type="warning"
      :closable="false"
      show-icon
      class="m2-wizard-alert"
    />

    <el-steps :active="activeStep" align-center finish-status="success" class="m2-wizard-steps">
      <el-step title="选预约" />
      <el-step title="确认快照" />
      <el-step title="入场" />
      <el-step title="离场试算" />
      <el-step title="支付" />
    </el-steps>

    <!-- ① 选预约 -->
    <el-card v-show="activeStep === 0" shadow="never" class="m2-wizard-card">
      <div slot="header" class="m2-card-header">
        <span>① 选择待入场预约</span>
        <el-button type="text" size="small" @click="$router.push('/index/wodeTingche')">去我的停车</el-button>
      </div>
      <el-button type="primary" size="small" native-type="button" :loading="loadingYuyueList" @click="loadPendingYuyueList">刷新列表</el-button>
      <div v-loading="loadingYuyueList" class="m2-yuyue-pick-list">
        <el-empty v-if="!loadingYuyueList && pendingYuyueList.length === 0" description="暂无待入场预约，请先在车位详情页预约" />
        <div
          v-for="row in pendingYuyueList"
          :key="row.id"
          class="m2-yuyue-pick-item"
          :class="{ 'is-selected': String(selectedYuyueId) === String(row.id) }"
          @click="pickYuyue(row)"
        >
          <div class="m2-pick-title">预约 #{{ row.id }} · {{ row.tingchechangmingcheng || '—' }}</div>
          <div class="m2-pick-meta">{{ row.quyu || '' }} {{ row.cheweibianhao ? '· ' + row.cheweibianhao : '' }}</div>
          <div class="m2-pick-meta">{{ row.kaishiShijian }} ~ {{ row.jieshuShijian }}</div>
        </div>
      </div>
      <div class="m2-wizard-actions">
        <el-button type="primary" :disabled="!selectedYuyueId" @click="goStepConfirmSnapshot">下一步：确认快照</el-button>
      </div>
    </el-card>

    <!-- ② 确认快照 -->
    <el-card v-show="activeStep === 1" shadow="never" class="m2-wizard-card">
      <div slot="header">② 确认预约快照</div>
      <div v-if="selectedYuyueId" class="m2-id-chip">当前预约单：<b>#{{ selectedYuyueId }}</b></div>
      <div v-loading="loadingSnapshot">
        <div v-if="snapshot && snapshot.yuyue" class="m2-snapshot-body">
          <ul class="m2-snapshot-list">
            <li><span>流程节点</span><b>{{ snapshot.yuyue.liuchengJiedian || '—' }}</b></li>
            <li><span>预约状态</span><b>{{ snapshot.yuyue.zhuangtai }}</b></li>
            <li><span>时段</span><b>{{ snapshot.yuyue.kaishiShijian }} ~ {{ snapshot.yuyue.jieshuShijian }}</b></li>
            <li v-if="snapshot.chewei"><span>车位</span><b>{{ snapshot.chewei.cheweibianhao }}（{{ snapshot.chewei.zhuangtai }}）</b></li>
            <li v-if="snapshot.cheweixinxi"><span>停车场</span><b>{{ snapshot.cheweixinxi.tingchechangmingcheng }} / {{ snapshot.cheweixinxi.quyu }}</b></li>
            <li v-if="snapshot.cheweixinxi"><span>小时单价</span><b>{{ snapshot.cheweixinxi.xiaoshidanjia }} 元</b></li>
          </ul>
          <el-alert v-if="jifeiRule" :title="jifeiRuleHint" type="success" :closable="false" show-icon class="m2-rule-alert" />
          <div v-if="snapshot.hint" class="m2-hint">{{ snapshot.hint }}</div>
        </div>
        <el-empty v-else-if="!loadingSnapshot" description="请返回上一步选择预约，或等待快照加载" />
      </div>
      <div class="m2-wizard-actions">
        <el-button @click="activeStep = 0">上一步</el-button>
        <el-button type="primary" :loading="loadingSnapshot" :disabled="!(snapshot && snapshot.yuyue)" @click="confirmSnapshotNext">确认并进入入场</el-button>
      </div>
    </el-card>

    <!-- ③ 入场 -->
    <el-card v-show="activeStep === 2" shadow="never" class="m2-wizard-card">
      <div slot="header">③ 预约校验后入场</div>
      <div v-if="selectedYuyueId" class="m2-id-chip">预约单 #{{ selectedYuyueId }}</div>
      <!-- 这是我cursor给父亲写的 — P1-22 展示用户绑定车牌 -->
      <el-alert
        v-if="userChepaihao"
        :title="'当前绑定车牌：' + userChepaihao"
        type="info"
        :closable="false"
        show-icon
        class="m2-wizard-alert"
      />
      <el-form label-width="100px" :model="ruchangForm" @submit.native.prevent="doM2Ruchang">
        <el-form-item label="用户账号">
          <el-input v-model="ruchangForm.yonghuzhanghao" readonly placeholder="登录后自动填充" />
        </el-form-item>
        <el-form-item label="进场时间">
          <el-date-picker v-model="ruchangForm.jinchangshijian" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="默认当前" style="width:260px" />
        </el-form-item>
        <el-form-item label="车牌号" required>
          <el-input v-model="ruchangForm.chepaihao" placeholder="登录后从个人资料带出，可修改" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="ruchangForm.xingming" placeholder="登录后自动填充" />
        </el-form-item>
        <el-form-item label="手机">
          <el-input v-model="ruchangForm.shouji" placeholder="登录后自动填充" />
        </el-form-item>
        <el-form-item label="车辆图片">
          <el-input v-model="ruchangForm.cheliangtupian" placeholder="可选" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="button" :loading="loadingRuchang" @click="doM2Ruchang">提交入场</el-button>
        </el-form-item>
      </el-form>
      <div v-if="ruchangResult && ruchangResult.ruchang" class="m2-result-line">
        已入场 · 入场单 <b>#{{ ruchangResult.ruchang.id }}</b>
      </div>
      <el-collapse v-if="devMode" class="m2-dev-collapse">
        <el-collapse-item title="开发者：手填 id">
          <el-form label-width="100px" size="small">
            <el-form-item label="预约单 id"><el-input v-model="ruchangForm.yuyueId" /></el-form-item>
          </el-form>
        </el-collapse-item>
      </el-collapse>
      <div class="m2-wizard-actions">
        <el-button @click="activeStep = 1">上一步</el-button>
        <el-button type="primary" :disabled="!(ruchangResult && ruchangResult.ruchang)" @click="afterRuchangNext">下一步：离场试算</el-button>
      </div>
    </el-card>

    <!-- ④ 离场试算 -->
    <el-card v-show="activeStep === 3" shadow="never" class="m2-wizard-card">
      <div slot="header">④ 离场与费用试算</div>
      <div v-if="lichangForm.chezijinchangId" class="m2-id-chip">入场单 #{{ lichangForm.chezijinchangId }}</div>
      <el-form label-width="100px" @submit.native.prevent="doLichang">
        <el-form-item label="离场时间">
          <el-date-picker v-model="lichangForm.lichangshijian" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" style="width:260px" />
        </el-form-item>
        <el-form-item>
          <el-button type="info" native-type="button" :loading="loadingPreview" @click="previewFee">试算停车费</el-button>
          <el-button type="warning" native-type="button" :loading="loadingLichang" @click="doLichang">生成缴费单</el-button>
          <el-button type="text" @click="goM5Preview">M5 试算页</el-button>
        </el-form-item>
      </el-form>
      <el-alert v-if="previewJifei" :title="'试算：' + previewJifei.fee + ' 元 — ' + previewJifei.breakdown" type="info" :closable="false" show-icon class="m2-rule-alert" />
      <div v-if="orderAfterLichang" class="m2-result-line">
        缴费单 <b>#{{ orderAfterLichang.id }}</b> · {{ orderAfterLichang.bencitingchefeiyong }} 元 · {{ orderAfterLichang.ispay }}
      </div>
      <div v-if="bujiaoList.length" class="m2-bujiao-block">
        <div class="m2-bujiao-title">补缴单（N7）</div>
        <el-button size="mini" @click="loadBujiao">刷新</el-button>
        <el-button size="mini" type="text" @click="$router.push('/index/n7Bujiao')">去补缴</el-button>
        <el-table :data="bujiaoList" size="small" border class="m2-bujiao-table">
          <el-table-column prop="danhao" label="单号" />
          <el-table-column prop="leixing" label="类型" width="90" />
          <el-table-column prop="jine" label="金额" width="70" />
          <el-table-column prop="zhuangtai" label="状态" width="90" />
        </el-table>
      </div>
      <el-collapse v-if="devMode" class="m2-dev-collapse">
        <el-collapse-item title="开发者：手填 id">
          <el-form inline size="small">
            <el-form-item label="入场单"><el-input v-model="lichangForm.chezijinchangId" style="width:120px" /></el-form-item>
            <el-form-item label="预约单"><el-input v-model="lichangForm.yuyueId" style="width:120px" /></el-form-item>
          </el-form>
        </el-collapse-item>
      </el-collapse>
      <div class="m2-wizard-actions">
        <el-button @click="activeStep = 2">上一步</el-button>
        <el-button type="primary" :disabled="!orderAfterLichang" @click="afterLichangNext">下一步：支付</el-button>
      </div>
    </el-card>

    <!-- ⑤ 支付 -->
    <el-card v-show="activeStep === 4" shadow="never" class="m2-wizard-card">
      <div slot="header">⑤ 支付与关单</div>
      <!-- 这是我cursor给父亲写的 — P1-18 统一支付路径说明 -->
      <p class="m2-pay-path-hint">待支付停车费请在本步「模拟结算关单」或「缴费详情页」完成，勿使用通用支付页。</p>
      <div v-if="orderAfterLichang" class="m2-pay-summary">
        <p>缴费单号：<b>{{ orderAfterLichang.dingdanhao || ('#' + orderAfterLichang.id) }}</b></p>
        <p>应付金额：<b class="m2-fee">{{ orderAfterLichang.bencitingchefeiyong }} 元</b></p>
        <p>支付状态：{{ orderAfterLichang.ispay || '未支付' }}</p>
      </div>
      <div class="m2-wizard-actions m2-pay-actions">
        <el-button type="success" :loading="loadingJiesuan" @click="doJiesuan">模拟结算（关单）</el-button>
        <el-button type="primary" @click="goPayDetail">去支付详情页</el-button>
        <el-button type="text" @click="$router.push('/index/wodeTingche')">返回我的停车</el-button>
      </div>
      <el-collapse v-if="devMode" class="m2-dev-collapse">
        <el-collapse-item title="开发者：手填缴费单 id">
          <el-input v-model="jiesuanId" placeholder="数字 id" style="width:160px" />
        </el-collapse-item>
      </el-collapse>
    </el-card>

    <div class="m2-dev-toggle">
      <el-checkbox v-model="devMode">开发者模式（显示手填 id）</el-checkbox>
    </div>
  </div>
</template>

<script>
// 这是我cursor给父亲写的 — P1-10 / P1-11 M2 步骤向导；P1-15 入场表单自动填充用户信息
import { requireFrontLogin, handleAuthFail, autofillM2RuchangForm, goWodeTingcheDaiZhifu, getYonghuChepaihao } from '@/common/auth'

const LIUCHENG_DAIRUCHANG = '已预约待入场'

export default {
  data() {
    return {
      activeStep: 0,
      devMode: false,
      hasToken: !!localStorage.getItem('frontToken'),
      selectedYuyueId: '',
      lockedYuyueId: null,
      pendingYuyueList: [],
      loadingYuyueList: false,
      snapshot: null,
      loadingSnapshot: false,
      loadingRuchang: false,
      loadingLichang: false,
      loadingPreview: false,
      loadingJiesuan: false,
      previewJifei: null,
      lastJifeiCalc: null,
      jifeiRule: null,
      ruchangForm: {
        yuyueId: '',
        jinchangshijian: '',
        yonghuzhanghao: '',
        xingming: '',
        shouji: '',
        chepaihao: '',
        cheliangtupian: ''
      },
      ruchangResult: null,
      lichangForm: {
        chezijinchangId: '',
        yuyueId: '',
        lichangshijian: ''
      },
      orderAfterLichang: null,
      jiesuanId: '',
      bujiaoList: [],
      userChepaihao: ''
    }
  },
  computed: {
    jifeiRuleHint() {
      const g = this.jifeiRule
      if (!g) return ''
      let s = 'M5 规则：' + (g.guizeMingcheng || '') + '，模式「' + (g.jifeiMoshi || '') + '」'
      if (g.fengdingYuan) s += '，封顶 ' + g.fengdingYuan + ' 元'
      if (g.shouxiaoshiYuan) s += '，首小时 ' + g.shouxiaoshiYuan + ' 元'
      if (g.jietiDanjia) s += '，后续 ' + g.jietiDanjia + ' 元/时'
      return s
    }
  },
  created() {
    const q = this.$route.query || {}
    if (this.hasToken) {
      // 这是我cursor给父亲写的 — P1-22 登录后默认填入 yonghu 车牌号
      autofillM2RuchangForm(this, this.ruchangForm, true).then(() => {
        this.syncUserChepaihaoDisplay()
      })
    }
    if (q.yuyueId) {
      this.applyYuyueId(String(q.yuyueId), true)
    } else if (q.chezijinchangId) {
      this.lichangForm.chezijinchangId = String(q.chezijinchangId)
      this.loadChain(q.chezijinchangId, true)
    } else if (this.hasToken) {
      this.loadPendingYuyueList()
    }
  },
  methods: {
    syncUserChepaihaoDisplay() {
      this.userChepaihao = getYonghuChepaihao() || (this.ruchangForm.chepaihao || '').trim()
    },
    parseId(val, label) {
      if (val === null || val === undefined || String(val).trim() === '') {
        if (label) this.$message.warning('请填写' + label + '（纯数字）')
        return null
      }
      const s = String(val).trim()
      if (!/^\d+$/.test(s)) {
        this.$message.error(label + '须为数字主键')
        return null
      }
      return parseInt(s, 10)
    },
    onHttpFail(err, fallback) {
      if (handleAuthFail(this, err)) return
      const body = err && err.body
      if (body && handleAuthFail(this, body)) return
      const msg = (body && body.msg) || (err && err.message) || fallback || '请求失败'
      this.$message.error(msg)
    },
    applyYuyueId(id, skipToSnapshot) {
      this.selectedYuyueId = id
      this.lockedYuyueId = id
      this.ruchangForm.yuyueId = id
      this.lichangForm.yuyueId = id
      if (skipToSnapshot) {
        this.activeStep = 1
        this.loadSnapshot()
      }
    },
    loadPendingYuyueList() {
      if (!this.hasToken) {
        this.$message.warning('请先登录')
        return
      }
      this.loadingYuyueList = true
      this.$http.get('chewei/n4/yuyue/myPage', {
        params: { page: 1, limit: 50, zhuangtai: '有效' }
      }).then(res => {
        if (res.data && res.data.code === 0) {
          const page = res.data.data || {}
          const raw = page.list || []
          this.pendingYuyueList = raw.filter(y => {
            const jn = (y.liuchengJiedian || '').trim()
            return jn === LIUCHENG_DAIRUCHANG && !y.chezijinchangId
          })
        } else {
          this.$message.error((res.data && res.data.msg) || '加载预约列表失败')
        }
      }).catch(err => this.onHttpFail(err, '加载预约列表失败')).finally(() => {
        this.loadingYuyueList = false
      })
    },
    pickYuyue(row) {
      if (!row || !row.id) return
      this.applyYuyueId(String(row.id), false)
    },
    goStepConfirmSnapshot() {
      if (!this.selectedYuyueId) {
        this.$message.warning('请先选择一条预约')
        return
      }
      this.activeStep = 1
      this.loadSnapshot()
    },
    confirmSnapshotNext() {
      if (!(this.snapshot && this.snapshot.yuyue)) {
        this.$message.warning('请先加载预约快照')
        return
      }
      if (!requireFrontLogin(this)) return
      this.activeStep = 2
      // 这是我cursor给父亲写的 — P1-15/P1-22 进入入场步骤时刷新用户资料（含车牌，不覆盖已改字段）
      autofillM2RuchangForm(this, this.ruchangForm, true).then(() => {
        this.syncUserChepaihaoDisplay()
      })
    },
    afterRuchangNext() {
      if (!(this.ruchangResult && this.ruchangResult.ruchang)) {
        this.$message.warning('请先完成入场')
        return
      }
      this.activeStep = 3
    },
    afterLichangNext() {
      if (!this.orderAfterLichang) {
        this.$message.warning('请先生成缴费单')
        return
      }
      this.activeStep = 4
    },
    loadSnapshot() {
      const id = this.parseId(this.selectedYuyueId || this.ruchangForm.yuyueId, '预约单 id')
      if (id == null) return
      this.loadingSnapshot = true
      this.$http.get('n3/tingcheli/m2/yuyue/snapshot', { params: { yuyueId: id } }).then(res => {
        if (res.data && res.data.code === 0) {
          this.snapshot = res.data.data
          this.ruchangForm.yuyueId = String(id)
          this.lichangForm.yuyueId = String(id)
          this.selectedYuyueId = String(id)
          this.loadJifeiRule()
        } else {
          this.$message.error((res.data && res.data.msg) || '加载失败')
        }
      }).catch(err => this.onHttpFail(err, '加载快照失败')).finally(() => {
        this.loadingSnapshot = false
      })
    },
    loadChain(cid, skipToLichang) {
      const id = this.parseId(cid, '入场单 id')
      if (id == null) return
      this.$http.get('n3/tingcheli/chain', { params: { chezijinchangId: id } }).then(res => {
        if (res.data && res.data.code === 0) {
          const d = res.data.data || {}
          if (d.ruchang) {
            this.ruchangResult = { ruchang: d.ruchang, yuyueId: this.lichangForm.yuyueId }
            this.lichangForm.chezijinchangId = String(d.ruchang.id)
          }
          if (d.yuyue && d.yuyue.id) {
            this.applyYuyueId(String(d.yuyue.id), false)
            this.snapshot = { yuyue: d.yuyue, chewei: d.chewei, hint: '已从业务链带出关联预约单。' }
          }
          if (d.bujiaoList) this.bujiaoList = d.bujiaoList
          this.loadBujiao()
          this.loadJifeiRule()
          if (skipToLichang) {
            this.activeStep = d.ruchang ? 3 : 2
            if (this.activeStep === 2 && this.hasToken) {
              autofillM2RuchangForm(this, this.ruchangForm, true).then(() => {
                this.syncUserChepaihaoDisplay()
              })
            }
          }
        } else {
          this.$message.error((res.data && res.data.msg) || '加载失败')
        }
      }).catch(err => this.onHttpFail(err, '加载业务链失败'))
    },
    doM2Ruchang() {
      if (!requireFrontLogin(this)) return
      const yuyueId = this.parseId(this.ruchangForm.yuyueId || this.selectedYuyueId, '预约单 id')
      if (yuyueId == null) return
      const submit = () => {
        if (!(this.ruchangForm.chepaihao || '').trim()) {
          this.$message.error('请填写车牌号，或在个人中心维护车牌后重试')
          return
        }
        const body = { yuyueId: yuyueId }
        const chepaihao = (this.ruchangForm.chepaihao || '').trim()
        const yonghuzhanghao = (this.ruchangForm.yonghuzhanghao || '').trim()
        const xingming = (this.ruchangForm.xingming || '').trim()
        const shouji = (this.ruchangForm.shouji || '').trim()
        const cheliangtupian = (this.ruchangForm.cheliangtupian || '').trim()
        if (chepaihao) body.chepaihao = chepaihao
        if (yonghuzhanghao) body.yonghuzhanghao = yonghuzhanghao
        if (xingming) body.xingming = xingming
        if (shouji) body.shouji = shouji
        if (cheliangtupian) body.cheliangtupian = cheliangtupian
        if (this.ruchangForm.jinchangshijian) body.jinchangshijian = this.ruchangForm.jinchangshijian
        this.loadingRuchang = true
        this.$http.post('n3/tingcheli/m2/ruchang', body).then(res => {
        if (res.data && res.data.code === 0) {
          this.ruchangResult = res.data.data
          const r = res.data.data && res.data.data.ruchang
          if (r && r.id) {
            this.lichangForm.chezijinchangId = String(r.id)
            if (res.data.data.yuyueId) this.lichangForm.yuyueId = String(res.data.data.yuyueId)
            this.loadBujiao()
            this.loadJifeiRule()
            // 这是我cursor给父亲写的 — P1-12 入场成功自动进入离场步骤
            if (!this.lichangForm.lichangshijian) {
              this.lichangForm.lichangshijian = this.formatNow()
            }
            this.activeStep = 3
          }
          this.$message.success('入场成功，已进入离场试算步骤')
        } else {
          if (handleAuthFail(this, res.data)) return
          this.$message.error((res.data && res.data.msg) || '入场失败')
        }
        }).catch(err => this.onHttpFail(err, '入场请求失败')).finally(() => {
          this.loadingRuchang = false
        })
      }
      // 这是我cursor给父亲写的 — P1-15 提交前再拉一次 session，与 P1-14 后端补全配合
      autofillM2RuchangForm(this, this.ruchangForm, true).then(() => submit())
    },
    getM5Context() {
      const r = this.ruchangResult && this.ruchangResult.ruchang
      const y = this.snapshot && this.snapshot.yuyue
      const info = this.snapshot && this.snapshot.cheweixinxi
      const cw = this.snapshot && this.snapshot.chewei
      return {
        tingchechangmingcheng: (r && r.tingchechangmingcheng) || (y && y.tingchechangmingcheng) || (info && info.tingchechangmingcheng) || (cw && cw.tingchechangmingcheng) || '',
        quyu: (r && r.quyu) || (y && y.quyu) || (info && info.quyu) || (cw && cw.quyu) || '',
        xiaoshidanjia: (r && r.xiaoshidanjia) || (info && info.xiaoshidanjia) || 1
      }
    },
    loadJifeiRule() {
      const ctx = this.getM5Context()
      if (!ctx.tingchechangmingcheng) return
      this.$http.get('chewei/m5/guize/resolve', { params: { tingchechangmingcheng: ctx.tingchechangmingcheng, quyu: ctx.quyu } }).then(res => {
        if (res.data && res.data.code === 0) this.jifeiRule = res.data.data
      })
    },
    goM5Preview() {
      const ctx = this.getM5Context()
      const r = this.ruchangResult && this.ruchangResult.ruchang
      this.$router.push({
        path: '/index/m5Jifei',
        query: {
          tingchechangmingcheng: ctx.tingchechangmingcheng,
          quyu: ctx.quyu,
          xiaoshidanjia: ctx.xiaoshidanjia,
          jinchangshijian: r && r.jinchangshijian,
          lichangshijian: this.lichangForm.lichangshijian || ''
        }
      })
    },
    buildM5CalcBody() {
      const r = this.ruchangResult && this.ruchangResult.ruchang
      if (!r || !r.jinchangshijian) {
        this.$message.warning('请先完成入场')
        return null
      }
      const ctx = this.getM5Context()
      return {
        jinchangshijian: r.jinchangshijian,
        lichangshijian: this.lichangForm.lichangshijian || this.formatNow(),
        tingchechangmingcheng: ctx.tingchechangmingcheng,
        quyu: ctx.quyu,
        xiaoshidanjia: ctx.xiaoshidanjia
      }
    },
    formatNow() {
      const d = new Date()
      const p = n => (n < 10 ? '0' : '') + n
      return d.getFullYear() + '-' + p(d.getMonth() + 1) + '-' + p(d.getDate()) + ' ' + p(d.getHours()) + ':' + p(d.getMinutes()) + ':' + p(d.getSeconds())
    },
    previewFee() {
      const body = this.buildM5CalcBody()
      if (!body) return
      this.loadingPreview = true
      this.$http.post('chewei/m5/jifei/calc', body).then(res => {
        if (res.data && res.data.code === 0) {
          this.previewJifei = res.data.data
          this.$message.success('试算完成')
        } else {
          this.$message.error((res.data && res.data.msg) || '试算失败')
        }
      }).catch(err => this.onHttpFail(err, '试算失败')).finally(() => { this.loadingPreview = false })
    },
    doLichang() {
      if (!requireFrontLogin(this)) return
      const chezijinchangId = this.parseId(this.lichangForm.chezijinchangId, '入场单 id')
      if (chezijinchangId == null) return
      const body = { chezijinchangId: chezijinchangId }
      const yuyueRaw = (this.lichangForm.yuyueId || this.selectedYuyueId || '').trim()
      if (yuyueRaw) {
        const yuyueId = this.parseId(yuyueRaw, '预约单 id')
        if (yuyueId == null) return
        body.yuyueId = yuyueId
      }
      if (this.lichangForm.lichangshijian) body.lichangshijian = this.lichangForm.lichangshijian
      this.loadingLichang = true
      this.$http.post('n3/tingcheli/lichang', body).then(res => {
        if (res.data && res.data.code === 0) {
          const d = res.data.data || {}
          const order = d.order || d
          this.orderAfterLichang = order
          this.lastJifeiCalc = d.jifeiCalc || null
          this.previewJifei = this.lastJifeiCalc
          // 这是我cursor给父亲写的 — P1-12 离场成功自动进入支付并填入 tingchejiaofeiId
          this.jiesuanId = order.id != null ? String(order.id) : ''
          let msg = '已生成缴费单'
          if (d.bujiaoMerged > 0) msg += '（含已支付补缴 ' + d.bujiaoMerged + ' 元）'
          if (this.jiesuanId) {
            this.activeStep = 4
            msg += '，已进入支付步骤'
          }
          this.$message.success(msg)
        } else {
          if (handleAuthFail(this, res.data)) return
          this.$message.error((res.data && res.data.msg) || '离场失败')
        }
      }).catch(err => this.onHttpFail(err, '离场请求失败')).finally(() => {
        this.loadingLichang = false
      })
    },
    doJiesuan() {
      if (!requireFrontLogin(this)) return
      const raw = this.jiesuanId || (this.orderAfterLichang && this.orderAfterLichang.id)
      const tingchejiaofeiId = this.parseId(raw, '缴费单 id')
      if (tingchejiaofeiId == null) return
      this.loadingJiesuan = true
      this.$http.post('n3/tingcheli/jiesuan', { tingchejiaofeiId: tingchejiaofeiId }).then(res => {
        if (res.data && res.data.code === 0) {
          if (this.orderAfterLichang) this.orderAfterLichang.ispay = '已支付'
          this.$message.success('结算成功，已返回我的停车')
          // 这是我cursor给父亲写的 — P1-20 跳转我的停车待支付 Tab 并刷新
          goWodeTingcheDaiZhifu(this)
        } else {
          if (handleAuthFail(this, res.data)) return
          this.$message.error((res.data && res.data.msg) || '结算失败')
        }
      }).catch(err => this.onHttpFail(err, '结算请求失败')).finally(() => {
        this.loadingJiesuan = false
      })
    },
    goPayDetail() {
      const raw = this.jiesuanId || (this.orderAfterLichang && this.orderAfterLichang.id)
      const id = this.parseId(raw, '缴费单 id')
      if (id == null) return
      this.$router.push({ path: '/index/tingchejiaofeiDetail', query: { id: String(id) } })
    },
    loadBujiao() {
      const id = this.parseId(this.lichangForm.chezijinchangId, null)
      if (id == null) return
      this.$http.get('chewei/n7/bujiao/list', { params: { chezijinchangId: id } }).then(res => {
        if (res.data && res.data.code === 0) this.bujiaoList = res.data.data || []
      })
    }
  }
}
</script>

<style scoped>
.m2-wizard-page {
  width: 80%;
  padding: 20px;
  margin: 10px auto;
  position: relative;
  z-index: 1;
}
.m2-wizard-title {
  margin: 0 0 8px;
  font-size: 20px;
  color: #0f172a;
}
.m2-wizard-desc {
  color: #64748b;
  margin: 0 0 16px;
  line-height: 1.6;
  font-size: 14px;
}
.m2-wizard-alert {
  margin-bottom: 16px;
}
.m2-wizard-steps {
  margin-bottom: 24px;
}
.m2-wizard-card {
  margin-bottom: 16px;
  border-radius: 10px;
}
.m2-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.m2-id-chip {
  font-size: 14px;
  color: #475569;
  margin-bottom: 12px;
  padding: 8px 12px;
  background: #f1f5f9;
  border-radius: 8px;
}
.m2-yuyue-pick-list {
  margin-top: 12px;
  min-height: 80px;
}
.m2-yuyue-pick-item {
  padding: 12px 14px;
  margin-bottom: 8px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  transition: border-color 0.2s, background 0.2s;
}
.m2-yuyue-pick-item:hover {
  border-color: #10b981;
  background: #f0fdf4;
}
.m2-yuyue-pick-item.is-selected {
  border-color: #10b981;
  background: #ecfdf5;
  box-shadow: 0 0 0 1px #10b981;
}
.m2-pick-title {
  font-weight: 600;
  color: #0f172a;
  margin-bottom: 4px;
}
.m2-pick-meta {
  font-size: 13px;
  color: #64748b;
  line-height: 1.6;
}
.m2-snapshot-list {
  list-style: none;
  padding: 0;
  margin: 0;
  font-size: 14px;
  line-height: 2;
}
.m2-snapshot-list li span {
  display: inline-block;
  width: 88px;
  color: #94a3b8;
}
.m2-rule-alert {
  margin-top: 12px;
}
.m2-hint {
  margin-top: 8px;
  color: #909399;
  font-size: 13px;
}
.m2-wizard-actions {
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
}
.m2-pay-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}
.m2-result-line {
  margin-top: 12px;
  font-size: 14px;
  color: #059669;
}
.m2-bujiao-block {
  margin-top: 16px;
}
.m2-bujiao-title {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 8px;
}
.m2-bujiao-table {
  margin-top: 8px;
}
.m2-pay-path-hint {
  margin: 0 0 12px;
  font-size: 13px;
  color: #64748b;
  line-height: 1.6;
}
.m2-pay-summary {
  font-size: 15px;
  line-height: 2;
  margin-bottom: 8px;
}
.m2-fee {
  color: #dc2626;
  font-size: 18px;
}
.m2-dev-collapse {
  margin-top: 12px;
}
.m2-dev-toggle {
  margin-top: 8px;
  text-align: right;
  font-size: 13px;
  color: #94a3b8;
}
</style>
