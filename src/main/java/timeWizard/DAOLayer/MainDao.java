package timeWizard.DAOLayer;

import org.springframework.stereotype.Component;
import timeWizard.entity.TableColumn;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


@Component
public class MainDao implements Dao{
	private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("persistence");//name use from persistence.xml
    
    public void create(Object obj) throws SQLDataException {
    	EntityManager em = entityManagerFactory.createEntityManager();
    	em.getTransaction().begin();
    	em.persist(obj);
		try{
			em.getTransaction().commit();
		}
		catch (Exception e){
			em.getTransaction().rollback();
			throw new SQLDataException();
		}
    	em.close();
    }
    
    public Object read(Class<?> entityClass, String id) {
    	EntityManager em = entityManagerFactory.createEntityManager();
    	Object obj = em.find(entityClass, id);
    	em.close();
    	return obj;
    }
    
    public List<?> readAll(Class<?> cl, String email){
    	EntityManager em = entityManagerFactory.createEntityManager();
    	List<?> list = em.createQuery("SELECT e FROM " + cl.getName() + " e where e.email = '" + email +"'",cl).getResultList();
    	em.close();
    	return list;
    }

	public List<?> readAll(Class<?> cl, TableColumn column){
		EntityManager em = entityManagerFactory.createEntityManager();
		List<?> list = em.createQuery("SELECT e FROM " + cl.getName() + " e where e.tableColumn.id = '" + column.getId() +"'",cl).getResultList();
		em.close();
		return list;
	}
    
    public void update(Object obj) throws SQLDataException{
    	EntityManager em = entityManagerFactory.createEntityManager();
    	em.getTransaction().begin();
    	em.merge(obj);
		try{
			em.getTransaction().commit();
		}
		catch (Exception e){
			em.getTransaction().rollback();
			throw new SQLDataException();
		}

    	em.close();
    }
    
    public void delete(Object obj) throws SQLDataException {
    	EntityManager em = entityManagerFactory.createEntityManager();
    	em.getTransaction().begin();
    	em.remove(em.merge(obj));
		try{
			em.getTransaction().commit();
		}
		catch (Exception e){
			em.getTransaction().rollback();
			throw new SQLDataException();
		}
    	em.close();
    }
}