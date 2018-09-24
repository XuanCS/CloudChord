package frontEnd;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import chord.Gcloud;
import chord.Node;
import utils.Encryption;
import utils.FileUtils;
import utils.Helper;
import utils.Props;
import utils.SplitFile;

public class Main implements ActionListener {

	private static final int FRAME_WIDTH = 850;
	private static final int FRAME_HEIGHT = 650;

	private static final int FIELD_WIDTH = 250;
	private static final int FIELD_HEIGHT = 35;

	private static final int LABEL_WIDTH = 150;
	private static final int LABEL_LONG_WIDTH = 350;
	private static final int LABEL_HEIGHT = 30;

	private static final int button_WIDTH = 75;
	private static final int button_HEIGHT = 35;
	private static final int button_LG_HEIGHT = 50;

	private static final int TEXTAREA_WIDTH = 650;
	private static final int TEXTAREA_HEIGHT = 250;

	private static final int firstX_loc = 25;
	private static final int secondX_loc = 300;
	private static final int thirdX_loc = 450;
	private static final int fourthX_loc = 550;
	private static final int fifthX_loc = 650;
	private static final int lastX_loc = 750;

	private static final int aboveFirstLineY_loc = 10;
	private static final int firstLineY_loc = 35;
	private static final int aboveSecondLineY_loc = 80;
	private static final int secondLineY_loc = 105;
	private static final int belowSecondLineY_loc = 140;
	private static final int thirdLineY_loc = 175;
	private static final int belowThirdLineY_loc = 210;
	private static final int fourthLineY_loc = 245;
	private static final int fifthLineY_loc = 315;
	private static final int lastLineY_loc = 400;

	private JFrame frame;
	public static JTextArea output;

	private JTextField starter;
	private JTextField follower;
	private JTextField uploadField;
	private JTextField downloadField;

	private JLabel illegalUpload;
	private JLabel illegalDownload;
	private JLabel illegalClick;

	private static Node m_node;
	private static InetSocketAddress m_contact;
	private static Helper m_helper;
	public static InetSocketAddress localAddress;

	private String local_ip = null;
	private String localPortNum;
	private String DirName;

