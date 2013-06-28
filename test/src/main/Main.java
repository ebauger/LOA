package main;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Grid grid = new Grid(
				"10011100" +
				"01120010" +
				"00000012" +
				"00022001" +
				"00000000" +
				"00022000" +
				"00000000" +
				"00000002");
		grid.printBits();
		System.out.println(grid.isConnected());

	}

}
