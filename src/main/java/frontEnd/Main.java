package frontEnd;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import chord.Gcloud;
import chord.Node;
import utils.Encryption;
import utils.FileUtils;
import utils.Helper;
import utils.Props;
import utils.SplitFile;

public class Main {

	public static final int FRAME_WIDTH = 850;
	public static final int FRAME_HEIGHT = 600;

	public static final int FIELD_WIDTH = 250;
	public static final int FIELD_HEIGHT = 35;

	public static final int LABEL_WIDTH = 150;
	public static final int LABEL_LONG_WIDTH = 350;
	public static final int LABEL_HEIGHT = 30;

	public static final int button_WIDTH = 75;
	public static final int button_HEIGHT = 35;
	public static final int button_LG_HEIGHT = 50;
	
	public static final int TEXTAREA_WIDTH = 600;
	public static final int TEXTAREA_HEIGHT = 150;

	public static final int firstX_loc = 25;
	public static final int secondX_loc = 300;
	public static final int thirdX_loc = 450;
	public static final int fourthX_loc = 550;
	public static final int lastX_loc = 725;

	public static final int aboveFirstLineY_loc = 10;
	public static final int firstLineY_loc = 35;
	public static final int aboveSecondLineY_loc = 80;
	public static final int secondLineY_loc = 105;
	public static final int belowSecondLineY_loc = 140;
	public static final int thirdLineY_loc = 175;
	public static final int belowThirdLineY_loc = 210;
	public static final int fourthLineY_loc = 245;
	public static final int lastLineY_loc = 400;

	private JFrame frame;
	private JTextArea output;

	private JTextField starter;
	private JTextField follower;
	private JTextField uploadField;
	private JTextField downloadField;

	private JLabel illegalUpload;
	private JLabel illegalDownload;

	private static Node m_node;
	private static InetSocketAddress m_contact;
	private static Helper m_helper;
	private static InetSocketAddress localAddress;

	private String local_ip = null;
	private String localPortNum;
	private String DirName;
	private long blockLen = 1024 * 2;


