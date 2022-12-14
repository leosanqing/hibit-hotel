package cn.iocoder.yudao.module.hotel.controller.admin.building;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.annotations.*;

import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.hotel.controller.admin.building.vo.*;
import cn.iocoder.yudao.module.hotel.dal.dataobject.building.BuildingDO;
import cn.iocoder.yudao.module.hotel.convert.building.BuildingConvert;
import cn.iocoder.yudao.module.hotel.service.building.BuildingService;

@Api(tags = "管理后台 - 公司分部")
@RestController
@RequestMapping("/hotel/building")
@Validated
public class BuildingController {

    @Resource
    private BuildingService buildingService;

    @PostMapping("/create")
    @ApiOperation("创建公司分部")
    @PreAuthorize("@ss.hasPermission('hotel:building:create')")
    public CommonResult<Long> createBuilding(@Valid @RequestBody BuildingCreateReqVO createReqVO) {
        return success(buildingService.createBuilding(createReqVO));
    }

    @PutMapping("/update")
    @ApiOperation("更新公司分部")
    @PreAuthorize("@ss.hasPermission('hotel:building:update')")
    public CommonResult<Boolean> updateBuilding(@Valid @RequestBody BuildingUpdateReqVO updateReqVO) {
        buildingService.updateBuilding(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除公司分部")
    @ApiImplicitParam(name = "id", value = "编号", required = true, dataTypeClass = Long.class)
    @PreAuthorize("@ss.hasPermission('hotel:building:delete')")
    public CommonResult<Boolean> deleteBuilding(@RequestParam("id") Long id) {
        buildingService.deleteBuilding(id);
        return success(true);
    }

    @GetMapping("/get")
    @ApiOperation("获得公司分部")
    @ApiImplicitParam(name = "id", value = "编号", required = true, example = "1024", dataTypeClass = Long.class)
    @PreAuthorize("@ss.hasPermission('hotel:building:query')")
    public CommonResult<BuildingRespVO> getBuilding(@RequestParam("id") Long id) {
        BuildingDO building = buildingService.getBuilding(id);
        return success(BuildingConvert.INSTANCE.convert(building));
    }

    @GetMapping("/list")
    @ApiOperation("获得公司分部列表")
    @ApiImplicitParam(name = "ids", value = "编号列表", required = true, example = "1024,2048", dataTypeClass = List.class)
    @PreAuthorize("@ss.hasPermission('hotel:building:query')")
    public CommonResult<List<BuildingRespVO>> getBuildingList(@RequestParam("ids") Collection<Long> ids) {
        List<BuildingDO> list = buildingService.getBuildingList(ids);
        return success(BuildingConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @ApiOperation("获得公司分部分页")
    @PreAuthorize("@ss.hasPermission('hotel:building:query')")
    public CommonResult<PageResult<BuildingRespVO>> getBuildingPage(@Valid BuildingPageReqVO pageVO) {
        PageResult<BuildingDO> pageResult = buildingService.getBuildingPage(pageVO);
        return success(BuildingConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/export-excel")
    @ApiOperation("导出公司分部 Excel")
    @PreAuthorize("@ss.hasPermission('hotel:building:export')")
    @OperateLog(type = EXPORT)
    public void exportBuildingExcel(@Valid BuildingExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<BuildingDO> list = buildingService.getBuildingList(exportReqVO);
        // 导出 Excel
        List<BuildingExcelVO> datas = BuildingConvert.INSTANCE.convertList02(list);
        ExcelUtils.write(response, "公司分部.xls", "数据", BuildingExcelVO.class, datas);
    }

}
