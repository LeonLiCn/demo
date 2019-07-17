package com.example.client.dao;

import com.example.client.entity.TestGoodsModel;
import com.example.client.entity.TestOrderModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TestOrderDao {

    List<TestOrderModel> getList();

    int saveData(TestOrderModel model);


}
