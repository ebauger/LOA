package main;

import java.util.ArrayList;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Grid grid = new Grid(
				"12211100" +
				"01100010" +
				"00000010" +
				"00020021" +
				"00000000" +
				"00000000" +
				"00010020" +
				"00000000");
		grid.printBits();
		
//		System.out.println(grid.isConnected());
//
//		
//		for (long l : grid.generatePossibleMvt(grid.getmPions())) 
//			{
//				System.out.println(Long.toBinaryString((long)1<<63 | l));
//			}
//			
//			System.out.println();
//			
//			grid.init();
		ArrayList<Long> mvts = grid.generatePossibleMvt();
		System.out.println("1000000010000000100000001000000010000000100000001000000010000000");
		for(Long mvt: mvts){
			grid.printBits(mvt);
		}
	}

}
