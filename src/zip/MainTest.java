package zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class MainTest {

	private static String CLIENT_PATH = "C:/zip/adem";
	private static String SENDER_PATH = "C:/zip/aysenur";
	private static String UNZIPPING_PATH = "C:/zip/unzipping";

	public static void main(String[] args) throws IOException {
//		startDirZipping();
		startDirUnZipping();
	}

	public static List<File> filter(List<File> clientFiles, List<File> senderFiles) {
		List<File> filterList = new ArrayList<>();

		for (File clientFile : clientFiles) {
			boolean isHave = false;
			for (File senderFile : senderFiles) {
				if (clientFile.getName().equals(senderFile.getName())) {
					isHave = true;
					break;
				}
			}
			if (!isHave) {
				filterList.add(clientFile);
			}
		}

		return filterList;
	}

	public static List<File> getFileList(String dirPath) {
		File file = new File(dirPath);
		if (file.isDirectory()) {
			return Arrays.asList(file.listFiles());
		}

		return null;
	}

	public static void startDirUnZipping() throws IOException {
		unZipFile(SENDER_PATH + "/dirFiles.zip", UNZIPPING_PATH);
	}

	public static void startDirZipping() throws IOException {
		FileOutputStream fos = new FileOutputStream(SENDER_PATH + "/dirFiles.zip");
		ZipOutputStream zipOut = new ZipOutputStream(fos);

		File fileToZip = new File(CLIENT_PATH);
		zipFile(fileToZip, fileToZip.getName(), zipOut);
		zipOut.close();
		fos.close();

	}

	private static void unZipFile(String zipFilePath, String destDir) throws IOException {
		ZipFile zipFile = new ZipFile(zipFilePath);
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			Path entryDestination = Paths.get(destDir, entry.getName());
			if (entry.isDirectory()) {
				Files.createDirectories(entryDestination);
			} else {
				Files.createDirectories(entryDestination.getParent());
				try (InputStream in = zipFile.getInputStream(entry)) {
					Files.copy(in, entryDestination);
				}
			}
		}
		System.out.println("başarıyla unzip yapıldı");
	}

	private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
		if (fileToZip.isHidden()) {
			return;
		}
		if (fileToZip.isDirectory()) {
			if (fileName.endsWith("/")) {
				zipOut.putNextEntry(new ZipEntry(fileName));
				zipOut.closeEntry();
			} else {
				zipOut.putNextEntry(new ZipEntry(fileName + "/"));
				zipOut.closeEntry();
			}
			List<File> clientFiles = getFileList(CLIENT_PATH);
			List<File> senderFiles = getFileList(SENDER_PATH);
			List<File> filterList = filter(clientFiles, senderFiles);

			for (File childFile : filterList) {
				zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
			}
			return;
		}
		FileInputStream fis = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zipOut.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}
		fis.close();
		System.out.println("başarıyla ziplendi.");
	}
}
