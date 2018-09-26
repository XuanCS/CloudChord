package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import frontEnd.Log;

public class Props {
	public static void writeProp(String key, String value, String propFileName) {
		Properties prop = new Properties();
		OutputStream output = null;
		try {
			output = new FileOutputStream(propFileName);
			prop.setProperty(key, value);
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
		Properties props = loadProp(propFileName);

		InputStream input;
		try {
			input = new FileInputStream(propFileName);
			// load a properties file
			props.load(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// get the property value and print it out
		String res = props.getProperty(key);
		System.out.println(res);
		return res;
	}

	public static void updateProp(String key, String value, String propFileName) {
		Properties props = loadProp(propFileName);
		FileOutputStream out;
		try {
			out = new FileOutputStream(propFileName);
			props.setProperty(key, value);
			props.store(out, null);
			out.close();
			System.out.println("successfully update to " + propFileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String seekProp(String target, String propFileName) {
		Properties props = loadProp(propFileName);
		// Iterating properties using For-Each
		Set<String> keys = props.stringPropertyNames();
		return findSameKey(keys, target, props);

	}

	public static String getRandPropFile(String propFileName) {
		Properties props = loadProp(propFileName);
		// Iterating properties using For-Each
		Set<String> keys = props.stringPropertyNames();
		for (String str : keys) {
			if (str.length() > 0) {
				return str;
			}
		}
		return "";
	}

	private static String findSameKey(Set<String> keys, String target, Properties props) {
		for (String key : keys) {
			if (key.equals(target)) {
				return props.getProperty(target);
			}
		}
		System.out.println("cannot find file in local record");
		return null;
	}

	public static List<String> seekPrefixKey(String target, String propFileName) {
		List<String> arrList = new ArrayList<>();
		Properties props = loadProp(propFileName);
		// Iterating properties using For-Each
		Set<String> keys = props.stringPropertyNames();
		for (String key : keys) {
			if (key.startsWith((target))) {
				arrList.add(key);
			}
		}
		if (arrList.isEmpty()) {
			System.out.println("does not send out the target file");
		}
		return arrList;
	}

	public static String findPrefixKey(String target, String propFileName) {
		Properties props = loadProp(propFileName);
		// Iterating properties using For-Each
		Set<String> keys = props.stringPropertyNames();
		for (String key : keys) {
			if (key.startsWith((target))) {
				System.out.println("key is: " + key);
				String sock = key.split("_")[1];
				System.out.println("sock is: " + sock);
				return key;
			}
		}
		return null;
	}

	public static String findPrefixValue(String target, String propFileName) {
		Properties props = loadProp(propFileName);
		// Iterating properties using For-Each
		Set<String> keys = props.stringPropertyNames();
		for (String key : keys) {
			if (key.startsWith((target))) {
				System.out.println("key is: " + key);
				String sock = key.split("_")[1];
				System.out.println("sock is: " + sock);
				return props.getProperty(key);
			}
		}
		return null;
	}

	public static void rmPropKey(String key, String propFileName) {
		FileInputStream in;
		try {
			in = new FileInputStream(propFileName);
			FileOutputStream out = new FileOutputStream(propFileName);

			Properties props = new Properties();
			props.load(in);
			in.close();
			props.remove(key);
			props.store(out, null);
			out.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void outputSentPropKV(String dirName) {
		String propFileName = dirName + Helper.SENT_FILE_LIST;
		Properties props = loadProp(propFileName);
		Set<String> keys = props.stringPropertyNames();
		Log.print();
		for (String key : keys) {
			String value = props.getProperty(key);
			String tmpOutput = key + "\t\t" + value;
			Log.print(tmpOutput);
		}
	}
	
	public static void outputCloudPropKV(String dirName) {
		String propFileName = dirName + Helper.CLOUD_LIST;
		Properties props = loadProp(propFileName);
		Set<String> keys = props.stringPropertyNames();
		Log.print();
		for (String key : keys) {
			String value = props.getProperty(key);
			String prefixFN = key.split("_")[0];
			String tmpOutput = prefixFN + "\t" + value;
			Log.print(tmpOutput);
		}
	}
	
	public static void outputNamePropKV(String dirName) {
		String propFileName = dirName + Helper.NAME_LIST;
		Properties props = loadProp(propFileName);
		Set<String> keys = props.stringPropertyNames();
		Log.print();
		for (String key : keys) {
			String value = props.getProperty(key);
			String tmpOutput = value + "\t" + key;
			Log.print(tmpOutput);
		}
	}

	private static Properties loadProp(String propFileName) {
		FileInputStream in;
		Properties props = null;
		try {
			in = new FileInputStream(propFileName);
			props = new Properties();
			props.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return props;
	}
	
	public static String[] getActiveNodesInfo() {
		List<String> arrList = new ArrayList<>();
		String propFileName = Helper.NODES_INFO;
		Properties props = loadProp(propFileName);
		Set<String> keys = props.stringPropertyNames();
		for(String key : keys) {
			String value = props.getProperty(key);
			if (value.equals(Helper.ACTIVE)) {
				arrList.add(key);
			}
		}
		String[] nodesArr = new String[arrList.size()];
		nodesArr = arrList.toArray(nodesArr);
		return nodesArr;
	}
}
