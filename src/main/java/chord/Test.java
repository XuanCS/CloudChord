package chord;

/**
	public static void main(String[] args) throws SocketException, UnknownHostException {
		// TODO Auto-generated method stub
		// get local machine's ip test
		// InetAddress local_ip = null;
		// try {
		// local_ip = InetAddress.getLocalHost();
		//
		// } catch (UnknownHostException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// System.out.println("localIP: " + local_ip);
		// System.out.println("**************");
		//
		// InetAddress inetAddress2 = InetAddress.getByName("localhost");
		// System.out.println("localhost: " + inetAddress2);

		// System.out.println("\nType \"info\" to check this node's data or \n
		// type \"quit\"to leave ring: ");
		System.out.println(
				"\nType \"info\" to check this node's data or \n type \"quit\"to leave ring or \n type \"file\"to transfer files: ");

		String fileName = "test_8002.txt";
		Helper h = new Helper();
		// long hash = Helper.hashString("test_8002.txt");
		// long hash = Helper.hashString("testHW2.pdf");
		System.out.println("\nfile Hash value is " + Helper.hexFileNameAndPosition(fileName));

		// System.out.println("number of args: " + args.length);
		// String cmd = "gradle run -PappArgs=[" + "\'1\', " + "\'test.txt\'" +
		// "]";
		// System.out.println("cmd is: " + cmd);

		// String a = "t " || "s";

		// String fileName = "test";
		// System.out.println(Arrays.toString(fileName.getBytes()));
		// byte[] arr = {116, 101, 115, 116, 0,0,0};
		// String newString = new String(arr);
		// System.out.println(newString.length());

		// Log.print(" ERROE: todo
		// xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.\n");

		// Enumeration e = NetworkInterface.getNetworkInterfaces();
		// while(e.hasMoreElements())
		// {
		// NetworkInterface n = (NetworkInterface) e.nextElement();
		// Enumeration ee = n.getInetAddresses();
		// while (ee.hasMoreElements())
		// {
		// InetAddress i = (InetAddress) ee.nextElement();
		// System.out.println(i.getHostAddress());
		// }
		// }
		// Socket s = new Socket();
		// System.out.println(s.getInetAddress());


	}

	*/
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Test extends Frame implements ActionListener {
	Button ab, rb; // ab for accept button and rb for reject button

	public Test() // a constructor to create frame, set the layout to frame and
					// create GUI components
	{
		setLayout(new FlowLayout()); // frame is set to FlowLayout style of
										// arranging buttons

		ab = new Button("Accept"); // convert the above reference variables into
									// objects
		rb = new Button("Reject");

		ab.addActionListener(this); // link the button ab with ActionListener.
									// "this" refers ActionListener
		rb.addActionListener(this); // now ActionListener takes care of
									// ActionEvent generated by ab and rb
									// buttons

		add(ab);
		add(rb); // add the buttons one-by-one to frame

		setTitle("Buttons by S N Rao"); // frame creation methods
		setSize(300, 200);
		setVisible(true);
	} // now, override the abstract method of ActionListener interface

	public void actionPerformed(ActionEvent e) {
		String str = e.getActionCommand();

		if (str.equals("Accept"))
			System.out.println("You clicked " + str + " button, Thank you for accepting the agreement");
		else if (str.equals("Reject"))
			System.out.println("You clicked " + str + " button, Sorry for rejecting the agreement");
	}

	public static void main(String args[]) {
//		new Test(); // just call the constructor because all the code
		
		
		
		JFrame sampleFrame = new JFrame();
		// Code defining frame size, location and funcionality
		 
		JTextArea textArea = new JTextArea();
		// Code defining text area parameters and functionality.
		 
//		sampleFrame.add(textArea);
//		sampleFrame.setVisible(true);
		
		// Code defining text area parameters and functionality.
		JScrollPane taScroll = new JScrollPane(textArea); // Adds the scrolls when there are too much text.
//		taScroll.setSize(); 
		
		sampleFrame.add(taScroll); // You should add the scroll pane now, not the text area.
		sampleFrame.setVisible(true);
		sampleFrame.setSize(300, 200);
	}
}
