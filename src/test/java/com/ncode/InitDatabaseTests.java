package com.ncode;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.ncode.dao.CommentDAO;
import com.ncode.dao.MessageDAO;
import com.ncode.dao.QuestionDAO;
import com.ncode.dao.UserDAO;
import com.ncode.model.Comment;
import com.ncode.model.Message;
import com.ncode.model.Question;
import com.ncode.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DiscussApplication.class)
@Sql("/init-schema.sql")
public class InitDatabaseTests {

	@Autowired
    UserDAO userDAO;

	@Autowired
    QuestionDAO questionDAO;

	@Autowired
    MessageDAO messageDAO;

	@Autowired
    CommentDAO commentDAO;

	@Test
	public void initDatabase() {
		Random random = new Random();

		for(int i = 0; i < 10; i++) {
			User user = new User();
			user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
			user.setName(String.format("USER%d", i+1));
			user.setPassword(String.valueOf(i));
			user.setSalt("XX");

			user.setPassword("xxx");
			userDAO.updatePassword(user);

			userDAO.addUser(user);

            Question question = new Question();
            question.setCommentCount(i+10);
            question.setContent("nnnnnnnnnnnnnnnsjdfewf");
            Date date = new Date();
            date.setTime(date.getTime()+1000*3600*i);
            question.setCreatedDate(date);
            question.setTitle(String.format("TITLE%d", i+1));
            question.setUserId(i+1);

            questionDAO.addQuestion(question);
            questionDAO.selectById(i+1);

            Message message = new Message();
            message.setContent("message content!!!");
            message.setConversationId(i+1);
            message.setFromId(i+1);
            message.setToId(10-i);
            message.setCreatedDate(date);

            messageDAO.addMessage(message);

            Comment comment = new Comment();
            comment.setContent("Comment Content....");
            comment.setCreatedDate(date);
            comment.setEntityId(i+1);
            comment.setEntityType("question");
            comment.setUserId(10-i);

            commentDAO.addComment(comment);
		}

        Assert.assertEquals("xxx", userDAO.selectById(1).getPassword());
//		userDAO.deleteById(1);
//		Assert.assertNull(userDAO.selectById(1));

        System.out.println(questionDAO.selectLatestQuestions(0, 0, 10));

        System.out.println(messageDAO.getMessageByToId(5, 0, 10));

        System.out.println(commentDAO.selectByEntity(2, "question", 0, 10));
    }
}
