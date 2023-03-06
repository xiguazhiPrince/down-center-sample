package com.watermelon.downcenter.config;

/**
 * @author water
 * @date 2023/2/24 0:53
 */
//@Configuration
//@ConfigurationProperties(prefix = "down-center", ignoreUnknownFields = true)
public class DownSettingConfig {

    /**
     * 临时文件的路径
     */
    private String tempFilePath = "/temp/";

    /**
     * excel文件后缀
     */
    private String tempExcelSuffix = ".xlsx";

    /**
     * 每批数据的条数
     */
    private Integer pageSize = 5000;

    /**
     * 绑定任务失效的时间，分钟
     */
    private Integer bindTimeoutMinutes = 6;

    public String getTempFilePath() {
        return tempFilePath;
    }

    public void setTempFilePath(String tempFilePath) {
        this.tempFilePath = tempFilePath;
    }

    public String getTempExcelSuffix() {
        return tempExcelSuffix;
    }

    public void setTempExcelSuffix(String tempExcelSuffix) {
        this.tempExcelSuffix = tempExcelSuffix;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getBindTimeoutMinutes() {
        return bindTimeoutMinutes;
    }

    public void setBindTimeoutMinutes(Integer bindTimeoutMinutes) {
        this.bindTimeoutMinutes = bindTimeoutMinutes;
    }

}
