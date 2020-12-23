/**
 *
 */
package tool.dat.tooldat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
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
public class GeneralChecker {

	/** Logger object */
	private static final Logger logger = LoggerFactory.getLogger(GeneralChecker.class);

	@Autowired
	DatDao dao;

	@Autowired
	DatDaoServer daoServer;

	/**
	 * isUtf8 is used to check the file encoding
	 * @param file
	 * @return
	 */
	public boolean isUtf8(File file){
		byte[] buffer = new byte[4096];
		InputStream is = null;
		try {
			is = new FileInputStream(file.getAbsolutePath());
			is.read(buffer);
		} catch (IOException e) {
			logger.error("File encoding Error "+e);
		}
		try {
			Charset.availableCharsets().get("UTF-8").newDecoder().decode(ByteBuffer.wrap(buffer));
			return true;
		} catch (CharacterCodingException e) {
			return false;
		}
	}

	/**
	 * Determines the end-of-line {@link Mode} of a text file.
	 *
	 * @param file the file to investigate
	 * @return the end-of-line {@link Mode} of the given file, or {@code null} if it could not be determined
	 * @throws Exception
	 */
	public String determineEOL(File file ) throws Exception{
		FileInputStream fileIn = new FileInputStream( file );
		BufferedInputStream bufferIn = new BufferedInputStream( fileIn );
		try{
			int prev = -1;
			int ch;
			while ( ( ch = bufferIn.read() ) != -1 ){
				if ( ch == '\n' ){
					if ( prev == '\r' ){
						return "CRLF";
					}
					else{
						return "LF";
					}
				}
				else if ( prev == '\r' ){
					return "CR";
				}
				prev = ch;
			}
			throw new Exception( "Could not determine end-of-line marker mode" );
		}
		catch ( IOException ioe ){
			throw new Exception( "Could not determine end-of-line marker mode", ioe );
		}
		finally{
			bufferIn.close();
		}
	}

	/**
	 * isBomPresent method is used to check the BOM
	 * @param file
	 * @return
	 */
	public boolean isBomPresent(File file){
		final String UTF8_BOM = "\uFEFF";
		byte[] a = new byte[4096];
		InputStream is = null;
		String line = new String();

		try {
			is = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while (br.ready()) {
				line= br.readLine();
				if(line.contains(UTF8_BOM)){
					return true;
				}
			}
		} catch (IOException e) {
			logger.error("Error in checking BOM "+e);
		}
		return false;
	}

	/**
	 * to check the eof in present
	 * @param file
	 * @return
	 */
	public boolean isEOFLinePresent(File file){
		RandomAccessFile raf = null;
		try {
			int ch,ch1;
			raf = new RandomAccessFile(file, "r");
			long pos = raf.length() - 2;
			if (pos < 0){
				return false;
			}
			raf.seek(pos);
			ch = raf.read();
			ch1 = raf.read();

			if(ch == '\r' && ch1 == '\n'){
				return true;
			}else if(ch1 == '\n'){
				return true;
			}else{
				return false;
			}
		} catch (IOException e) {
			logger.error("Error in EOF "+e);
			return false;
		} finally {
			if (raf != null) try {
				raf.close();
			} catch (IOException ignored) {
				logger.error("Error in EOF "+ignored);
			}
		}
	}

