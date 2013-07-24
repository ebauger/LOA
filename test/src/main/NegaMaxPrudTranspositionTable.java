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
		
		public BestPathState(int meilleurNegaMax, int depth ) {

			this.meilleurNegaMax = meilleurNegaMax;
			this.depth = depth;
			
			
		}		
	}
	
	protected static int nbEntryTransReuse=0;

	@Override
	protected void inverse() {
		super.inverse();
		whitePions = !whitePions;
	}
	
	public NegaMaxPrudTranspositionTable(NegaMaxPrudTranspositionTable nmp) {
		super(nmp);
	}
	
	public NegaMaxPrudTranspositionTable(String str, int type, int myColor) {
		super(str, type, myColor);
		
	}
		
	protected int NegaMax(int alpha, int beta, int depth){
		
		int firstKey = whitePions?nbMPions<<5+nbPionsAdv:nbPionsAdv<<3+nbMPions;
		
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
					int meilleurTdt = bps.meilleurNegaMax+bps.depth-depth;
					meilleurTdt *= whitePions?1:-1;
					return meilleurTdt<MIN_HEURISTIQUE+depth?MIN_HEURISTIQUE:meilleurTdt-depth;
				}
			}
		}

		
		int partieTerm = checkPartieTerm();
		if(partieTerm == PARTIE_GAGNE){
			++nbfeuilles;
			return PARTIE_GAGNE-depth;
		}else if(partieTerm == PARTIE_PERDU){
			++nbfeuilles;
			return PARTIE_PERDU+depth;
		}else if (depth == MaxLvl)//this.lvlMax_heuristique)
		{
			++nbfeuilles;
			int heur = calculeHeuristique();
			return heur<MIN_HEURISTIQUE+depth?MIN_HEURISTIQUE+1:heur-depth;
		}else
		{
			
			ArrayList<Long> coups = generatePossibleMvt();
			
			for (Long move : coups) {
				
				NegaMaxPrudTranspositionTable nmp = new NegaMaxPrudTranspositionTable(this);
				nmp.MakeMvtAndUpdate(move,false);
				nmp.inverse();
				
				
				int val = -nmp.NegaMax(-beta, -alpha, depth+1);
//				--lvlparcouru;
				
				if(val>alpha)
				{
					alpha = val;					
					
					if(alpha>=beta)
					{
						return alpha;
					}
						
				}
			}
			
			
			if(alpha != M_INFINITY)
			{
				tdt =tableDeTransposition.get(firstKey);
				
				int meilleurReel = alpha * (whitePions?1:-1);
				
				if(tdt == null)
				{
					bps = new BestPathState(meilleurReel, depth);
					
					HashMap<Long, BestPathState> hm = new HashMap<Long, NegaMaxPrudTranspositionTable.BestPathState>();
					hm.put(secondKey, bps);
					
					tableDeTransposition.put(firstKey, hm);
					
				}else{
					
					bps = tdt.get(secondKey);
					
					if(bps == null)
						tdt.put(secondKey, new BestPathState(meilleurReel, depth));
					else				
						if(depth < bps.depth){
							bps.meilleurNegaMax = meilleurReel;
						}
				}
			}
			
			return alpha;
		}
		

	}
	
	protected int FirstNegaMax(int alpha, int beta, int depth){
		
		int partieTerm = checkPartieTerm();
		if(partieTerm == PARTIE_GAGNE){
			++nbfeuilles;
			return PARTIE_GAGNE;
		}else if(partieTerm == PARTIE_PERDU){
			++nbfeuilles;
			return PARTIE_GAGNE;
		}else
		{			
			ArrayList<Long> coups = generatePossibleMvt();
			
			this.bestMove = coups.get(0);
			
			for (Long move : coups) {

				NegaMaxPrudTranspositionTable nmp = new NegaMaxPrudTranspositionTable(this);
				nmp.MakeMvtAndUpdate(move,false);
				nmp.inverse();
				
				
				int val = -nmp.NegaMax(-beta, -alpha, depth+1);
				
//				--lvlparcouru;
				if(val>alpha)
				{
					this.bestMove = move;
					
					alpha = val;
					
					if(alpha>=beta)
					{
						return alpha;
					}
				}
			}
				
			
			
			if(alpha != M_INFINITY)
			{
				int firstKey = whitePions?nbMPions<<3+nbPionsAdv:nbPionsAdv<<3+nbMPions;
				
				Long secondKey = mPions|mPionsAdv;
					
				HashMap<Long, BestPathState> tdt = tableDeTransposition.get(firstKey);
				
				BestPathState bps = null;
				
				int meilleurReel = alpha* (whitePions?1:-1);
								
				if(tdt == null)
				{
					bps = new BestPathState(meilleurReel, depth);
					
					HashMap<Long, BestPathState> hm = new HashMap<Long, NegaMaxPrudTranspositionTable.BestPathState>();
					
					hm.put(secondKey, bps);
					tableDeTransposition.put(firstKey, hm);
					
				}else{
					
					bps = tdt.get(secondKey);
					
					if(bps == null)
						tdt.put(secondKey, new BestPathState(meilleurReel, depth));
					else				
						if(depth < bps.depth){
							bps.meilleurNegaMax = meilleurReel;
						}
				}
			}
				
			return alpha;
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
