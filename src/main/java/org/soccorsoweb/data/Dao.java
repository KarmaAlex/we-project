package org.soccorsoweb.data;
import java.sql.Connection;

public class Dao implements GenericDao {
    
    protected final DataLayer dataLayer;
    protected final Connection connection;

    public Dao(DataLayer d) {
        this.dataLayer = d;
        this.connection = d.getConnection();
    }

    protected DataLayer getDataLayer() {
        return dataLayer;
    }

    protected Connection getConnection() {
        return connection;
    }

    @Override
    public void init() throws DataException {

    }

    @Override
    public void destroy() throws DataException {

    }
    
}