package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import chord.Gcloud;
import chord.Node;
import fileSys.FileMsg;
import fileSys.Msg;
import fileSys.SigMsg;

public class Helper {

	private static HashMap<Integer, Long> powerOfTwo = null;
	// private static String prefix = "recv_";
	public static final String DOWNLOADS = "_Downloads";
	public static final String chordPrefix = "Chord_";
	public static final String cpPrefix = "cp_";

	public static String home_path = "/Users/luxuan/Documents/workspace/CloudChord";
	public static final String SENT_FILE_LIST = "/sent_file.props";
	public static final String CLOUD_LIST = "/cloud.props";
	public static final String NAME_LIST = "/name.props";
	public static final String NODES_INFO = "nodes.props";
	public static final String CLIENT_SECRET = "client_secret.json";

	public static final int CHORD_SIG = 0;
	public static final int UPLOAD_SIG = 1;
	public static final int DOWNLOAD_SIG = 2;
	public static final int FILESOCK_SIG = 3;
	public static final int FILEMOVE_SIG = 4;

	public static final String fileCmd = "FILE";
	public static final String ACTIVE = "Active";
	public static final String INACTIVE = "Inactive";

	public static final long blockLen = 256;
	public static final long lbLimit = 2048;
	public static long totalFileSize = 0;

	public Helper() {
		// initialize power of two table
		powerOfTwo = new HashMap<Integer, Long>();
		long base = 1;
		for (int i = 0; i <= 32; i++) {
			powerOfTwo.put(i, base);
			base *= 2;
		}
	}

	public static long hashSocketAddress(InetSocketAddress addr) {
		int i = addr.hashCode();
		return hashHashCode(i);
	}

	public static long hashString(String s) {
		int i = s.hashCode();
		return hashHashCode(i);
	}

	/**
	 * Compute a 32 bit integer's identifier
	 * 
	 * @param i:
	 *            integer
	 * @return 32-bit identifier in long type
	 */
	private static long hashHashCode(int i) {

		// 32 bit regular hash code -> byte[4]
		byte[] hashbytes = new byte[4];
		hashbytes[0] = (byte) (i >> 24);
		hashbytes[1] = (byte) (i >> 16);
		hashbytes[2] = (byte) (i >> 8);
		hashbytes[3] = (byte) (i /* >> 0 */);

		// try to create SHA1 digest
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// successfully created SHA1 digest
		// try to convert byte[4]
		// -> SHA1 result byte[]
		// -> compressed result byte[4]
		// -> compressed result in long type
		if (md != null) {
			md.reset();
			md.update(hashbytes);
			byte[] result = md.digest();

			byte[] compressed = new byte[4];
			for (int j = 0; j < 4; j++) {
				byte temp = result[j];
				for (int k = 1; k < 5; k++) {
					temp = (byte) (temp ^ result[j + k]);
				}
				compressed[j] = temp;
			}

			long ret = (compressed[0] & 0xFF) << 24 | (compressed[1] & 0xFF) << 16 | (compressed[2] & 0xFF) << 8
					| (compressed[3] & 0xFF);
			ret = ret & (long) 0xFFFFFFFFl;
			return ret;
		}
		return 0;
	}

	/**
	 * Normalization, computer universal id's value relative to local id (regard
	 * local node as 0)
	 * 
	 * @param original:
	 *            original/universal identifier
	 * @param n:
	 *            node's identifier
	 * @return relative identifier
	 */
	public static long computeRelativeId(long universal, long local) {
		long ret = universal - local;
		if (ret < 0) {
			ret += powerOfTwo.get(32);
		}
		return ret;
	}

	/**
	 * Compute a socket address' SHA-1 hash in hex and its approximate position
	 * in string
	 * 
	 * @param addr
	 * @return
	 */
	public static String hexIdAndPosition(InetSocketAddress addr) {
		long hash = hashSocketAddress(addr);
		return (longTo8DigitHex(hash) + " (" + hash * 100 / Helper.getPowerOfTwo(32) + "%)");
	}

