package fileSys;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ClientFile {

	private Socket socket = null;
	private ObjectInputStream inputStream = null;
	private ObjectOutputStream outputStream = null;
	private boolean isConnected = false;

	public ClientFile() {

	}

	public void communicate(String fileName) {
		File myFile = new File(fileName);
		if (!myFile.exists()) {
			System.out.print("" + "File does not exit");
		}

		while (!isConnected) {
			try {
				socket = new Socket("localHost", 4445);
				System.out.println("Connected");
				isConnected = true;
				outputStream = new ObjectOutputStream(socket.getOutputStream());

				// SigMsg msg = new SigMsg(0, "KEEP");

				FileMsg msg = new FileMsg(1, "FILE");
				msg.setFileName(fileName);
				
				int fileLen = (int) myFile.length();
				msg.setFileSize(fileLen);

				byte[] myByteArray = new byte[(int) myFile.length()];
				FileInputStream fis = new FileInputStream(myFile);
				BufferedInputStream bis = new BufferedInputStream(fis);
				bis.read(myByteArray);
				msg.setContents(myByteArray);

				System.out.println("Object to be written: " + msg);
				outputStream.writeObject(msg);

			} catch (SocketException se) {
				se.printStackTrace();
				// System.exit(0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		ClientFile client = new ClientFile();
		String fileName = "test_copy_2.txt";
		client.communicate(fileName);
	}

}
