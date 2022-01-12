<template>
  <div>
    <el-menu
        style="width: 200px; height: calc(100vh - 50px)"
        :default-active="path"
        router
        :default-openeds="['1']"
        class="el-menu-vertical-demo"
    >
      <el-sub-menu index="1">
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

    </el-menu>
  </div>
</template>

<script>

import {
  Menu as IconMenu,
  Setting,
} from '@element-plus/icons-vue'

export default {
  name: "Aside",
  components: {
    Setting,
    IconMenu,
  },
  data() {
    return {
      path: this.$route.path  // 默认设置高亮的菜单
    }
  }
}
</script>

<style scoped>

</style>