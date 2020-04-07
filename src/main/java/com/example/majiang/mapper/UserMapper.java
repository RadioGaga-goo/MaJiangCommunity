package com.example.majiang.mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

import com.example.majiang.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    User selectByToken(@Param("token")String token);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}