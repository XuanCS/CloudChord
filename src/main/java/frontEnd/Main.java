package frontEnd;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import chord.Helper;
import chord.Node;

public class Main {

	private JFrame frame;
	private JTextField starter;
	private JTextField follower;

	private JLabel writingDone;
	private JTextArea Information;
	private ArrayList<String> current_info;
	private JTextArea Prediction;

	private static Node m_node;
	private static InetSocketAddress m_contact;
	private static Helper m_helper;
	private static InetSocketAddress localAddress;

	private String local_ip = null;

	public Main() {
		current_info = new ArrayList<String>();
		frame = new JFrame("Cloud Chord App");
		frame.setSize(800, 600);
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
		M_dir.setBounds(25, 10, 150, 25);

		starter = new JTextField();
		starter.setBounds(20, 45, 250, 25);

		JLabel M_star = new JLabel("Join");
		M_star.setBounds(25, 80, 150, 25);

		follower = new JTextField();
		follower.setBounds(20, 115, 250, 25);

		JLabel info = new JLabel("Function");
		info.setBounds(425, 10, 150, 25);

		JButton startBtn = new JButton("Start");
		startBtn.setBounds(300, 35, 75, 35);
		// submit1.setBackground(Color.GREEN);
		startBtn.setOpaque(true);
		
		JButton joinBtn = new JButton("Join");
		joinBtn.setBounds(300, 105, 75, 35);
		// submit2.setBackground(Color.GREEN);
		joinBtn.setOpaque(true);

		JButton clear = new JButton("Clear");
		clear.setBounds(600, 250, 75, 50);
		// clear.setBackground(Color.BLUE);
		clear.setOpaque(true);
		
		JButton inforBtn = new JButton("Info");
		inforBtn.setBounds(450, 35, 75, 35);
		// submit1.setBackground(Color.GREEN);
		inforBtn.setOpaque(true);
		
		JButton downloadBtn = new JButton("Download");
		downloadBtn.setBounds(650, 35, 75, 35);
		// submit1.setBackground(Color.GREEN);
		downloadBtn.setOpaque(true);
		
		JButton uploadBtn = new JButton("Upload");
		uploadBtn.setBounds(550, 35, 75, 35);
		// submit1.setBackground(Color.GREEN);
		uploadBtn.setOpaque(true);
		
		
		JButton quitBtn = new JButton("Quit");
		quitBtn.setBounds(450, 100, 75, 35);
		// submit1.setBackground(Color.GREEN);
		quitBtn.setOpaque(true);
		
		// function for button
		startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String localPortNum = starter.getText();

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
				
				
				String localPortNum = introNode.split(" ")[0];
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


		uploadBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String dir_name = starter.getText();
				if (dir_name.length() == 0) {
					return;
				}
			}
		});


		downloadBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String dir_name = starter.getText();
				if (dir_name.length() == 0) {
					return;
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
		String DirName = Helper.createFolder(localPortNum);
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

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main user = new Main();
		user.mainFrame();
	}

}
