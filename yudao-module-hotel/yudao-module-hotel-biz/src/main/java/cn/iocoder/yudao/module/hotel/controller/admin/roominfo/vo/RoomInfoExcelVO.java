package cn.iocoder.yudao.module.hotel.controller.admin.roominfo.vo;

import lombok.*;
import java.util.*;
import io.swagger.annotations.*;

import com.alibaba.excel.annotation.ExcelProperty;

/**
 * 房间信息 Excel VO
 *
 * @author 芋道源码
 */
@Data
public class RoomInfoExcelVO {

    @ExcelProperty("id")
    private Long id;

    @ExcelProperty("房间号")
    private String no;

    @ExcelProperty("房间类型表")
    private Integer roomType;

    @ExcelProperty("楼层")
    private Integer floor;

    @ExcelProperty("状态")
    private Integer statusId;

    @ExcelProperty("房间钥匙信息")
    private String keyInfo;

    @ExcelProperty("创建时间")
    private Date createTime;

}
