package tool.dat.tooldat;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.stereotype.Component;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * @author balabala1
 * Source Db
 *
 */
@Component
public class DatDataSource {

	private static ComboPooledDataSource cpds = new ComboPooledDataSource();

    static {
        try {
            cpds.setDriverClass("oracle.jdbc.driver.OracleDriver");
            cpds.setJdbcUrl("dburl");
            cpds.setUser("name");
            cpds.setPassword("pass");
        } catch (PropertyVetoException e) {
            // handle the exception
        }
    }

    public static Connection getConnection() throws SQLException {
        return cpds.getConnection();
    }
	/**
	 *
	 */
	public DatDataSource() {
	}

}
