package utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SplitFile {
	public static void join(String fileName, String dirName) {
		long leninfile = 0, leng = 0;
		int count = 1, data = 0;
		try {
			String localFileName = FileUtils.getLocalFileName(fileName, dirName);
			File filename = new File(localFileName);
			OutputStream outfile = new BufferedOutputStream(new FileOutputStream(filename));
			while (true) {
				filename = new File(fileName + count + ".sp");
				if (filename.exists()) {
					InputStream infile = new BufferedInputStream(new FileInputStream(filename));
					data = infile.read();
					while (data != -1) {
						outfile.write(data);
						data = infile.read();
					}
					leng++;
					infile.close();
					count++;
				} else {
					break;
				}
			}
			System.out.println("finish joining " + fileName);
			outfile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<String> split(String fileName, String dirName, long splitlen) {
		long leninfile = 0; 
		long leng = 0;
		int count = 1, data;
		List<String> splitList = new ArrayList<String>();
		try {
			String localFileName = FileUtils.getLocalFileName(fileName, dirName);
			File filename = new File(localFileName);
			InputStream infile = new BufferedInputStream(new FileInputStream(filename));
			data = infile.read();
			while (data != -1) {
				String localSplitFileName = fileName + count + ".sp";
				splitList.add(localSplitFileName);
				
				String splitFileName = FileUtils.getLocalFileName(localSplitFileName, dirName);
				filename = new File(splitFileName);
				OutputStream outfile = new BufferedOutputStream(new FileOutputStream(filename));
				while (data != -1 && leng < splitlen) {
					outfile.write(data);
					leng++;
					data = infile.read();
				}
				leninfile += leng;
				leng = 0;
				outfile.close();
				count++;
			}
			infile.close();
			System.out.println("finish spliting " + fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return splitList;
	}
}
