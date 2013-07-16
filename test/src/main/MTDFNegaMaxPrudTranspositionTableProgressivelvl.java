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
public class MTDFNegaMaxPrudTranspositionTableProgressivelvl extends NegaMaxPrud {

	
	private final static int P_INFINITY = Integer.MAX_VALUE-1;
	private final static int M_INFINITY = Integer.MIN_VALUE+1;
	private final static int PARTIE_GAGNE = Integer.MAX_VALUE-2;
	private final static int PARTIE_PERDU = Integer.MIN_VALUE+2;
	private final static int MATCH_NULL = 0;
	
	private final static int MAX_HEURISTIQUE = Integer.MAX_VALUE+2;
	private final static int MIN_HEURISTIQUE = Integer.MIN_VALUE-2;
	private final static int UNDEFINED_HEURISTIQUE = 0;
	
	private final static int PARTIE_NON_TERMINEE = Integer.MAX_VALUE; // ou min
	
	private int lvlMax_heuristique = 0;
	private int lvlMin_heuristique = 0;
	
	private int heuristiqueTrouve = 0;
	
	protected final static HashMap<String,BestPathState> tableDeTransposition = new HashMap<String, MTDFNegaMaxPrudTranspositionTableProgressivelvl.BestPathState>(5000);
	
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

