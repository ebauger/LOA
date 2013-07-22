/**
 * 
 */
package main;

import java.util.ArrayList;
import java.util.Map.Entry;


/**
 * @author Charly
 *
 */
public class NegaMaxPrud extends Grid {

	
	public final static int P_INFINITY = Integer.MAX_VALUE-1;
	public final static int M_INFINITY = Integer.MIN_VALUE+2;
	public final static int PARTIE_GAGNE = Integer.MAX_VALUE-2;
	public final static int PARTIE_PERDU = Integer.MIN_VALUE+3;
	public final static int MATCH_NULL = 0;
	
	
	public final static int MAX_HEURISTIQUE = PARTIE_GAGNE;
	public final static int MIN_HEURISTIQUE = PARTIE_PERDU;
	public final static int UNDEFINED_HEURISTIQUE = 0;
	
	public final static int PARTIE_NON_TERMINEE = Integer.MAX_VALUE; // ou min // ou min -1
	
	protected int nbFirstCoupAleatoire = 0;
	
	protected long bestMove = 0L;
	
	
	protected int heuristiqueTrouve =0;
	
	
	
	
	public NegaMaxPrud(NegaMaxPrud nmp) {
		super(nmp);

	}
	
	public NegaMaxPrud(String str, int type, int myColor) {
		super(str, type, myColor);
		
		this.bestMove = 0L;
	}
		
	protected int NegaMax(int alpha, int beta, int depth){
		
		
		
		int partieTerm = checkPartieTerm();
		if(partieTerm == PARTIE_GAGNE){
			++nbfeuilles;
			return PARTIE_GAGNE - depth;
		}else if(partieTerm == PARTIE_PERDU){
			++nbfeuilles;
			return PARTIE_GAGNE + depth;
		}else if (depth == MaxLvl)//this.lvlMax_heuristique)
		{
			++nbfeuilles;
			return calculeHeuristique() - depth;
		}else
		{
			int meilleur = M_INFINITY;
			
			ArrayList<Long> coups = generatePossibleMvt();
			
			for (Long move : coups) {

				NegaMaxPrud nmp = new NegaMaxPrud(this);
				nmp.MakeMvtAndUpdate(move,false);
				nmp.inverse();
								
				int val = -nmp.NegaMax(-beta, -alpha, depth+1);
				
				--lvlparcouru;
				
				if(val>meilleur)
				{
					meilleur = val;
					
					if(meilleur > alpha)
					{
						alpha = val;
						
						
						if(alpha>=beta)
						{
							
							return meilleur;
						}
					}	
				}
			}
			

			return meilleur;
		}
		

	}
	
	protected int FirstNegaMax(int alpha, int beta, int depth){
	
		
		int partieTerm = checkPartieTerm();
		if(partieTerm == PARTIE_GAGNE){
			++nbfeuilles;
			return PARTIE_GAGNE - depth;
		}
		else if(partieTerm == PARTIE_PERDU){
			++nbfeuilles;
			return PARTIE_PERDU + depth;
		}else if (depth == MaxLvl){
			++nbfeuilles;
			return this.calculeHeuristique() - depth;
		}else
		{
			int meilleur = M_INFINITY;
			
			ArrayList<Long> coups = generatePossibleMvt();
			
			this.bestMove = coups.get(0);
			
			for (Long move : coups) {

				NegaMaxPrud nmp = new NegaMaxPrud(this);
				nmp.MakeMvtAndUpdate(move,false);
				nmp.inverse();
				
				
				int val = -nmp.NegaMax(-beta, -alpha, depth+1);
				
				--lvlparcouru;
				
				if(val>meilleur)
				{
					meilleur = val;
					
					this.bestMove = move;
					
					if(meilleur > alpha)
					{
						alpha = val;
						
						if(alpha>=beta)
						{
							return meilleur;
						}

					}	
				}
			}
			
	
			return meilleur;
		}
		
	}
	
