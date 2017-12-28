package com.ncode.dao;

import com.ncode.model.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentDAO {
    String TABLE_NAME = "comment";
    String INSERT_FIELD = "content, user_id, created_date, entity_id, entity_type";
    String SELECT_FIELD = "id, " + INSERT_FIELD;

    @Select({"select", SELECT_FIELD, " from ", TABLE_NAME,
            " where entity_id = #{entityId} and entity_type = #{entityType}",
            " order by id desc limit #{offset}, #{limit}"})
    List<Comment> selectByEntity(@Param("entityId") int entityId,
                                 @Param("entityType") String entityType,
                                 @Param("offset") int offset,
                                 @Param("limit") int limit);

    @Insert({"insert into ", TABLE_NAME, " ( ", INSERT_FIELD, " ) ",
            " values( #{content}, #{userId}, #{createdDate}, #{entityId}, #{entityType} )"})
    void addComment(Comment comment);
}
