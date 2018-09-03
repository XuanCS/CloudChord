package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Set;

public class Props {
	public static void writeProp(String key, String value, String propFileName) {
		Properties prop = new Properties();
		OutputStream output = null;
		try {
			output = new FileOutputStream(propFileName);
			// set the properties value
			prop.setProperty(key, value);
			// save properties to project root folder
			prop.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		System.out.println("successfully written to local cloud.props");

	}

	public static String readProp(String key, String propFileName) {
		Properties prop = new Properties();
		InputStream input = null;
		String res = null;

		try {
			input = new FileInputStream(propFileName);

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			res = prop.getProperty(key);
			System.out.println(res);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return res;
	}

	public static void updateProp(String key, String value, String propFileName) throws IOException {
		FileInputStream in = new FileInputStream(propFileName);
		Properties props = new Properties();
		props.load(in);
		in.close();

		FileOutputStream out = new FileOutputStream(propFileName);
		props.setProperty(key, value);
		props.store(out, null);
		out.close();
		System.out.println("successfully update to local config.property");
	}

	public static String seekProp(String target, String propFileName) throws IOException {
		FileInputStream in = new FileInputStream(propFileName);
		Properties props = new Properties();
		props.load(in);
		in.close();

		// Iterating properties using For-Each

		Set<String> keys = props.stringPropertyNames();
		for (String key : keys) {
			if (key.equals(target)) {
				return props.getProperty(target);
			}
		}
		System.out.println("cannot file in local record");
		return null;
	}
}
