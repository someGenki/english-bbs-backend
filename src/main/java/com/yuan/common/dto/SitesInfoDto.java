package com.yuan.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@ApiModel("网站相关信息")
@Data
@Accessors(chain = true)
public class SitesInfoDto {
    @ApiModelProperty("签到总数排行榜")
    List<SignInfoDto> allSignRank;
    @ApiModelProperty("连续签到排行榜")
    List<SignInfoDto> nowSignRank;
    @ApiModelProperty("今日签到人数")
    Long dailyCount;
    @ApiModelProperty("今月签到人数")
    Long monthlyCount;
}
