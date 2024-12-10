package Utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class XJPABE {
    private static EntityManagerFactory factory;

    static {
        try {
        	factory = Persistence.createEntityManagerFactory("sof308be");
        	factory = Persistence.createEntityManagerFactory("sof308be");
        } catch (Exception e) {
            e.printStackTrace();
          
            throw new ExceptionInInitializerError("EntityManagerFactory initialization failed.");
        }
    }

    public static EntityManager getEntityManager() {
        if (factory == null) {
            throw new IllegalStateException("EntityManagerFactory is not initialized.");
        }
        return factory.createEntityManager();
    }

    public static void close() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }
}

