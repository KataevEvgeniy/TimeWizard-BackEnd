package timeWizard.DAOLayer;

import timeWizard.entity.TableColumn;

import java.sql.SQLDataException;
import java.util.List;


public interface Dao {
    void create(Object obj) throws SQLDataException;

    Object read(Class<?> entityClass, String id);


    List<?> readAll(Class<?> cl, String email);

    List<?> readAll(Class<?> cl, TableColumn column);


    void update(Object obj) throws SQLDataException;

    void delete(Object obj) throws SQLDataException;
}
