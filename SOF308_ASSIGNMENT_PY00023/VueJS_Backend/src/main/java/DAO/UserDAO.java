package DAO;

import Entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import Utils.XJPABE;

public class UserDAO {
    private EntityManager em = XJPABE.getEntityManager();

    // Tìm User theo userName và pass (phục vụ đăng nhập)
    public User login(String userName, String pass) {
        String jpql = "SELECT u FROM User u WHERE u.userName = :userName AND u.pass = :pass";
        TypedQuery<User> query = em.createQuery(jpql, User.class);
        query.setParameter("userName", userName);
        query.setParameter("pass", pass);
        return query.getResultStream().findFirst().orElse(null); // Trả về User hoặc null nếu không tìm thấy
    }

    // Tạo mới User (phục vụ đăng ký)
    public void create(User user) {
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    // Tìm User theo userName (khóa chính)
    public User findById(String userName) {
        return em.find(User.class, userName);
    }

    // Cập nhật thông tin User (phục vụ đổi thông tin cá nhân)
    public void update(User user) {
        try {
            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    // Đóng EntityManager
    public void close() {
        if (em.isOpen()) {
            em.close();
        }
    }
}
