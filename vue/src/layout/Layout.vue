<template>

  <el-config-provider :locale="locale">
    <div>
      <!-- 头部 -->
      <Header :user="user"/>
      <!-- 主体 -->
      <div style="display: flex">
        <!-- 侧边栏 -->
        <Aside />
        <!-- 内容区域 -->
        <router-view style="flex: 1"  @userInfo="refreshUser"/>
      </div>
    </div>
  </el-config-provider>

</template>

<script>

import Header from "../components/Header";
import Aside from "../components/Aside";

import { ElConfigProvider } from 'element-plus'
import zhCn from 'element-plus/lib/locale/lang/zh-cn'
import request from "../utils/request";

export default {
  name: "Layout",
  components: {
    Aside,
    Header,
    ElConfigProvider,
  },
  setup() {
    return {
      locale: zhCn,
    }
  },
  data() {
    return {
      user: {}
    }
  },
  created() {
    this.refreshUser()
  },
  methods: {
    refreshUser() {
      let userJson = sessionStorage.getItem("user");
      if (!userJson) {
        return
      }
      let userId = JSON.parse(userJson).id
      request.get("/user/" + userId).then(res => {
        this.user = res.data
      })
    }
  }
}
</script>

<style scoped>

</style>