/**
 *
 */
package tool.dat.tooldat;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Data;
import tool.dat.tooldat.Points.Status;
import tool.dat.tooldat.downloader.URLDownloader;

/**
 * @author balabala1
 *
 */
@Component
public class DatChecklistSheet {

	/**
	 *
	 */
	public DatChecklistSheet() {
		// TODO Auto-generated constructor stub
	}

	@Autowired
	CheckListSheet1 sheetOne;

	@Autowired
	CheckListSheet2 sheetTwo;

	@Autowired
	CheckListSheet3 sheetThree;

	String ticketId;

	@Autowired
	URLDownloader updater;


	public void createCheckList() throws IOException{
		File checkList = new File("DAT_Review_checklist_DAT-Critic_V2.1.ods");
		String path = System.getProperty("user.dir");
		File localChecklist = new File(path+"\\lib\\DAT_Review_checklist_DAT-Critic_V2.1.ods");
		try{
			updater.downloadFile("http://{url}/mediawiki/images/b/bb/DAT_Review_checklist_V2.0.ods", checkList);
		}catch(Exception e){
			System.out.println("Error In download Checklist from mediawiki.");
		}
		try {
			SpreadSheet sheet;
			if(localChecklist.exists()){
				sheet = SpreadSheet.createFromFile(localChecklist);
			}else{
				sheet = SpreadSheet.createFromFile(checkList);
			}
			sheetOne.getPoint19().setStatus(Status.NON);
			Sheet sheet1 = sheet.getSheet(0);
			Iterator<Points> it = sheetOne.iterator();
			int i = 3;
			while(it.hasNext()){
				Points pt = it.next();
				sheet1.setValueAt(pt.getStatus(),5, i);
				sheet1.setValueAt(pt.getRemarks(),6, i);
				i++;
			}

			Sheet sheet2 = sheet.getSheet(1);
			Iterator<Points> it2 = sheetTwo.iterator();
			int j = 2;
			while(it2.hasNext()){
				Points pt = it2.next();
				sheet2.setValueAt(pt.getStatus(),3, j);
				sheet2.setValueAt(pt.getRemarks(),4, j);
				j++;
			}

			Sheet sheet3 = sheet.getSheet(2);
			Iterator<Points> it3 = sheetThree.iterator();
			int k = 2;
			while(it3.hasNext()){
				Points pt = it3.next();
				sheet3.setValueAt(pt.getStatus(),3, k);
				sheet3.setValueAt(pt.getRemarks(),4, k);
				k++;
			}

			sheet.saveAs(new File(path+"\\"+"#"+getTicketId()+"_DAT_Review_checklist_DAT-Critic_V2.1.ods"));
			System.out.println("Checklist created");
			checkList.delete();
			File f = new File(path+"\\"+"#"+getTicketId()+"_DAT_Review_checklist_DAT-Critic_V2.1.ods");
			Desktop d = Desktop.getDesktop();
			d.open(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Have you closed the checklist: yes/no");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String result = reader.readLine();
			if(result.equals("yes")){
				createCheckList();
			}else{
				System.out.println("Close the checklist");
			}
		}
	}


	/**
	 * @return the ticketId
	 */
	public String getTicketId() {
		return ticketId;
	}


	/**
	 * @param ticketId the ticketId to set
	 */
	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

}
