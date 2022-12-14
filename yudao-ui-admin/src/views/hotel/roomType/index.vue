<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="最大入住人数" prop="livingPopulation">
        <el-input v-model="queryParams.livingPopulation" placeholder="请输入最大入住人数" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="name" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入name" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="房间总量" prop="totalRoom">
        <el-input v-model="queryParams.totalRoom" placeholder="请输入房间总量" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
                   v-hasPermi="['hotel:room-type:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['hotel:room-type:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="id" align="center" prop="id" />
      <el-table-column label="name" align="center" prop="name" />
      <el-table-column label="面积" align="center" prop="area" />
      <el-table-column label="最大入住人数" align="center" prop="livingPopulation" />
      <el-table-column label="价格" align="center" prop="price" />
      <el-table-column label="描述" align="center" prop="desc" />
      <el-table-column label="图片" align="center" prop="imgs" />
      <el-table-column label="房间总量" align="center" prop="totalRoom" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['hotel:room-type:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['hotel:room-type:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="500px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="name" prop="name">
          <el-input v-model="form.name" placeholder="请输入name" />
        </el-form-item>
        <el-form-item label="房间总量" prop="totalRoom">
          <el-input v-model="form.totalRoom" placeholder="请输入房间总量" />
        </el-form-item>
        <el-form-item label="面积" prop="area">
          <el-input v-model="form.area" placeholder="请输入面积" />
        </el-form-item>
        <el-form-item label="最大入住人数" prop="livingPopulation">
          <el-input v-model="form.livingPopulation" placeholder="请输入最大入住人数" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input v-model="form.price" placeholder="请输入价格" />
        </el-form-item>
        <el-form-item label="描述" prop="desc">
          <el-input v-model="form.desc" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="图片" prop="imgs">
          <el-input v-model="form.imgs" placeholder="请输入图片" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createRoomType, updateRoomType, deleteRoomType, getRoomType, getRoomTypePage, exportRoomTypeExcel } from "@/api/hotel/roomType";

export default {
  name: "RoomType",
  components: {
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 房间类型列表
      list: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        livingPopulation: null,
        name: null,
        totalRoom: null,
        createTime: [],
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        area: [{ required: true, message: "面积不能为空", trigger: "blur" }],
        livingPopulation: [{ required: true, message: "最大入住人数不能为空", trigger: "blur" }],
        name: [{ required: true, message: "name不能为空", trigger: "blur" }],
        price: [{ required: true, message: "价格不能为空", trigger: "blur" }],
        totalRoom: [{ required: true, message: "房间总量不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getRoomTypePage(this.queryParams).then(response => {
        this.list = response.data.list;
        this.total = response.data.total;
        this.loading = false;
      });
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    },
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        area: undefined,
        livingPopulation: undefined,
        name: undefined,
        price: undefined,
        desc: undefined,
        imgs: undefined,
        totalRoom: undefined,
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNo = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加房间类型";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getRoomType(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改房间类型";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) {
          return;
        }
        // 修改的提交
        if (this.form.id != null) {
          updateRoomType(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createRoomType(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除房间类型编号为"' + id + '"的数据项?').then(function() {
          return deleteRoomType(id);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      // 处理查询参数
      let params = {...this.queryParams};
      params.pageNo = undefined;
      params.pageSize = undefined;
      this.$modal.confirm('是否确认导出所有房间类型数据项?').then(() => {
          this.exportLoading = true;
          return exportRoomTypeExcel(params);
        }).then(response => {
          this.$download.excel(response, '房间类型.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
