package main;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Grid grid = new Grid(
				"10011100" +
				"01100010" +
				"00000010" +
				"00000001" +
				"00000000" +
				"00000000" +
				"00010000" +
				"00000000");
		grid.printBits();
		System.out.println(grid.isConnected());

	}

}
