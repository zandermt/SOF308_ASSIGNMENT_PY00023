package DAO;

import Entity.Article;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import Utils.XJPABE;

import java.util.List;

public class ArticleDAO {

    private EntityManager em = XJPABE.getEntityManager();

    // Lấy tất cả các bài viết
    public List<Article> findAll() {
        String jpql = "SELECT a FROM Article a";
        TypedQuery<Article> query = em.createQuery(jpql, Article.class);
        return query.getResultList();
    }

    // Lấy bài viết theo ID
    public Article findById(Long idArticles) {
        return em.find(Article.class, idArticles);
    }

    // Tạo bài viết mới
    public void create(Article article) {
        try {
            em.getTransaction().begin();
            em.persist(article);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    // Cập nhật bài viết
    public void update(Article article) {
        try {
            em.getTransaction().begin();
            em.merge(article);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    // Xóa bài viết theo ID
    public void deleteById(Long idArticles) {
        Article article = em.find(Article.class, idArticles);
        if (article != null) {
            try {
                em.getTransaction().begin();
                em.remove(article);
                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
                e.printStackTrace();
            }
        }
    }

    // Lấy danh sách bài viết trên trang chủ
    public List<Article> findHomeArticles() {
        String jpql = "SELECT a FROM Article a WHERE a.home = true";
        TypedQuery<Article> query = em.createQuery(jpql, Article.class);
        return query.getResultList();
    }

    // Đóng EntityManager
    public void close() {
        if (em.isOpen()) {
            em.close();
        }
    }
}
