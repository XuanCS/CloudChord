package chord;

public class Timer {
	protected int cur_time;
	
	public Timer(){
		
	}
	
	public int getTime(){
		return this.cur_time;
	}
	
	public void updateTime(int time){
		this.cur_time=time;
	}
}
