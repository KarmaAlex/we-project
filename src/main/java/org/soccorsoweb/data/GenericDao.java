package org.soccorsoweb.data;

public interface GenericDao {
    void init() throws DataException;
    void destroy() throws DataException;
}
