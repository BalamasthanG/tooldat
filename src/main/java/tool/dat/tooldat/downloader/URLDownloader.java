/**
 *
 */
package tool.dat.tooldat.downloader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.springframework.stereotype.Component;

/**
 * @author balabala1
 *
 */
@Component
public class URLDownloader {

	public File downloadFile(String url, File writeTo) throws IOException {
		URL webUrl = new URL(url);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = new BufferedInputStream(webUrl.openStream());

		byte[] b = new byte['?'];
		int n;
		while (-1 != (n = in.read(b))) {
			out.write(b, 0, n);
		}
		out.close();
		in.close();
		byte[] response = out.toByteArray();
		FileOutputStream fos = new FileOutputStream(writeTo);
		fos.write(response);
		fos.close();
		return writeTo;
	}
}
