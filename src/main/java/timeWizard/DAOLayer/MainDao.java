package timeWizard.DAOLayer;

import java.sql.SQLDataException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public class MainDao implements Dao{
	public final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("persistence");//name use from persistence.xml
    
    public String create(Object obj) throws SQLDataException{
    	EntityManager em = entityManagerFactory.createEntityManager();
    	em.getTransaction().begin();
    	em.persist(obj);
    	em.getTransaction().commit();
    	em.close();
    	return "success";
    }
    
    public Object read(Class<?> entityClass, String id) {
    	EntityManager em = entityManagerFactory.createEntityManager();
    	Object obj = em.find(entityClass, id);
    	em.close();
    	return obj;
    }
    
    public Object read(Class<?> entityClass, long id) {
    	EntityManager em = entityManagerFactory.createEntityManager();
    	Object obj = em.find(entityClass, id);
    	em.close();
    	return obj;
    }
    
    public long getMax(Class<?> cl, String row) {
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
    
    public List<?> readAll(Class<?> cl, String email){
    	EntityManager em = entityManagerFactory.createEntityManager();
    	List<?> list = em.createQuery("SELECT e FROM " + cl.getName() + " e where e.email = '" + email +"'",cl).getResultList();
    	em.close();
    	return list;
    }
    
    public List<?> readAll(Class<?> cl) {
    	EntityManager em = entityManagerFactory.createEntityManager();
    	List<?> list = em.createQuery("SELECT e FROM " + cl.getName() + " e",cl).getResultList();
    	em.close();
    	return list;
    }
    
    public String update(Object obj) throws SQLDataException{
    	EntityManager em = entityManagerFactory.createEntityManager();
    	em.getTransaction().begin();
    	em.merge(obj);
    	em.getTransaction().commit();
    	em.close();
    	return "success";
    }
    
    public String delete(Object obj) throws SQLDataException {
    	EntityManager em = entityManagerFactory.createEntityManager();
    	em.getTransaction().begin();
    	em.remove(em.merge(obj));
    	em.getTransaction().commit();
    	em.close();
    	return "success";
    }
}