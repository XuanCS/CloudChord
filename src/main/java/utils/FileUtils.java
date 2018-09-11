package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

	public static void deletelocalFile(String dirName, String fileName) {
		String localFileName = FileUtils.getLocalFileName(fileName, dirName);
		File f = new File(localFileName);
		if (!f.exists()) {
			System.out.println("File " + fileName + " does not exist");
			return;
		}
		if (f.delete()) {
			System.out.println(f.getName() + " is deleted!");
		} else {
			System.out.println("Delete operation is failed.");
		}
	}

	public static String createFolder(String localChordNum) {
		String DirName = "Chord_" + localChordNum;

		File dirFileName = new File(DirName);
		if (!dirFileName.exists()) {
			if (dirFileName.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				System.out.println("Failed to create directory!");
			}
		}
		return DirName;
	}

	public static void writeFile(String fileName, String dirName, byte[] byteArr) {
		FileOutputStream encFile;
		String outputFilePath = FileUtils.getLocalFileName(fileName, dirName);
		try {
			encFile = new FileOutputStream(outputFilePath);
			encFile.write(byteArr);
			encFile.close();
			System.out.println("finished writing to file " + fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static byte[] readFile(String fileName, String dirName) {
		String localFileName = FileUtils.getLocalFileName(fileName, dirName);
		File plainText = new File(localFileName);
		if (!plainText.exists()) {
			System.out.print(fileName + "File does not exit");
			return null;
		}
		byte[] plainTextArr = null;
		FileInputStream fisPlainText;
		try {
			fisPlainText = new FileInputStream(plainText);

			plainTextArr = new byte[(int) plainText.length()];
			fisPlainText.read(plainTextArr);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return plainTextArr;
	}

	public static void duplicateFile(String fileName, String dirName, String cpFileName) {
		byte[] byteArr = readFile(fileName, dirName);
		writeFile(cpFileName, dirName, byteArr);
		System.out.println("Finish duplication of file: " + fileName);
	}

	public static String getLocalFileName(String fileName, String dirName) {
		String localFile = dirName + "/" + fileName;
		return localFile;
	}
}