	public Main() {
		frame = new JFrame("Cloud Chord App");
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		
		output = new JTextArea("Empty");
		output.setEditable(false);
		output.setBounds(firstX_loc, lastLineY_loc, TEXTAREA_WIDTH, TEXTAREA_HEIGHT);
		
		
		illegalUpload = new JLabel();
		illegalDownload = new JLabel();
		m_helper = new Helper();

		try {
			local_ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void mainFrame() {
		// set label
		JPanel panel = new JPanel();
		panel.setLayout(null); // add components by coordinates

		JLabel M_dir = new JLabel("Initiate");
		M_dir.setBounds(firstX_loc, aboveFirstLineY_loc, LABEL_WIDTH, LABEL_HEIGHT);

		JLabel M_star = new JLabel("Join");
		M_star.setBounds(firstX_loc, aboveSecondLineY_loc, LABEL_WIDTH, LABEL_HEIGHT);

		JLabel info = new JLabel("Function");
		info.setBounds(thirdX_loc, aboveFirstLineY_loc, LABEL_WIDTH, LABEL_HEIGHT);

		illegalUpload.setBounds(thirdX_loc, belowSecondLineY_loc, LABEL_LONG_WIDTH, LABEL_HEIGHT);
		illegalUpload.setForeground(Color.RED);

		illegalDownload.setBounds(thirdX_loc, belowThirdLineY_loc, LABEL_LONG_WIDTH, LABEL_HEIGHT);
		illegalDownload.setForeground(Color.RED);

		// set field
		starter = new JTextField();
		starter.setBounds(firstX_loc, firstLineY_loc, FIELD_WIDTH, FIELD_HEIGHT);

		follower = new JTextField();
		follower.setBounds(firstX_loc, secondLineY_loc, FIELD_WIDTH, FIELD_HEIGHT);

		uploadField = new JTextField();
		uploadField.setBounds(fourthX_loc, secondLineY_loc, FIELD_WIDTH, FIELD_HEIGHT);

		downloadField = new JTextField();
		downloadField.setBounds(fourthX_loc, thirdLineY_loc, FIELD_WIDTH, FIELD_HEIGHT);

		// set button
		JButton startBtn = new JButton("Start");
		startBtn.setBounds(secondX_loc, firstLineY_loc, button_WIDTH, button_HEIGHT);
		startBtn.setOpaque(true);

		JButton joinBtn = new JButton("Join");
		joinBtn.setBounds(secondX_loc, secondLineY_loc, button_WIDTH, button_HEIGHT);
		joinBtn.setOpaque(true);

		JButton inforBtn = new JButton("Info");
		inforBtn.setBounds(thirdX_loc, firstLineY_loc, button_WIDTH, button_HEIGHT);
		inforBtn.setOpaque(true);

		JButton downloadBtn = new JButton("Download");
		downloadBtn.setBounds(thirdX_loc, thirdLineY_loc, button_WIDTH, button_HEIGHT);
		downloadBtn.setOpaque(true);

		JButton uploadBtn = new JButton("Upload");
		uploadBtn.setBounds(thirdX_loc, secondLineY_loc, button_WIDTH, button_HEIGHT);
		uploadBtn.setOpaque(true);

		JButton quitBtn = new JButton("Quit");
		quitBtn.setBounds(fourthX_loc, firstLineY_loc, button_WIDTH, button_HEIGHT);
		quitBtn.setOpaque(true);

		JButton clear = new JButton("Clear");
		clear.setBounds(lastX_loc, fourthLineY_loc, button_WIDTH, button_LG_HEIGHT);
		// clear.setBackground(Color.BLUE);
		clear.setOpaque(true);

		// function for button
		startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				localPortNum = starter.getText();

				// illegal check

				startNodeAndFolder(localPortNum);
				m_contact = m_node.getAddress();
				isJoinRing();
				
				output.setText("Initiate the Chord ring\nLocal IP: " + local_ip +", Local Port Num: " + localPortNum + "\nYour positions is " + Helper.hexIdAndPosition(localAddress));
			}
		});

		joinBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
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
				output.setText("Joining the Chord ring\nLocal IP: " + local_ip +", Local Port Num: " + localPortNum + "\nYour positions is " + Helper.hexIdAndPosition(localAddress));

			}
		});

		inforBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String dir_name = starter.getText();
				if (dir_name.length() == 0) {
					return;
				}
			}
		});

		quitBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// iterate all files in cloud
				InetSocketAddress successor = m_node.getSuccessor();

				// iterate all files in cloud
				String localSock = local_ip + " " + localPortNum;

				Helper.downSendAllCloudFiles(DirName, localSock, successor);

				m_node.stopAllThreads();
				output.setText("send out all files from user's cloud account, Leaving the ring...");
				System.out.println("Leaving the ring...");
				System.exit(0);
			}
		});

		uploadBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// get file info, encryption and split file
				String inputFileName = uploadField.getText();
				checkInputFile(inputFileName);
				
				String splitFile = inputFileName;
//				String encFileName = Encryption.EncodePrefix + inputFileName;
//				Encryption.encrypt(inputFileName, DirName, encFileName);
//				List<String> splitList = SplitFile.split(encFileName, DirName, blockLen);

				// send out the each of file
