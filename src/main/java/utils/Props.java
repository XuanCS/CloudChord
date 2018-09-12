package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
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
		System.out.println("successfully written to " + propFileName);

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

	public static void updateProp(String key, String value, String propFileName) {
		FileInputStream in;
		try {
			in = new FileInputStream(propFileName);

			Properties props = new Properties();
			props.load(in);
			in.close();

			FileOutputStream out = new FileOutputStream(propFileName);
			props.setProperty(key, value);
			props.store(out, null);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("successfully update to " + propFileName);
	}

	public static String seekProp(String target, String propFileName) {
		FileInputStream in;
		try {
			in = new FileInputStream(propFileName);

			Properties props = new Properties();
			props.load(in);
			in.close();

			// Iterating properties using For-Each
			Set<String> keys = props.stringPropertyNames();
			return findSameKey(keys, target, props);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static String findSameKey(Set<String> keys,String target,  Properties props) {
		for (String key : keys) {
			if (key.equals(target)) {
				return props.getProperty(target);
			}
		}
		System.out.println("cannot file in local record");
		return null;
	}

	public static List<String> seekPrefixKey(String target, String propFileName) {
		List<String> arrList = new ArrayList<>();	
		FileInputStream in;
		try {
			in = new FileInputStream(propFileName);
			Properties props = new Properties();
			props.load(in);
			in.close();

			// Iterating properties using For-Each
			Set<String> keys = props.stringPropertyNames();
			for (String key : keys) {
				if (key.startsWith((target))) {
					arrList.add(key);
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (arrList.isEmpty()) {
			System.out.println("does not send out the target file");
		}
		return arrList;
	}
}
