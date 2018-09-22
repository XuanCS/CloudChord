package chord;

import java.util.concurrent.TimeUnit;

public class Timer extends Thread {
	protected int curTime;
	private Node local;
	boolean alive;
	
	public Timer(Node node) {
		local = node;
		alive = true;
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
//			System.out.println("current time:" + curTime);
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
