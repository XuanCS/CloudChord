package fileSys;

import java.io.Serializable;

public class Msg implements Serializable{
	private int type = -1;
	private String cmd;
	private int fileSize;
	private String contents;
	private static final long serialVersionUID = 7278433854927874578L;

	public Msg(int type, String cmd) {
		this.type = type;
		this.cmd = cmd;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public String getCmd() {
		return cmd;
	}
	
	public void setCmd(String str) {
		cmd = str;
	}

	
	public String toString() {
		return "Type = " + getType() + " ; Cmd = " + getCmd();
	}

}
