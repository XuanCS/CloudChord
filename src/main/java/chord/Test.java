package chord;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Enumeration;


public class Test {

	public static void main(String[] args) throws SocketException, UnknownHostException {
		// TODO Auto-generated method stub
		// get local machine's ip 
//		InetAddress local_ip = null;
//		try {
//			local_ip = InetAddress.getLocalHost();
//
//		} catch (UnknownHostException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		System.out.println("localIP: " + local_ip);
//		System.out.println("**************");
//
//		InetAddress inetAddress2 = InetAddress.getByName("localhost");
//		System.out.println("localhost: " + inetAddress2);
		
		//System.out.println("\nType \"info\" to check this node's data or \n type \"quit\"to leave ring: ");
		System.out.println("\nType \"info\" to check this node's data or \n type \"quit\"to leave ring or \n type \"file\"to transfer files: ");

		long hash = Helper.hashString("note.txt");
//		long hash = Helper.hashString("testHW2.pdf");
		System.out.println("\nHash value is " + Long.toHexString(hash));
		
//        System.out.println("number of args: " + args.length);
//		String cmd = "gradle run -PappArgs=[" + "\'1\', " + "\'test.txt\'" + "]";		
//		System.out.println("cmd is: " + cmd);
		
//		String a = "t " || "s";
		
		
//		String fileName = "test";
//		System.out.println(Arrays.toString(fileName.getBytes()));
//		byte[] arr = {116, 101, 115, 116, 0,0,0};
//		String newString = new String(arr);
//		System.out.println(newString.length());
		
//		Log.print("  ERROE: todo xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.\n");


		
//		Enumeration e = NetworkInterface.getNetworkInterfaces();
//		while(e.hasMoreElements())
//		{
//		    NetworkInterface n = (NetworkInterface) e.nextElement();
//		    Enumeration ee = n.getInetAddresses();
//		    while (ee.hasMoreElements())
//		    {
//		        InetAddress i = (InetAddress) ee.nextElement();
//		        System.out.println(i.getHostAddress());
//		    }
//		}
//		Socket s = new Socket();
//		System.out.println(s.getInetAddress());
	}

}