		private MTDFNegaMaxPrudTranspositionTableProgressivelvl getOuterType() {
			return MTDFNegaMaxPrudTranspositionTableProgressivelvl.this;
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
	

	
	public MTDFNegaMaxPrudTranspositionTableProgressivelvl(MTDFNegaMaxPrudTranspositionTableProgressivelvl nmp) {
		super(nmp);
		

	}
	
	public MTDFNegaMaxPrudTranspositionTableProgressivelvl(String str, int type, int myColor) {
		super(str, type, myColor);
		
	}
	

	protected void inverse() {
		Long temp = mPions;
		mPions = new Long(mPionsAdv);
		mPionsAdv = new Long(temp);
		
//		int lvlMax_heuristiqueTmp = this.lvlMax_heuristique;
//		this.lvlMax_heuristique = lvlMin_heuristique;
//		this.lvlMin_heuristique = lvlMax_heuristiqueTmp;
		
	}
	
	protected int NegaMax(int alpha, int beta, int depth){
		
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
			
//			if(partieTerm == PARTIE_GAGNE)
//				++this.lvlMax_heuristique ;
//			else if(partieTerm == PARTIE_PERDU)
//				++this.lvlMin_heuristique ;
			
			++nbfeuilles;
			return partieTerm;
		}else if (depth == 0 )// this.lvlMax_heuristique){
		{
			++nbfeuilles;
			return calculeHeuristique();
		}else
		{
			int meilleur = M_INFINITY;
			
			ArrayList<Long> coups = generatePossibleMvt();
			
			for (Long move : coups) {
				
				MTDFNegaMaxPrudTranspositionTableProgressivelvl nmp = new MTDFNegaMaxPrudTranspositionTableProgressivelvl(this);
				nmp.MakeMvtAndUpdate(move);
				nmp.inverse();
				
				
				int val = -nmp.NegaMax(-beta, -alpha, depth-1);

				
				if(val>meilleur)
				{
					meilleur = val;
					
					if(meilleur > alpha)
					{
						alpha = val;
						
//						this.lvlMax_heuristique = nmp.lvlMin_heuristique;
//						this.lvlMin_heuristique = nmp.lvlMax_heuristique;
							
						if(alpha>=beta){
							
							return beta;
						}
						else
						{
							this.bestMoveAdv = nmp.bestMove;
							
							this.bestMove = move;
						}
					}	
				}
			}
			
			
			if(depth > 2){
				tdt =tableDeTransposition.get(key);
				
				if(tdt == null)
				{
					BestPathState bps = new BestPathState(mPions,mPionsAdv, meilleur, this.bestMove,this.bestMoveAdv, depth);
					tableDeTransposition.put(key,bps);
				}else if(tdt.getDepth()<depth){
					BestPathState bps = new BestPathState(mPions,mPionsAdv, meilleur, this.bestMove,this.bestMoveAdv, depth);
					tableDeTransposition.put(key,bps);
				}
			}
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
			}
			
			this.bestMove = precedentMove;
			
			return meilleur;
		}
	}
	
	protected static int nbEntryTransReuse=0;
	
	public long getBestMove(int maxlvl) {
		
		nbBoucleMTDF = 0;
		
		nbEntryTransReuse = 0;
		
//		if(tableDeTransposition.size()>100000)
//			lvl+=2;
//		else 
//		if(tableDeTransposition.size()>7000)
//			maxlvl++;
		
		
		//System.out.println("MAX Profondeur = "+ maxlvl);
		
		
		ArrayList<Long> coups;
		
		this.bestMove = 0L;
		
		if(nbFirstCoupAleatoire >0){
			
			nbFirstCoupAleatoire--;
			
//		}else if(bestMove != 0L){
//			
//			NegaMaxPrudTranspositionTable g = new NegaMaxPrudTranspositionTable(this);
//			
//			g.MakeMvtAndUpdate(bestMove);
//			g.coupAdvAndUpdate(this.bestMoveAdv, false);
//			
//			g.lvlMax_heuristique = this.lvlMax_heuristique;
//			g.lvlMin_heuristique = this.lvlMin_heuristique;
//			
//			System.out.println("Coup précédent repris");
//			
//			g.calcule(M_INFINITY,P_INFINITY,maxlvl);
//			
		}
		else{
			
			nbfeuilles = 0;
			 long start = System.nanoTime();
			 
			 
			 heuristiqueTrouve = progressivelvl(maxlvl,heuristiqueTrouve);
			
			 long end = System.nanoTime();
			 float time = end - start;
			
			 System.out.println("MTDF : time " + time / 1000000 + "micro");
			
			 
			 
		}
		
		
		if (this.bestMove == 0L) {
			coups = generatePossibleMvt();
			this.bestMove = coups.get((int) (Math.random() * coups.size()));
			++nbcoupAleatoire;
		} 
		
		
//		this.precedentMove = this.bestMove;
		
		System.out.println("Feuilles parcourues  = 	" + nbfeuilles);
		System.out.println("Coup Aleatoire	     = 	" + nbcoupAleatoire);
		System.out.println("\ntaille table de transposage     = 	" + MTDFNegaMaxPrudTranspositionTableProgressivelvl.tableDeTransposition.size());
		System.out.println("	reutilisées      = 	" + nbEntryTransReuse);
		System.out.println("\nnb Moves Repris    = 	" + nbMovesRepris);
		
		
		System.out.println();
		if(heuristiqueTrouve !=-1)
		{
			if(heuristiqueTrouve == PARTIE_GAGNE)
			{
				System.out.println(" PARTIE_GAGNE lvl = " + this.lvlMax_heuristique );
				
			}
			else if(heuristiqueTrouve == PARTIE_PERDU)
			{
				System.out.println(" PARTIE_PERDU lvl = " + this.lvlMin_heuristique );
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

	private int nbBoucleMTDF;
	protected int MTDF(int depth, int f )
	{
		
		int g = f;
		
		
		
		int upperBound = P_INFINITY;
		int lowerBound = M_INFINITY;
		
		while(lowerBound < upperBound)
		{
			//System.out.print(".");
			//nbBoucleMTDF++;
			int beta;
			
			if( g == lowerBound)
				beta = g+1;
			else
				beta = g;
			
			g=FirstNegaMax(beta-1, beta, depth);
			
			if(g<beta)
			{
				upperBound = g;
			}
			else
			{
				lowerBound = g;
			}
				
				
		}
		
		System.out.println("\nnbBoucleMTDF="+nbBoucleMTDF);
		return g;
		
	}
	
	protected int progressivelvl(int depth,int f){
		
		int g=f;
		
		for(int i = 2;i<=depth;i+=2)
		{
			g = MTDF(depth, g);
		}
		
		return g;
	}

}
