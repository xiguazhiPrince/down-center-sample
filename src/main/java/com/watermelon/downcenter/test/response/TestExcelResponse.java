package com.watermelon.downcenter.test.response;

import com.watermelon.domain.utils.ExcelHeader;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author water
 * @date 2023/2/24 16:08
 */
@Data
@Accessors(chain = true)
public class TestExcelResponse {

    @ExcelHeader("姓名")
    private String name;

    @ExcelHeader("年龄")
    private Integer age;

}
