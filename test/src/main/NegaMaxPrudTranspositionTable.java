/**
 * 
 */
package main;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Charly
 *
 */
public class NegaMaxPrudTranspositionTable extends NegaMaxPrud {

		
	protected final static HashMap<Integer, HashMap<Long, NegaMaxPrudTranspositionTable.BestPathState>> tableDeTransposition = new HashMap<Integer, HashMap<Long, NegaMaxPrudTranspositionTable.BestPathState>>(5000);
	
	protected class BestPathState{
		
		protected int meilleurNegaMax;
		protected int depth;
		protected long mPions;
		protected long pionsAdv;
		
		public BestPathState(long mPions,long pionsAdv ,int meilleurNegaMax, int depth ) {
			this.mPions = mPions;
			this.pionsAdv = pionsAdv;
			this.meilleurNegaMax = meilleurNegaMax;
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
	
	protected static int nbEntryTransReuse=0;

	
	public NegaMaxPrudTranspositionTable(NegaMaxPrudTranspositionTable nmp) {
		super(nmp);
	}
	
	public NegaMaxPrudTranspositionTable(String str, int type, int myColor) {
		super(str, type, myColor);
		
	}
		
	protected int NegaMax(int alpha, int beta, int depth){
		
		int firstKey = whitePions?nbMPions<<3+nbPionsAdv:nbPionsAdv<<3+nbMPions;
		
		Long secondKey = mPions|mPionsAdv;
		
		HashMap<Long, BestPathState> tdt = tableDeTransposition.get(firstKey);
		
		BestPathState bps = null;
		
		if(tdt != null)
		{
			bps = tdt.get(secondKey);
			
			if(bps != null)
			{
				if(depth >= bps.depth)
				{
					nbEntryTransReuse++;	
					int meilleurTdt = bps.meilleurNegaMax;
					meilleurTdt *= whitePions?1:-1;
					return meilleurTdt == PARTIE_PERDU? meilleurTdt + depth:meilleurTdt-depth;
				}
			}
		}

		
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
				
				NegaMaxPrudTranspositionTable nmp = new NegaMaxPrudTranspositionTable(this);
				nmp.MakeMvtAndUpdate(move,false);
				nmp.inverse();
				
				
				int val = -nmp.NegaMax(-beta, -alpha, depth+1);
//				--lvlparcouru;
				
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
			
			
			
			tdt =tableDeTransposition.get(firstKey);
			
			int meilleurReel = meilleur == PARTIE_PERDU?meilleur+depth:meilleur-depth;
			
			meilleurReel *= whitePions?1:-1;
			
			if(tdt == null)
			{
				bps = new BestPathState(mPions,mPionsAdv, meilleurReel, depth);
				
				HashMap<Long, BestPathState> hm = new HashMap<Long, NegaMaxPrudTranspositionTable.BestPathState>();
				hm.put(secondKey, bps);
				tableDeTransposition.put(firstKey, hm);
				
			}else{
				
				bps = tdt.get(secondKey);
				
				if(bps == null)
					tdt.put(secondKey, new BestPathState(mPions,mPionsAdv, meilleurReel, depth));
				else				
					if(depth < bps.depth){
						bps.meilleurNegaMax = meilleurReel;
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
		}else if(partieTerm == PARTIE_PERDU){
			++nbfeuilles;
			return PARTIE_GAGNE + depth;
		}else
		{
			int meilleur = M_INFINITY;
			
			ArrayList<Long> coups = generatePossibleMvt();
			
			this.bestMove = coups.get(0);
			
			for (Long move : coups) {

				NegaMaxPrudTranspositionTable nmp = new NegaMaxPrudTranspositionTable(this);
				nmp.MakeMvtAndUpdate(move,false);
				nmp.inverse();
				
				
				int val = -nmp.NegaMax(-beta, -alpha, depth+1);
				
//				--lvlparcouru;
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
				
			int firstKey = whitePions?nbMPions<<3+nbPionsAdv:nbPionsAdv<<3+nbMPions;
			
			Long secondKey = mPions|mPionsAdv;
				
			HashMap<Long, BestPathState> tdt = tableDeTransposition.get(firstKey);
			
			BestPathState bps = null;
			
			int meilleurReel = meilleur == PARTIE_PERDU?meilleur+depth+1:meilleur-depth-1;
			
			if(tdt == null)
			{
				bps = new BestPathState(mPions,mPionsAdv, meilleurReel, depth);
				
				HashMap<Long, BestPathState> hm = new HashMap<Long, NegaMaxPrudTranspositionTable.BestPathState>();
				hm.put(secondKey, bps);
				tableDeTransposition.put(firstKey, hm);
				
			}else{
				
				bps = tdt.get(secondKey);
				
				if(bps == null)
					tdt.put(secondKey, new BestPathState(mPions,mPionsAdv, meilleurReel, depth));
				else				
					if(depth < bps.depth){
						bps.meilleurNegaMax = meilleurReel;
					}
			}
				
			return meilleur;
		}
	}
	
	public long getBestMove() {
		
		nbEntryTransReuse = 0;
		
//		if(tableDeTransposition.size()>100000)
//			lvl+=2;
//		else 
//		if(tableDeTransposition.size()>7000)
//			maxlvl++;
		
		
		//System.out.println("MAX Profondeur = "+ maxlvl);
		
		int heuristiqueTrouve = -1;
		ArrayList<Long> coups;
		
		this.bestMove = 0L;
		
		if(nbFirstCoupAleatoire >0){
			
			nbFirstCoupAleatoire--;
			
		}else{
			
			nbfeuilles = 0;
			 long start = System.nanoTime();
			 
			 
			heuristiqueTrouve = FirstNegaMax(M_INFINITY,P_INFINITY,0);
			
			
			 long end = System.nanoTime();
			 float time = end - start;
			
			 System.out.println("Negax : time " + time / 1000000 + "micro");
			
			 
		}
		
		
		if (this.bestMove == 0L) {
			coups = generatePossibleMvt();
			this.bestMove = coups.get((int) (Math.random() * coups.size()));
			++nbcoupAleatoire;
		} 
		
		
//				System.out.println("Feuilles parcourues  = 	" + nbfeuilles);
//		System.out.println("Coup Aleatoire	     = 	" + nbcoupAleatoire);
//		System.out.println("\ntaille table de transposage     = 	" + MTDFNegaMaxPrudTranspositionTable.tableDeTransposition.size());
		System.out.println("	reutilisées      = 	" + nbEntryTransReuse);
//		System.out.println("\nnb Moves Repris    = 	" + nbMovesRepris);
		
		
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
