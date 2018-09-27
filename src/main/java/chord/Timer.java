package chord;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import frontEnd.Main;
import utils.Helper;
import utils.Props;

public class Timer extends Thread {
	protected int curTime;
	private Node localNode;
	private String DirName;
	boolean alive;
	
	public Timer(Node node, String dirName) {
		localNode = node;
		alive = true;
		DirName = dirName;
	}

	public int getTime() {
		return this.curTime;
	}

	public void updateTime(int time) {
		this.curTime = time;
	}

	@Override
	public void run() {
		while (alive) {
			if (curTime == 30) {
//				Helper.totalFileSize = 512;
				if (Helper.totalFileSize >= Helper.lbLimit) {
					System.out.println("\n" + DirName + "reached the Load Balance Limit");
					
					InetSocketAddress successor = localNode.getSuccessor();

					// get target file name
					String propFileName = DirName + Helper.CLOUD_LIST;
					String randFileNamePath = Props.getRandPropFile(propFileName);
//					randFileName = randFileNamePath.split("_")[0];
//					System.out.println("randFile: " + randFileName);

					// send file to the successor
					boolean isLastNode = Helper.checkLastNode(localNode, Main.localAddress);
					if (!isLastNode) {
						// get node info, keep track of where file comes from
						String oriFileName = randFileNamePath.split("_")[0];
						String oriFileNode = randFileNamePath.split("_")[1];
						System.out.println("oriFileName: " + oriFileName);

						System.out.println("Current Node is Last One: " + isLastNode);
						Helper.downSendOneCloudFile(oriFileName, DirName, successor, isLastNode);

						String succIP = successor.getAddress().toString().split("/")[1];
						String succPort = Integer.toString(successor.getPort());
						String succSock = succIP + " " + succPort;
						System.out.println("succSock: " + succSock);

						String srcIP = oriFileNode.split(" ")[0];
						String srcPort = oriFileNode.split(" ")[1];
						InetSocketAddress srcSock = Helper.createSocketAddress(srcIP + ":" + srcPort);

						Helper.sendSentPropSig(oriFileName, succSock, srcSock);
					}
				}
				curTime = 0;
			}
			
			System.out.println("current time:" + curTime);
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			curTime++;
		}
	}
	
	public void toDie() {
		alive = false;
	}
}
