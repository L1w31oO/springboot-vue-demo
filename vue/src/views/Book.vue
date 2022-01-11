<template>
  <div style="padding: 10px">
    <!-- 功能区域 -->
    <div style="margin: 10px 0">
      <el-button type="primary" @click="add">新增</el-button>
      <el-button type="primary">导入</el-button>
      <el-button type="primary">导出</el-button>
    </div>
    <!-- 搜索区域 -->
    <div style="margin: 10px 0">
      <el-input v-model="search" placeholder="请输入关键字..." style="width: 20%" clearable/>
      <el-button type="primary" style="margin-left: 5px" @click="load">查询</el-button>
    </div>
    <el-table
        :data="tableData"
        border
        style="width: 100%"
        stripe
    >
      <el-table-column prop="id" label="ID" sortable=""/>
      <el-table-column prop="name" label="书名" sortable=""/>
      <el-table-column prop="price" label="价格" sortable=""/>
      <el-table-column prop="author" label="作者" sortable=""/>
      <el-table-column prop="createTime" label="出版时间" sortable=""/>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="default" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-popconfirm title="确定删除吗？" @confirm="handleDelete(scope.row.id)">
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
    }
  },

  // 页面一加载就调用
  created() {
    this.load()
  },

  methods: {

    // 点击新增打开弹窗的方法
    add() {
      this.dialogVisible = true
      this.form = {}
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
      request.get("/book", {
        params: {
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          search: this.search
        }
      }).then(res => {
        console.log(res)
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },

    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))  // 深拷贝
      this.dialogVisible = true
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