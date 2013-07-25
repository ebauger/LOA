package main;

import java.io.IOException;


public class Main {

	private Grid mGrid;
	private static Client mClient;

	private static Messages mMessages;

	public static int MAX_LVL = 4;
	public final static int MAX_MVT_SAVE_LVL = 3;
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		//1 = white
		//2 = black
		String gridCustom =  
				 "00000102" +
				 "00000000" +
				 "00111000" +
				 "00000000" +
				 "00000000" +
				 "00002200" +
				 "00000000" +
				 "00000000";
		
		String gridDebutPartieStandard =  
				 "02222220" +
				 "10000001" +
				 "10000001" +
				 "10000001" +
				 "10000001" +
				 "10000001" +
				 "10000001" +
				 "02222220";
		 
//		 Grid.init();
		 //GridSaveMvt.init();
		MultiThreadNegaMaxTableFeuille.init();
		
		//Grid.printBits();		
		 
		 
		 
		 
//		 NegaMaxPrudTranspositionTable g = new NegaMaxPrudTranspositionTable(gridDebutPartieStandard, 1, Grid.WHITE);
//		 MAX_LVL = 6;
//		 g.getBestMoveAsString(MAX_LVL);
		 MAX_LVL = 4;
//		 g.printGame();
		 
//		 System.out.println("Max int=" + Integer.MAX_VALUE);
//		 System.out.println("Min int=" + Integer.MIN_VALUE);
		 
		 System.out.println("Pret a jouer");
		 
		 System.out.println("Profondeur Max = "+MAX_LVL);
		 
		 System.out.println("Ready to play ?");
		 System.in.read();
		 System.in.read();
		 
		 
		mMessages = new Messages(); // play with Server
	
//		NegaMaxPrud white = new NegaMaxPrud(gridDebutPartieStandard, 1, 1);
//		MTDFNegaMaxPrudTranspositionTableProgressivelvl black = new MTDFNegaMaxPrudTranspositionTableProgressivelvl(gridDebutPartieStandard, 1, 2);
//		
//		
//		boolean whiteTurn = true;
//		
//		while(white.checkPartieTerm() == NegaMaxPrud.PARTIE_NON_TERMINEE && black.checkPartieTerm() == NegaMaxPrud.PARTIE_NON_TERMINEE)
//		{
//			if(whiteTurn)
//			{
//				black.coupAdvAndUpdate(white.getBestMovelong(MAX_LVL), true);
//				
//				System.out.println("white move :");
//				white.printGame();
//				
//			}else
//			{
//			
//				white.coupAdvAndUpdate(black.getBestMovelong(MAX_LVL), true);
//				
//				System.out.println("black move :");
//				black.printGame();
//				
//			}
//			
//			whiteTurn = !whiteTurn;
//			
//
////			System.in.read();
////			System.in.read();
//		}
//		
//		int whiteEndGame =  white.checkPartieTerm();
//		int blackEndGame =  black.checkPartieTerm();
//		
//		if(whiteEndGame == NegaMaxPrud.MATCH_NULL && blackEndGame == NegaMaxPrud.MATCH_NULL)
//			System.out.println("match null");
//		else if(whiteEndGame == NegaMaxPrud.PARTIE_GAGNE && blackEndGame == NegaMaxPrud.PARTIE_PERDU)
//			System.out.println("white win");
//		else if(whiteEndGame == NegaMaxPrud.PARTIE_PERDU && blackEndGame == NegaMaxPrud.PARTIE_GAGNE)
//			System.out.println("black win");
//		else if(whiteEndGame == NegaMaxPrud.PARTIE_GAGNE && blackEndGame == NegaMaxPrud.PARTIE_GAGNE)
//			System.out.println("both win");
//		else if(whiteEndGame == NegaMaxPrud.PARTIE_PERDU && blackEndGame == NegaMaxPrud.PARTIE_PERDU)
//			System.out.println("both lose");
//		else 
//			System.err.println("error");
	}

}
