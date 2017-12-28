package com.ncode.dao;

import com.ncode.model.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageDAO {
    String TABLE_NAME = " message ";
    String INSERT_FIELD = " from_id, to_id, content, conversation_id, created_date ";
    String SELECT_FIELD = " id, " + INSERT_FIELD;

    @Select({"select ", SELECT_FIELD, " from ", TABLE_NAME,
            " where to_id = #{toId} order by id desc limit #{offset}, #{limit}"})
    List<Message> getMessageByToId(@Param("toId") int toId,
                                   @Param("offset") int offset,
                                   @Param("limit") int limit);

    @Insert({"insert into ", TABLE_NAME, " ( ", INSERT_FIELD, " ) ",
            "values(#{fromId}, #{toId}, #{content}, #{conversationId}, #{createdDate})"})
    void addMessage(Message message);
}
