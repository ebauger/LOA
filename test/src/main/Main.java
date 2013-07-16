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
		
		 Grid whitePlayer = new StratMass1(
				 "01111110" +
				 "20000002" +
				 "20000002" +
				 "20000002" +
				 "20000002" +
				 "20000002" +
				 "20000002" +
				 "01111110", 1,Messages.White);
		 Grid blackPlayer = new StratMass1(
				 "01111110" +
				 "20000002" +
				 "20000002" +
				 "20000002" +
				 "20000002" +
				 "20000002" +
				 "20000002" +
				 "01111110", 1,Messages.Black);
		 whitePlayer.printGame();
		 loop : while(!whitePlayer.gameOver()){
			
			long whiteMove = whitePlayer.getBestMove(5);
			whitePlayer.MakeMvtAndUpdate(whiteMove);
			blackPlayer.coupAdvAndUpdate(whiteMove);
			whitePlayer.printGame();
			if(whitePlayer.gameOver())
				break loop;
			long blackMove = blackPlayer.getBestMove(7);
			blackPlayer.MakeMvtAndUpdate(blackMove);
			whitePlayer.coupAdvAndUpdate(blackMove);
			whitePlayer.printGame();
		 }
		 whitePlayer.printGame();
		
	}

}
