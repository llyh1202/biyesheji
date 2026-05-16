<template>
  <!-- 这是我cursor给父亲写的 — P1-18 通用支付页；停车费请走 M2 关单或缴费详情页 -->
  <div class="container tech-page-panel" :style='{"margin":"0 auto","maxWidth":"720px","width":"90%"}' style="padding: 40px 24px;">
    <el-alert
      v-if="blocked"
      :title="blockTitle"
      type="warning"
      :description="blockDesc"
      :closable="false"
      show-icon
      class="pay-block-alert"
    />
    <el-alert
      v-else
      title="确认支付前请先核对订单信息"
      type="success"
      :closable="false"
    />
    <div v-if="!blocked" class="pay-type-content">
      <div class="pay-type-item">
        <el-radio v-model="type" label="微信支付"></el-radio>
        <img src="@/assets/weixin.png" alt>
      </div>
      <div class="pay-type-item">
        <el-radio v-model="type" label="支付宝支付"></el-radio>
        <img src="@/assets/zhifubao.png" alt>
      </div>
      <div class="pay-type-item">
        <el-radio v-model="type" label="建设银行"></el-radio>
        <img src="@/assets/jianshe.png" alt>
      </div>
      <div class="pay-type-item">
        <el-radio v-model="type" label="农业银行"></el-radio>
        <img src="@/assets/nongye.png" alt>
      </div>
      <div class="pay-type-item">
        <el-radio v-model="type" label="中国银行"></el-radio>
        <img src="@/assets/zhongguo.png" alt>
      </div>
      <div class="pay-type-item">
        <el-radio v-model="type" label="交通银行"></el-radio>
        <img src="@/assets/jiaotong.png" alt>
      </div>
    </div>
    <div class="buton-content">
      <el-button v-if="!blocked" @click="submitTap" type="primary">确认支付</el-button>
      <el-button v-if="redirectDetailId" type="primary" plain @click="goParkingFeeDetail">前往缴费详情支付</el-button>
      <el-button @click="goM2">停车闭环 (M2)</el-button>
      <el-button @click="back()">返回</el-button>
    </div>
  </div>
</template>
<script>
import {
  isTingchejiaofeiPayTable,
  hasLichangForPay,
  readPayLocalObject
} from '@/common/parkingPay'

export default {
  data() {
    return {
      type: '',
      table: '',
      obj: null,
      blocked: false,
      blockTitle: '',
      blockDesc: '',
      redirectDetailId: ''
    }
  },
  mounted() {
    this.table = localStorage.getItem('paytable') || ''
    this.obj = readPayLocalObject()
    this.validateParkingFeeEntry()
  },
  methods: {
    validateParkingFeeEntry() {
      if (!isTingchejiaofeiPayTable(this.table)) {
        return
      }
      const order = this.obj
      const id = order && order.id != null ? String(order.id) : ''
      if (id) {
        this.redirectDetailId = id
      }
      if (!hasLichangForPay(order)) {
        this.blocked = true
        this.blockTitle = '须先完成离场'
        this.blockDesc = '停车费不能在未离场时支付。请先在「停车闭环 M2」完成离场并生成缴费单，再在缴费详情页支付，或使用 M2「模拟结算关单」。'
        sessionStorage.setItem(
          'p1_pay_hint',
          '须先在 M2 完成离场生成缴费单后再支付；本页不支持未离场订单。'
        )
        if (id) {
          setTimeout(() => {
            this.$router.replace({ path: '/index/tingchejiaofeiDetail', query: { id } })
          }, 1200)
        }
        return
      }
      this.blocked = true
      this.blockTitle = '请使用统一支付入口'
      this.blockDesc = '待支付停车费请通过「缴费详情页」支付，或在 M2 停车闭环中使用「模拟结算（关单）」。'
      sessionStorage.setItem(
        'p1_pay_hint',
        '停车费请使用缴费详情页或 M2 关单完成支付，勿使用本通用支付页。'
      )
      if (id) {
        setTimeout(() => {
          this.$router.replace({ path: '/index/tingchejiaofeiDetail', query: { id } })
        }, 800)
      }
    },
    goParkingFeeDetail() {
      if (!this.redirectDetailId) {
        this.$message.warning('缺少缴费单信息')
        return
      }
      this.$router.push({ path: '/index/tingchejiaofeiDetail', query: { id: this.redirectDetailId } })
    },
    goM2() {
      const q = {}
      if (this.obj && this.obj.crossrefid) {
        q.chezijinchangId = String(this.obj.crossrefid)
      }
      this.$router.push({ path: '/index/m2TingcheLi', query: q })
    },
    submitTap() {
      if (isTingchejiaofeiPayTable(this.table)) {
        this.$message.warning('停车费请使用缴费详情页或 M2 关单，本页不支持直接改支付状态')
        this.validateParkingFeeEntry()
        return
      }
      if (!this.type) {
        this.$message.error('请选择支付方式')
        return
      }
      if (!this.obj || !this.table) {
        this.$message.error('订单信息无效')
        return
      }
      this.$confirm('确定支付?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.obj.ispay = '已支付'
        this.$http.post(`${this.table}/update`, this.obj).then(res => {
          if (res.data && res.data.code === 0) {
            this.$message({
              message: '支付成功',
              type: 'success',
              duration: 1500,
              onClose: () => {
                this.$router.go(-1)
              }
            })
          } else {
            this.$message.error(res.data.msg)
          }
        })
      })
    },
    back() {
      this.$router.go(-1)
    }
  }
}
</script>
<style lang="scss" scoped>
.container {
  margin: 10px;
  font-size: 14px;
  .pay-block-alert {
    margin-bottom: 16px;
  }
  .pay-type-content {
    display: flex;
    align-items: center;
    margin-top: 20px;
    flex-wrap: wrap;
    .pay-type-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      width: 300px;
      margin: 20px;
      border: 1px solid #eeeeee;
      padding: 20px;
    }
  }
  .buton-content {
    margin: 20px;
  }
}
</style>
