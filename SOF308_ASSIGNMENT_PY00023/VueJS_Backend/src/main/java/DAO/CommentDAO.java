package DAO;

import Entity.Comment;
import Entity.Article;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import Utils.XJPABE;

import java.time.LocalDateTime;
import java.util.List;

public class CommentDAO {
    
    private EntityManager em = XJPABE.getEntityManager();

    // Lấy tất cả các comment của một bài viết cụ thể
    public List<Comment> findCommentsByArticle(Long articleId) {
        String jpql = "SELECT c FROM Comment c WHERE c.article.idArticles = :articleId ORDER BY c.dateCmt DESC";
        TypedQuery<Comment> query = em.createQuery(jpql, Comment.class);
        query.setParameter("articleId", articleId);
        return query.getResultList();
    }

    // Tạo comment mới
    public void createComment(Comment comment) {
        try {
            // Đảm bảo ngày comment được set là thời gian hiện tại
            comment.setDateCmt(LocalDateTime.now());
            
            em.getTransaction().begin();
            em.persist(comment);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    // Xóa comment theo ID
    public void deleteCommentById(Long commentId) {
        Comment comment = em.find(Comment.class, commentId);
        if (comment != null) {
            try {
                em.getTransaction().begin();
                em.remove(comment);
                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
                e.printStackTrace();
            }
        }
    }

    // Đếm số lượng comment của một bài viết
    public long countCommentsByArticle(Long articleId) {
        String jpql = "SELECT COUNT(c) FROM Comment c WHERE c.article.idArticles = :articleId";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        query.setParameter("articleId", articleId);
        return query.getSingleResult();
    }

    // Đóng EntityManager
    public void close() {
        if (em.isOpen()) {
            em.close();
        }
    }
}