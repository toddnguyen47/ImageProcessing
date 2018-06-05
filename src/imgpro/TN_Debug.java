package imgpro;

/**
 * Debug Singleton for development.
 * @author Todd Nguyen
 */
public class TN_Debug {
	private static TN_Debug instance = null;
	private TN_Debug() {};
	
	public static TN_Debug getInstance() {
		if (instance == null) {
			instance = new TN_Debug();
		}
		
		return instance;
	}
	
	/**
	 * Print a variety of variables enclosed in brackets.
	 * @param objInput An array of objects to be printed
	 */
	public void print(Object objInput[]) {
		System.out.print("[");
		for (int i = 0; i < objInput.length; i++) {
			if (i < objInput.length - 1)
				System.out.print(objInput[i] + ", ");
			else
				System.out.print(objInput[i]);
		}
		System.out.println("]");
	}
}
