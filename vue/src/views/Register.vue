<template>
  <div style="width: 100%; height: 100vh; background-color: whitesmoke; overflow: hidden">
    <div style="width: 400px; margin: 150px auto">
      <div style="color: teal; font-size: 25px; text-align: center; padding: 25px">
        注 册
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
        <el-form-item prop="confirm">
          <el-input v-model="form.confirm" placeholder="请再次输入密码..." show-password>
            <template #prefix>
              <el-icon class="el-input__icon"><circle-check /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button style="width: 100%; font-weight: bold" type="primary" @click="register">注 册</el-button>
        </el-form-item>
      </el-form>

    </div>
  </div>
</template>



<script>
import { User, Lock, CircleCheck } from '@element-plus/icons-vue'
import {ElMessage} from "element-plus";
import request from "../utils/request";

export default {
  name: "Register",
  components: {
    User,
    Lock,
    CircleCheck,
  },
  data() {
    return  {
      form: {},
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
        confirm: [
          {
            required: true,
            message: '请再次输入密码',
            trigger: 'blur',
          },
        ],
      }
    }
  },
  methods: {
    register() {

      if (this.form.password !== this.form.confirm) {
        ElMessage({
          type: "error",
          message: "两次输入的密码不一致！"
        })
        return
      }
      this.$refs['form'].validate((valid) => {
        if (valid) {
          request.post("/user/register", this.form).then(res => {
            if (res.code === '0') {
              ElMessage({
                type: "success",
                message: "注册成功"
              })
              this.$router.push("/login")  // 注册成功后跳转登录页面
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

</style>