	public static String hexFileNameAndPosition(String fileName) {
		int i = fileName.hashCode();
		Long hash = hashHashCode(i);
		return (longTo8DigitHex(hash) + " (" + hash * 100 / Helper.getPowerOfTwo(32) + "%)");

	}

	/**
	 * 
	 * @param Generate
	 *            a long type number's 8-digit hex string
	 * @return
	 */
	public static String longTo8DigitHex(long l) {
		String hex = Long.toHexString(l);
		int lack = 8 - hex.length();
		StringBuilder sb = new StringBuilder();
		for (int i = lack; i > 0; i--) {
			sb.append("0");
		}
		sb.append(hex);
		return sb.toString();
	}

	/**
	 * Return a node's finger[i].start, universal
	 * 
	 * @param node:
	 *            node's identifier
	 * @param i:
	 *            finger table index
	 * @return finger[i].start's identifier
	 */
	public static long ithStart(long nodeid, int i) {
		return (nodeid + powerOfTwo.get(i - 1)) % powerOfTwo.get(32);
	}

	/**
	 * Get power of 2
	 * 
	 * @param k
	 * @return 2^k
	 */
	public static long getPowerOfTwo(int k) {
		return powerOfTwo.get(k);
	}

	/**
	 * Generate requested address by sending request to server
	 * 
	 * @param server
	 * @param req:
	 *            request
	 * @return generated socket address, might be null if (1) invalid input (2)
	 *         response is null (typically cannot send request) (3) fail to
	 *         create address from reponse
	 * @throws IOException
	 */
	public static InetSocketAddress requestAddress(InetSocketAddress server, String req) {

		// invalid input, return null
		if (server == null || req == null) {
			return null;
		}

		// send request to server
		String response = sendRequest(server, req);

		// if response is null, return null
		if (response == null) {
			return null;
		}

		// or server cannot find anything, return server itself
		else if (response.startsWith("NOTHING"))
			return server;

		// server find something,
		// using response to create, might fail then and return null
		else {
			InetSocketAddress ret = Helper.createSocketAddress(response.split("_")[1]);
			return ret;
		}
	}

