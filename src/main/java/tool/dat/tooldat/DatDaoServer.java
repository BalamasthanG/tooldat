/**
 *
 */
package tool.dat.tooldat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author balabala1
 *
 */
@Component
public class DatDaoServer {

	/** Logger object */
	private static final Logger logger = LoggerFactory.getLogger(DatDaoServer.class);

	@Autowired
	DatDataSourceServer connection;

	public Map<String, Object> isDBContainUiqData(List<HashMap<String, String>> list, String tableName , List primaryKey){

		int size = primaryKey.size();

		boolean result = false;
		List<HashMap<String, String>> dbList = new ArrayList<HashMap<String, String>>();
		List<HashMap<String, String>> nonDbList = new ArrayList<HashMap<String, String>>();

		Map<String, Object> resultMap = new HashMap<String,Object>();

		for(int i=0; i< list.size() ; i++){
			HashMap<String, String> data = list.get(i);
			String sql = " SELECT * FROM "+tableName+" ";
			sql = sql + " WHERE ";
			for(int j=0; j< size; j++){

				sql = sql + primaryKey.get(j) + "=";

				sql = sql + "'" + data.get(primaryKey.get(j)).replace("\"", "") + "'";

				if(size-1 !=  j){
					sql = sql + " AND ";
				}
			}

			result = isDbCheckPass(sql);
			if(result){
				dbList.add(list.get(i));
				result = true;
			}else{
				nonDbList.add(list.get(i));
			}
		}

		resultMap.put("result", result);
		resultMap.put("dbList", dbList);
		resultMap.put("nonDbList", nonDbList);
		return resultMap;
	}

	private boolean isDbCheckPass(String sql){
		Connection con = null;
		boolean result = false;

		try {
			con = connection.getConnection();
			Statement st = con.createStatement();

			ResultSet rs = st.executeQuery(sql);

			if(rs.next()){
				result = true;
			}

			st.close();
		} catch (SQLException e) {
			logger.error(" Db Connectivity issue "+e);
		} finally{
			try {
				con.close();
			} catch (SQLException e) {
				logger.error(" Db Connectivity issue "+e);
			}
		}
		return result;
	}

}
