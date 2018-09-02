import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Chord class that offers the UI to create chord node and join a existing chord
 * ring.
 *
 */

public class Chord {

	private static Node m_node;
	private static InetSocketAddress m_contact;
	private static Helper m_helper;
	private static InetSocketAddress localAddress;

	public static void main(String[] args) throws IOException {

		m_helper = new Helper();

		// get local machine's ip
		String local_ip = null;
		try {
			local_ip = InetAddress.getLocalHost().getHostAddress();

		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("");
		// create node

		System.out.println("initiator or join? 1 for initiate Chord, 2 for join the Chord");
		Scanner scan = new Scanner(System.in);
		int choice = scan.nextInt();
		int localPortNum = 0;
		String targetIP = null;
		int targetIPPort = 0;

		if (choice == 1) {
			System.out.println("please input local port number");
			localPortNum = scan.nextInt();
			System.out.println("initiating");

		} else if (choice == 2) {
			System.out.println("please input local port number, target chord ip and port number");
			localPortNum = scan.nextInt();
			targetIP = scan.next();
			targetIPPort = scan.nextInt();
			System.out.println("waiting for join");
		}

		// create local and downloads directory based on folder
		String DirName = Helper.createFolder(Integer.toString(localPortNum));
		String downloadDirName = Helper.createFolder(Integer.toString(localPortNum) + Helper.DOWNLOADS);

		localAddress = Helper.createSocketAddress(local_ip + ":" + localPortNum);	
		m_node = new Node(localAddress, DirName);

		// determine if it's creating or joining a existing ring
		// create, contact is this node itself
		if (choice == 1) {
			m_contact = m_node.getAddress();
		}

		// join, contact is another node
		else if (choice == 2) {
			m_contact = Helper.createSocketAddress(targetIP + ":" + targetIPPort);
			if (m_contact == null) {
				System.out.println("Cannot find address you are trying to contact. Now exit.");
				return;
			}
		}

		else {
			System.out.println("Wrong input. Now exit.");
			System.exit(0);
		}

		// try to join ring from contact node
		boolean successful_join = m_node.join(m_contact);

		// fail to join contact node
		if (!successful_join) {
			System.out.println("Cannot connect with node you are trying to contact. Now exit.");
			System.exit(0);
		}

		// print join info
		System.out.println("Joining the Chord ring.");
		System.out.println("Local IP: " + local_ip);
		m_node.printNeighbors();

		// begin to take user input, "info" or "quit"
		while (true) {
			System.out.println(
					"\nType \"info\" to check this node's data or \n type \"quit\" to leave ring or \n type \"file\"to transfer files: ");
			Scanner userinput = new Scanner(System.in);
			String command = userinput.next();
			if (command.startsWith("quit")) {
				InetSocketAddress successor = m_node.getSuccessor();
				
				// iterate all files in cloud
				String localSock = local_ip + " " + localPortNum;

				Helper.downSendAllCloudFiles(DirName, localSock, successor);
		
				m_node.stopAllThreads();
				System.out.println("Leaving the ring...");
				System.exit(0);

			} else if (command.startsWith("info")) {
				m_node.printDataStructure();
			} else if (command.startsWith("file")) {
				System.out.println("please input file name:");
				String fileName = userinput.next();
				System.out.println("file operations-- 1: upload, 2: download");
				int op = userinput.nextInt();

				long hash = Helper.hashString(fileName);
				System.out.println("\nHash value is " + Long.toHexString(hash));
				InetSocketAddress result = Helper.requestAddress(localAddress, "FINDSUCC_" + hash);

				// if fail to send request, local node is disconnected, exit
				if (result == null) {
					System.out.println("The node your are contacting is disconnected. Now exit.");
					System.exit(0);
				}

				// print out response
				System.out.println("\nResponse from node " + localAddress.getAddress().toString() + ", port "
						+ localAddress.getPort() + ", position " + Helper.hexIdAndPosition(localAddress) + ":");
				System.out.println("Node " + result.getAddress().toString() + ", port " + result.getPort()
						+ ", position " + Helper.hexIdAndPosition(result));

				// upload command
				if (op == 1) {
					// keep track of all the uploaded files from current node
					String targetFilePath = DirName + "/" + fileName;
					File targetFile = new File(targetFilePath);
					if (!targetFile.exists()) {
						System.err.println("cannot upload the target file, the target is not in the user's directory");
					} else {
						if (result.equals(localAddress)) {
							Gcloud gc = new Gcloud(DirName);
							gc.uploadTextFile(fileName);
							
							String propFileName = DirName + Helper.RECV_FILE_LIST;
							File propFile = new File(propFileName);
							String sentSockStr = result.getHostString() + " "+result.getPort();
							if (propFile.exists()) {
								Helper.updateProp(fileName, sentSockStr, propFileName);
							} else {
								Helper.writeProp(fileName, sentSockStr, propFileName);
							}
						} else {
							String localSock = local_ip + " " + localPortNum;
							String tmp_response = Helper.sendFile(result, DirName, fileName, localSock, false);
							System.out.println("sending: " + fileName + " success");
							System.out.println("feedback: " + tmp_response);
						}

						// keep track of all the uploaded files from current
						// node
						String propFileName = DirName + Helper.SENT_FILE_LIST;
						File propFile = new File(propFileName);
//						System.out.println("res addr: " + result.getHostString());
						String sentSockStr = result.getHostString() + " "+ result.getPort();
						if (propFile.exists()) {
							Helper.updateProp(fileName, sentSockStr, propFileName);
						} else {
							Helper.writeProp(fileName, sentSockStr, propFileName);
						}
					}
					// System.err.println("run 1");
				}
				// download command
				else if (op == 2) {
					// check whether is the legal file from current node
					String propFileName = DirName + Helper.SENT_FILE_LIST;
					String queryRes = Helper.readProp(fileName, propFileName);
					if (queryRes == null) {
						System.err.println(
								"cannot download the target file, the target file does not belong with the current user");
					} else {
						if (result.equals(localAddress)) {
							Gcloud gc = new Gcloud(DirName);
							gc.downLoadFile(fileName);
						} else {
							String res = Helper.sendQueryFile(result, DirName, fileName);
							System.out.println("feedback: " + res);
						}
					}
				} else {
					System.out.println("invalid file operation");
				}
				// System.err.println("run 2");
			}
			// System.err.println("run 3");
		}
	}
}