	/**
	 * Send request to server and read response
	 * 
	 * @param server
	 * @param request
	 * @return response, might be null if (1) invalid input (2) cannot open
	 *         socket or write request to it (3) response read by
	 *         inputStreamToString() is null
	 * @throws IOException
	 */
	public static String sendRequest(InetSocketAddress server, String req) {

		// invalid input
		if (server == null || req == null)
			return null;

		Socket talkSocket = null;
		try {
			talkSocket = new Socket(server.getAddress(), server.getPort());
			ObjectOutputStream outputStream = new ObjectOutputStream(talkSocket.getOutputStream());
			SigMsg msg = new SigMsg(CHORD_SIG, req);
			outputStream.writeObject(msg);

		} catch (IOException e) {
			return null;
		}

		try {
			Thread.sleep(60);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// get input stream, try to read something from it
		ObjectInputStream input = null;
		try {
			input = new ObjectInputStream(talkSocket.getInputStream());
		} catch (IOException e) {
			System.out.println("Cannot get input stream from " + server.toString() + "\nRequest is: " + req + "\n");
		}
		String response = Helper.inputStreamToString(input, null);

		// try to close socket
		try {
			talkSocket.close();
		} catch (IOException e) {
			throw new RuntimeException("Cannot close socket", e);
		}
		return response;
	}

	/**
	 * Create InetSocketAddress using ip address and port number
	 * 
	 * @param addr:
	 *            socket address string, e.g. 127.0.0.1:8080
	 * @return created InetSocketAddress object; return null if: (1) not valid
	 *         input (2) cannot find split input into ip and port strings (3)
	 *         fail to parse ip address.
	 */
	public static InetSocketAddress createSocketAddress(String addr) {

		// input null, return null
		if (addr == null) {
			return null;
		}

		// split input into ip string and port string
		String[] splitted = addr.split(":");

		// can split string
		if (splitted.length >= 2) {

			// get and pre-process ip address string
			String ip = splitted[0];
			if (ip.startsWith("/")) {
				ip = ip.substring(1);
			}

			// parse ip address, if fail, return null
			InetAddress m_ip = null;
			try {
				m_ip = InetAddress.getByName(ip);
			} catch (UnknownHostException e) {
				System.out.println("Cannot create ip address: " + ip);
				return null;
			}

			// parse port number
			String port = splitted[1];
			int m_port = Integer.parseInt(port);

			// combine ip addr and port in socket address
			return new InetSocketAddress(m_ip, m_port);
		}

		// cannot split string
		else {
			return null;
		}

	}

	/**
	 * Send File to server and read response
	 * 
	 */
	public static String sendFile(InetSocketAddress server, String dirName, String fileName, String lockSock, boolean fromDownFolder) {
		// invalid input
		if (server == null || fileName == null)
			return null;

		Socket talkSocket = null;
		try {
			talkSocket = new Socket(server.getAddress(), server.getPort());
			String downLoadDirName = dirName + Helper.DOWNLOADS;
			byte[] myByteArray = null;

			// read file from local or download folder
			if (fromDownFolder) {
				myByteArray = FileUtils.readFile(fileName, downLoadDirName);
			} else {
				myByteArray = FileUtils.readFile(fileName, dirName);
			}
			int fileSize = myByteArray.length;

			ObjectOutputStream outputStream = new ObjectOutputStream(talkSocket.getOutputStream());
			System.out.println("Sending " + fileName);

			FileMsg msg = prepUploadMsg(dirName, fileName, lockSock, fromDownFolder, fileSize, myByteArray);
			outputStream.writeObject(msg);
			if (fromDownFolder) {
				FileUtils.deletelocalFile(downLoadDirName, fileName);
			}

		} catch (IOException e) {
			return null;
		}

		// sleep for a short time, waiting for response
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// get input stream, try to read something from it
		ObjectInputStream input = null;
		try {
			input = new ObjectInputStream(talkSocket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		String response = Helper.inputStreamToString(input, dirName);

		try {
			talkSocket.close();
		} catch (IOException e) {
			throw new RuntimeException("Cannot close socket", e);
		}
		return response;
	}

	public static String sendQueryFile(InetSocketAddress server, String dirName, String fileName) {
		// invalid input
		if (server == null || fileName == null)
			return null;

		Socket talkSocket = null;

		// try to open talkSocket, output request to this socket
		// return null if fail to do so
		try {
			talkSocket = new Socket(server.getAddress(), server.getPort());
			ObjectOutputStream outputStream = new ObjectOutputStream(talkSocket.getOutputStream());
			FileMsg msg = new FileMsg(DOWNLOAD_SIG, Helper.fileCmd);
			msg.setFileName(fileName);
			outputStream.writeObject(msg);

			System.out.println("sending Query Msg: " + fileName + " success");

		} catch (IOException e) {
		}

		// get input stream, try to read something from it
		ObjectInputStream input = null;
		try {
			input = new ObjectInputStream(talkSocket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		String response = Helper.inputStreamToString(input, dirName);

		try {
			talkSocket.close();
		} catch (IOException e) {
			throw new RuntimeException("Cannot close socket", e);
		}
		return response;

	}

	public static String inputStreamToString(ObjectInputStream inStream, String dirName) {

		// invalid input
		if (inStream == null) {
			return null;
		}

		Msg msg;
		String res = "";
		try {
			msg = (Msg) inStream.readObject();
			int type = msg.getType();
			if (type == CHORD_SIG) {
				SigMsg sigMsg = (SigMsg) msg;
				String sigCmd = sigMsg.getCmd();
				res = sigCmd;
			} else if (type == UPLOAD_SIG) {
				if (dirName == null) {
					System.out.println("need to inialize local directory first");
				}
				String fileName = uploadFileInfo(msg, dirName);
				FileMsg file = (FileMsg) msg;
				String localSockDir = file.getFileSockInfo();

				// upload to cloud
				System.out.println("uploading to cloud...");
				Gcloud gc = new Gcloud(dirName);
				gc.uploadTextFile(fileName, localSockDir);
				FileUtils.deletelocalFile(dirName, fileName);
				res = fileCmd;
			} else if (type == DOWNLOAD_SIG) {
				if (dirName == null) {
					System.out.println("need to inialize local directory first");
				}
				// retrieve file info
				FileMsg file = (FileMsg) msg;
				String fileName = file.getFileName();
				String downFileName = fileName;

				System.out.println("downloading from cloud...");
				// download from local cloud
				Gcloud gc = new Gcloud(dirName);
				gc.downLoadFile(fileName);
				res = "DOWNLOAD#" + downFileName;
			} else if (type == FILESOCK_SIG) {
				fileSockInfo(msg, dirName);
				res = fileCmd;
			} else if (type == FILEMOVE_SIG) {
				FileMsg file = (FileMsg) msg;
				String encfileName = file.getFileName();
				String sucNode = new String(file.getContents());
				System.out.println("encfileName: " + encfileName);
				System.out.println("sucNode: " + sucNode);

				String nameProp = dirName + Helper.NAME_LIST;
				String sentProp = dirName + Helper.SENT_FILE_LIST;

				String srcFileName = Props.seekProp(encfileName, nameProp);
				System.out.println("srcFileName: " + srcFileName);
				Props.updateProp(srcFileName, sucNode, sentProp);
				System.out.println("succesfully update dest of " + srcFileName + "to " + sucNode);
			} else {
				System.out.println("ERROR CMD, please Resend");
				return null;
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	public static void downSendAllCloudFiles(String dirName, InetSocketAddress successor,
			boolean isLastNode) {
		String propFileName = dirName + Helper.CLOUD_LIST;
		FileInputStream in;
		try {
			in = new FileInputStream(propFileName);
			Properties props = new Properties();
			props.load(in);
			in.close();

			// last node do not migrate files, only delete cloud files
			Gcloud gc = new Gcloud(dirName);
			if (isLastNode) {
				for (String key : props.stringPropertyNames()) {
					String res = props.getProperty(key);
					gc.directDelFile(res);
					System.out.println();
				}
			} else {
				// download and send to successor
				for (String key : props.stringPropertyNames()) {
					gc.downLoadFile(key);
					String srcFileName = key.split("_")[0];
					String localSock = key.split("_")[1];
					
					String tmp_response = Helper.sendFile(successor, dirName, srcFileName, localSock, true);
					System.out.println("sending: " + srcFileName + " success");
					System.out.println("feedback: " + tmp_response);
					System.out.println();
					
					String succIP = successor.getAddress().toString().split("/")[1];
					String succPort = Integer.toString(successor.getPort());
					String succSock = succIP + " " + succPort;
					System.out.println("succSock: " + succSock);

					String oriFileNode = key.split("_")[1];
					System.out.println("oriFileNode: " + oriFileNode);
					String srcIP = oriFileNode.split(" ")[0];
					String srcPort = oriFileNode.split(" ")[1];
					InetSocketAddress srcSock = Helper.createSocketAddress(srcIP + ":" + srcPort);

					Helper.sendSentPropSig(srcFileName, succSock, srcSock);
				}
				// delete cloud.prop
				for (String key : props.stringPropertyNames()) {
					String res = props.getProperty(key);
					gc.directDelFile(res);
					System.out.println();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void sendSentPropSig(String encFileName, String succNode, InetSocketAddress srcSock) {
		Socket talkSocket = null;
		try {
			talkSocket = new Socket(srcSock.getAddress(), srcSock.getPort());
			ObjectOutputStream outputStream = new ObjectOutputStream(talkSocket.getOutputStream());
			System.out.println("updating " + encFileName + "to " + succNode);
			FileMsg msg = prepFileMvMsg(encFileName, succNode);
			outputStream.writeObject(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void downSendOneCloudFile(String fileName, String dirName, InetSocketAddress successor,
			boolean isLastNode) {
		String propFileName = dirName + Helper.CLOUD_LIST;
		// String res = Props.findPrefixKey(fileName, propFileName);
		System.out.println("res key is: " + fileName);
		String res = Props.findPrefixKeyValue(fileName, propFileName)[1];
		String fileSock = Props.findPrefixKeyValue(fileName, propFileName)[0];
		Gcloud gc = new Gcloud(dirName);
		if (isLastNode) {
			gc.directDelFile(res);
		} else {
			gc.downLoadFile(fileName);
			// gc.directDelFile(res);
			String tmp_response = Helper.sendFile(successor, dirName, fileName, fileSock, true);
			System.out.println("sending: " + fileName + " success");
			System.out.println("feedback: " + tmp_response);
		}
	}

	private static String uploadFileInfo(Msg msg, String dirName) {
		// receive the target file
		FileMsg file = (FileMsg) msg;
		String fileName = file.getFileName();
		byte[] contents = file.getContents();

		FileUtils.writeFile(fileName, dirName, contents);
		System.out.println("Object received: " + msg);
		return fileName;
	}

	private static void fileSockInfo(Msg msg, String dirName) {
		FileMsg file = (FileMsg) msg;
		String fileName = file.getFileName();
		byte[] contents = file.getContents();

		String downLoadDirName = dirName + Helper.DOWNLOADS;
		FileUtils.writeFile(fileName, downLoadDirName, contents);
		;

		System.out.println("finished writing to file");
		System.out.println("Object received: " + msg);
	}

	private static FileMsg prepUploadMsg(String dirName, String fileName, String lockSock, boolean fromDownFolder, int fileSize,
			byte[] myByteArray) {

		FileMsg msg = new FileMsg(UPLOAD_SIG, Helper.fileCmd);
		msg.setFileName(fileName);
		msg.setFileSize(fileSize);
		msg.setContents(myByteArray);
		msg.setFileSockInfo(lockSock);
		return msg;
	}

	private static FileMsg prepFileMvMsg(String fileName, String sucNode) {
		FileMsg msg = new FileMsg(FILEMOVE_SIG, Helper.fileCmd);
		msg.setFileName(fileName);
		byte[] myByteArray = sucNode.getBytes();
		msg.setContents(myByteArray);
		return msg;
	}

	// just generate one copy
	public static List<String> genTotalList(List<String> splitList, String DirName) {
		List<String> cpList = new ArrayList<>();
		for (String splitFile : splitList) {
			String cpSplitFileName = Helper.cpPrefix + splitFile;
			FileUtils.duplicateFile(splitFile, DirName, cpSplitFileName);
			cpList.add(cpSplitFileName);
		}
		List<String> allList = new ArrayList<>();
		allList.addAll(splitList);
		allList.addAll(cpList);
		return allList;
	}

	public static String genCldProSurfix(String fileSockDir, String title) {
		return title + "_" + fileSockDir;
	}

	public static boolean checkLastNode(Node m_node, InetSocketAddress localAddress) {
		// check whether this is the last node
		InetSocketAddress successor = m_node.getSuccessor();
		InetSocketAddress predecessor = m_node.getPredecessor();
		boolean isRes = false;
		if ((predecessor == null || predecessor.equals(localAddress))
				&& (successor == null || successor.equals(localAddress))) {
			isRes = true;
			System.out.println("This is the last Node");
		}
		return isRes;
	}

	// public static String getCldFileName(String targetFN) {
	// return targetFN.split("_")[1];
	// }
}
