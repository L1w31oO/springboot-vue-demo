<template>
  <div style="height: 50px; line-height: 50px; border-bottom: 1px solid #ccc; display: flex">
    <div style="width: 200px; padding-left: 30px; font-weight: bold; color: teal">后台管理</div>
    <div style="flex: 1"></div>
    <div style="width: 150px">
      <el-dropdown>
        <span class="el-dropdown-link">
          <el-avatar :size="30" :src="user.avatar" style="position: relative; top: 10px"></el-avatar>
          {{ user.nickName }}
          <el-icon class="el-icon--right">
            <arrow-down />
          </el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="person">个人信息</el-dropdown-item>
            <el-dropdown-item @click="logout">退出系统</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script>

import {ArrowDown} from "@element-plus/icons-vue";

export default {
  name: "Header",
  props: ['user'],
  components: {
    ArrowDown
  },
  data() {
    return {

    }
  },
  created() {

  },
  methods: {
    person() {
      this.$router.push('/person')
    },
    logout() {
      this.$router.push('/login')
    }
  }
}

</script>


<style scoped>

</style>