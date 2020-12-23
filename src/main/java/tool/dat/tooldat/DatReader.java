/**
 *
 */
package tool.dat.tooldat;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tool.dat.tooldat.exception.FilePathException;
import tool.dat.tooldat.updater.DatUpdaterMain;

/**
 * @author balabala1
 *
 */
@Component
public class DatReader {

	/** Logger object */
	private static final Logger logger = LoggerFactory.getLogger(DatReader.class);

	/** file path for the dat */
	File filePath;

	@Autowired
	DatValidater datValidater;

	@Autowired
	DatChecklistSheet sheet;

	@Autowired
	DatUpdaterMain datUpdater;

	String ticketId;

	/**
	 * to read the files in the directory
	 * @throws InterruptedException
	 */
	public void readFile() throws InterruptedException{
		if(!filePath.isFile() && filePath.list().length > 0){
			File[] fileList = filePath.listFiles();
			for(int fileCount=0;fileCount<filePath.list().length;fileCount++){
				datValidater.doCheck(fileList[fileCount]);
			}
			datValidater.checkNewEntry();
			datValidater.checkItemMstUpd();
		}else{
			System.out.println(" Invalid Path ");
			logger.error(" Invalid directory ");
		}
	}

	/**
	 * to generate the log file
	 */
	public void generateLog(){
		LocalDateTime currentTime = LocalDateTime.now();
		LocalDate date = currentTime.toLocalDate();
		int hr = currentTime.getHour();
		int min = currentTime.getMinute();
		int sec = currentTime.getSecond();
		String path = System.getProperty("user.dir");
		path = path + "\\log\\"+ticketId+"\\";

		String fileDate = date+"_"+hr+"_"+min+"_"+sec;
		String fileNamePath = path+"\\"+"log_"+fileDate+".log";
		File createFile = new File(fileNamePath);

		if(!createFile.getParentFile().exists()){
			createFile.getParentFile().mkdirs();
		}

		FileWriter write = null;
		BufferedWriter buffer = null;
		String content = datValidater.getErrorContent();

		if(content.isEmpty()){
			System.out.print("Invalid path / DAT / SQL Files not found ");
			System.exit(0);
		}

		try {
			write = new FileWriter(createFile);
			buffer = new BufferedWriter(write);
			buffer.write(content);
			System.out.println("Log generated in the path "+fileNamePath);
			TimeUnit.SECONDS.sleep(2);
			File f = new File(fileNamePath);
			Desktop d = Desktop.getDesktop();
			d.open(f);
		} catch (IOException | InterruptedException e) {
			System.out.println("File is in used"+e);
			logger.error("File is in used"+e);
		}finally{

			try {
				if(buffer!=null){
					buffer.close();
				}
				if(write!=null){
					write.close();
				}
			} catch (IOException e) {
				logger.error(" error in file handling "+e);
			}
		}
	}

	public File getFilePath() {
		return filePath;
	}

	public void setFilePath(File filePath) throws FilePathException {
		if(!filePath.isFile() && filePath.list().length > 0){
			this.filePath = filePath;
		}else{
			throw new FilePathException();
		}
	}

	public void createCheckList() throws IOException {
		sheet.createCheckList();
	}

	public void setTicket(String ticketId){
		this.ticketId = ticketId;
		datValidater.setTicketId(ticketId);
		sheet.setTicketId(ticketId);
	}

	public void checkForUpdate(){
		datUpdater.checkForUpdate();
	}
}
