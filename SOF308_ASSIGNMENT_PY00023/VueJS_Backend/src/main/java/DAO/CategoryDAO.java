package DAO;

import Entity.Category;
import Entity.Article;
import Utils.XJPABE;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class CategoryDAO {
    private EntityManager em = XJPABE.getEntityManager();
    
    // Find category by ID (changing to Long to match Category class)
    public Category findById(Long id) {
        return em.find(Category.class, id);
    }
    
    // Find category by name
    public Category findByName(String name) {
        try {
            TypedQuery<Category> query = em.createQuery(
                "SELECT c FROM Category c WHERE c.names = :name", 
                Category.class
            );
            query.setParameter("name", name);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    
    // Find all categories
    public List<Category> findAll() {
        TypedQuery<Category> query = em.createQuery(
            "SELECT c FROM Category c", 
            Category.class
        );
        return query.getResultList();
    }
    
//    public List<Article> findArticlesByCategory(Long categoryId) {
//        try {
//            TypedQuery<Article> query = em.createQuery(
//                "SELECT a FROM Article a WHERE a.category.id = :categoryId", 
//                Article.class
//            );
//            query.setParameter("categoryId", categoryId);
//            return query.getResultList();
//        } catch (Exception e) {
//        	 e.printStackTrace(); // Log lỗi để kiểm tra vấn đề
//             throw new RuntimeException("Error fetching articles for category ID " + categoryId, e);
//        }
//    }
    
    public List<Article> findArticlesByCategory(Long categoryId) {
        EntityTransaction transaction = null;
        EntityManager em = null;
        try {
            em = XJPABE.getEntityManager();
            transaction = em.getTransaction();
            transaction.begin();

            TypedQuery<Article> query = em.createQuery(
                "SELECT a FROM Article a WHERE a.category.id = :categoryId", 
                Article.class
            );
            query.setParameter("categoryId", categoryId);
            List<Article> articles = query.getResultList();

            transaction.commit();
            return articles;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Error fetching articles for category ID " + categoryId, e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    // Create a new category
    public Category create(Category category) {
        try {
            em.getTransaction().begin();
            em.persist(category);
            em.getTransaction().commit();
            return category;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error creating category", e);
        }
    }
    
    // Update an existing category
    public Category update(Category category) {
        try {
            em.getTransaction().begin();
            Category updatedCategory = em.merge(category);
            em.getTransaction().commit();
            return updatedCategory;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error updating category", e);
        }
    }
    
    // Delete a category by ID
    public void deleteById(Long id) {
        try {
            em.getTransaction().begin();
            Category category = em.find(Category.class, id);
            if (category != null) {
                em.remove(category);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error deleting category", e);
        }
    }
    
    // Close EntityManager
    public void close() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
}