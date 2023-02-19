package timeWizard.DAOLayer;

import java.sql.SQLDataException;
import java.util.List;

public interface Dao {
    String create(Object obj) throws SQLDataException;

    Object read(Class<?> entityClass, String id);

    Object read(Class<?> entityClass, long id);

    long getMax(Class<?> cl, String row);

    List<?> readAll(Class<?> cl, String email);

    List<?> readAll(Class<?> cl);

    String update(Object obj) throws SQLDataException;

    String delete(Object obj) throws SQLDataException;
}
