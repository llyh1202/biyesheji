<template>
  <!-- 这是我cursor给父亲写的 — M4 时段预约（余位查询 + 并发校验） -->
  <div class="tech-feature-wrap tech-page-panel" :style='{"width":"80%","padding":"20px","margin":"10px auto","background":"transparent"}'>
    <h2 :style='{"margin":"0 0 12px","fontSize":"20px"}'>车位时段预约（M4）</h2>
    <p :style='{"color":"#666","marginBottom":"16px","lineHeight":"1.6"}'>
      先查询余位，再提交预约。若并发抢位失败，将提示<strong>余位不足</strong>或冲突信息。
    </p>
    <el-form label-width="120px" :model="form">
      <el-form-item label="车位 id" required>
        <el-input v-model="form.cheweiId" placeholder="chewei 表主键数字" />
        <el-button type="text" @click="loadCheweiHint">加载车位信息</el-button>
        <span v-if="cheweiHint" :style='{"marginLeft":"8px","color":"#909399","fontSize":"13px"}'>{{ cheweiHint }}</span>
      </el-form-item>
      <el-form-item label="开始时间" required>
        <el-date-picker v-model="form.kaishiShijian" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" style="width:260px" />
      </el-form-item>
      <el-form-item label="结束时间" required>
        <el-date-picker v-model="form.jieshuShijian" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" style="width:260px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" native-type="button" :loading="loadingAvail" @click="checkAvailability">查询余位</el-button>
        <el-button type="success" native-type="button" :loading="loadingReserve" @click="submitReserve">提交预约</el-button>
      </el-form-item>
    </el-form>
    <el-alert v-if="availInfo" :title="availTitle" :type="availInfo.available > 0 ? 'success' : 'warning'" show-icon :closable="false" />
    <!-- 这是我cursor给父亲写的 — P1-04 预约成功结果 -->
    <el-dialog title="预约成功" :visible.sync="reserveSuccessVisible" width="460px" :close-on-click-modal="false">
      <div class="reserve-success-body" v-if="reserveResult">
        <p :style='{"textAlign":"center","color":"#10b981","fontSize":"40px","margin":"0 0 12px"}'><i class="el-icon-success"></i></p>
        <ul :style='{"listStyle":"none","padding":"12px 16px","margin":0,"background":"#f8fafc","borderRadius":"8px","fontSize":"14px","lineHeight":"2"}'>
          <li>预约单号：<b>{{ reserveResult.yuyueId }}</b></li>
          <li>停车场：<b>{{ reserveResult.tingchechangmingcheng }}</b></li>
          <li v-if="reserveResult.quyu">区域：<b>{{ reserveResult.quyu }}</b></li>
          <li>预约时段：<b>{{ reserveResult.kaishiShijian }} ~ {{ reserveResult.jieshuShijian }}</b></li>
        </ul>
      </div>
      <span slot="footer">
        <el-button @click="reserveSuccessVisible = false">关闭</el-button>
        <el-button type="primary" @click="goM2AfterReserve">前往入场（M2）</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