//				for (String splitFile : splitList) {								
					InetSocketAddress result = getFileSuccessor(splitFile);
					String targetFilePath = FileUtils.getLocalFileName(splitFile, DirName);
					File targetFile = new File(targetFilePath);
					if (!targetFile.exists()) {
						illegalUpload.setText("the target file is not in the user's directory");
					} else {
						if (result.equals(localAddress)) {
							Gcloud gc = new Gcloud(DirName);
							gc.uploadTextFile(splitFile);

							String propFileName = DirName + Helper.RECV_FILE_LIST;
							File propFile = new File(propFileName);
							String sentSockStr = result.getHostString() + " " + result.getPort();
							if (propFile.exists()) {
								Props.updateProp(splitFile, sentSockStr, propFileName);
							} else {
								Props.writeProp(splitFile, sentSockStr, propFileName);
							}
							output.setText("file " + splitFile + ", Position is " + Helper.hexFileNameAndPosition(splitFile) + "\nsuccesfully upload file: " + splitFile);
						} else {
							String localSock = local_ip + " " + localPortNum;
							String tmp_response = Helper.sendFile(result, DirName, splitFile, localSock, false);
							System.out.println("sending: " + splitFile + " success");
							System.out.println("feedback: " + tmp_response);
							output.setText("file " + splitFile + ", Position is " + Helper.hexFileNameAndPosition(splitFile) + "\nsuccesfully send file to successor cloud account, and upload file: " + splitFile);
						}

						// keep track of all the uploaded files from current
						String propFileName = DirName + Helper.SENT_FILE_LIST;
						File propFile = new File(propFileName);
						String sentSockStr = result.getHostString() + " " + result.getPort();
						if (propFile.exists()) {
							Props.updateProp(splitFile, sentSockStr, propFileName);
						} else {
							Props.writeProp(splitFile, sentSockStr, propFileName);
						}
					}
//				}
		
			}
		});

		downloadBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String inputFileName = downloadField.getText();
				checkInputFile(inputFileName);
				InetSocketAddress result = getFileSuccessor(inputFileName);

				String propFileName = DirName + Helper.SENT_FILE_LIST;
				String queryRes = Props.readProp(inputFileName, propFileName);
				if (queryRes == null) {
					illegalDownload.setText("the target file does not belong with the current user");
				} else {
					if (result.equals(localAddress)) {
						Gcloud gc = new Gcloud(DirName);
						gc.downLoadFile(inputFileName);
						output.setText("file " + inputFileName + ", Position is " + Helper.hexFileNameAndPosition(inputFileName) +"\nsuccesfully download file: " + inputFileName);

					} else {
						String res = Helper.sendQueryFile(result, DirName, inputFileName);
						System.out.println("feedback: " + res);
						output.setText("file " + inputFileName + ", Position is " + Helper.hexFileNameAndPosition(inputFileName) +"\nsuccesfully download file: " + inputFileName + " from target cloud account");

					}
				}

			}
		});

		clear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// starter.setEditable(true);
				// follower.setEditable(true);

				uploadField.setText("");
				downloadField.setText("");
				illegalUpload.setText("");
				illegalDownload.setText("");

			}
		});

		panel.add(M_dir);
		panel.add(M_star);
		panel.add(starter);
		panel.add(follower);
		panel.add(uploadField);
		panel.add(downloadField);
		panel.add(illegalUpload);
		panel.add(illegalDownload);
		panel.add(info);
		panel.add(startBtn);
		panel.add(joinBtn);
		panel.add(inforBtn);
		panel.add(uploadBtn);
		panel.add(downloadBtn);
		panel.add(quitBtn);
		panel.add(clear);
		panel.add(output);

		frame.add(panel);
		frame.setVisible(true);
	}

	private void startNodeAndFolder(String localPortNum) {
		DirName = FileUtils.createFolder(localPortNum);
		String downloadDirName = FileUtils.createFolder(localPortNum) + Helper.DOWNLOADS;

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
		m_node.printNeighbors();
		return true;
	}

	private boolean checkInputFile(String fileName) {
		long hash = Helper.hashString(fileName);
		System.out.println("\nHash value is " + Long.toHexString(hash));
		InetSocketAddress result = Helper.requestAddress(localAddress, "FINDSUCC_" + hash);

		// if fail to send request, local node is disconnected, exit
		if (result == null) {
			System.out.println("The node your are contacting is disconnected. Now exit.");
			System.exit(0);
		}
		return true;
	}

	private InetSocketAddress getFileSuccessor(String fileName) {
		long hash = Helper.hashString(fileName);
		System.out.println("\nHash value is " + Long.toHexString(hash));
		InetSocketAddress result = Helper.requestAddress(localAddress, "FINDSUCC_" + hash);
		return result;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main user = new Main();
		user.mainFrame();
	}

}
