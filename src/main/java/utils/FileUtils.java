package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

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
		String DirName = Helper.chordPrefix + localChordNum;

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
		System.out.println("localFileName: " + localFileName);
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

	public static File createFile(String fileName) {
		File f = new File(fileName);
		if (f.exists()) {
			return f;
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return f;
	}
	
	public static String isFromSentProp(String splitFile, String DirName) {
		String propFileName = DirName + Helper.SENT_FILE_LIST;
		String res = Props.seekProp(splitFile, propFileName);
		 if(res == null) {
			 return "";
		 }
		 return res;
	}
	
	// assume it is from sentProp
	public static boolean isSameIPport( String splitFile, String DirName, String targetSock, InetSocketAddress localAddress) {	
		String targetIP = targetSock.split(" ")[0];
		String targetPortNum = targetSock.split(" ")[1];
		
		String localIP = localAddress.getAddress().toString().split("/")[1];
		String localPortNum = Integer.toString(localAddress.getPort());
		return targetIP.equals(localIP) && targetPortNum.equals(localPortNum);
	}

	public static void updateSentPropFile(String fileName, String DirName, String sentSockStr) {
		String propFileName = DirName + Helper.SENT_FILE_LIST;
		Props.updateProp(fileName, sentSockStr, propFileName);
	}

	public static void updateNamePropFile(String encName, String srcName, String DirName) {
		String propFileName = DirName + Helper.NAME_LIST;
		Props.updateProp(encName, srcName, propFileName);
	}

	public static void renameFile(String oldName, String newName, String dirName) {
		String oldFilePath = getLocalFileName(oldName, dirName);
		String newFilePath = getLocalFileName(newName, dirName);
		File file = new File(oldFilePath);
		File file2 = new File(newFilePath);

//		if (file2.exists())
//			try {
//				throw new java.io.IOException("file exists");
//			} catch (IOException e) {
//				e.printStackTrace();
//			}

		// Rename file (or directory)
		boolean success = file.renameTo(file2);
		if (!success) {
			System.out.println("rename failed");
		} else {
			System.out.println("rename success");
		}
	}
	
	public static String getFileHash(String fileName) {
		long hash = Helper.hashString(fileName);
		String res = Long.toHexString(hash);
		return res;
	}
	
	public static InetSocketAddress getFileSuccessor(String fileName, InetSocketAddress localAddress) {
		long hash = Helper.hashString(fileName);
		System.out.println("Hash value is " + Long.toHexString(hash));
		InetSocketAddress result = Helper.requestAddress(localAddress, "FINDSUCC_" + hash);
		return result;
	}
	
	public static boolean checkInputFile(String fileName, InetSocketAddress localAddress) {
		long hash = Helper.hashString(fileName);
		// System.out.println("Hash value is " + Long.toHexString(hash));
		InetSocketAddress result = Helper.requestAddress(localAddress, "FINDSUCC_" + hash);

		// if fail to send request, local node is disconnected, exit
		if (result == null) {
			System.out.println("The node your are contacting is disconnected. Now exit.");
			System.exit(0);
		}
		return true;
	}
}
