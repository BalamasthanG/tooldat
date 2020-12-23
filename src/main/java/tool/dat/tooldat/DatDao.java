/**
 *
 */
package tool.dat.tooldat;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
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
public class DatDao {

	/** Logger object */
	private static final Logger logger = LoggerFactory.getLogger(DatDao.class);

	@Autowired
	DatDataSource connection;

	public boolean isValidTableName(String tableName){

		Connection con;
		boolean result = false;
		try {
			con = connection.getConnection();
			DatabaseMetaData dbm = con.getMetaData();

			ResultSet tables = dbm.getTables(null, null, tableName , null);

			if(tables.next()){
				result = true;
			}else{
				result = false;
			}
			tables.close();
			con.close();
		} catch (SQLException e) {
			logger.error(" Db Connectivity issue "+e);
		}
		return result;
	}

	public Map<String, Object> isValidColumns(String tableName, String[] header, boolean flag){

		Connection con;
		List<String> headerList = new ArrayList<String>();
		List<String> missedHeader = new ArrayList<String>();
		String result = null;
		String resultCheck = "pass";
		String sql = " SELECT * FROM "+tableName+" ";
		List<String> headerAsList = Arrays.asList(header);

		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			con = connection.getConnection();
			Statement st = con.createStatement();

			ResultSet rs = st.executeQuery(sql);

			ResultSetMetaData rsmd = rs.getMetaData();

			int cnt = rsmd.getColumnCount();
			for(int i=1; i <= cnt ; i++){
				headerList.add(rsmd.getColumnName(i));
			}

			if(headerList.contains("PRC_DATE") && !(containsAKeyword("PRC_DATE",headerAsList))){
				headerList.remove(headerList.indexOf("PRC_DATE"));
			}

			if(header.length != headerList.size()){
				resultCheck = "error";
				result = "header_lenght_mismatched";
				for(int i=0; i < headerList.size() ; i++){
					if(!headerAsList.contains(headerList.get(i))){
						missedHeader.add(headerList.get(i));
					}
				}
				resultMap.put("result", result);
				resultMap.put("missedHeader", missedHeader);
				return resultMap;
			}
			result = "success";
			List keys = hasAllPrimayKeys(tableName);
			for(int i=0; i < headerList.size() ; i++){
				if(!(headerList.get(i).equalsIgnoreCase(header[i]))){
					result = "header_name_mismatched";
					missedHeader.add(headerList.get(i));
					if(keys.contains(rsmd.getColumnName(i))){
						result = "header_name_mismatched_with_primarykey";
						resultCheck = "error";
					}
				}
			}
			if(missedHeader.size() > 0){
				resultMap.put("missedHeader", missedHeader);
			}
			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			logger.error(" Db Connectivity issue "+e);
		} catch (Exception e){
			e.printStackTrace();
		}
		if(flag){
			resultMap.put("result", resultCheck);
			return resultMap;
		}
		resultMap.put("result", result);
		return resultMap;
	}

	public boolean containsAKeyword(String myString, List<String> keywords){
		for(String keyword : keywords){
			if(keyword.contains(myString)){
				return true;
			}
		}

		return false;
	}

	public ArrayList<ArrayList<?>> getColTypes(String tableName, String[] header){
		Connection con;
		ArrayList<Boolean> coltypeList = new ArrayList<Boolean>();
		ArrayList<Boolean> colNullable = new ArrayList<Boolean>();
		ArrayList<Integer> colLength = new ArrayList<Integer>();
		ArrayList<Boolean> doCheck = new ArrayList<Boolean>();
		ArrayList<Boolean> colNumber = new ArrayList<Boolean>();
		ArrayList<ArrayList<?>> allList = new ArrayList<ArrayList<?>>();

		String sql = " SELECT * FROM "+tableName+" ";
		try {
			con = connection.getConnection();
			Statement st = con.createStatement();

			ResultSet rs = st.executeQuery(sql);

			ResultSetMetaData rsmd = rs.getMetaData();

			Map<String, Object> result = isValidColumns(tableName, header, true);
			if(result.get("result").toString().equals("error")){
				return allList;
			}
			int cnt = rsmd.getColumnCount();
			for(int i=1; i <=cnt ; i++){
				if(rsmd.getColumnTypeName(i).equals("CHAR") ||
						rsmd.getColumnTypeName(i).equals("VARCHAR") ||
						rsmd.getColumnTypeName(i).equals("VARCHAR2")||
						rsmd.getColumnTypeName(i).equals("NVARCHAR2")||
						rsmd.getColumnTypeName(i).equals("DATE")){
					coltypeList.add(true);
				}else{
					coltypeList.add(false);
				}
				if(rsmd.getColumnTypeName(i).equalsIgnoreCase("NVARCHAR2")){
					colLength.add(3*rsmd.getPrecision(i));
				}else{
					colLength.add(rsmd.getPrecision(i) + rsmd.getScale(i));
				}
				if(ResultSetMetaData.columnNoNulls == rsmd.isNullable(i)){
					colNullable.add(false);
				}else{
					colNullable.add(true);
				}
				if(rsmd.getColumnTypeName(i).equals("CHAR") ||
						rsmd.getColumnTypeName(i).equals("VARCHAR") ||
						rsmd.getColumnTypeName(i).equals("VARCHAR2")||
						rsmd.getColumnTypeName(i).equals("NVARCHAR2") ||
						rsmd.getColumnTypeName(i).equals("NUMBER")){
					doCheck.add(true);
				}else{
					doCheck.add(false);
				}
				if(rsmd.getColumnTypeName(i).equals("NUMBER")){
					colNumber.add(true);
				}else{
					colNumber.add(false);
				}
			}
			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			logger.error(" Db Connectivity issue "+e);
		}
		allList.add(coltypeList);
		allList.add(colNullable);
		allList.add(colLength);
		allList.add(doCheck);
		allList.add(colNumber);
		return allList;
	}

	public List hasAllPrimayKeys(String tableName){
		Connection con;
		boolean result = false;
		List primaryKeys = new ArrayList();
		try {
			con = connection.getConnection();
			DatabaseMetaData dbm = con.getMetaData();
			ResultSet keys = dbm.getPrimaryKeys(null, null, tableName);
			while(keys.next()){
				primaryKeys.add(keys.getString(4));
			}
			keys.close();
			con.close();
			return primaryKeys;
		} catch (SQLException e) {
			logger.error(" Db Connectivity issue "+e);
		}
		return primaryKeys;
	}
}
