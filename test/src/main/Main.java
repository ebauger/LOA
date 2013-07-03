package main;


public class Main {
	
	
private Grid mGrid;
private static Client mClient;

private static Messages mMessages;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*Grid grid = new Grid(
				"12211100" +
				"01100010" +
				"00000010" +
				"00020021" +
				"00000000" +
				"00000000" +
				"00010020" +
				"00000000", 1);*/
		

		
		mClient = new Client(new Messages());
		Thread th1 = new Thread(mClient);
		
		th1.start();
		
		
//		mClient = new Client(new Messages());
//		Thread th2 = new Thread(mClient);
//		
//		th2.start();
		
		//grid.printBits();
		
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
		/*ArrayList<Long> mvts = grid.generatePossibleMvt();
		System.out.println("1000000010000000100000001000000010000000100000001000000010000000");
		for(Long mvt: mvts){
			grid.printBits(mvt);
		}*/
		
		//System.out.println(Long.MAX_VALUE | Long.MIN_VALUE);
//		grid.printGame();
//		grid.update(1L<<60 | 1L<<36);
//		grid.printGame();
	}
	
	

}
