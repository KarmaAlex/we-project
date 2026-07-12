package org.soccorsoweb.data;
import java.sql.Connection;

public class Dao {
    
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

    public void init() throws DataException {

    }

    public void destroy() throws DataException {

    }
    
}
