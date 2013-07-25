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
		//NMMassBlockTTWhioutThsV2.init2();
		//NMMassBlockTTThs.init2();
		Strat2.init2();
		// Grid.print();
		
		Grid whitePlayer = new Strat2("01111110" + "20000002"
				+ "20000002" + "20000002" + "20000002" + "20000002"
				+ "20000002" + "01111110", 1, true);
		//whitePlayer.print();
		Grid blackPlayer = new StratMass1("01111110" + "20000002"
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
		
		System.out.println(whitePlayer.getClass().getName()+" playing whites");
		System.out.println(blackPlayer.getClass().getName()+" playing blacks");
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
		if(whitePlayer.isConnected())
			System.out.println(whitePlayer.getClass().getName()+" playing whites won");
		if(blackPlayer.isConnected())	
			System.out.println(blackPlayer.getClass().getName()+" playing blacks won");
		
		
		
	}

}
