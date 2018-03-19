package com.ncode.service;

import com.ncode.dao.QuestionDAO;
import com.ncode.model.HostHolder;
import com.ncode.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    HostHolder hostHolder;

    @Autowired
    QuestionDAO questionDAO;

    @Autowired
    SensitiveService sensitiveService;

    public Question getQuestionById(int qid) {
        return questionDAO.selectById(qid);
    }

    public int addQuestion(Question question) {
        // 过滤脚本
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));

        // 过滤敏感词
        question.setContent(sensitiveService.filter(question.getContent()));
        question.setTitle(sensitiveService.filter(question.getTitle()));
        return questionDAO.addQuestion(question);
    }

    public  List<Question> getLastestQuestionsByTag(int userId, int offset, int limit, String tag) {
        return questionDAO.selectLatestQuestionsByTag(userId, offset, limit , tag);
    }

    public List<Question> getLastestQuestions(int userId, int offset, int limit) {
        return questionDAO.selectLatestQuestions(userId, offset, limit);
    }

    public int updateCommentCount(int count, int id) {
        return questionDAO.updateCommentCount(count ,id);
    }
}
