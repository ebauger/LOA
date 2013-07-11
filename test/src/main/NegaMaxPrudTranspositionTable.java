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
public class NegaMaxPrudTranspositionTable extends NegaMaxPrud {

	
	private final static int P_INFINITY = Integer.MAX_VALUE-1;
	private final static int M_INFINITY = Integer.MIN_VALUE+1;
	private final static int PARTIE_GAGNE = Integer.MAX_VALUE-2;
	private final static int PARTIE_PERDU = Integer.MIN_VALUE+2;
	private final static int MATCH_NULL = 0;
	
	private final static int MAX_HEURISTIQUE = Integer.MAX_VALUE+2;
	private final static int MIN_HEURISTIQUE = Integer.MIN_VALUE-2;
	private final static int UNDEFINED_HEURISTIQUE = 0;
	
	private final static int PARTIE_NON_TERMINEE = Integer.MAX_VALUE; // ou min
	
	protected final static HashMap<String,BestPathState> tableDeTransposition = new HashMap<String, NegaMaxPrudTranspositionTable.BestPathState>(5000);
	
		protected class BestPathState{
		
		private int meilleurNegaMax;
		private long bestMove;
		private long bestMoveAdv;
		private int depth;
		private long mPions;
		private long pionsAdv;
		
		public int getMeilleurNegaMax() {
			return meilleurNegaMax;
		}

		public long getBestMove() {
			return bestMove;
		}
		
		public long getBestMoveAdv() {
			return bestMoveAdv;
		}

		public int getDepth() {
			return depth;
		}
		
		public BestPathState(long mPions,long pionsAdv ,int meilleurNegaMax, long bestMove,long bestMoveAdv, int depth ) {
			this.mPions = mPions;
			this.pionsAdv = pionsAdv;
			this.meilleurNegaMax = meilleurNegaMax;
			this.bestMove = bestMove;
			this.bestMoveAdv = bestMoveAdv;
			this.depth = depth;
			
			
		}
		
		
		

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + (int) (mPions ^ (mPions >>> 32));
			result = prime * result + (int) (pionsAdv ^ (pionsAdv >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof BestPathState))
				return false;
			BestPathState other = (BestPathState) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (mPions != other.mPions)
				return false;
			if (pionsAdv != other.pionsAdv)
				return false;
			return true;
		}

		private NegaMaxPrudTranspositionTable getOuterType() {
			return NegaMaxPrudTranspositionTable.this;
		}
		
		
		public String ID()
		{
			
			String res = Long.toString(mPions);
			
			res+= Long.toBinaryString(pionsAdv);
			
			return res;
		}
		
	}
	
	
	public static void init()
	{
		Grid.init();
		
		
		
	}
	
	private int nbFirstCoupAleatoire = 0;
	

	
	public NegaMaxPrudTranspositionTable(NegaMaxPrudTranspositionTable nmp) {
		super(nmp);

	}
	
	public NegaMaxPrudTranspositionTable(String str, int type, int myColor) {
		super(str, type, myColor);
		
	}
	
	protected int calcule(int alpha, int beta, int depth){
		
		String key = Long.toBinaryString(mPions) + Long.toBinaryString(mPionsAdv);
		
		BestPathState tdt = tableDeTransposition.get(key);
		
		if(tdt != null)
		{
			
			if(depth <= tdt.getDepth())
			{
				
				nbEntryTransReuse++;
				
				this.bestMove = tdt.bestMove;
				this.bestMoveAdv = tdt.bestMoveAdv;
				
				return tdt.getMeilleurNegaMax();
			}
		}
		
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
				
				NegaMaxPrudTranspositionTable nmp = new NegaMaxPrudTranspositionTable(this);
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
						
						if(alpha>=beta){
							
							tdt =tableDeTransposition.get(key);
						
							if(tdt == null)
							{
								BestPathState bps = new BestPathState(mPions,mPionsAdv, meilleur, this.bestMove,this.bestMoveAdv, depth);
								tableDeTransposition.put(key,bps);
							}else if(tdt.getDepth()<depth){
								BestPathState bps = new BestPathState(mPions,mPionsAdv, meilleur, this.bestMove,this.bestMoveAdv, depth);
								tableDeTransposition.put(key,bps);
							}
							
							return meilleur;
						}
					}	
				}
			}
			
			
			
			tdt =tableDeTransposition.get(key);
			
			if(tdt == null)
			{
				BestPathState bps = new BestPathState(mPions,mPionsAdv, meilleur, this.bestMove,this.bestMoveAdv, depth);
				tableDeTransposition.put(key,bps);
			}else if(tdt.getDepth()<depth){
				BestPathState bps = new BestPathState(mPions,mPionsAdv, meilleur, this.bestMove,this.bestMoveAdv, depth);
				tableDeTransposition.put(key,bps);
			}
				
			return meilleur;
		}
		

	}

	protected static int nbEntryTransReuse=0;
	
	public String getBestMove(int lvl) {
		
		nbEntryTransReuse = 0;
		
//		if(tableDeTransposition.size()>100000)
//			lvl+=2;
//		else 
//		if(tableDeTransposition.size()>7000)
//			lvl++;
//		
		
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
		System.out.println("	reutilisées      = 	" + nbEntryTransReuse);
		System.out.println("\nnb Moves Repris    = 	" + nbMovesRepris);
		
		
	
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
