<template>
  <div style="width: 100%; height: 100vh; overflow: hidden" :style="bg">
    <div style="width: 400px; margin: 150px auto">
      <div style="color: teal; font-size: 25px; text-align: center; padding: 25px">
        登 录
      </div>
      <el-form
          ref="form"
          :model="form"
          :rules="rules"
          size="normal"
      >
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名...">
            <template #prefix>
              <el-icon class="el-input__icon"><user /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" placeholder="请输入密码..." show-password>
            <template #prefix>
              <el-icon class="el-input__icon"><lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <div style="display: flex">
            <el-input prefix-icon="el-icon-key" v-model="form.validCode" style="width: 50%;" placeholder="请输入验证码"></el-input>
            <ValidCode @input="createValidCode" />
          </div>
        </el-form-item>
        <el-form-item>
          <el-radio v-model="form.role" :label="1" style="color: #eee">管理员</el-radio>
          <el-radio v-model="form.role" :label="2" style="color: #eee">普通用户</el-radio>
        </el-form-item>
        <el-form-item>
          <el-button style="width: 100%; font-weight: bold" type="primary" @click="login">登 录</el-button>
        </el-form-item>
      </el-form>

    </div>
  </div>
</template>



<script>
import { User, Lock } from '@element-plus/icons-vue'
import {ElMessage} from "element-plus";
import request from "../utils/request";
import ValidCode from "../components/ValidCode";

export default {
  name: "Login",
  components: {
    User,
    Lock,
    ValidCode
  },
  data() {
    return  {
      form: {role: 1},
      rules: {
        username: [
          {
            required: true,
            message: '请输入用户名',
            trigger: 'blur',
          },
        ],
        password: [
          {
            required: true,
            message: '请输入密码',
            trigger: 'blur',
          },
        ],
      },
      validCode: '',
      // 加背景图片
      // bg: {
      //   backgroundImage: "url(" + require("../assets/Naruto.jpg") + ")",
      //   backgroundRepeat: "no-repeat",
      //   backgroundSize: "100% 100%"
      // }
    }
  },
  created() {
    sessionStorage.removeItem("user")
  },
  methods: {

    createValidCode(data) {
      this.validCode = data
    },

    login() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          if (!this.form.validCode) {
            ElMessage({
              type: "error",
              message: "请填写验证码"
            })
            return
          }
          if(this.form.validCode.toLowerCase() !== this.validCode.toLowerCase()) {
            ElMessage({
              type: "error",
              message: "验证码错误"
            })
            return
          }
          request.post("/user/login", this.form).then(res => {
            if (res.code === '0') {
              ElMessage({
                type: "success",
                message: "登录成功"
              })
              sessionStorage.setItem("user", JSON.stringify(res.data)) // 缓存用户信息
              this.$router.push("/")  // 登录成功后跳转主页面
            } else {
              ElMessage({
                type: "error",
                message: res.msg
              })
            }
          })
        }
      })
    },
  }
}
</script>

<style scoped>
.ValidCode{
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}
.ValidCode span{
  display: inline-block;
}
</style>