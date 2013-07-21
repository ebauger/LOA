package main;

import java.util.concurrent.ExecutionException;

public class Main {

	private Grid mGrid;
	private static Client mClient;

	private static Messages mMessages;

	/**
	 * @param args
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		 Grid.init();
		//Grid.print();
		 /*Grid whitePlayer = new StratSimpleNegaMaxMass(
				 "01111110" +
				 "20000002" +
				 "20000002" +
				 "20000002" +
				 "20000002" +
				 "20000002" +
				 "20000002" +
				 "01111110", 1,Messages.White);
		 Grid blackPlayer = new StratSimpleNegaMaxMassThreads(
				 "01111110" +
				 "20000002" +
				 "20000002" +
				 "20000002" +
				 "20000002" +
				 "20000002" +
				 "20000002" +
				 "01111110", 1,Messages.Black);*/
		 Grid whitePlayer = new StratSimpleNegaMaxMass(
				 "01100000" +
				 "00000000" +
				 "00000000" +
				 "00020002" +
				 "00000002" +
				 "00000000" +
				 "00000000" +
				 "00110000", 1,Messages.White);
		 Grid blackPlayer = new StratSimpleNegaMaxMassThreads(
				 "01100000" +
						 "00000000" +
						 "00000000" +
						 "00020002" +
						 "00000002" +
						 "00000000" +
						 "00000000" +
						 "00110000", 1,Messages.Black);
		 whitePlayer.printGame();
		 loop : while(!whitePlayer.gameOver()){
			
			long whiteMove = whitePlayer.getBestMove(5);
			whitePlayer.MakeMvtAndUpdate(whiteMove);
			blackPlayer.coupAdvAndUpdate(whiteMove);
			whitePlayer.printGame();
			if(whitePlayer.gameOver())
				break loop;
			long blackMove = blackPlayer.getBestMove(5);
			blackPlayer.MakeMvtAndUpdate(blackMove);
			whitePlayer.coupAdvAndUpdate(blackMove);
			whitePlayer.printGame();
		 }
		 whitePlayer.printGame();
		
		//Messages msg = new Messages();
		}

}
