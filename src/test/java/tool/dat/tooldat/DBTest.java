/**
 *
 */
package tool.dat.tooldat;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author balabala1
 *
 */
public class DBTest {

	/**
	 *
	 */
	public DBTest() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws SQLException
	 */
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		DatDataSource source = new DatDataSource();
		Connection con = source.getConnection();
		System.out.println("Connected success fully");
		con.close();

//		ApplicationContext ctx =
//		         new AnnotationConfigApplicationContext(DatConfig.class);
//
//		DataSource src = (DataSource) ctx.getBean("dataSource");
//		src.getConnection();
//		System.out.println("Connected success fully");
	}

}
