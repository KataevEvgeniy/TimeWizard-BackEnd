package timeWizard.DAOLayer;

import java.sql.SQLDataException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public class MainDAO {
	public static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("persistence");//name use from persistence.xml
	
	public static int num = 0;
    
    public static String create(Object obj) throws SQLDataException{
    	EntityManager em = entityManagerFactory.createEntityManager();
    	em.getTransaction().begin();
    	em.persist(obj);
    	try {
    	em.getTransaction().commit();
    	} catch(Exception e) {
    		throw new SQLDataException(obj.getClass() + " already exists in the database");
    	}
    	em.close();
    	return "success";
    }
    
    public static Object read(Class<?> entityClass, String id) {
    	EntityManager em = entityManagerFactory.createEntityManager();
    	Object obj = em.find(entityClass, id);
    	em.close();
    	return obj;
    }
    
    public static Object read(Class<?> entityClass, long id) {
    	EntityManager em = entityManagerFactory.createEntityManager();
    	Object obj = em.find(entityClass, id);
    	em.close();
    	return obj;
    }
    
    public static long getMax(Class<?> cl, String row) {
    	EntityManager em = entityManagerFactory.createEntityManager();
    	long max;
    	try {
    		max = (long) em.createQuery("SELECT MAX(e." + row + ") FROM " + cl.getName() + " e").getSingleResult();
    	} catch(NullPointerException e) {
    		max = 0;
    	}
    	em.close();
    	return max;
    }
    
    public static List<?> readAll(Class<?> cl, String email){
    	EntityManager em = entityManagerFactory.createEntityManager();
    	List<?> list = em.createQuery("SELECT e FROM " + cl.getName() + " e where e.email = '" + email +"'",cl).getResultList();
    	em.close();
    	return list;
    }
    
    public static List<?> readAll(Class<?> cl) {
    	EntityManager em = entityManagerFactory.createEntityManager();
    	List<?> list = em.createQuery("SELECT e FROM " + cl.getName() + " e",cl).getResultList();
    	em.close();
    	return list;
    }
    
    public static String update(Object obj) throws SQLDataException{
    	EntityManager em = entityManagerFactory.createEntityManager();
    	em.getTransaction().begin();
    	em.merge(obj);
    	em.getTransaction().commit();
    	em.close();
    	return "success";
    }
    
    public static String delete(Object obj) throws SQLDataException {
    	EntityManager em = entityManagerFactory.createEntityManager();
    	em.getTransaction().begin();
    	em.remove(em.merge(obj));
    	em.getTransaction().commit();
    	em.close();
    	return "success";
    }
}