	public Main() {
		frame = new JFrame("Cloud Chord App");
		output = new JTextArea("");
		illegalUpload = new JLabel();
		illegalDownload = new JLabel();
		illegalClick = new JLabel();
		m_helper = new Helper();

		try {
			local_ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
	}

	public void mainFrame() {
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		output.setEditable(false);
		output.setBounds(firstX_loc, fifthLineY_loc, TEXTAREA_WIDTH, TEXTAREA_HEIGHT);

		JScrollPane taScroll = new JScrollPane(output); 
		taScroll.setBounds(firstX_loc, fifthLineY_loc, TEXTAREA_WIDTH, TEXTAREA_HEIGHT);

		// set label
		JPanel panel = new JPanel();
		panel.setLayout(null); // add components by coordinates

		JLabel M_dir = new JLabel("Initiate");
		M_dir.setBounds(firstX_loc, aboveFirstLineY_loc, LABEL_WIDTH, LABEL_HEIGHT);
		panel.add(M_dir);

		JLabel M_star = new JLabel("Join");
		M_star.setBounds(firstX_loc, aboveSecondLineY_loc, LABEL_WIDTH, LABEL_HEIGHT);
		panel.add(M_star);

		JLabel info = new JLabel("Function");
		info.setBounds(thirdX_loc, aboveFirstLineY_loc, LABEL_WIDTH, LABEL_HEIGHT);
		panel.add(info);

		illegalUpload.setBounds(thirdX_loc, belowSecondLineY_loc, LABEL_LONG_WIDTH, LABEL_HEIGHT);
		illegalUpload.setForeground(Color.RED);
		panel.add(illegalUpload);

		illegalDownload.setBounds(thirdX_loc, belowThirdLineY_loc, LABEL_LONG_WIDTH, LABEL_HEIGHT);
		illegalDownload.setForeground(Color.RED);
		panel.add(illegalDownload);

		illegalClick.setBounds(firstX_loc, thirdLineY_loc, LABEL_LONG_WIDTH, LABEL_HEIGHT);
		illegalClick.setForeground(Color.RED);
		panel.add(illegalClick);

		// set field
		starter = new JTextField();
		starter.setBounds(firstX_loc, firstLineY_loc, FIELD_WIDTH, FIELD_HEIGHT);
		panel.add(starter);

		follower = new JTextField();
		follower.setBounds(firstX_loc, secondLineY_loc, FIELD_WIDTH, FIELD_HEIGHT);
		panel.add(follower);

		uploadField = new JTextField();
		uploadField.setBounds(fourthX_loc, thirdLineY_loc, FIELD_WIDTH, FIELD_HEIGHT);
		panel.add(uploadField);

		downloadField = new JTextField();
		downloadField.setBounds(fourthX_loc, fourthLineY_loc, FIELD_WIDTH, FIELD_HEIGHT);
		panel.add(downloadField);

		JButton startBtn = new JButton("Start");
		startBtn.setBounds(secondX_loc, firstLineY_loc, button_WIDTH, button_HEIGHT);
		startBtn.addActionListener(this);
		panel.add(startBtn);

		JButton joinBtn = new JButton("Join");
		joinBtn.setBounds(secondX_loc, secondLineY_loc, button_WIDTH, button_HEIGHT);
		joinBtn.addActionListener(this);
		panel.add(joinBtn);

		JButton aboutBtn = new JButton("About");
		aboutBtn.setBounds(thirdX_loc, firstLineY_loc, button_WIDTH, button_HEIGHT);
		aboutBtn.addActionListener(this);
		panel.add(aboutBtn);

		JButton ftBtn = new JButton("FigureTbl");
		ftBtn.setBounds(fourthX_loc, firstLineY_loc, button_WIDTH, button_HEIGHT);
		ftBtn.addActionListener(this);
		panel.add(ftBtn);

		JButton quitBtn = new JButton("Quit");
		quitBtn.setBounds(fifthX_loc, firstLineY_loc, button_WIDTH, button_HEIGHT);
		quitBtn.addActionListener(this);
		panel.add(quitBtn);

		JButton uploadBtn = new JButton("Upload");
		uploadBtn.setBounds(thirdX_loc, thirdLineY_loc, button_WIDTH, button_HEIGHT);
		uploadBtn.addActionListener(this);
		panel.add(uploadBtn);

		JButton downloadBtn = new JButton("Download");
		downloadBtn.setBounds(thirdX_loc, fourthLineY_loc, button_WIDTH, button_HEIGHT);
		downloadBtn.addActionListener(this);
		panel.add(downloadBtn);

		JButton checkBtn = new JButton("Check");
		checkBtn.setBounds(lastX_loc, firstLineY_loc, button_WIDTH, button_HEIGHT);
		checkBtn.addActionListener(this);
		// panel.add(checkBtn);

		JButton clearBtn = new JButton("Clear");
		clearBtn.setBounds(lastX_loc, fifthLineY_loc, button_WIDTH, button_LG_HEIGHT);
		clearBtn.addActionListener(this);
		panel.add(clearBtn);

		JButton sentInfoBtn = new JButton("SentInfo");
		sentInfoBtn.setBounds(thirdX_loc, secondLineY_loc, button_WIDTH, button_HEIGHT);
		sentInfoBtn.addActionListener(this);
		panel.add(sentInfoBtn);

		JButton cloudInfoBtn = new JButton("CloudInfo");
		cloudInfoBtn.setBounds(fourthX_loc, secondLineY_loc, button_WIDTH, button_HEIGHT);
		cloudInfoBtn.addActionListener(this);
		panel.add(cloudInfoBtn);

		JButton nameInfoBtn = new JButton("NameInfo");
		nameInfoBtn.setBounds(fifthX_loc, secondLineY_loc, button_WIDTH, button_HEIGHT);
		nameInfoBtn.addActionListener(this);
		panel.add(nameInfoBtn);

		Random rand = new Random();
		int margin= rand.nextInt(25);

		panel.add(taScroll);
		frame.add(panel);
		frame.setVisible(true);
		frame.setLocation(50 + margin, 50 + margin);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String str = e.getActionCommand();
		switch (str) {
		case "Start":
			startBtnCall();
			break;
		case "Join":
			joinBtnCall();
			break;
		case "About":
			aboutBtnCall();
			break;
		case "FigureTbl":
			ftBtnCall();
			break;
		case "Quit":
			quitBtnCall();
			break;
		case "Check":
			checkBtnCall();
			break;
		case "SentInfo":
			sentInfoBtnCall();
			break;
		case "CloudInfo":
			cloudInfoBtnCall();
			break;
		case "NameInfo":
			nameInfoBtnCall();
			break;
		case "Upload":
			uploadBtnCall();
			break;
		case "Download":
			downloadBtnCall();
			break;
		case "Clear":
			clearBtnCall();
			break;
		default:
			break;
		}
	}

	private void startBtnCall() {
		// illegal check

		localPortNum = starter.getText();
		startNodeAndFolder(localPortNum);
		m_contact = m_node.getAddress();
		isJoinRing();
		Log.print("Initiate the Chord ring");
		printCurNodeInfo();
	}

	private void joinBtnCall() {
		String introNode = follower.getText();

		// illegal check

		localPortNum = introNode.split(" ")[0];
		String targetIP = introNode.split(" ")[1];
		String targetIPPort = introNode.split(" ")[2];
		System.out.println("port num: " + localPortNum);
		System.out.println("join ip: " + targetIP);
		System.out.println("join port num: " + targetIPPort);

		startNodeAndFolder(localPortNum);
		m_contact = Helper.createSocketAddress(targetIP + ":" + targetIPPort);
		if (m_contact == null) {
			System.out.println("Cannot find address you are trying to contact. Now exit.");
			return;
		}
		isJoinRing();
		Log.print("Joining the Chord ring");
		printCurNodeInfo();
	}

	private void aboutBtnCall() {
		if (!isClickOK()) {
			return;
		}
		Log.print();
		printCurNodeInfo();
	}

	private void ftBtnCall() {
		if (!isClickOK()) {
			return;
		}
		printFTInfo();
	}

	private void quitBtnCall() {
		if (!isClickOK()) {
			return;
		}
		InetSocketAddress successor = m_node.getSuccessor();
		boolean isLastNode = Helper.checkLastNode(m_node, localAddress);

		System.out.println("Current Node is Last One: " + isLastNode);
		System.out.println("local: " + localAddress);
		// iterate all files in cloud
		String localSock = local_ip + " " + localPortNum;

		Helper.downSendAllCloudFiles(DirName, localSock, successor, isLastNode);

		m_node.stopAllThreads();
		output.setText("send out all files from user's cloud account, Leaving the ring...");
		System.out.println("Leaving the ring...");
		System.exit(0);
	}

	private void checkBtnCall() {

	}

	private void sentInfoBtnCall() {
		if (!isClickOK()) {
			return;
		}
		Props.outputSentPropKV(DirName);
	}

	private void cloudInfoBtnCall() {
		if (!isClickOK()) {
			return;
		}
		Props.outputCloudPropKV(DirName);
	}

	private void nameInfoBtnCall() {
		if (!isClickOK()) {
			return;
		}
		Props.outputNamePropKV(DirName);
	}

	private void uploadBtnCall() {
		if (!isClickOK()) {
			return;
		}
		// get file info, encryption and split file
		String inputFileName = uploadField.getText();
		FileUtils.checkInputFile(inputFileName, localAddress);

		// encrypt and then split and delete the encode file
		String encFileName = Encryption.EncodePrefix + inputFileName;
		Encryption.encrypt(inputFileName, DirName, encFileName);
		List<String> splitList = SplitFile.split(encFileName, DirName, Helper.blockLen);
		FileUtils.deletelocalFile(DirName, encFileName);
		// generate all List
		List<String> allList = Helper.genTotalList(splitList, DirName);

		// send out the each of file
		for (String splitFile : allList) {
			System.out.println("\nCurrent Split File: " + splitFile);

			// send out the split file
			String targetFilePath = FileUtils.getLocalFileName(splitFile, DirName);
			File targetFile = new File(targetFilePath);
			String hashFileName = FileUtils.getFileHash(splitFile);

			if (!targetFile.exists()) {
				illegalUpload.setText("the target file is not in the user's directory");
			} else {
				// rename the split file and update cloud.props
				FileUtils.renameFile(splitFile, hashFileName, DirName);
				// FileUtils.updateNamePropFile(splitFile, DirName,
				// hashFileName);
				String localSock = local_ip + " " + localPortNum;
				System.out.println("sock: " + localSock);

				// send out files
				InetSocketAddress result = FileUtils.getFileSuccessor(hashFileName, localAddress);
				if (result.equals(localAddress)) {
					Gcloud gc = new Gcloud(DirName);
					gc.uploadTextFile(hashFileName, localSock);

					output.setText("file " + splitFile + ", Position is " + Helper.hexFileNameAndPosition(splitFile)
							+ "\nsuccesfully upload file: " + splitFile);
				} else {
					String tmp_response = Helper.sendFile(result, DirName, hashFileName, false);
					System.out.println("sending: " + splitFile + "(" + hashFileName + ")" + " success");
					System.out.println("feedback: " + tmp_response);
					output.setText("file " + splitFile + ", Position is " + Helper.hexFileNameAndPosition(splitFile)
							+ "\nsuccesfully send file to successor cloud account, and upload file: " + splitFile);
				}

				// keep track of all the uploaded files from current
				String sentSockStr = result.getHostString() + " " + result.getPort();
				FileUtils.updateSentPropFile(splitFile, DirName, sentSockStr);
				FileUtils.updateNamePropFile(hashFileName, splitFile, DirName);
			}

			System.out.println("cur total size: " + Helper.totalFileSize);

			// delete all the split files
			FileUtils.deletelocalFile(DirName, hashFileName);
		}
	}

	private void downloadBtnCall() {
		if (!isClickOK()) {
			return;
		}
		String inputFileName = downloadField.getText();
		String encFileName = Encryption.EncodePrefix + inputFileName;
		String sentPropFileName = DirName + Helper.SENT_FILE_LIST;
		List<String> splitList = Props.seekPrefixKey(encFileName, sentPropFileName);

		for (String splitFile : splitList) {
			String targetSock = FileUtils.isFromSentProp(splitFile, DirName);
			InetSocketAddress result = Helper
					.createSocketAddress(targetSock.split(" ")[0] + ":" + targetSock.split(" ")[1]);

			if (targetSock.length() == 0) {
				illegalDownload.setText("the target file does not belong with the current user");
			} else {
				String hashFileName = FileUtils.getFileHash(splitFile);
				if (localAddress.equals(result)) {
					Gcloud gc = new Gcloud(DirName);
					gc.downLoadFile(hashFileName);
					output.setText("file " + splitFile + ", Position is " + Helper.hexFileNameAndPosition(splitFile)
							+ "\nsuccesfully download file: " + splitFile);

				} else {
					String res = Helper.sendQueryFile(result, DirName, hashFileName);
					System.out.println("feedback: " + res);
					output.setText("file " + splitFile + ", Position is " + Helper.hexFileNameAndPosition(splitFile)
							+ "\nsuccesfully download file: " + splitFile + " from target cloud account");
				}
				FileUtils.renameFile(hashFileName, splitFile, DirName + Helper.DOWNLOADS);
			}
			System.out.println();
		}

		// join and decrpt
		String downloadFolder = DirName + Helper.DOWNLOADS;
		SplitFile.join(encFileName, downloadFolder);
		Encryption.decrpt(encFileName, DirName, inputFileName);

		// delete encoded file and split files
		for (String splitFile : splitList) {
			FileUtils.deletelocalFile(downloadFolder, splitFile);
		}
		FileUtils.deletelocalFile(downloadFolder, encFileName);
	}

	private void clearBtnCall() {
		uploadField.setText("");
		downloadField.setText("");
		illegalUpload.setText("");
		illegalDownload.setText("");
		illegalClick.setText("");
	}

	private void startNodeAndFolder(String localPortNum) {
		String cloudPropName = Helper.chordPrefix + localPortNum + Helper.CLOUD_LIST;
		String sentPropName = Helper.chordPrefix + localPortNum + Helper.SENT_FILE_LIST;
		String namePropName = Helper.chordPrefix + localPortNum + Helper.NAME_LIST;

		DirName = FileUtils.createFolder(localPortNum);
		String downloadDirName = FileUtils.createFolder(localPortNum) + Helper.DOWNLOADS;
		FileUtils.createFile(cloudPropName);
		FileUtils.createFile(sentPropName);
		FileUtils.createFile(namePropName);

		localAddress = Helper.createSocketAddress(local_ip + ":" + localPortNum);
		m_node = new Node(localAddress, DirName);
	}

	private boolean isJoinRing() {
		boolean successful_join = m_node.join(m_contact);
		if (!successful_join) {
			System.out.println("Cannot connect with node you are trying to contact. Now exit.");
			System.exit(0);
		}

		System.out.println("Joining the Chord ring.");
		System.out.println("Local IP: " + local_ip);
		return true;
	}

	private void printCurNodeInfo() {
		Log.print("Local IP: " + local_ip + ", \nLocal Port Num: " + localPortNum + "\nYour positions is "
				+ Helper.hexIdAndPosition(localAddress));

		InetSocketAddress successor = m_node.getFiger().get(1);
		InetSocketAddress predecessor = m_node.getPredecessor();

		if ((predecessor == null || predecessor.equals(localAddress))
				&& (successor == null || successor.equals(localAddress))) {
			Log.print("Your predecessor is yourself.");
			Log.print("Your successor is yourself.");
		} else {
			if (predecessor != null) {
				Log.print("Your predecessor is node " + predecessor.getAddress().toString() + ", " + "port "
						+ predecessor.getPort() + ", position " + Helper.hexIdAndPosition(predecessor) + ".");
			} else {
				Log.print("Your predecessor is updating.");
			}

			if (successor != null) {
				Log.print("Your successor is node " + successor.getAddress().toString() + ", " + "port "
						+ successor.getPort() + ", position " + Helper.hexIdAndPosition(successor) + ".");
			} else {
				Log.print("Your successor is updating.");
			}
		}
	}

	private void printFTInfo() {
		InetSocketAddress predecessor = m_node.getPredecessor();
		Map<Integer, InetSocketAddress> finger = m_node.getFiger();

		Log.print();
		Log.print("FINGER TABLE:");
		for (int i = 1; i <= 32; i++) {
			long ithstart = Helper.ithStart(Helper.hashSocketAddress(localAddress), i);
			InetSocketAddress f = finger.get(i);
			StringBuilder sb = new StringBuilder();
			sb.append(i + "\t" + Helper.longTo8DigitHex(ithstart) + "\t\t");
			if (f != null) {
				sb.append(f.toString() + "\t" + Helper.hexIdAndPosition(f));
			}
			else {
				sb.append("NULL");
			}
			Log.print(sb.toString());
		}
	}

	private boolean isClickOK() {
		if (starter.getText().length() == 0 && follower.getText().length() == 0) {
			illegalClick.setText("Need to specify Initiate or Join PortNum above");
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		Main user = new Main();
		user.mainFrame();
	}
}
