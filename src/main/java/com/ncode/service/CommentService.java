package com.ncode.service;

import com.ncode.dao.CommentDAO;
import com.ncode.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    CommentDAO commentDAO;

    @Autowired
    SensitiveService sensitiveService;

    public Comment getCommentById(int id) {
        return commentDAO.getCommentById(id);
    }

    public int addComment(Comment comment) {
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveService.filter(comment.getContent()));
        return commentDAO.addComment(comment) > 0 ? comment.getId() : 0;
    }

    public List<Comment> selectCommentByEntity(int entityId, int entityType, int offset, int limit) {
        return commentDAO.selectByEntity(entityId, entityType, offset, limit);
    }

    public int getCommentCountByEntity(int entityId, int entityType) {
        return commentDAO.getCommentCountByEntity(entityId, entityType);
    }

    public int getCommentCountByUserId(int userId) {
        return commentDAO.getCommentCountByUserId(userId);
    }
}
