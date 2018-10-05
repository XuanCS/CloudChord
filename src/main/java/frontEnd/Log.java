package frontEnd;


public class Log {
	
	public static void print() {
		Main.output.append("\n");
	}
	public static void print(String str) {
		Main.output.append(str+ "\n");
	}
	
	public static void nlPrint(String str) {
		Main.output.append("\n" +str+ "\n");
	}
}
