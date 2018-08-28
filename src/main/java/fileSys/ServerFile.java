package fileSys;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;


public class ServerFile {
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	private ObjectInputStream inStream = null;

	public ServerFile() {

	}

	public void communicate() {
		try {
			serverSocket = new ServerSocket(4445);
			System.out.println("waiting for connection");
			socket = serverSocket.accept();
			System.out.println("Connected");
			inStream = new ObjectInputStream(socket.getInputStream());

			Msg msg =  (Msg) inStream.readObject();
			if (msg.getType() == 1) {
				String prefix = "recv_";
				
				FileMsg file = (FileMsg) msg;
				int fileSize = file.getFileSize();
				String fileName = file.getFileName();
				byte[] contents = file.getContents();
				
				FileOutputStream fos = new FileOutputStream(prefix + fileName);
				fos.write(contents);
				
				System.out.println("file size: " + fileSize);
				System.out.println("finished writing to file");
				System.out.println("Object received: " + msg);

			} else if (msg.getType() == 0) {
				SigMsg sigCmd = (SigMsg) msg;
				System.out.println("Object received: " + sigCmd);
			} else {
				System.out.println("ERROR CMD, please Resend");
			}
			socket.close();

		} catch (SocketException se) {
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException cn) {
			cn.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ServerFile server = new ServerFile();
		server.communicate();
	}

}
