package fileSys;


public class FileMsg extends Msg {
	/**
	 * 
	 */
	private int fileSize;
	private String fileName;
//	private String dirName;
	private byte[] contents;
	private String fileSockDir;
	
	
	public FileMsg(int type, String cmd) {
		super(type, cmd);
	}
	
	public void setFileSize(int size) {
		fileSize = size;
	}
	
	public int getFileSize() {
		return fileSize;
	}
	
	public byte[] getContents() {
		return contents;
	}
	
	public void setContents(byte[] str) {
		contents = str;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String str) {
		fileName = str;
	}
	
	public void setFileSockInfo(String dir) {
		fileSockDir = dir;
	}
	
	public String getFileSockInfo() {
		return fileSockDir;
	}
	


}
