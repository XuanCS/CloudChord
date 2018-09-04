package frontEnd;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import chord.Gcloud;
import chord.Node;
import utils.Helper;
import utils.Props;

public class Main {

	private JFrame frame;
	private JTextField starter;
	private JTextField follower;
	private JTextField uploadField;
	private JTextField downloadField;

	private JLabel writingDone;
	private JTextArea Information;
	private ArrayList<String> current_info;
	private JTextArea Prediction;

	private static Node m_node;
	private static InetSocketAddress m_contact;
	private static Helper m_helper;
	private static InetSocketAddress localAddress;

	private String local_ip = null;
	private String localPortNum;
	private String DirName;

	public Main() {
		current_info = new ArrayList<String>();
		frame = new JFrame("Cloud Chord App");
		frame.setSize(frontUtils.FRAME_WIDTH, frontUtils.FRAME_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		writingDone = new JLabel();
		m_helper = new Helper();

		try {
			local_ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void mainFrame() {
		JPanel panel = new JPanel();
		panel.setLayout(null); // add components by coordinates

		JLabel M_dir = new JLabel("Initiate");
		M_dir.setBounds(frontUtils.firstX_loc, frontUtils.aboveFirstLineY_loc, frontUtils.LABEL_WIDTH,
				frontUtils.LABEL_HEIGHT);

		starter = new JTextField();
		starter.setBounds(frontUtils.firstX_loc, frontUtils.firstLineY_loc, frontUtils.FIELD_WIDTH,
				frontUtils.FIELD_HEIGHT);

		JLabel M_star = new JLabel("Join");
		M_star.setBounds(frontUtils.firstX_loc, frontUtils.aboveSecondLineY_loc, frontUtils.LABEL_WIDTH,
				frontUtils.LABEL_HEIGHT);

		follower = new JTextField();
		follower.setBounds(frontUtils.firstX_loc, frontUtils.secondLineY_loc, frontUtils.FIELD_WIDTH,
				frontUtils.FIELD_HEIGHT);

		uploadField = new JTextField();
		uploadField.setBounds(frontUtils.fourthX_loc, frontUtils.secondLineY_loc, frontUtils.FIELD_WIDTH,
				frontUtils.FIELD_HEIGHT);

		downloadField = new JTextField();
		downloadField.setBounds(frontUtils.fourthX_loc, frontUtils.thirdLineY_loc, frontUtils.FIELD_WIDTH,
				frontUtils.FIELD_HEIGHT);

		JButton startBtn = new JButton("Start");
		startBtn.setBounds(frontUtils.secondX_loc, frontUtils.firstLineY_loc, frontUtils.button_WIDTH,
				frontUtils.button_HEIGHT);
		// submit1.setBackground(Color.GREEN);
		startBtn.setOpaque(true);

		JButton joinBtn = new JButton("Join");
		joinBtn.setBounds(frontUtils.secondX_loc, frontUtils.secondLineY_loc, frontUtils.button_WIDTH,
				frontUtils.button_HEIGHT);
		// submit2.setBackground(Color.GREEN);
		joinBtn.setOpaque(true);

		JLabel info = new JLabel("Function");
		info.setBounds(frontUtils.thirdX_loc, frontUtils.aboveFirstLineY_loc, frontUtils.LABEL_WIDTH,
				frontUtils.LABEL_HEIGHT);

		JButton inforBtn = new JButton("Info");
		inforBtn.setBounds(frontUtils.thirdX_loc, frontUtils.firstLineY_loc, frontUtils.button_WIDTH,
				frontUtils.button_HEIGHT);
		// submit1.setBackground(Color.GREEN);
		inforBtn.setOpaque(true);

		JButton downloadBtn = new JButton("Download");
		downloadBtn.setBounds(frontUtils.thirdX_loc, frontUtils.thirdLineY_loc, frontUtils.button_WIDTH,
				frontUtils.button_HEIGHT);
		// submit1.setBackground(Color.GREEN);
		downloadBtn.setOpaque(true);

		JButton uploadBtn = new JButton("Upload");
		uploadBtn.setBounds(frontUtils.thirdX_loc, frontUtils.secondLineY_loc, frontUtils.button_WIDTH,
				frontUtils.button_HEIGHT);
		// submit1.setBackground(Color.GREEN);
		uploadBtn.setOpaque(true);

		JButton quitBtn = new JButton("Quit");
		quitBtn.setBounds(frontUtils.fourthX_loc, frontUtils.firstLineY_loc, frontUtils.button_WIDTH,
				frontUtils.button_HEIGHT);
		// submit1.setBackground(Color.GREEN);
		quitBtn.setOpaque(true);

		JButton clear = new JButton("Clear");
		clear.setBounds(frontUtils.lastX_loc, frontUtils.fourthLineY_loc, frontUtils.button_WIDTH,
				frontUtils.button_LG_HEIGHT);
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
				System.out.println("Leaving the ring...");
				System.exit(0);
			}
		});

		uploadBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String inputFileName = uploadField.getText();
				checkInputFile(inputFileName);
				InetSocketAddress result = getFileSuccessor(inputFileName);

				String targetFilePath = DirName + "/" + inputFileName;
				File targetFile = new File(targetFilePath);
				if (!targetFile.exists()) {
					System.err.println("cannot upload the target file, the target is not in the user's directory");
				} else {
					if (result.equals(localAddress)) {
						Gcloud gc = new Gcloud(DirName);
						gc.uploadTextFile(inputFileName);

						String propFileName = DirName + Helper.RECV_FILE_LIST;
						File propFile = new File(propFileName);
						String sentSockStr = result.getHostString() + " " + result.getPort();
						if (propFile.exists()) {
							Props.updateProp(inputFileName, sentSockStr, propFileName);
						} else {
							Props.writeProp(inputFileName, sentSockStr, propFileName);
						}
					} else {
						String localSock = local_ip + " " + localPortNum;
						String tmp_response = Helper.sendFile(result, DirName, inputFileName, localSock, false);
						System.out.println("sending: " + inputFileName + " success");
						System.out.println("feedback: " + tmp_response);
					}

					// keep track of all the uploaded files from current
					// node
					String propFileName = DirName + Helper.SENT_FILE_LIST;
					File propFile = new File(propFileName);
					// System.out.println("res addr: " +
					// result.getHostString());
					String sentSockStr = result.getHostString() + " " + result.getPort();
					if (propFile.exists()) {
						Props.updateProp(inputFileName, sentSockStr, propFileName);
					} else {
						Props.writeProp(inputFileName, sentSockStr, propFileName);
					}
				}
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
					System.err.println(
							"cannot download the target file, the target file does not belong with the current user");
				} else {
					if (result.equals(localAddress)) {
						Gcloud gc = new Gcloud(DirName);
						gc.downLoadFile(inputFileName);
					} else {
						String res = Helper.sendQueryFile(result, DirName, inputFileName);
						System.out.println("feedback: " + res);
					}
				}

			}
		});

		clear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				current_info.clear();
				Information.setText("");
				starter.setEditable(true);
				follower.setEditable(true);
				Prediction.setText("");
				writingDone.setText("");
			}
		});

		panel.add(M_dir);
		panel.add(M_star);
		panel.add(starter);
		panel.add(follower);
		panel.add(uploadField);
		panel.add(downloadField);
		panel.add(info);
		panel.add(startBtn);
		panel.add(joinBtn);
		panel.add(inforBtn);
		panel.add(uploadBtn);
		panel.add(downloadBtn);
		panel.add(quitBtn);
		panel.add(clear);
		panel.add(writingDone);

		frame.add(panel);
		frame.setVisible(true);
	}

	private void startNodeAndFolder(String localPortNum) {
		DirName = Helper.createFolder(localPortNum);
		String downloadDirName = Helper.createFolder(localPortNum) + Helper.DOWNLOADS;

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