// 这是我cursor给父亲写的 — M4 预约
export default {
  data() {
    return {
      form: {
        cheweiId: '',
        kaishiShijian: '',
        jieshuShijian: ''
      },
      cheweiHint: '',
      availInfo: null,
      loadingAvail: false,
      loadingReserve: false,
      reserveSuccessVisible: false,
      reserveResult: null
    }
  },
  computed: {
    availTitle() {
      if (!this.availInfo) return ''
      return `总车位 ${this.availInfo.total}，该时段可预约 ${this.availInfo.available} 个`
    }
  },
  methods: {
    parseCheweiId() {
      const s = String(this.form.cheweiId || '').trim()
      if (!/^\d+$/.test(s)) {
        this.$message.warning('车位 id 须为数字')
        return null
      }
      return parseInt(s, 10)
    },
    loadCheweiHint() {
      const id = this.parseCheweiId()
      if (id == null) return
      this.$http.get('chewei/detail/' + id).then(res => {
        if (res.data && res.data.code === 0 && res.data.data) {
          const c = res.data.data
          this.cheweiHint = (c.tingchechangmingcheng || '') + ' / ' + (c.quyu || '') + ' / ' + (c.cheweibianhao || '') + ' [' + (c.zhuangtai || '') + ']'
        }
      })
    },
    buildLotQuyuFromChewei(cb) {
      const id = this.parseCheweiId()
      if (id == null) return
      this.$http.get('chewei/detail/' + id).then(res => {
        if (res.data && res.data.code === 0 && res.data.data) {
          cb(res.data.data)
        } else {
          this.$message.error('车位不存在')
        }
      })
    },
    checkAvailability() {
      if (!this.form.kaishiShijian || !this.form.jieshuShijian) {
        this.$message.warning('请选择开始与结束时间')
        return
      }
      this.buildLotQuyuFromChewei(cw => {
        this.loadingAvail = true
        this.$http.post('chewei/n4/availability', {
          tingchechangmingcheng: cw.tingchechangmingcheng,
          quyu: cw.quyu,
          kaishiShijian: this.form.kaishiShijian,
          jieshuShijian: this.form.jieshuShijian
        }).then(res => {
          if (res.data && res.data.code === 0) {
            this.availInfo = res.data.data
            if (this.availInfo.available < 1) {
              this.$message.error('余位不足')
            }
          } else {
            this.handleM4Error(res.data)
          }
        }).catch(() => this.$message.error('查询失败')).finally(() => {
          this.loadingAvail = false
        })
      })
    },
    submitReserve() {
      const id = this.parseCheweiId()
      if (id == null) return
      if (!this.form.kaishiShijian || !this.form.jieshuShijian) {
        this.$message.warning('请选择时段')
        return
      }
      this.loadingReserve = true
      this.$http.post('chewei/n4/reserve', {
        cheweiId: id,
        kaishiShijian: this.form.kaishiShijian,
        jieshuShijian: this.form.jieshuShijian
      }).then(res => {
        if (res.data && res.data.code === 0) {
          const d = res.data.data || {}
          const y = d.yuyue
          const cw = d.chewei
          if (y && y.id) {
            // 这是我cursor给父亲写的 — P1-04
            this.reserveResult = {
              yuyueId: y.id,
              kaishiShijian: y.kaishiShijian || this.form.kaishiShijian,
              jieshuShijian: y.jieshuShijian || this.form.jieshuShijian,
              tingchechangmingcheng: (y.tingchechangmingcheng || (cw && cw.tingchechangmingcheng) || ''),
              quyu: y.quyu || (cw && cw.quyu) || '',
              cheweibianhao: cw && cw.cheweibianhao
            }
            this.reserveSuccessVisible = true
            this.$message.success('预约成功')
          } else {
            this.$message.success('预约成功')
          }
        } else {
          this.handleM4Error(res.data)
        }
      }).catch(err => {
        this.onHttpFail(err)
      }).finally(() => {
        this.loadingReserve = false
      })
    },
    handleM4Error(data) {
      if (!data) {
        this.$message.error('操作失败')
        return
      }
      if (data.code === 4601 || data.m4Code === 'YUWEI_BUZU') {
        this.$message.error('余位不足')
        return
      }
      this.$message.error(data.msg || '预约失败')
    },
    onHttpFail(err) {
      const body = err && err.body
      if (body) {
        this.handleM4Error(body)
      } else {
        this.$message.error('请求失败')
      }
    },
    goM2AfterReserve() {
      if (!this.reserveResult || !this.reserveResult.yuyueId) return
      this.reserveSuccessVisible = false
      this.$router.push({
        path: '/index/m2TingcheLi',
        query: { yuyueId: String(this.reserveResult.yuyueId) }
      })
    }
  }
}
</script>
