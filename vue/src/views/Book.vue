<template>
  <div style="padding: 10px">
    <!-- 功能区域 -->
    <div style="margin: 10px 0">
      <el-button type="primary" @click="add" v-if="user.role === 1">新增</el-button>
      <el-popconfirm
          title="确定删除吗？"
          @confirm="deleteBatch"
          v-if="user.role === 1"
      >
        <template #reference>
          <el-button type="danger">批量删除</el-button>
        </template>
      </el-popconfirm>
    </div>
    <!-- 搜索区域 -->
    <div style="margin: 10px 0">
      <el-input v-model="search" placeholder="请输入关键字..." style="width: 20%" clearable/>
      <el-button type="primary" style="margin-left: 5px" @click="load">查询</el-button>
    </div>
    <el-table
        v-loading="loading"
        :data="tableData"
        border
        style="width: 100%"
        stripe
        @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55"/>
      <el-table-column prop="id" label="ID" sortable=""/>
      <el-table-column prop="name" label="书名" sortable=""/>
      <el-table-column prop="price" label="价格" sortable=""/>
      <el-table-column prop="author" label="作者" sortable=""/>
      <el-table-column prop="createTime" label="出版时间" sortable=""/>
      <el-table-column prop="cover" label="封面">
        <template #default="scope">
          <el-image
              style="width: 100px; height: 100px"
              :src="scope.row.cover"
              :preview-src-list="[scope.row.cover]"
          >
          </el-image>
        </template>
      </el-table-column>

      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="default" size="small" @click="handleEdit(scope.row)" v-if="user.role === 1">编辑</el-button>
          <el-popconfirm title="确定删除吗？" @confirm="handleDelete(scope.row.id)" v-if="user.role === 1">
            <template #reference>
              <el-button type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <div style="margin: 10px 0">
      <el-pagination
          v-model:currentPage="currentPage"
          :page-sizes="[5, 10, 20, 30]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      >
      </el-pagination>
    </div>

    <el-dialog
        v-model="dialogVisible"
        title="提示"
        width="30%"
    >
      <el-form :model="form" label-width="120px">
        <el-form-item label="书名">
          <el-input v-model="form.name" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="价格">
          <el-input v-model="form.price" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="form.author" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="出版时间">
          <el-date-picker value-format="YYYY-MM-DD" type="date" v-model="form.createTime"  style="width: 80%" clearable></el-date-picker>
        </el-form-item>
        <el-form-item label="封面">
          <el-upload
              ref="upload"
              :action="filesUploadUrl"
              :on-success="filesUploadSuccess"
          >
            <el-button type="primary">点击上传</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="save">确 定</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script>

import request from "../utils/request";
import { ElMessage } from 'element-plus'

export default {
  name: 'Book',
  components: {

  },

  data() {
    return {
      form: {},
      dialogVisible: false,
      search: '',
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 100,
      user: {},
      loading: true,
      filesUploadUrl: 'http://' + window.server.filesUploadUrl + ':9090/files/upload',
      ids: []
    }
  },

  // 页面一加载就调用
  created() {
    let userStr = sessionStorage.getItem("user") || "{}"
    this.user = JSON.parse(userStr)

    // 请求服务端，确认当前登录用户的 合法信息
    request.get("/user/" + this.user.id).then(res => {
      if (res.code === '0') {
        this.user = res.data
      }
    })

    this.load()
  },

  methods: {

    deleteBatch() {
      if (!this.ids.length) {
        ElMessage({
          type: "warning",
          message: "请选择数据！"
        })
        return
      }
      request.post("/book/deleteBatch", this.ids).then(res => {
        if (res.code === '0') {
          ElMessage({
            type: "success",
            message: "批量删除成功"
          })
          this.load()
        } else {
          ElMessage({
            type: "error",
            message: res.msg
          })
        }
      })
    },

    handleSelectionChange(val) {
      this.ids = val.map(v => v.id)  // [{id,name}, {id,name}] => [id,id]
    },

    filesUploadSuccess(res) {
      console.log(res)
      this.form.cover = res.data
    },
    // 点击新增打开弹窗的方法
    add() {
      this.dialogVisible = true
      this.form = {}
      if (this.$refs['upload']) {
        this.$refs['upload'].clearFiles()  // 点击时清除上次上传的文件
      }
    },

    // 弹窗中 确 定 按钮的保存方法
    save() {
      if (this.form.id) { // 更新
        request.put("/book", this.form).then(res => {  // .then() es6的语法，链式操作，将前一步的结果放在res里
          console.log(res)
          if (res.code === '0') {
            ElMessage({
              type: "success",
              message: "更新成功"
            })
          } else {
            ElMessage({
              type: "error",
              message: res.msg
            })
          }
          this.load()  // 刷新表格数据
          this.dialogVisible = false  // 关闭弹窗
        })
      } else {  // 新增
        request.post("/book", this.form).then(res => {
          console.log(res)
          if (res.code === '0') {
            ElMessage({
              type: "success",
              message: "新增成功"
            })
          } else {
            ElMessage({
              type: "error",
              message: res.msg
            })
          }
          this.load()  // 刷新表格数据
          this.dialogVisible = false  // 关闭弹窗
        })
      }
    },

    // 加载数据的方法
    load() {
      this.loading = true
      request.get("/book", {
        params: {
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          search: this.search
        }
      }).then(res => {
        // console.log(res)
        this.loading = false
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },

    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))  // 深拷贝
      this.dialogVisible = true
      this.$nextTick(() => {  // 处理未来元素的方法，解决未来dom不存在的问题
        if (this.$refs['upload']) {
          this.$refs['upload'].clearFiles()  // 点击时清除上次上传的文件
        }
      })
    },
    handleDelete(id) {
      console.log(id)
      request.delete("/book/" + id, ).then(res => {
        if (res.code === '0') {
          ElMessage({
            type: "success",
            message: "删除成功"
          })
        } else {
          ElMessage({
            type: "error",
            message: res.msg
          })
        }
        this.load()  // 刷新表格数据
      })
    },

    handleSizeChange(pageSize) {  // 改变当前每页的个数触发
      this.pageSize = pageSize
      this.load()
    },

    handleCurrentChange(pageNum) {  // 改变当前页码触发
      this.pageNum = pageNum
      this.load()
    }
  }
}

</script>
