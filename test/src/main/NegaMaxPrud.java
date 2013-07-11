/**
 * 
 */
package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * @author Charly
 *
 */
public class NegaMaxPrud extends Grid {

	
	private final static int P_INFINITY = Integer.MAX_VALUE-1;
	private final static int M_INFINITY = Integer.MIN_VALUE+1;
	private final static int PARTIE_GAGNE = Integer.MAX_VALUE-2;
	private final static int PARTIE_PERDU = Integer.MIN_VALUE+2;
	private final static int MATCH_NULL = 0;
	
	
	private final static int MAX_HEURISTIQUE = Integer.MAX_VALUE+2;
	private final static int MIN_HEURISTIQUE = Integer.MIN_VALUE-2;
	private final static int UNDEFINED_HEURISTIQUE = 0;
	
	private final static int PARTIE_NON_TERMINEE = Integer.MAX_VALUE; // ou min
		
	
	public static void init()
	{
		Grid.init();
		
		
		
	}
	
	private int nbFirstCoupAleatoire = 0;
	
	protected long bestMove;
	protected long bestMoveAdv;
	
	public NegaMaxPrud(NegaMaxPrud nmp) {
		super(nmp);
		
		this.bestMove = 0L;
		this.bestMoveAdv = 0L;
	}
	
	public NegaMaxPrud(String str, int type, int myColor) {
		super(str, type, myColor);
		
		this.bestMove = 0L;
		this.bestMoveAdv = 0L;
	}
	
	
	public void coupAdvAndUpdate(long move,boolean checkMove) {
		inverse();
		MakeMvtAndUpdate(move);
		inverse();
		
		if(checkMove & this.bestMoveAdv!=move)
		{
				this.bestMove = 0L;		
		}
	}
	
	protected int calcule(int alpha, int beta, int depth){
		
		int partieTerm = checkPartieTerm();
		if(partieTerm != PARTIE_NON_TERMINEE){
			++nbfeuilles;
			return partieTerm;
		}else if (depth == 0){
			++nbfeuilles;
			return calculeHeuristique();
		}else
		{
			int meilleur = M_INFINITY;
			
			ArrayList<Long> coups = generatePossibleMvt();
			
			for (Long move : coups) {

					NegaMaxPrud nmp = new NegaMaxPrud(this);
					nmp.MakeMvtAndUpdate(move);
					nmp.inverse();
					
					
					int val = nmp.calcule(-beta, -alpha, depth-1);
					
					if(val>meilleur)
					{
						meilleur = val;
						
						if(meilleur > alpha)
						{
							alpha = val;
							this.bestMoveAdv = nmp.bestMove;
							
							this.bestMove = move;
							
							if(alpha>=beta)
								return meilleur;
						}	
					}
				}
			return meilleur;
		}
		

	}

	protected int calculeHeuristique() {
		int heuristique = UNDEFINED_HEURISTIQUE;
		
		
//		int nbMove = generatePossibleMvt().size();
//		
//		this.inverse();
//		
//		int nbMoveAdv = generatePossibleMvt().size();
//		
//		this.inverse();
//		
//		heuristique = nbMove - nbMoveAdv;
		
		
		
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

	private int nbEntryTransReuse=0;
	
	public String getBestMove(int lvl) {
		
		System.out.println("MAX Profondeur = "+ lvl);
		
		ArrayList<Long> coups;
		
		this.bestMove = 0L;
		
		if(nbFirstCoupAleatoire >0){
			
			nbFirstCoupAleatoire--;
			
		}else if(bestMove != 0L){
			
			NegaMaxPrud g = new NegaMaxPrud(this);
			
			g.MakeMvtAndUpdate(bestMove);
			g.coupAdvAndUpdate(this.bestMoveAdv, false);

			System.out.println("Coup précédent repris");
			
			g.calcule(M_INFINITY,P_INFINITY,lvl);
			
		}
		else{
			
			nbfeuilles = 0;
			
			calcule(M_INFINITY,P_INFINITY,lvl);
		}
		
		
		if (this.bestMove == 0L) {
			coups = generatePossibleMvt();
			this.bestMove = coups.get((int) (Math.random() * coups.size()));
			++nbcoupAleatoire;
		} 
		
		
		this.precedentMove = this.bestMove;
		
		System.out.println("Feuilles parcourues  = 	" + nbfeuilles);
		System.out.println("Coup Aleatoire	     = 	" + nbcoupAleatoire);
		System.out.println("\ntaille table de transposage     = 	" + NegaMaxPrudTranspositionTable.tableDeTransposition.size());
		System.out.println("\n	reutilisées      = 	" + nbEntryTransReuse);
		
	
		Long fromLong = mPions & this.bestMove;
		Long toLong = this.bestMove ^ fromLong;
		int from = 63 - Long.numberOfLeadingZeros(fromLong);
		int to = 63 - Long.numberOfLeadingZeros(toLong);
		
		
		char[] res = new char[4];
		res[0] = (char) ('A' + (7 - (from % 8)));
		res[1] = (char) ('1' + (from / 8));
		res[2] = (char) ('A' + (7 - (to % 8)));
		res[3] = (char) ('1' + (to / 8));

		
   		String coup = "" + res[0] + res[1] + res[2] + res[3];	
		
		MakeMvtAndUpdate(this.bestMove);
		
		return coup;
	}


}
