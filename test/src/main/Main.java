package main;

import java.util.ArrayList;

public class Main {

	private Grid mGrid;
	private static Client mClient;

	private static Messages mMessages;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		 Grid.init();
		 
		 Grid grid = new Grid(
		 "00000000" +
		 "00000000" +
		 "00200200" +
		 "00000000" +
		 "00100200" +
		 "00021010" +
		 "00000000" +
		 "00000200", 1,0);
		 
		grid.getBestMove(1);
		//Grid.printBits();
		 //grid.printGame();
		/*ArrayList<Long> moves = grid.generatePossibleMvt();
		for(long mv: moves){
			long fromLong = grid.getmPions() & mv;
			long toLong = mv ^ fromLong;
			int from = 63 - Long.numberOfLeadingZeros(fromLong);
			int to = 63 - Long.numberOfLeadingZeros(toLong);
			
			
			
			char[] res = new char[4];
			res[0] = (char) ('A' + (7 - (from % 8)));
			res[1] = (char) ('1' + (from / 8));
			res[2] = (char) ('A' + (7 - (to % 8)));
			res[3] = (char) ('1' + (to / 8));

			System.out.println("" + res[0] + res[1] + res[2] + res[3]);
		}*/

		//mMessages = new Messages();
		
	/*long result = 0;
	for(int i = 0; i<8;++i){
		result = result << 8 | 1L << i;
		System.out.println(result);
		System.out.println(Long.toBinaryString(result | 1L << 63));
		//System.out.println(Long.toBinaryString(result >>> 63 | 1L << 63));
	}
	
	System.out.println();
	result = 0;
	for(int i = 63; i>=56;--i){
		result = result >>> 8 | 1L << i;
		System.out.println(result);
		System.out.println(Long.toBinaryString(result | 1L << 63));
		//System.out.println(Long.toBinaryString(result >>> 63 | 1L << 63));
	}*/
	//System.out.println(result);
	//System.out.println(Long.toBinaryString(result | 1L << 63));

		// mClient = new Client(new Messages());
		// Thread th2 = new Thread(mClient);
		//
		// th2.start();

		// grid.printBits();

		// System.out.println(grid.isConnected());
		//
		//
		// for (long l : grid.generatePossibleMvt(grid.getmPions()))
		// {
		// System.out.println(Long.toBinaryString((long)1<<63 | l));
		// }
		//
		// System.out.println();
		//
		// grid.init();
		/*
		 * ArrayList<Long> mvts = grid.generatePossibleMvt();
		 * System.out.println(
		 * "1000000010000000100000001000000010000000100000001000000010000000");
		 * for(Long mvt: mvts){ grid.printBits(mvt); }
		 */

		// System.out.println(Long.MAX_VALUE | Long.MIN_VALUE);
		// grid.printGame();
		// grid.update(1L<<60 | 1L<<36);
		// grid.printGame();
	}

}
