package chord;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import utils.Helper;



public class Node {

	private long localId;
	private InetSocketAddress localAddress;
	private InetSocketAddress predecessor;
	private HashMap<Integer, InetSocketAddress> finger;

	private Listener listener;
	private Stabilize stabilize;
	private FixFingers fixFinger;
	private AskPredecessor askPred;
	private Timer timer;

	public Node (InetSocketAddress address, String dirName) {

		localAddress = address;
		localId = Helper.hashSocketAddress(localAddress);
		predecessor = null;


		finger = new HashMap<Integer, InetSocketAddress>();
		for (int i = 1; i <= 32; i++) {
			updateIthFinger (i, null);
		}

		// initialize threads
		listener = new Listener(this, dirName);
		stabilize = new Stabilize(this);
		fixFinger = new FixFingers(this);
		askPred = new AskPredecessor(this);
		timer = new Timer(this, dirName);
	}


	public boolean join (InetSocketAddress contact) {
		if (contact != null && !contact.equals(localAddress)) {
			InetSocketAddress successor = Helper.requestAddress(contact, "FINDSUCC_" + localId);
			if (successor == null)  {
				System.out.println("\nCould not find node you are trying to contact.\n");
				return false;
			}
			updateIthFinger(1, successor);
		}

		listener.start();
		stabilize.start();
		fixFinger.start();
		askPred.start();
		return true;
	}

	
	public String notify(InetSocketAddress successor) {
		if (successor!=null && !successor.equals(localAddress)) {		
			return Helper.sendRequest(successor, "IAMPRE_"+localAddress.getAddress().toString()+":"+localAddress.getPort());
		}
		return null;
	}


	public void notified (InetSocketAddress newpre) {
		if (predecessor == null || predecessor.equals(localAddress)) {
			this.setPredecessor(newpre);
		}
		else {
			long oldpre_id = Helper.hashSocketAddress(predecessor);
			long local_relative_id = Helper.computeRelativeId(localId, oldpre_id);
			long newpre_relative_id = Helper.computeRelativeId(Helper.hashSocketAddress(newpre), oldpre_id);
			if (newpre_relative_id > 0 && newpre_relative_id < local_relative_id)
				this.setPredecessor(newpre);
		}
	}

	public InetSocketAddress find_successor (long id) {

		InetSocketAddress ret = this.getSuccessor();
		InetSocketAddress pre = findPredecessor(id);

		if (!pre.equals(localAddress))
			ret = Helper.requestAddress(pre, "YOURSUCC");

		if (ret == null)
			ret = localAddress;
		return ret;
	}

	private InetSocketAddress findPredecessor (long findid) {
		InetSocketAddress n = this.localAddress;
		InetSocketAddress n_successor = this.getSuccessor();
		InetSocketAddress most_recently_alive = this.localAddress;
		long n_successor_relative_id = 0;
		if (n_successor != null)
			n_successor_relative_id = Helper.computeRelativeId(Helper.hashSocketAddress(n_successor), Helper.hashSocketAddress(n));
		long findid_relative_id = Helper.computeRelativeId(findid, Helper.hashSocketAddress(n));

		while (!(findid_relative_id > 0 && findid_relative_id <= n_successor_relative_id)) {

			// temporarily save current node
			InetSocketAddress pre_n = n;

			// if current node is local node, find my closest
			if (n.equals(this.localAddress)) {
				n = this.closest_preceding_finger(findid);
			}

			// else current node is remote node, sent request to it for its closest
			else {
				InetSocketAddress result = Helper.requestAddress(n, "CLOSEST_" + findid);

				// if fail to get response, set n to most recently 
				if (result == null) {
					n = most_recently_alive;
					n_successor = Helper.requestAddress(n, "YOURSUCC");
					if (n_successor==null) {
						System.out.println("It's not possible.");
						return localAddress;
					}
					continue;
				}

				// if n's closest is itself, return n
				else if (result.equals(n))
					return result;

				// else n's closest is other node "result"
				else {	
					// set n as most recently alive
					most_recently_alive = n;		
					// ask "result" for its successor
					n_successor = Helper.requestAddress(result, "YOURSUCC");	
					// if we can get its response, then "result" must be our next n
					if (n_successor!=null) {
						n = result;
					}
					// else n sticks, ask n's successor
					else {
						n_successor = Helper.requestAddress(n, "YOURSUCC");
					}
				}

				// compute relative ids for while loop judgement
				n_successor_relative_id = Helper.computeRelativeId(Helper.hashSocketAddress(n_successor), Helper.hashSocketAddress(n));
				findid_relative_id = Helper.computeRelativeId(findid, Helper.hashSocketAddress(n));
			}
			if (pre_n.equals(n))
				break;
		}
		return n;
	}

