package main;


import java.util.concurrent.ExecutionException;



public class Main {

	//private Grid mGrid;
	//private static Client mClient;

	private static Messages mMessages;

	/**
	 * @param args
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException,
			ExecutionException {

		
		Grid.init();
		NMMassBlockTTWhioutThsV2.init2();
		// Grid.print();
		
		Grid whitePlayer = new StratNMMassThreadFixed("01111110" + "20000002"
				+ "20000002" + "20000002" + "20000002" + "20000002"
				+ "20000002" + "01111110", 1, true);
		//whitePlayer.print();
		Grid blackPlayer = new NMMassBlockTTWhioutThsV2("01111110" + "20000002"
				+ "20000002" + "20000002" + "20000002" + "20000002"
				+ "20000002" + "01111110", 1, false);
		 
		/*
		 * Grid whitePlayer = new StratSimpleNegaMaxMass( "01100000" +
		 * "00000000" + "00000000" + "00020002" + "00000002" + "00000000" +
		 * "00000000" + "00110000", 1,Messages.White); Grid blackPlayer = new
		 * StratSimpleNegaMaxMassThreads( "01100000" + "00000000" + "00000000" +
		 * "00020002" + "00000002" + "00000000" + "00000000" + "00110000",
		 * 1,Messages.Black);
		 */
		
		
		
		whitePlayer.printGame();
		loop: while (!whitePlayer.gameOver()) {

			long whiteMove = whitePlayer.getBestMove(3);
			whitePlayer.MakeMvtAndUpdate(whiteMove);
			blackPlayer.coupAdvAndUpdate(whiteMove);
			whitePlayer.printGame();
			if (whitePlayer.gameOver()){
				break loop;
			}
			long blackMove = blackPlayer.getBestMove(5);
			blackPlayer.MakeMvtAndUpdate(blackMove);
			whitePlayer.coupAdvAndUpdate(blackMove);
			whitePlayer.printGame();
		}
		whitePlayer.printGame();
		
		/*int parties = 0;
		Grid whitePlayer;
		Grid blackPlayer;
		int scoreW = 0;
		int scoreB = 0;
		do{
			int temp = scoreW;
			scoreW = scoreB;
			scoreB = temp;
			++parties;
			if(parties%2==0){
			whitePlayer = new StratNMMassThreadFixed("01111110" + "20000002"
					+ "20000002" + "20000002" + "20000002" + "20000002"
					+ "20000002" + "01111110", 1, true);
			//whitePlayer.print();
			blackPlayer = new NMMassBlockTTWhioutThsV2("01111110" + "20000002"
					+ "20000002" + "20000002" + "20000002" + "20000002"
					+ "20000002" + "01111110", 1, false);
			}
			else{
				whitePlayer = new NMMassBlockTTWhioutThsV2("01111110" + "20000002"
						+ "20000002" + "20000002" + "20000002" + "20000002"
						+ "20000002" + "01111110", 1, true);
				//whitePlayer.print();
				blackPlayer = new StratNMMassThreadFixed("01111110" + "20000002"
						+ "20000002" + "20000002" + "20000002" + "20000002"
						+ "20000002" + "01111110", 1, false);
				
			}
			whitePlayer.printGame();
			loop: while (!whitePlayer.gameOver()) {

				long whiteMove = whitePlayer.getBestMove(3);
				whitePlayer.MakeMvtAndUpdate(whiteMove);
				blackPlayer.coupAdvAndUpdate(whiteMove);
				whitePlayer.printGame();
				if (whitePlayer.gameOver()){
					break loop;
				}
				long blackMove = blackPlayer.getBestMove(3);
				blackPlayer.MakeMvtAndUpdate(blackMove);
				whitePlayer.coupAdvAndUpdate(blackMove);
				whitePlayer.printGame();
			}
			whitePlayer.printGame();
			if(whitePlayer.isConnected()){
				++scoreW;
			}
			if(blackPlayer.isConnected()){
				++scoreB;
			}
			
			System.out.println(scoreW +" : "+scoreB);
			
		}while(parties <= 100);
		System.out.println(scoreW +" : "+scoreB);*/

		//Messages msg = new Messages();
	/*	System.out.println(Math.atan(Math.toRadians(10)));
		System.out.println(Math.atan(Math.toRadians(100)));
		System.out.println(Math.atan(Math.toRadians(1000)));
		System.out.println(Math.atan(Math.toRadians(100000)));
		System.out.println();
		System.out.println(Math.atan(10));
		System.out.println(Math.atan(100));
		System.out.println(Math.atan(100));
		System.out.println(Math.atan(1000));
		System.out.println();
		System.out.println(Math.atan(Math.toDegrees(10)));
		System.out.println(Math.atan(Math.toDegrees(100)));
		System.out.println(Math.atan(Math.toDegrees(1000)));
		System.out.println(Math.atan(Math.toDegrees(10000)));*/
		
	}

}
