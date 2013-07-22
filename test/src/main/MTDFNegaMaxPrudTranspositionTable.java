/**
 * 
 */
package main;

import java.util.ArrayList;


/**
 * @author Charly
 *
 */
public class MTDFNegaMaxPrudTranspositionTable extends NegaMaxPrudTranspositionTable {

	protected int nbBoucleMTDF;
			
	
	
	
	
	public MTDFNegaMaxPrudTranspositionTable(MTDFNegaMaxPrudTranspositionTable nmp) {
		super(nmp);
		

	}
	
	public MTDFNegaMaxPrudTranspositionTable(String str, int type, int myColor) {
		super(str, type, myColor);
		
	}

	public long getBestMove() {
		
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
		}
		else{
			
			nbfeuilles = 0;
			 long start = System.nanoTime();
			 
			 
			 heuristiqueTrouve = MTDF(0,heuristiqueTrouve);
			
			 long end = System.nanoTime();
			 float time = end - start;
			
			 System.out.println("MTDF : time " + time / 1000000 + "mili");
			
			 
			 
		}
		
		
		if (this.bestMove == 0L) {
			coups = generatePossibleMvt();
			this.bestMove = coups.get((int) (Math.random() * coups.size()));
			++nbcoupAleatoire;
		} 
		
		
//		this.precedentMove = this.bestMove;
		
		System.out.println("Feuilles parcourues  = 	" + nbfeuilles);
		System.out.println("Coup Aleatoire	     = 	" + nbcoupAleatoire);
		System.out.println("\ntaille table de transposage     = 	" + MTDFNegaMaxPrudTranspositionTable.tableDeTransposition.size());
		System.out.println("	reutilisées      = 	" + nbEntryTransReuse);
		System.out.println("\nnb Moves Repris    = 	" + nbMovesRepris);
		
		
		System.out.println();
		if(heuristiqueTrouve !=-1)
		{
			if(heuristiqueTrouve == PARTIE_GAGNE)
			{
				System.out.println(" PARTIE_GAGNE ");
				
			}
			else if(heuristiqueTrouve == PARTIE_PERDU)
			{
				System.out.println(" PARTIE_PERDU lvl");
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

	protected int MTDF(int depth, int f )
	{
		
		int g = f;
		
		int upperBound = P_INFINITY;
		int lowerBound = M_INFINITY;
		
		while(lowerBound < upperBound)
		{
			
			nbBoucleMTDF++;
			int beta;
			
			if( g == lowerBound)
				beta = g+1;
			else
				beta = g;
			
			g=FirstNegaMax(beta-1, beta , depth);
			
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
	

}
