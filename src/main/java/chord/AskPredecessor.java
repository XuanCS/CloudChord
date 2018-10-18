package chord;
import java.net.InetSocketAddress;

import utils.Helper;

/**
 * Ask predecessor thread that periodically asks for predecessor's keep-alive,
 * and delete predecessor if it's dead.
 *
 */
public class AskPredecessor extends Thread {
	
	private Node local;
	private boolean alive;
	
	public AskPredecessor(Node _local) {
		local = _local;
		alive = true;
	}
	
	@Override
	public void run() {
		while (alive) {
			InetSocketAddress predecessor = local.getPredecessor();
			if (predecessor != null) {
				String response = Helper.sendRequest(predecessor, "KEEP");
				if (response == null || !response.equals("ALIVE")) {
					local.clearPredecessor();	
				}

			}
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void toDie() {
		alive = false;
	}
}


