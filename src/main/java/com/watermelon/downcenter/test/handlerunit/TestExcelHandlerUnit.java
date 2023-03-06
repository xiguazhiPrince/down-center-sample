package com.watermelon.downcenter.test.handlerunit;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.PageUtil;
import com.watermelon.domain.task.external.DataSelectorUnit;
import com.watermelon.downcenter.test.response.TestExcelResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author water
 * @date 2023/2/24 1:03
 */
public class TestExcelHandlerUnit extends DataSelectorUnit<TestExcelResponse> {

    private final static List<TestExcelResponse> data = new ArrayList<>();

    static {
        for (int i = 0; i < 10; i++) {
            data.add(new TestExcelResponse().setAge(i).setName("xxx"+i));
        }
    }

    @Override
    public String handlerName() {
        return "test2";
    }

    @Override
    public List<TestExcelResponse> page(String requestJson, Integer pageIndex, Integer pageSize) {
        PageUtil.setFirstPageNo(1);
        return ListUtil.page(pageIndex, pageSize, data);
    }

    @Override
    public int count(String requestJson) {
        return data.size();
    }


    @Override
    public Class<TestExcelResponse> excelModel() {
        return TestExcelResponse.class;
    }
}