	protected int calculeHeuristique() {
		int heuristique = UNDEFINED_HEURISTIQUE;
		
		int mheuristique = UNDEFINED_HEURISTIQUE;
		
		
		int sumX = 0;
		int sumY = 0;
		int cpt = 0;
		for(int i = 0; i< 64; ++i){
			if((1L << i & mPionsAdv) != 0){
				++cpt;
				sumX += i%8;
				sumY += i/8;
			}
			
		}
		
		double massX = sumX/cpt ;
		double massY = sumY/cpt ;
		for(int i = 0; i< 64; ++i){
			if((1L << i & mPions) != 0){
				mheuristique += Math.pow(massX- i%8, 2) + Math.pow(massY- i/8, 2);
				
			}
			
		}
					
		heuristique = MAX_HEURISTIQUE - mheuristique;
			
		return heuristique;
			
	}

	protected int checkPartieTerm() {
		
		int partieTerm = PARTIE_NON_TERMINEE;
		
		boolean mPionsConn = this.isConnected(mPions);
		boolean pionsAdvConn = this.isConnected(mPionsAdv);
		
		
		if (mPionsConn & pionsAdvConn)
			partieTerm = MATCH_NULL;
		else if (mPionsConn) { // valable aussi pour match nul!!!
			//printGame();
			partieTerm = PARTIE_GAGNE;
		} else if (pionsAdvConn) {
			//printGame();
			partieTerm = PARTIE_PERDU;
		}
		
		return partieTerm;
	}

	public long getBestMove()
	{
		//System.out.println("get best move : MAX Profondeur = "+ lvl);
		
		ArrayList<Long> coups;
		
		this.bestMove = 0L;
		
		this.heuristiqueTrouve = -1;
		
		if(nbFirstCoupAleatoire >0){
			
			nbFirstCoupAleatoire--;
			
		}
		else{
			
			nbfeuilles = 0;
			
			heuristiqueTrouve = FirstNegaMax(M_INFINITY,P_INFINITY,0);
		}
		
		
		if (this.bestMove == 0L) {
			coups = generatePossibleMvt();
			this.bestMove = coups.get((int) (Math.random() * coups.size()));
			++nbcoupAleatoire;
		} 
		
//		int nbMovesSaved = 0;
//		for (int i = 0; i < 12; i++) {
//			if(!moves.get(i).isEmpty())
//				for (int j = 0; j < 12; j++) {
//					if(!moves.get(i).get(j).isEmpty())
//						for (int k = 0; k < Main.MAX_MVT_SAVE_LVL; k++) {
//							if(!moves.get(i).get(j).get(k).isEmpty())
//								for (Entry<Long, ArrayList<Long>> mvts : moves.get(i).get(j).get(k).entrySet()) {
//									nbMovesSaved += mvts.getValue().size();
//								}
//						}
//					
//				}
//		}
//		
//		System.out.println(nbMovesSaved);
		
		
//		this.precedentMove = this.bestMove;
//		
//		System.out.println("Feuilles parcourues  = 	" + nbfeuilles);
//		System.out.println("Coup Aleatoire	     = 	" + nbcoupAleatoire);
//		System.out.println("\ntaille table de transposage     = 	" + NegaMaxPrudTranspositionTable.tableDeTransposition.size());
//		System.out.println("\n	reutilisées      = 	" + nbEntryTransReuse);
		
		
//		System.out.println("get best move : end move=" + Long.toBinaryString(this.bestMove));
//		
//		printGame();

		
		System.out.println();
		if(heuristiqueTrouve !=-1)
		{
			if(heuristiqueTrouve == PARTIE_GAGNE)
			{
				System.out.println(" PARTIE_GAGNE" );
				
			}
			else if(heuristiqueTrouve == PARTIE_PERDU)
			{
				System.out.println(" PARTIE_PERDU" );
			}
			else
			{
				System.out.println("heuristiqueTrouve = "+heuristiqueTrouve );
			}
		}
		else
			System.out.println("Probleme avec l'heuristique = -1");
		
		System.out.println();
		
		return this.bestMove;
		
	}

}
