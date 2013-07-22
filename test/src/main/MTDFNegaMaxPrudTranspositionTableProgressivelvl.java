/**
 * 
 */
package main;

import java.util.ArrayList;

/**
 * @author Charly
 *
 */
public class MTDFNegaMaxPrudTranspositionTableProgressivelvl extends MTDFNegaMaxPrudTranspositionTable {
	
	
	
	
	
	
	
	public MTDFNegaMaxPrudTranspositionTableProgressivelvl(MTDFNegaMaxPrudTranspositionTableProgressivelvl nmp) {
		super(nmp);
		

	}
	
	public MTDFNegaMaxPrudTranspositionTableProgressivelvl(String str, int type, int myColor) {
		super(str, type, myColor);
		
	}

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
				
		if(nbFirstCoupAleatoire >0){
			
			nbFirstCoupAleatoire--;	
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

	protected int progressivelvl(int depth,int f){
		
		int g=f;
		
		for(int i = 1;i<=depth;++i)
		{
			g = MTDF(depth, g);
			
			if(g==PARTIE_GAGNE)
				break;
		}
		
		return g;
	}

}