	public InetSocketAddress closest_preceding_finger (long findid) {
		long findid_relative = Helper.computeRelativeId(findid, localId);

		// check from last item in finger table
		for (int i = 32; i > 0; i--) {
			InetSocketAddress ith_finger = finger.get(i);
			if (ith_finger == null) {
				continue;
			}
			long ith_finger_id = Helper.hashSocketAddress(ith_finger);
			long ith_finger_relative_id = Helper.computeRelativeId(ith_finger_id, localId);

			// if its relative id is the closest, check if its alive
			if (ith_finger_relative_id > 0 && ith_finger_relative_id < findid_relative)  {
				String response  = Helper.sendRequest(ith_finger, "KEEP");

				//it is alive, return it
				if (response!=null &&  response.equals("ALIVE")) {
					return ith_finger;
				}

				// else, remove its existence from finger table
				else {
					updateFingers(-2, ith_finger);
				}
			}
		}
		return localAddress;
	}

	public synchronized void updateFingers(int i, InetSocketAddress value) {
		// valid index in [1, 32], just update the ith finger
		if (i > 0 && i <= 32) {
			updateIthFinger(i, value);
		}
		// caller wants to delete
		else if (i == -1) {
			deleteSuccessor();
		}
		// caller wants to delete a finger in table
		else if (i == -2) {
			deleteCertainFinger(value);
		}
		// caller wants to fill successor
		else if (i == -3) {
			fillSuccessor();
		}
	}

	private void updateIthFinger(int i, InetSocketAddress value) {
		finger.put(i, value);

		// if the updated one is successor, notify the new successor
		if (i == 1 && value != null && !value.equals(localAddress)) {
			notify(value);
		}
	}

	private void deleteSuccessor() {
		InetSocketAddress successor = getSuccessor();

		if (successor == null)
			return;

		int i = 32;
		for (i = 32; i > 0; i--) {
			InetSocketAddress ithfinger = finger.get(i);
			if (ithfinger != null && ithfinger.equals(successor))
				break;
		}

		for (int j = i; j >= 1 ; j--) {
			updateIthFinger(j, null);
		}

		if (predecessor!= null && predecessor.equals(successor))
			setPredecessor(null);

		fillSuccessor();
		successor = getSuccessor();

		// if successor is still null or local node, 
		// and the predecessor is another node, keep asking 
		// it's predecessor until find local node's new successor
		if ((successor == null || successor.equals(successor)) && predecessor!=null && !predecessor.equals(localAddress)) {
			InetSocketAddress p = predecessor;
			InetSocketAddress p_pre = null;
			while (true) {
				p_pre = Helper.requestAddress(p, "YOURPRE");
				if (p_pre == null)
					break;

				// if p's predecessor is node is just deleted, 
				// or itself (nothing found in p), or local address,
				// p is current node's new successor, break
				if (p_pre.equals(p) || p_pre.equals(localAddress)|| p_pre.equals(successor)) {
					break;
				}

				// else, keep asking
				else {
					p = p_pre;
				}
			}

			updateIthFinger(1, p);
		}
	}

	private void deleteCertainFinger(InetSocketAddress f) {
		for (int i = 32; i > 0; i--) {
			InetSocketAddress ithfinger = finger.get(i);
			if (ithfinger != null && ithfinger.equals(f))
				finger.put(i, null);
		}
	}

	private void fillSuccessor() {
		InetSocketAddress successor = this.getSuccessor();
		if (successor == null || successor.equals(localAddress)) {
			for (int i = 2; i <= 32; i++) {
				InetSocketAddress ithfinger = finger.get(i);
				if (ithfinger!=null && !ithfinger.equals(localAddress)) {
					for (int j = i-1; j >=1; j--) {
						updateIthFinger(j, ithfinger);
					}
					break;
				}
			}
		}
		successor = getSuccessor();
		if ((successor == null || successor.equals(localAddress)) && predecessor!=null && !predecessor.equals(localAddress)) {
			updateIthFinger(1, predecessor);
		}
	}

	public void clearPredecessor () {
		setPredecessor(null);
	}


	private synchronized void setPredecessor(InetSocketAddress pre) {
		predecessor = pre;
	}

	public long getId() {
		return localId;
	}

	public InetSocketAddress getAddress() {
		return localAddress;
	}

	public InetSocketAddress getPredecessor() {
		return predecessor;
	}

	public InetSocketAddress getSuccessor() {
		if (finger != null && finger.size() > 0) {
			return finger.get(1);
		}
		return null;
	}

	
	public  Map<Integer, InetSocketAddress> getFiger() {
		return finger;
	}
	
	public void initTimer() {
		timer.start();
	}

	public void stopAllThreads() {
		if (listener != null)
			listener.toDie();
		if (fixFinger != null)
			fixFinger.toDie();
		if (stabilize != null)
			stabilize.toDie();
		if (askPred != null)
			askPred.toDie();
	}
}