	/**
	 * to check the multiple line
	 * @param file
	 * @return
	 */
	public boolean isMultipleLine(File file){
		String eol;
		RandomAccessFile raf = null;
		int emptyCnt = 0;
		try {
			eol = determineEOL(file);
			raf = new RandomAccessFile(file, "r");
			long pos;
			if(eol.equalsIgnoreCase("crlf")){
				pos = raf.length() - 2;
				while(pos > 0){
					raf.seek(pos);
					if(raf.read() == '\r' && raf.read() == '\n'){
						pos = pos - 2;
						emptyCnt++;
					}else{
						break;
					}
				}
			}else if(eol.equalsIgnoreCase("lf")){
				pos = raf.length() - 1;
				while(pos > 0){
					raf.seek(pos);
					if(raf.read() == '\n'){
						pos = pos - 1;
						emptyCnt++;
					}else{
						break;
					}
				}
			}else if(eol.equalsIgnoreCase("cr")) {
				pos = raf.length() - 1;
				while(pos > 0){
					raf.seek(pos);
					if(raf.read() == '\r'){
						pos = pos - 1;
						emptyCnt++;
					}else{
						break;
					}
				}
			}
			if(emptyCnt > 1){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			logger.error("Error in checking multi line "+e);
		} finally{
			try {
				raf.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Error in checking multi line "+e);
			}
		}
		System.out.println(emptyCnt);
		return false;
	}

	/**
	 * check the dat contain atlease one row
	 * @param file
	 * @return
	 */
	public boolean isLeastDataRow(File file){
		LineNumberReader lnr = null;
		FileReader rdr = null;
		String readString;
		int dataCount = 0;
		try {
			rdr = new FileReader(file);
			lnr = new LineNumberReader(rdr);
			//header
			readString = lnr.readLine();
			//data
			readString = lnr.readLine();

			while(readString != null){
				if(readString.isEmpty()){
					readString = lnr.readLine();
					continue;
				}
				readString = lnr.readLine();
				dataCount++;
				if(dataCount > 0){
					break;
				}
			}

			if(dataCount > 0){
				return true;
			}else{
				return false;
			}
		} catch (IOException e) {
			logger.error("Error in checking least data row "+e);
		} finally{
			try {
				rdr.close();
				lnr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Error in checking least data row "+e);
			}

		}
		return false;
	}

	/**
	 * check for column and corresponding data
	 * @param tableName
	 * @param file
	 * @return
	 */
	public Map<String,Boolean> isValidColtype(String tableName, File file){
		LineNumberReader lnr = null;
		FileReader rdr = null;
		String readString;
		int dataCount = 0;
		Map<String,Boolean> resultMap = new HashMap<String, Boolean>();
		boolean resultFailStringColumn = false;
		boolean resultFailIntColumn = false;
		boolean resultFailDataColunm = false;
		boolean resultFailInNullValue = false;
		try {
			rdr = new FileReader(file);
			lnr = new LineNumberReader(rdr);
			//header
			readString = lnr.readLine();
			String[] header = readString.split(",");
			List<ArrayList<?>> colTypes = dao.getColTypes(tableName, header);
			ArrayList<Boolean> coltypeList = (ArrayList<Boolean>) colTypes.get(0);
			ArrayList<Boolean> colNullable = (ArrayList<Boolean>) colTypes.get(1);
			ArrayList<Integer> colLength = (ArrayList<Integer>) colTypes.get(2);
			ArrayList<Boolean> doCheck = (ArrayList<Boolean>) colTypes.get(3);
			ArrayList<Boolean> colNumber = (ArrayList<Boolean>) colTypes.get(4);

			//data
			readString = lnr.readLine();

			while(readString != null){
				if(readString.isEmpty()){
					readString = lnr.readLine();
					continue;
				}
				String value[] = readString.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
				if(header.length == value.length){
					for(int i = 0; i < value.length; i++){
						//Quotes check for integer and string
						if(coltypeList.get(i)){
							if(!value[i].startsWith("\"") || !value[i].endsWith("\"")){
								resultFailStringColumn = true;
							}
						}else{
							if(value[i].startsWith("\"") || value[i].endsWith("\"")){
								resultFailIntColumn = true;
							}
						}
						//Data length check
						if(doCheck.get(i)){
							String val = value[i];
							if(colNumber.get(i)){
								val = val.replace("-", "");
							}
							if(val.length() > colLength.get(i)){
								resultFailDataColunm = true;
							}
						}

						//nullable
						if(!colNullable.get(i)){
							if(value[i].replace("\"", "").isEmpty()){
								resultFailInNullValue = true;
							}
						}
					}
				}
				readString = lnr.readLine();
			}

			resultMap.put("fail_string_column", resultFailStringColumn);
			resultMap.put("fail_int_column", resultFailIntColumn);
			resultMap.put("fail_data_length", resultFailDataColunm);
			resultMap.put("fail_in_null_value", resultFailInNullValue);

		} catch (IOException e) {
			logger.error("Error in checking String and Int column "+e);
		} finally{
			try {
				rdr.close();
				lnr.close();
			} catch (IOException e) {
				logger.error("Error in checking String and Int column "+e);
			}

		}
		return resultMap;
	}

	/**
	 * check for column and corresponding data
	 * @param tableName
	 * @param file
	 * @return
	 */
	public boolean isContainUniqRows(String tableName, File file){
		LineNumberReader lnr = null, lnrInner = null;
		FileReader rdr = null, rdrInner = null;
		String readString, readStringInner;
		int lnrCount=0,lnrInnerCount = 0;
		boolean result = false;
		try {
			rdr = new FileReader(file);
			lnr = new LineNumberReader(rdr);
			//header
			readString = lnr.readLine();
			String[] header = readString.split(",");

			List primaryKeys = dao.hasAllPrimayKeys(tableName);

			List primaryIndex = new ArrayList();

			for(int i=0; i< primaryKeys.size(); i++){
				if(Arrays.asList(header).indexOf(primaryKeys.get(i)) > -1){
					primaryIndex.add(i);
				}
			}

			//data
			readString = lnr.readLine();
			lnrCount++;

			while(readString != null){
				if(readString.isEmpty()){
					readString = lnr.readLine();
					continue;
				}
				String value[] = readString.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

				List primaryData = new ArrayList();

				for(int i=0;i<primaryIndex.size();i++){
					primaryData.add(value[(int) primaryIndex.get(i)]);
				}


				rdrInner = new FileReader(file);
				lnrInner = new LineNumberReader(rdrInner);
				lnrInnerCount = 0;
				//header
				readStringInner = lnrInner.readLine();

				//data
				readStringInner = lnrInner.readLine();
				lnrInnerCount++;

				while(readStringInner != null){
					if(readStringInner.isEmpty()){
						readStringInner = lnr.readLine();
						lnrInnerCount--;
						continue;
					}
					String valueInner[] = readStringInner.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
					int same=0;

					if(lnrCount == lnrInnerCount){
						readStringInner = lnrInner.readLine();
						lnrInnerCount++;
						continue;
					}

					for(int i=0;i<primaryData.size(); i++){
						if(primaryData.get(i).equals(valueInner[(int) primaryIndex.get(i)])){
							same++;
						}
					}
					if(same == primaryData.size() && primaryData.size() > 0){
						result = true;
					}

					readStringInner = lnrInner.readLine();
					lnrInnerCount++;
				}

				if(result){
					break;
				}

				readString = lnr.readLine();
				lnrCount++;
			}

		} catch (IOException e) {
			logger.error("Error in checking String and Int column "+e);
		} finally{
			try {
				rdr.close();
				rdrInner.close();
				lnr.close();
				lnrInner.close();
			} catch (IOException e) {
				logger.error("Error in checking String and Int column "+e);
			}

		}
		return result;
	}

	public Map<String, Object> isDbContainUniqRow(String tableName, File file){
		LineNumberReader lnr = null;
		FileReader rdr = null;
		String readString;
		int lnrCount=0;
		boolean result = false;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			rdr = new FileReader(file);
			lnr = new LineNumberReader(rdr);
			//header
			readString = lnr.readLine();
			String[] header = readString.split(",");

			List primaryKeys = dao.hasAllPrimayKeys(tableName);

			List primaryIndex = new ArrayList();

			List<HashMap<String, String>> listData = new ArrayList<HashMap<String, String>>();

			for(int i=0; i< primaryKeys.size(); i++){
				if(Arrays.asList(header).indexOf(primaryKeys.get(i)) > -1){
					primaryIndex.add(i);
				}
			}

			if(primaryIndex.size() <= 0){
				resultMap.put("result", true);
				return resultMap;
			}
			//data
			readString = lnr.readLine();
			lnrCount++;

			while(readString != null){
				if(readString.isEmpty()){
					readString = lnr.readLine();
					continue;
				}
				String value[] = readString.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

				HashMap<String, String> primaryData = new HashMap<String, String>();

				for(int i=0;i<primaryIndex.size();i++){
					primaryData.put(header[(int)primaryIndex.get(i)], value[(int)primaryIndex.get(i)]);
				}

				listData.add(primaryData);
				readString = lnr.readLine();
				lnrCount++;
			}

			Map<String, Object> resultMapDb = daoServer.isDBContainUiqData(listData, tableName, primaryKeys);

			List<HashMap<String, String>> dbList = (List<HashMap<String, String>>) resultMapDb.get("dbList");

			List<HashMap<String, String>> nonDbList = (List<HashMap<String, String>>) resultMapDb.get("nonDbList");

			result = Boolean.valueOf(resultMapDb.get("result").toString());

			if(file.getName().contains("ins")){
				result = !result;
				resultMap.put("dbList", dbList);
			}else if(file.getName().contains("upd") || file.getName().contains("del")){
				result = result;
				resultMap.put("dbList", nonDbList);
			}
			resultMap.put("result", result);

		} catch (IOException e) {
			logger.error("Error in checking Uniq column in DB "+e);
		} finally{
			try {
				rdr.close();
				lnr.close();
			} catch (IOException e) {
				logger.error("Error in checking Uniq column in DB "+e);
			}

		}
		return resultMap;
	}

