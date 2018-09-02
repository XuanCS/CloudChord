package frontEnd;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Main {
	
	private JFrame frame;
	private JTextField starter;
	private JTextField follower;

	private JLabel writingDone;
	private JTextArea Information;
	private ArrayList<String> current_info;
	private JTextArea Prediction;
	
	
	public Main() {
		current_info = new ArrayList<String>();
		frame = new JFrame("Cloud Chord App");
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		writingDone = new JLabel();
	}
	
	public void mainFrame() {
		JPanel panel = new JPanel();
		panel.setLayout(null); // add components by coordinates
		
		JLabel M_dir = new JLabel("Initiate");
		M_dir.setBounds(25,10,150, 25);
	
		starter = new JTextField();
		starter.setBounds(20, 45, 250, 25);
	
		JLabel M_star= new JLabel("Join");
		M_star.setBounds(25,80,150, 25);
		
		follower = new JTextField();
		follower.setBounds(20, 115, 250, 25);
		
		
		JLabel info = new JLabel("Function");
		info.setBounds(425,10,150,25);
	
		
		Prediction = new JTextArea("Empty");
		Prediction.setEditable(false);
		Prediction.setBounds(30, 400, 600, 150);
		
		writingDone.setBounds(650, 500, 150, 25);
		writingDone.setForeground(Color.RED);
				
		JButton startBtn = new JButton("Start");
		startBtn.setBounds(300, 35, 75, 35);
//		submit1.setBackground(Color.GREEN);
		startBtn.setOpaque(true);
		startBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String dir_name = starter.getText();
				if(dir_name.length()==0)
				{
					return;
				}			
			}
		});
		
		JButton joinBtn = new JButton("Join");
		joinBtn.setBounds(300, 105, 75, 35);
//		submit2.setBackground(Color.GREEN);
		joinBtn.setOpaque(true);

	
		JButton clear = new JButton("Clear");
		clear.setBounds(600, 250, 75, 50);
//		clear.setBackground(Color.BLUE);
		clear.setOpaque(true);
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
		
		
		JButton inforBtn = new JButton("Info");
		inforBtn.setBounds(450, 35, 75, 35);
//		submit1.setBackground(Color.GREEN);
		inforBtn.setOpaque(true);
		inforBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String dir_name = starter.getText();
				if(dir_name.length()==0)
				{
					return;
				}			
			}
		});
		
		
		JButton uploadBtn = new JButton("Upload");
		uploadBtn.setBounds(550, 35, 75, 35);
//		submit1.setBackground(Color.GREEN);
		uploadBtn.setOpaque(true);
		uploadBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String dir_name = starter.getText();
				if(dir_name.length()==0)
				{
					return;
				}			
			}
		});
		
		JButton downloadBtn = new JButton("Download");
		downloadBtn.setBounds(650, 35, 75, 35);
//		submit1.setBackground(Color.GREEN);
		downloadBtn.setOpaque(true);
		downloadBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String dir_name = starter.getText();
				if(dir_name.length()==0)
				{
					return;
				}			
			}
		});
		
		JButton Predict = new JButton("Execute");
		Predict.setBounds(200, 330, 150, 50);
		Font myfont = new Font("no_name",Font.ITALIC,16);
		Predict.setFont(myfont);
		Predict.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				// TODO Auto-generated method stub
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
		panel.add(clear);
		panel.add(Predict);
		panel.add(Prediction);
		panel.add(writingDone);
		
		frame.add(panel);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main user = new Main();
		user.mainFrame();
	}

}
