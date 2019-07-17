package com.example.client.dao;

import com.example.client.entity.TestGoodsModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TestGoodsDao {

    List<TestGoodsModel> getList();

    TestGoodsModel getById(Integer id);

    int updateById(TestGoodsModel model);

}
