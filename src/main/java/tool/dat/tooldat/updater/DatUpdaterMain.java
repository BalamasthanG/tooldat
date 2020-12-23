/**
 *
 */
package tool.dat.tooldat.updater;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tool.dat.tooldat.downloader.URLDownloader;

/**
 * @author balabala1
 *
 */
@Component
public class DatUpdaterMain {

	public static int BUILD_VERSION = 2;

	@Autowired
	URLDownloader updater;

	public void checkForUpdate(){
		File buildInfo = new File("build-info.txt");
		File downloadInfo = new File("download-info.txt");
		String path = System.getProperty("user.dir");
		FileReader reader;
		try {
			updater.downloadFile("http://url:8085/DatCritic/build", buildInfo);
			reader = new FileReader(buildInfo);
			LineNumberReader lnr = new LineNumberReader(reader);
			int build = Integer.parseInt(lnr.readLine());
			System.out.println("Dat-Critic Latest build-version-"+build);
			System.out.println("Dat-Critic Your build-version-"+BUILD_VERSION);
			lnr.close();
			reader.close();
			buildInfo.delete();
			if(build != BUILD_VERSION){
				System.out.println("Downloading Latest version...");
				File zipFile = new File("Dat-Critic.zip");
				updater.downloadFile("http://192.168.55.41:8085/DatCritic/auto", zipFile);
				System.out.println("Download completed.\n\nInstalling latest update...");
				System.out.println();
				unzip(zipFile.getAbsolutePath(), new File("").getAbsolutePath());
				zipFile.delete();
				updater.downloadFile("http://url:8085/DatCritic/update", downloadInfo);
				readFile(downloadInfo);
				downloadInfo.delete();
				System.out.println("\nPlease restart the application.");
				System.exit(0);
			}else{
				System.out.println("You have Latest version.");
			}
		}catch (InterruptedException e){
			System.out.println("Interrupted the download");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Downloading server is not alive state...");
			System.out.println("Could not check the latest version...");
			System.out.println();
		}
	}

	public void unzip(String zipFilePath, String destinationDir) throws IOException {
		File destDir = new File(destinationDir);
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
		ZipEntry entry = zipIn.getNextEntry();
		while (entry != null) {
			String filePath = destinationDir + File.separator + entry.getName();
			if (!entry.isDirectory()) {
				extractFile(zipIn, filePath);
			} else {
				File dir = new File(filePath);
				dir.mkdir();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
	}

	private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		byte[] bytesIn = new byte['?'];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}

	private void readFile(File file) throws IOException, InterruptedException {
	    BufferedReader reader = new BufferedReader(new FileReader (file));
	    String         line = null;
	    String         ls = System.getProperty("line.separator");
	    try {
	        while((line = reader.readLine()) != null) {
	            System.out.println(line);
	            TimeUnit.SECONDS.sleep(1);
	        }
	    } finally {
	        reader.close();
	    }
	}
}
