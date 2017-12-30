package com.ncode.dao;

import com.ncode.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageDAO {
    String TABLE_NAME = " message ";
    String INSERT_FIELD = " from_id, to_id, content, conversation_id, created_date, has_read ";
    String SELECT_FIELD = " id, " + INSERT_FIELD;

    @Update({"update ", TABLE_NAME, " set has_read=#{status} where conversation_id=#{conversationId} and to_id=#{toId} "})
    int updateHasReadByToId(@Param("status") int status, @Param("conversationId") String conversationId,
                            @Param("toId") int toId);

    @Select({"select count(id) from ", TABLE_NAME,
            " where conversation_id=#{conversationId} and from_id!=#{userId} and has_read=0"})
    int selectCountUnreadByConversationIdUserId(@Param("conversationId") String conversationId,
                                                @Param("userId") int userId);

    @Select({"select ", SELECT_FIELD, " from ", TABLE_NAME, " where conversation_id=#{conversationId} ",
            " order by created_date desc limit #{offset}, #{limit}"})
    List<Message> selectMessageByConversationId(@Param("conversationId") String conversationId,
                                                @Param("offset") int offset,
                                                @Param("limit") int limit);

    @Select({"select from_id, to_id, content, tt.conversation_id, created_date, has_read, tt.id",
            "from message, (select count(id) as id, conversation_id, max(created_date) as maxDate from message group by conversation_id) tt",
            "where message.created_date = tt.maxDate and message.conversation_id = tt.conversation_id and (from_id = #{userId} or to_id = #{userId})",
            "order by created_date desc limit #{offset}, #{limit}"})
//    @Select({"select ", INSERT_FIELD, ", count(id) as id from ",
//            "(select * from message where from_id=#{userId} or to_id=#{userId} order by created_date desc ) tt ",
//            "group by conversation_id order by created_date desc limit #{offset}, #{limit}"})
    List<Message> selectMessageByUserId(@Param("userId") int userId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    @Insert({"insert into ", TABLE_NAME, " ( ", INSERT_FIELD, " ) ",
            "values(#{fromId}, #{toId}, #{content}, #{conversationId}, #{createdDate}, #{hasRead})"})
    int addMessage(Message message);
}
