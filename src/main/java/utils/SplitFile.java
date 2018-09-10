package utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class SplitFile {
	public static void join(String fileName) {
		long leninfile = 0, leng = 0;
		int count = 1, data = 0;
		try {
			File filename = new File(fileName);
			// RandomAccessFile outfile = new RandomAccessFile(filename,"rw");

			OutputStream outfile = new BufferedOutputStream(new FileOutputStream(filename));
			while (true) {
				filename = new File(fileName + count + ".sp");
				if (filename.exists()) {
					// RandomAccessFile infile = new
					// RandomAccessFile(filename,"r");
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

	public static void split(String fileName, long splitlen) {
		long leninfile = 0, leng = 0;
		int count = 1, data;
		try {
			File filename = new File(fileName);
			// RandomAccessFile infile = new RandomAccessFile(filename, "r");
			InputStream infile = new BufferedInputStream(new FileInputStream(filename));
			data = infile.read();
			while (data != -1) {
				filename = new File(fileName + count + ".sp");
				// RandomAccessFile outfile = new RandomAccessFile(filename,
				// "rw");
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
			System.out.println("finish spliting " + fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
