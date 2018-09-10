package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
	
	public static void deletelocalFile(String dirName, String fileName) {

		File f = new File(dirName + "/" + fileName);
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
	
	public static void writeFile(String fileName, byte[] byteArr) {
		FileOutputStream encFile;
		try {
			encFile = new FileOutputStream(fileName);
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
	
	public static byte[] readFile(String fileName) {
		File plainText = new File(fileName);
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
	
	public static void duplicateFile(String fileName, String cpFileName) {
		byte[] byteArr = readFile(fileName);
		writeFile(cpFileName, byteArr);
		System.out.println("Finish duplication of file: " + fileName);
	}
}
