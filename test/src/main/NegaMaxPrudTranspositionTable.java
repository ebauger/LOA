/**
 * 
 */
package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Charly
 *
 */
public class NegaMaxPrudTranspositionTable extends NegaMaxPrud {

	protected static int nbMoveGenerate;
		
	protected final static ConcurrentHashMap<Integer, ConcurrentHashMap<Long,Long>> tableDeTransposition = new ConcurrentHashMap<Integer, ConcurrentHashMap<Long, Long>>(5000);
	
	protected static int nbEntryTransReuse=0;
	protected static int nbEntryTransSaved=0;
	@Override
	protected void inverse() {
		super.inverse();
		whitePions = !whitePions;
	}
	
	public NegaMaxPrudTranspositionTable(NegaMaxPrudTranspositionTable nmp) {
		super(nmp);
		this.whitePions = nmp.whitePions;
	}
	
	public NegaMaxPrudTranspositionTable(String str, int type, int myColor) {
		super(str, type, myColor);
		
	}
		
	protected int NegaMax(int alpha, int beta, int depth){
		
		int firstKey = whitePions?(nbMPions<<5)+nbPionsAdv:(nbPionsAdv<<5)+nbMPions;
		
		
		
		ConcurrentHashMap<Long, Long> tdt = tableDeTransposition.get(firstKey);
		

		if(tdt != null)
		{
			
			Long secondKey = mPions|mPionsAdv;
			
			Long bps = tdt.get(secondKey);
			
			if(bps != null)
			{
				
				int saveDepth = (int) (bps >> 32);
				
				if(depth >= saveDepth)
				{
					
					nbEntryTransReuse++;
					
					int saveNegaMax = (int) (bps << 0);
							
							
					int meilleurTdt = (saveNegaMax < 0)?saveNegaMax+depth-saveDepth:saveNegaMax-depth + saveDepth ;
					meilleurTdt *= whitePions?1:-1;
					return meilleurTdt;
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
		}else if (depth == (Thread.currentThread().getName().equals("pool-1-thread-1")?MultiThreadNegaMax.MaxLvlThread1:MaxLvl))//this.lvlMax_heuristique)
		{
			++nbfeuilles;
			return calculeHeuristique()-depth;
		}else
		{
			
			ArrayList<Long> coups = generatePossibleMvt();
			Collections.shuffle(coups);
			
			nbMoveGenerate += coups.size();
			
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
				long start = System.nanoTime();
				
				Long secondKey = mPions|mPionsAdv;
				
				int meilleurReel = alpha * (whitePions?1:-1);
				
				synchronized (tableDeTransposition) {
					
//					System.out.println("I'm checking Table to insert: " + Thread.currentThread().getName());
					
					tdt =tableDeTransposition.get(firstKey);
							
					if(tdt == null)
					{
						ConcurrentHashMap<Long, Long> hm = new ConcurrentHashMap<Long, Long>();
						
						long bps = depth;
						bps <<= 32;
						
						bps += meilleurReel;

						
						hm.put(secondKey, bps);
						
						
						tableDeTransposition.put(firstKey, hm);
						
						
						nbEntryTransSaved++;
						
					}else{
						
						Long bps = tdt.get(secondKey);
						
						if(bps == null)
						{	
							

							bps = new Long(depth);
							
							bps <<= 32;
							
							bps += meilleurReel;

							
							tdt.put(secondKey, bps);
							nbEntryTransSaved++;
						}
						else	
						{
							int saveDepth = (int) (bps >> 32);
							
							if(depth < saveDepth){
								
								long newbps = depth;
								newbps <<= 32;
								
								newbps += meilleurReel;

								tableDeTransposition.get(firstKey).put(secondKey, newbps);

							}
						}
					}				
				}
				
				 long end = System.nanoTime();
				 float time = end - start;
				
//				 System.out.println(Thread.currentThread().getName() + " time " + time + " nano");
				
			}
			
			return alpha;
		}
		

	}
	
	protected int FirstNegaMax(int alpha, int beta, int depth){
		
		int partieTerm = checkPartieTerm();
		if(partieTerm == PARTIE_GAGNE){
			++nbfeuilles;
			return PARTIE_GAGNE-depth;
		}else if(partieTerm == PARTIE_PERDU){
			++nbfeuilles;
			return PARTIE_PERDU + depth;
		}
		else
		{
			
			ArrayList<Long> coups = generatePossibleMvt();
			Collections.shuffle(coups);
			this.bestMove = coups.get(0);
			
			nbMoveGenerate += coups.size();
			
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
				
				int firstKey = whitePions?(nbMPions<<5)+nbPionsAdv:(nbPionsAdv<<5)+nbMPions;
				
				ConcurrentHashMap<Long, Long> tdt = tableDeTransposition.get(firstKey);
								
				Long secondKey = mPions|mPionsAdv;
				
				int meilleurReel = alpha * (whitePions?1:-1);
				
				synchronized (tableDeTransposition) {
					
//					System.out.println("I'm checking Table to insert: " + Thread.currentThread().getName());
					
					tdt =tableDeTransposition.get(firstKey);
							
					if(tdt == null)
					{
						ConcurrentHashMap<Long, Long> hm = new ConcurrentHashMap<Long, Long>();
						
						long bps = depth;
						bps <<= 32;
						
						bps += meilleurReel;

						
						hm.put(secondKey, bps);
						
						
						tableDeTransposition.put(firstKey, hm);
						
						
						nbEntryTransSaved++;
						
					}else{
						
						Long bps = tdt.get(secondKey);
						
						if(bps == null)
						{	
							

							bps = new Long(depth);
							
							bps <<= 32;
							
							bps += meilleurReel;

							
							tdt.put(secondKey, bps);
							nbEntryTransSaved++;
						}
						else	
						{
							int saveDepth = (int) (bps >> 32);
							
							if(depth < saveDepth){
								
								long newbps = depth;
								newbps <<= 32;
								
								newbps += meilleurReel;

								tableDeTransposition.get(firstKey).put(secondKey, newbps);

							}
						}
					}				
				}
			}
			
				
			return alpha;
		}
	}
	
	public long getBestMove() {
		
		nbEntryTransReuse = 0;
		nbMoveGenerate = 0;
		
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
			
			heuristiqueTrouve = FirstNegaMax(M_INFINITY,P_INFINITY,0);

		}
		
		
		if (this.bestMove == 0L) {
			coups = generatePossibleMvt();
			this.bestMove = coups.get((int) (Math.random() * coups.size()));
			++nbcoupAleatoire;
		} 
		
		
//				System.out.println("Feuilles parcourues  = 	" + nbfeuilles);
//		System.out.println("Coup Aleatoire	     = 	" + nbcoupAleatoire);
		
		int tailletTrans = 0;
		for (Entry<Integer, ConcurrentHashMap<Long, Long>> es1 : tableDeTransposition.entrySet()) {
			
				tailletTrans+=es1.getValue().size();
			
		}
		
		
		
		System.out.println("\ntaille table de transposage     = 	" + nbEntryTransSaved + " reel=" + tailletTrans);
		System.out.println("	reutilisées      = 	" + nbEntryTransReuse);
		System.out.println("\nnb Moves generate    = 	" + nbMoveGenerate);
//		System.out.println("\nnb Moves Repris    = 	" + nbMovesRepris);
		
		
		System.out.println();
		if(heuristiqueTrouve <MIN_HEURISTIQUE || heuristiqueTrouve > MAX_HEURISTIQUE)
		{
			System.out.println("Probleme avec l'heuristique");
		}
		else
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
			
		
		System.out.println();
		
		
		return this.bestMove;
	}


	
}
