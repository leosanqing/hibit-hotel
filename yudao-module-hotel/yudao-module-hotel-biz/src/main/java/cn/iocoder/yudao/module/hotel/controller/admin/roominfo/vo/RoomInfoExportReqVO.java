package cn.iocoder.yudao.module.hotel.controller.admin.roominfo.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@ApiModel(value = "管理后台 - 房间信息 Excel 导出 Request VO", description = "参数和 RoomInfoPageReqVO 是一致的")
@Data
public class RoomInfoExportReqVO {

    @ApiModelProperty(value = "房间号")
    private String no;

    @ApiModelProperty(value = "房间类型表")
    private Integer roomType;

    @ApiModelProperty(value = "楼层")
    private Integer floor;

    @ApiModelProperty(value = "状态")
    private Integer statusId;

    @ApiModelProperty(value = "房间钥匙信息")
    private String keyInfo;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private Date[] createTime;

}
