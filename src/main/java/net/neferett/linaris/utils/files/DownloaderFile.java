package net.neferett.linaris.utils.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloaderFile {

	public static void downloadFromURL(final String fileURL, final String saveDir) {
		try {
			final URL url = new URL(fileURL);
			final HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			final int responseCode = httpConn.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				String fileName = "";
				final String disposition = httpConn.getHeaderField("Content-Disposition");

				if (disposition != null) {
					final int index = disposition.indexOf("filename=");
					if (index > 0)
						fileName = disposition.substring(index + 10, disposition.length() - 1);
				} else
					fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
				final InputStream inputStream = httpConn.getInputStream();
				final String saveFilePath = saveDir + File.separator + fileName;

				final FileOutputStream outputStream = new FileOutputStream(saveFilePath);

				int bytesRead = -1;
				final byte[] buffer = new byte[4096];
				while ((bytesRead = inputStream.read(buffer)) != -1)
					outputStream.write(buffer, 0, bytesRead);

				outputStream.close();
				inputStream.close();

				System.out.println("Downloaded");
			} else
				System.out.println("Erreur: " + responseCode);
			httpConn.disconnect();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
