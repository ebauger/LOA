/**
 * 
 */
package main;

import java.util.ArrayList;

import java.util.List;


/**
 * @author Charly
 *
 */
public class NegaMaxPrud extends Grid {

	
	private final static int P_INFINITY = Integer.MAX_VALUE-1;
	private final static int M_INFINITY = Integer.MIN_VALUE+2;
	public final static int PARTIE_GAGNE = Integer.MAX_VALUE-2;
	public final static int PARTIE_PERDU = Integer.MIN_VALUE+3;
	public final static int MATCH_NULL = 0;
	
	
	private final static int MAX_HEURISTIQUE = PARTIE_GAGNE;
	private final static int MIN_HEURISTIQUE = PARTIE_PERDU;
	private final static int UNDEFINED_HEURISTIQUE = 0;
	
	public final static int PARTIE_NON_TERMINEE = Integer.MAX_VALUE; // ou min // ou min -1
	
	public static void init()
	{
		Grid.init();
		
		
		
	}
	
	private int nbFirstCoupAleatoire = 0;
	
	protected long bestMove;
	protected long bestMoveAdv;
	
	protected long precedentMove=0L;
	
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
	
	
//	public List<NegaMaxPrud> GeneratePossibleGridAdv()
//	{
//		
//		List<NegaMaxPrud> possibleGridAdv = new ArrayList<NegaMaxPrud>();
//				
//			//System.out.println("generate possible move");
//			List<Long> possMvt = this.generatePossibleMvt();
//			
//			for (long move : possMvt) {
//				
//				NegaMaxPrud nmp = new NegaMaxPrud(this);
//				
//				nmp.MakeMvtAndUpdate(move);
//				nmp.inverse(); // devient une grille adverse
//				
//				nmp.bestMoveAdv = move;
//				
//				nmp.calculeHeuristique();
//				
//				if(possibleGridAdv.isEmpty())
//				{
//					possibleGridAdv.add(nmp);
//				}
//				
//
//				if(nmp.heuristique == PARTIE_PERDU)
//				{
//					//insert first
//					possibleGridAdv.clear();
//					possibleGridAdv.add(nmp);
//					return possibleGridAdv;
//				}
//				else if(nmp.heuristique == PARTIE_GAGNE)
//				{
//					//insert last
//					possibleGridAdv.add(nmp);
//				}
//				else
//				{
//					//insertion dichotomique
//					
//					int index = -1;
//					
//					//recheche index
//					int borneGauche = 0, borneDroite = possibleGridAdv.size() -1;
//					int milieu;
//					
//					//System.out.println();
//					
//					while (borneGauche <= borneDroite)
//					{
//						milieu = (borneGauche + borneDroite) / 2;
//						
//						//System.out.print(".");
//						
//						if (nmp.heuristique < possibleGridAdv.get(milieu).heuristique) 
//							borneDroite = milieu - 1;
//						else if (nmp.heuristique >= possibleGridAdv.get(milieu).heuristique) 
//							borneGauche = milieu+1;
//					}
//					//System.out.println();
//					index = borneGauche;
//					
//					
//					
//					
//					possibleGridAdv.add(index, nmp);
//					
//				}
//				
//				
//			}
//		
//		return possibleGridAdv;
//	}
	
	protected int NegaMax(int alpha, int beta, int depth){
		
		int partieTerm = checkPartieTerm();
		if(partieTerm != PARTIE_NON_TERMINEE){
			++nbfeuilles;
			return partieTerm;
		}else if (depth == 0){
			++nbfeuilles;
			return this.calculeHeuristique();
		}else
		{
			int meilleur = M_INFINITY;
			
			ArrayList<Long> coups = generatePossibleMvt();
			
			for (Long move : coups) {

					NegaMaxPrud nmp = new NegaMaxPrud(this);
					nmp.MakeMvtAndUpdate(move);
					nmp.inverse();
					
					
					int val = -nmp.NegaMax(-beta, -alpha, depth-1);
					
					if(val>meilleur)
					{
						meilleur = val;
						
						if(meilleur > alpha)
						{
							alpha = val;
							
							
							if(alpha>=beta)
								return beta;
							else
							{
								this.bestMoveAdv = nmp.bestMove;
								
								this.bestMove = move;
							}
						}	
					}
				}
			
//			this.GeneratePossibleGridAdv();
			
			
			
			//System.out.println(this.possibleGridAdv);
			
//			for (NegaMaxPrud nmp : this.GeneratePossibleGridAdv()) {
//				
//				
//				int val = -nmp.NegaMax(-beta, -alpha, depth-1);
//				
//				if(val>meilleur)
//				{
//					meilleur = val;
//					
//					if(meilleur > alpha)
//					{
//						alpha = val;
//						
//
//						this.bestMoveAdv = nmp.bestMove;
//						
//						this.bestMove = nmp.bestMoveAdv;
//						
//						if(alpha>=beta)
//						{
//							return beta;
//						}
//
//					}	
//				}
//			}
			
			return meilleur;
		}
		

	}
	
