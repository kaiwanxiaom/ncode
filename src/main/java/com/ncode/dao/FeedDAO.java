package com.ncode.dao;

import com.ncode.model.Feed;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FeedDAO {
    String TABLE_NAME = " feed ";
    String INSERT_FIELD = " type, user_id, created_date, content ";
    String SELECT_FIELD = " id, " + INSERT_FIELD;

    @Select({"select", SELECT_FIELD, " from ", TABLE_NAME, " where id=#{id}"})
    Feed selectById(int id);

    @Select({"select", SELECT_FIELD, " from ", TABLE_NAME, " where user_id=#{userId}"})
    List<Feed> selectByUserId(int userId);

    List<Feed> selectUsersFeed(@Param("offset") int offset,
                               @Param("userIds") List<Integer> userIds,
                               @Param("limit") int limit);

    @Insert({"insert into ", TABLE_NAME, " ( ", INSERT_FIELD, " ) ",
            " values( #{type}, #{userId}, #{createdDate}, #{content} ) "})
    int insertFeed(Feed feed);

}