	public Map<String, Boolean> isUniqRowWhere(String tableName, File file){
		LineNumberReader lnr = null;
		FileReader rdr = null;
		String readString;
		int lnrCount=0;
		boolean whereFlag = false;
		boolean commitFlag = false;
		boolean uniqFlag = false;
		boolean reservedName = false;
		Map<String, Boolean> resultMap = new HashMap<String, Boolean>();
		try {
			rdr = new FileReader(file);
			lnr = new LineNumberReader(rdr);

			List primaryKeys = dao.hasAllPrimayKeys(tableName);

			readString = lnr.readLine();
			lnrCount++;

			Map<String, String> whereMap = new HashMap<String, String>();

			while(readString != null){
				if(readString.isEmpty()){
					readString = lnr.readLine();
					continue;
				}

				if(readString.toLowerCase().contains("group") ||
						readString.toLowerCase().contains("like") ||
						readString.toLowerCase().contains("load") ||
						readString.toLowerCase().contains("select")){
					reservedName = true;
				}

				if(!whereFlag){
					whereFlag = readString.trim().equalsIgnoreCase("where");
				}

				if(whereFlag){
					whereFlag = true;
					readString = lnr.readLine();
					while(readString != null){
						if(readString.toLowerCase().contains("commit")){
							break;
						}
						String[] whereColumn = readString.toUpperCase().replace("AND", "").replace("OR", "").replace(" ", "").replace(";", "").split("=");
						if(whereColumn.length > 1){
							whereMap.put(whereColumn[0], whereColumn[1]);
						}
						readString = lnr.readLine();
					}

					if(readString != null){
						if(readString.toLowerCase().equals("commit;")){
							commitFlag = true;
						}
					}
					whereFlag = false;
				}

				readString = lnr.readLine();
				lnrCount++;
			}


			int index = primaryKeys.indexOf("LANG");

			if(index > -1){
				primaryKeys.remove(index);
			}

			int size = primaryKeys.size();
			int count=0;
			for(int i=0; i< size; i++){
				if(whereMap.containsKey(primaryKeys.get(i))){
					count++;
				}
			}
			if(size != count){
				uniqFlag = true;
			}
			resultMap.put("whereColumns", uniqFlag);
			resultMap.put("commitKey", !commitFlag);
			resultMap.put("reservedName", reservedName);
		} catch (IOException e) {
			logger.error("Error in Where columns "+e);
		}catch(Exception e){
			logger.error("Error SQL file format");
		}finally{
			try {
				rdr.close();
				lnr.close();
			} catch (IOException e) {
				logger.error("Error in Where columns "+e);
			}

		}
		return resultMap;
	}

