package com.ncode.dao;

import com.ncode.model.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionDAO {
    String TABLE_NAME = " question ";
    String INSERT_FIELDS = " title, content, user_id, created_date, comment_count, tag";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ")",
            " values( ", "#{title}, #{content}, #{userId}, #{createdDate}, #{commentCount}, #{tag})"})
    int addQuestion(Question question);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id = #{id}"})
    Question selectById(int id);

    List<Question> selectLatestQuestionsByTag(@Param("userId") int userId,
                                              @Param("offset") int offset,
                                              @Param("limit") int limit,
                                              @Param("tag") String tag);

    List<Question> selectLatestQuestions(@Param("userId") int userId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    @Update({"update ", TABLE_NAME, " set comment_count = #{commentCount} where id=#{id}"})
    int updateCommentCount(@Param("commentCount") int commentCount, @Param("id") int id);
}