	protected int FirstNegaMax(int alpha, int beta, int depth){
	
		int partieTerm = checkPartieTerm();
		if(partieTerm != PARTIE_NON_TERMINEE){
			++nbfeuilles;
			return partieTerm;
		}else if (depth == 0){
			++nbfeuilles;
			return this.calculeHeuristique();
		}else
		{
			int meilleur = M_INFINITY;
			
			ArrayList<Long> coups = generatePossibleMvt();
			
			for (Long move : coups) {
				if(move!= this.precedentMove)
				{
					NegaMaxPrud nmp = new NegaMaxPrud(this);
					nmp.MakeMvtAndUpdate(move);
					nmp.inverse();
					
					
					int val = -nmp.NegaMax(-beta, -alpha, depth-1);
					
					if(val>meilleur)
					{
						meilleur = val;
						
						if(meilleur > alpha)
						{
							if(alpha>=beta)
								return alpha;

							this.bestMoveAdv = nmp.bestMove;
							
							this.bestMove = move;
							
							alpha = val;
						}	
					}
				}
			}
			
			this.bestMove = precedentMove;
			
			return meilleur;
		}
		
	}
	protected int calculeHeuristique() {
		int heuristique = UNDEFINED_HEURISTIQUE;
		
//		int partTerm = this.checkPartieTerm();
//		
//		if(partTerm == PARTIE_NON_TERMINEE)
//		{
			int mheuristique = UNDEFINED_HEURISTIQUE;
//			int heuristiqueAdv = UNDEFINED_HEURISTIQUE;
		
			int sumX = 0;
			int sumY = 0;
			int cpt = 0;
			for(int i = 0; i< 64; ++i){
				if((1L << i & mPions) != 0){
					++cpt;
					sumX += i%8;
					sumY += i/8;
				}
				
			}
			
			int massX = sumX / cpt;
			int massY = sumY / cpt;
			for(int i = 0; i< 64; ++i){
				if((1L << i & mPions) != 0){
					mheuristique += Math.pow(massX- i%8, 2) + Math.pow(massY- i/8, 2);
					//heuristique += Math.abs(massX- i%8) + Math.abs(massY- i/8);
				}
				
			}
			
			mheuristique = MAX_HEURISTIQUE-mheuristique;
			
			
//			sumX = 0;
//			sumY = 0;
//			cpt = 0;
//			for(int i = 0; i< 64; ++i){
//				if((1L << i & mPionsAdv) != 0){
//					++cpt;
//					sumX += i%8;
//					sumY += i/8;
//				}
//				
//			}
//			
//			massX = sumX / cpt;
//			massY = sumY / cpt;
//			for(int i = 0; i< 64; ++i){
//				if((1L << i & mPionsAdv) != 0){
//					heuristiqueAdv += Math.pow(massX- i%8, 2) + Math.pow(massY- i/8, 2);
//					//heuristique += Math.abs(massX- i%8) + Math.abs(massY- i/8);
//				}
//				
//			}
//			
//			heuristiqueAdv = MIN_HEURISTIQUE + heuristiqueAdv;
			
			
			heuristique = mheuristique ;//- heuristiqueAdv;
			
			
//		}
//		else
//		{
//			heuristique = partTerm;
//		}
		
		
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

	protected int nbEntryTransReuse=0;
	
	public long getBestMove(int lvl)
	{
		//System.out.println("get best move : MAX Profondeur = "+ lvl);
		
		ArrayList<Long> coups;
		
		this.bestMove = 0L;
		
		if(nbFirstCoupAleatoire >0){
			
			nbFirstCoupAleatoire--;
			
		}else if(bestMove != 0L){
			
			NegaMaxPrud g = new NegaMaxPrud(this);
			
			g.MakeMvtAndUpdate(bestMove);
			g.coupAdvAndUpdate(this.bestMoveAdv, false);

			//System.out.println("Coup précédent repris");
			
			g.NegaMax(M_INFINITY,P_INFINITY,lvl);
			
		}
		else{
			
			nbfeuilles = 0;
			
			FirstNegaMax(M_INFINITY,P_INFINITY,lvl);
		}
		
		
		if (this.bestMove == 0L) {
			coups = generatePossibleMvt();
			this.bestMove = coups.get((int) (Math.random() * coups.size()));
			++nbcoupAleatoire;
		} 
		
		
//		this.precedentMove = this.bestMove;
//		
//		System.out.println("Feuilles parcourues  = 	" + nbfeuilles);
//		System.out.println("Coup Aleatoire	     = 	" + nbcoupAleatoire);
//		System.out.println("\ntaille table de transposage     = 	" + NegaMaxPrudTranspositionTable.tableDeTransposition.size());
//		System.out.println("\n	reutilisées      = 	" + nbEntryTransReuse);
		
		
		//System.out.println("get best move : end move=" + Long.toBinaryString(this.bestMove));
		return this.bestMove;
		
	}

}