	public boolean isSameWhere(String tableName, File file){
		LineNumberReader lnr = null;
		FileReader rdr = null;
		String readString;
		int lnrCount=0;
		boolean whereFlag = false;
		boolean result = false;
		Map<String, Boolean> resultMap = new HashMap<String, Boolean>();
		try {
			rdr = new FileReader(file);
			lnr = new LineNumberReader(rdr);

			List primaryKeys = dao.hasAllPrimayKeys(tableName);

			readString = lnr.readLine();
			lnrCount++;

			Map<String, String> whereMap = null;

			List mapList = new ArrayList();
			while(readString != null){
				if(readString.isEmpty()){
					readString = lnr.readLine();
					continue;
				}
				if(!whereFlag){
					whereFlag = readString.trim().equalsIgnoreCase("where");
				}

				if(whereFlag){
					whereFlag = true;
					readString = lnr.readLine();
					whereMap = new HashMap<String, String>();
					while(readString != null){
						if(readString.toLowerCase().contains("commit")){
							break;
						}
						String[] whereColumn = readString.toUpperCase().replace("AND", "").replace("OR", "").replace(" ", "").replace(";", "").split("=");
						if(whereColumn.length > 1){
							whereMap.put(whereColumn[0], whereColumn[1]);
						}
						readString = lnr.readLine();
					}
					whereFlag = false;
					mapList.add(whereMap);
				}

				readString = lnr.readLine();
				lnrCount++;
			}

			for(int i= 0 ; i < mapList.size() ; i++){
				for(int j=i ; j < mapList.size() ; j++){
					if(i!=j){
						HashMap<String, String> map1 = (HashMap<String, String>) mapList.get(i);
						HashMap<String, String> map2 = (HashMap<String, String>) mapList.get(j);
						if(map1.equals(map2)){
							result = true;
						}
					}
				}
			}

		} catch (IOException e) {
			logger.error("Error in Where columns "+e);
		}catch(Exception e){
			logger.error("Error SQL file format");
		}finally{
			try {
				rdr.close();
				lnr.close();
			} catch (IOException e) {
				logger.error("Error in Where columns "+e);
			}

		}
		return result;
	}
}
