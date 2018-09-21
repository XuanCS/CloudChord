package chord;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

import fileSys.FileMsg;
import fileSys.SigMsg;
import utils.FileUtils;
import utils.Helper;

/**
 * Talker thread that processes request accepted by listener and writes response
 * to socket.
 *
 */

public class Talker implements Runnable {

	Socket talkSocket;
	private Node local;
	private String dirName;


	public Talker(Socket _talkSocket, Node _local, String dirName) {
		talkSocket = _talkSocket;
		local = _local;
		this.dirName = dirName;
	}

	public void run() {
		ObjectInputStream input = null;
		ObjectOutputStream output = null;

	
		try {
			input = new ObjectInputStream(talkSocket.getInputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		String request = Helper.inputStreamToString(input, dirName);

		String response;
		try {
			response = processRequest(request);
			SigMsg msg = new SigMsg(0, response);
			if (response != null) {
				output = new ObjectOutputStream(talkSocket.getOutputStream());
				output.writeObject(msg);
			}
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String processRequest(String request) throws IOException {
		InetSocketAddress result = null;
		String ret = null;
		if (request == null) {
			return null;
		}
		if (request.startsWith("CLOSEST")) {
			long id = Long.parseLong(request.split("_")[1]);
			result = local.closest_preceding_finger(id);
			String ip = result.getAddress().toString();
			int port = result.getPort();
			ret = "MYCLOSEST_" + ip + ":" + port;
		} else if (request.startsWith("YOURSUCC")) {
			result = local.getSuccessor();
			if (result != null) {
				String ip = result.getAddress().toString();
				int port = result.getPort();
				ret = "MYSUCC_" + ip + ":" + port;
			} else {
				ret = "NOTHING";
			}
		} else if (request.startsWith("YOURPRE")) {
			result = local.getPredecessor();
			if (result != null) {
				String ip = result.getAddress().toString();
				int port = result.getPort();
				ret = "MYPRE_" + ip + ":" + port;
			} else {
				ret = "NOTHING";
			}
		} else if (request.startsWith("FINDSUCC")) {
			long id = Long.parseLong(request.split("_")[1]);
			result = local.find_successor(id);
			String ip = result.getAddress().toString();
			int port = result.getPort();
			ret = "FOUNDSUCC_" + ip + ":" + port;
		} else if (request.startsWith("IAMPRE")) {
			InetSocketAddress new_pre = Helper.createSocketAddress(request.split("_")[1]);
			local.notified(new_pre);
			ret = "NOTIFIED";
		} else if (request.startsWith("KEEP")) {
			ret = "ALIVE";
		} else if (request.startsWith("TEST")) {
			ret = "TEST_SUCC";
		} else if (request.startsWith("FILE")) {
			ret = "FINISH_RECV";
		} else if (request.startsWith("UPLOAD")) {	
			ret = "FINISH_UPLOAD_RECV";
		}	
		else if (request.startsWith("DOWNLOAD")) {
			String downfName = request.split("#")[1];

			File myFile = new File(dirName + Helper.DOWNLOADS + "/" + downfName);
			if (!myFile.exists()) {
				System.out.print("" + "Downloading File does not exit");
				 return ret;
			}

			byte[] myByteArray = new byte[(int) myFile.length()];
			FileInputStream fis = new FileInputStream(myFile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			bis.read(myByteArray);
			
			ObjectOutputStream outputStream = new ObjectOutputStream(talkSocket.getOutputStream());
			System.out.println("Sending Querying File " + downfName + "(" + myByteArray.length + " bytes)");

			
			FileMsg msg = new FileMsg(Helper.FILESOCK_SIG, Helper.fileCmd);
			msg.setFileName(downfName);			
			int fileLen = (int) myFile.length();
			msg.setFileSize(fileLen);
			msg.setContents(myByteArray);
			
			outputStream.writeObject(msg);
			ret = "FINISH_SENDING_DOWNFILE";
			FileUtils.deletelocalFile(dirName + Helper.DOWNLOADS, downfName);
		}
		else {
			System.out.println("recv header: " + request);
		}
		return ret;
	}

}