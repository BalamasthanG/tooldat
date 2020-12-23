/**
 *
 */
package tool.dat.tooldat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import tool.dat.tooldat.exception.FilePathException;

/**
 * @author balabala1
 *
 */
public class DatMain {

	static{
		Properties p = new Properties(System.getProperties());
		p.put("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
		p.put("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "OFF"); // Off or any other level
		System.setProperties(p);
	}

	/** Logger object */
	private static final Logger logger = LoggerFactory.getLogger(DatMain.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		ApplicationContext ctx =
		         new AnnotationConfigApplicationContext(DatConfig.class);

		try {
			DatReader datRead = ctx.getBean(DatReader.class);
			datRead.checkForUpdate();
			infoForSql();
			System.out.println("Enter Ticket Id: ");
			String ticket = reader.readLine();
			System.out.println("Enter the file path: ");
			String path = reader.readLine();
			File file = new File(path);
			datRead.setTicket(ticket);
			datRead.setFilePath(file);
			datRead.readFile();
			datRead.generateLog();
			datRead.createCheckList();
			authorInfo();
		}catch(FilePathException e){
			logger.error(e.toString());
		}catch (IOException | InterruptedException e) {
			logger.error(" Error in Entered Directory or file ");
		} catch (Exception e){
			logger.error(" Error in Entered Directory or file ");
		}
		//new DatFrame();
	}

	private static void infoForSql(){
		System.out.println();
		System.out.println("=======================================================================");
		System.out.println("If you are giving SQL, then It should be formatted using tool");
		System.out.println("=======================================================================");
		System.out.println();
	}

	private static void authorInfo(){
		System.out.println();
		System.out.println("=======================================================================================");
		System.out.println("If you have any queries, Please contact Balamastan G ");
		System.out.println("=======================================================================================");
		System.out.println();
	}

}
