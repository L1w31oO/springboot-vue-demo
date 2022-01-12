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
      <el-table-column prop="title" label="标题" sortable=""/>
      <el-table-column prop="author" label="作者" sortable=""/>
      <el-table-column prop="time" label="时间" sortable=""/>

      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="default" size="small" @click="details(scope.row)">详情</el-button>
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
        width="60%"
    >
      <el-form :model="form" label-width="120px">
        <el-form-item label="标题">
          <el-input v-model="form.title" style="width: 60%"></el-input>
        </el-form-item>
<!--        <el-form-item label="内容">-->
<!--          <el-input v-model="form.content" style="width: 80%"></el-input>-->
<!--        </el-form-item>-->
        <div id="div1" ref="editor"></div>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="save">确 定</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog
        v-model="detailVisible"
        title="详情"
        width="60%"
    >
      <el-card>
        <div v-html="detail.content" style="min-height: 100px"></div>
      </el-card>
    </el-dialog>

  </div>
</template>

<script>

import request from "../utils/request";
import { ElMessage } from 'element-plus'
import E from 'wangeditor'

let editor

export default {
  name: 'News',
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
      detail: {},
      detailVisible: false
    }
  },

  // 页面一加载就调用
  created() {
    this.load()
  },

  // 页面所有元素加载完成之后执行代码
  // mounted() {
  //
  // },

  methods: {
    details(row) {
      this.detailVisible = true
      this.detail = row
    },
    filesUploadSuccess(res) {
      console.log(res)
      this.form.cover = res.data
    },
    // 点击新增打开弹窗的方法
    add() {
      this.dialogVisible = true
      this.form = {}

      this.$nextTick(() => {  // 处理未来元素的方法，解决未来dom不存在的问题
        if (!editor) {
          // 关联弹窗里的div, new一个editor对象
          editor = new E('#div1')
          // 配置 server 接口地址
          editor.config.uploadImgServer = 'http://localhost:9090/files/editor/upload'
          editor.config.uploadFileName = "file"  // 设置上传参数名称（后台MultipartFile file）
          editor.create()
        }
        editor.txt.html()
      })
    },

    // 弹窗中 确 定 按钮的保存方法
    save() {
      this.form.content = editor.txt.html()  // 获取编辑器里面的值，然后赋予到实体中

      if (this.form.id) { // 更新
        request.put("/news", this.form).then(res => {  // .then() es6的语法，链式操作，将前一步的结果放在res里
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

        // 给新增的新闻赋予作者，从session里user的nickName里拿
        let userStr = sessionStorage.getItem("user")  || {}
        let user = JSON.parse(userStr)
        this.form.author = user.nickName

        request.post("/news", this.form).then(res => {
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
      request.get("/news", {
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

      this.$nextTick(() => {  // 处理未来元素的方法，解决未来dom不存在的问题
        if(!editor){
          // 关联弹窗里的div, new一个editor对象
          editor = new E('#div1')
          // 配置 server 接口地址
          editor.config.uploadImgServer = 'http://localhost:9090/files/editor/upload'
          editor.config.uploadFileName = "file"  // 设置上传参数名称（后台MultipartFile file）

          editor.create()
        }
        editor.txt.html(row.content)
      })
    },
    handleDelete(id) {
      console.log(id)
      request.delete("/news/" + id, ).then(res => {
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
