<template>
  <div>
    <el-menu
        style="width: 200px; height: calc(100vh - 50px)"
        :default-active="path"
        router
        :default-openeds="['1']"
    >
      <el-menu-item index="/home">
        <el-icon><icon-menu /></el-icon>
        <template #title>
          <span>主页</span>
        </template>
      </el-menu-item>

      <el-menu-item index="/category">
        <el-icon><icon-menu /></el-icon>
        <template #title>
          <span>分类管理</span>
        </template>
      </el-menu-item>

      <el-menu-item index="/book">
        <el-icon><icon-menu /></el-icon>
        <template #title>
          <span>书籍管理</span>
        </template>
      </el-menu-item>

      <el-menu-item index="/news">
        <el-icon><icon-menu /></el-icon>
        <template #title>
          <span>新闻管理</span>
        </template>
      </el-menu-item>

      <el-menu-item index="/map">
        <el-icon><icon-menu /></el-icon>
        <template #title>
          <span>百度地图</span>
        </template>
      </el-menu-item>

      <el-sub-menu index="1" v-if="user.role === 1">
        <template #title>
          <el-icon><setting /></el-icon>
          <span>系统管理</span>
        </template>
        <el-menu-item index="/user">
          <el-icon><icon-menu /></el-icon>
          <template #title>
            <span>用户管理</span>
          </template>
        </el-menu-item>
      </el-sub-menu>




    </el-menu>
  </div>
</template>

<script>

import {
  Menu as IconMenu,
  Setting,
} from '@element-plus/icons-vue'
import request from "../utils/request";

export default {
  name: "Aside",
  components: {
    Setting,
    IconMenu,
  },
  data() {
    return {
      user: {},
      path: this.$route.path  // 默认设置高亮的菜单
    }
  },
  created() {
    let userStr = sessionStorage.getItem("user") || "{}"
    this.user = JSON.parse(userStr)

    // 请求服务端，确认当前登录用户的 合法信息
    request.get("/user/" + this.user.id).then(res => {
      if (res.code === '0') {
        this.user = res.data
      }
    })
  }
}
</script>

<style scoped>

</style>