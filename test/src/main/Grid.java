package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Grid {// implements Runnable{

	private static Map<Integer, Long> MASK;
	public static int TYPE_DECODE_SERVER = 0;
	//private Stack<Stack<Long>> mStackMvts;
	//private Stack<Grid> mStackGame;
	
	//les masques qui retourne tout les bits à 1 sur une même ligne en fonction de la case i, avec 0<=i<64
	private static final Map<Integer, Long> LOA_MASK_LINES = new HashMap<Integer, Long>(64);
	private static final Map<Integer, Long> LOA_MASK_COLUMNS = new HashMap<Integer, Long>(64);
	private static final Map<Integer, Long> LOA_MASK_DR = new HashMap<Integer, Long>(64);
	private static final Map<Integer, Long> LOA_MASK_DL = new HashMap<Integer, Long>(64);
	
	
	//Contien le nombre de bit correspondant à la valuer (long) de la clé
	private static final Map<Long, Integer> COMPTEUR = new HashMap<Long, Integer>(6000); //5628 mais c'est pas sûr

	private long mPions = 0;
	private long mPionsAdv = 0;

	

	

	private static final Map<Long, Long> MASK_MOVEMENT = new HashMap<Long, Long>(
			2016);

	private Grid(Grid g, long mvt, boolean inverse) {
		if(inverse){
			this.mPions = g.mPionsAdv;
			this.mPionsAdv = g.mPions;
			coupAdvAndUpdate(mvt);
		}
		else{
			this.mPions = g.mPions;
			this.mPionsAdv = g.mPionsAdv;
			MakeMvtAndUpdate(mvt);
		}

		
	}
	
	

	public Grid(String str, int type, int myColor) {
		
		int offset = 0;

		if (type == TYPE_DECODE_SERVER) {

			long pionsWhite = 0;
			long pionsBlack = 0;

			for (char c : str.toCharArray()) {
				if (c == '4') {
					pionsWhite |= (long) 1 << 63 - offset;
					

				} else if (c == '2') {
					pionsBlack |= (long) 1 << 63 - offset;
					
				}

				++offset;
			}

			if (myColor == Messages.White) {
				mPions = pionsWhite;
				mPionsAdv = pionsBlack;
			} else {
				mPions = pionsBlack;
				mPionsAdv = pionsWhite;
			}

		} else {

			for (char c : str.toCharArray()) {
				if (c == '1') {
					mPions |= (long) 1 << 63 - offset;
					

				} else if (c == '2') {
					mPionsAdv |= (long) 1 << 63 - offset;
					
				}

				++offset;
			}
		}

	}

	public static void printBits(Long arrayBits) {
		int i = Long.numberOfLeadingZeros(arrayBits);

		while (i-- > 0) {
			System.out.print("0");
		}
		System.out.println(Long.toBinaryString(arrayBits));

	}

	public boolean isConnected() {
		return isConnected(mPions);
	}

	private boolean isConnected(long pions) {


		if (pions != 0) {
			int i = 63 - Long.numberOfLeadingZeros(pions);

			long nexts = 1L << i;
			while (nexts != 0) {

				long comboMask = 0;

				pions ^= nexts;
				while (nexts != 0) {
					int j = 63 - Long.numberOfLeadingZeros(nexts);
					comboMask |= MASK.get(j);
					nexts ^= 1L << j;
				}

				nexts = pions & comboMask;

			}

		}

		return pions == 0;
	}

	public static void printBits() {

		/*printBits(LOA_MASK_DR.get(62));
		printBits(LOA_MASK_DL.get(12));
		printBits(1L<<3 | 1L<<11 | 1L<<51);
		System.out.println(COMPTEUR.get(1L<<3 | 1L<<11 | 1L<<51));
		System.out.println(COMPTEUR.get(1L<<3 | 1L<<10 | 1L<<24));
		System.out.println(COMPTEUR.get(1L<<30 | 1L<<44 | 1L<<51));
		System.out.println(COMPTEUR.get(1L<<13 | 1L<<27 | 1L<<34 | 1L<<48));
		System.out.println(COMPTEUR.get(1L<<23 | 1L<<5));
		System.out.println(COMPTEUR.get(1L<<55 | 1L<<37 | 1L<<28));
		System.out.println(COMPTEUR.get(1L<<24 | 1L<<60 | 1L<<33 | 1L <<42));
		System.out.println(COMPTEUR.get(1L<<63 | 1L<<18));*/
		/*System.out.println(COMPTEUR.get(1L<<62 | 1L<<55));
		System.out.println(COMPTEUR.get(1L<<38 | 1L<<45));
		System.out.println(COMPTEUR.get(1L<<39 | 1L<<60));
		System.out.println(COMPTEUR.get(1L<<47 | 1L<<61));*/

	}

	/**
	 * 
	 * @param pions
	 * @return the List of possible moves
	 */
	public ArrayList<Long> generatePossibleMvt() {
		ArrayList<Long> possMvt = new ArrayList<Long>();

		// parcour chaque bits (boucle de 64)
		// + condition si 1L<<i et pions != 0
		long totalPions = mPions|mPionsAdv;
		for (int i = 63; i >= 0; --i) {
			if ((1L << i & mPions) != 0) {
				
				int actionLine = COMPTEUR.get(totalPions & LOA_MASK_LINES.get(i));
				int actionCol = COMPTEUR.get(totalPions & LOA_MASK_COLUMNS.get(i));
				int actionDR = COMPTEUR.get(totalPions & LOA_MASK_DR.get(i));
				int actionDL = COMPTEUR.get(totalPions & LOA_MASK_DL.get(i));
				
				long movement = 0;
				
				int endI = i + actionLine;  // à gauche
				if(((1L << endI) & LOA_MASK_LINES.get(i)) != 0 && (1L << endI & mPions)==0 ){
					movement = 1L << i | 1L << endI;
					
					if((MASK_MOVEMENT.get(movement)&mPionsAdv) == 0)
						possMvt.add(movement);
					
				}
				endI = i - actionLine; //mvt à droite
				if(((1L << endI) & LOA_MASK_LINES.get(i)) != 0 && (1L << endI & mPions)==0 ){
					movement = 1L << i | 1L << endI;
					if((MASK_MOVEMENT.get(movement)&mPionsAdv) == 0)
						possMvt.add(movement);
				}
				/*if(((1L << i - actionLine) & LOA_MASK_LINES.get(i)) != 0){
					possMvt.add(1L << i | 1L << i-actionLine);
				}*/
				endI = i + 8*actionCol; //mvt en haut
				if(endI < 64 && (1L << endI & mPions)==0 ){
					movement = 1L << i | 1L << endI;
					
					if((MASK_MOVEMENT.get(movement)&mPionsAdv) == 0)
						possMvt.add(movement);
				}
				/*if(((1L << i + 8*actionCol) & LOA_MASK_COLUMNS.get(i)) != 0){
					possMvt.add(1L << i | 1L << i+ 8*actionCol);
				}*/
				endI = i - 8*actionCol; //mvt en bas
				if(endI>=0 && (1L << endI & mPions)==0 ){
					movement = 1L << i | 1L << endI;
					if((MASK_MOVEMENT.get(movement)&mPionsAdv) == 0)
						possMvt.add(movement);
				}
				/*if(((1L << i - 8*actionCol) & LOA_MASK_COLUMNS.get(i)) != 0){
					possMvt.add(1L << i | 1L << i-8*actionCol);
				}*/
				endI = i + 7*actionDR; //mvt en haut droite
				if(((1L << endI) & LOA_MASK_DR.get(i)) != 0 && (1L << endI & mPions)==0 ){
					movement = 1L << i | 1L << endI;
					if((MASK_MOVEMENT.get(movement)&mPionsAdv) == 0)
						possMvt.add(movement);
				}
				/*if(((1L << i + 7*actionDR) & LOA_MASK_DR.get(i)) != 0){
					possMvt.add(1L << i | 1L << i+7*actionDR);
				}*/
				endI = i - 7*actionDR; //mvt en base gauche
				if(((1L << endI) & LOA_MASK_DR.get(i)) != 0 && (1L << endI & mPions)==0 ){
					movement = 1L << i | 1L << endI;
					if((MASK_MOVEMENT.get(movement)&mPionsAdv) == 0)
						possMvt.add(movement);
				}
				/*if(((1L << i - 7*actionDR) & LOA_MASK_DR.get(i)) != 0){
					possMvt.add(1L << i | 1L << i-7*actionDR);
				}*/
				endI = i + 9*actionDL; //mvt en haut gauche
				if(((1L << endI) & LOA_MASK_DL.get(i)) != 0 && (1L << endI & mPions)==0 ){
					movement = 1L << i | 1L << endI;
					if((MASK_MOVEMENT.get(movement)&mPionsAdv) == 0)
						possMvt.add(movement);
				}
				/*if(((1L << i + 9*actionDL) & LOA_MASK_DL.get(i)) != 0){
					possMvt.add(1L << i | 1L << i+9*actionDL);
				}*/
				endI = i - 9*actionDL; //mvt en bas droite
				if(((1L << endI) & LOA_MASK_DL.get(i)) != 0 && (1L << endI & mPions)==0 ){
					movement = 1L << i | 1L << endI;
					if((MASK_MOVEMENT.get(movement)&mPionsAdv) == 0)
						possMvt.add(movement);
				}
				/*if(((1L << i - 9*actionDL) & LOA_MASK_DL.get(i)) != 0){
					possMvt.add(1L << i | 1L << i-9*actionDL);
				}*/
				
			}
			
		}

		return possMvt;

	}

	public long getmPions() {
		return mPions;
	}

	public static void init() {

		
		
		
		//Début génération de LOA MASKS
		
		//72340172838076673L = 0000000100000001000000010000000100000001000000010000000100000001
		for(int i = 0; i< 64; ++i){
			LOA_MASK_LINES.put(i, 255L<<(i/8)*8);
			LOA_MASK_COLUMNS.put(i,72340172838076673L << i%8);
			
		}
		long result = 0;
		for(int i = 0; i<8; ++i){
			
			
				result = result << 8 | 1L << i;
			
			int endX = i;
			int endY = 0;
			while (endX >= 0 && endY < 8) {
				int pos = endX + endY * 8;
				
				
				
				LOA_MASK_DR.put(pos, result);

				--endX;
				++endY;
			
			}
		
		}
		result = 0;
		for(int i = 63; i>=56;--i){
			
			
				result = result >>> 8 | 1L << i;
			int endX = i%8;
			int endY = 7;
			while (endX < 8 && endY >=0) {
				int pos = endX + endY * 8;
				
				
				
				LOA_MASK_DR.put(pos, result);

				++endX;
				--endY;
			
			}
		
		}
		result = 0;
		for(int i = 7; i>=0; --i){
			
				result = result << 8 | 1L << i;
			int endX = i;
			int endY = 0;
			while (endX < 8  && endY < 8) {
				int pos = endX + endY * 8;
				
				
				
				LOA_MASK_DL.put(pos, result);

				++endX;
				++endY;
			
			}
		
		}
		result = 0;
		for(int i = 56; i<63;++i){
			
		
				result = result >>> 8 | 1L << i;
				
			int endX = i%8;
			int endY = 7;
			while (endX >=0 && endY >=0) {
				int pos = endX + endY * 8;
				
				
				
				LOA_MASK_DL.put(pos, result);

				--endX;
				--endY;
			
			}
		
		}
		
		
		//Fin génération LOA_MASKS
		
		
		//Début construction du compteur
		//ajout de lignes et de colonnes
		for(long i = 0L; i <256L; ++i){
			
			for(int j = 0; j<8; ++j){
				int bitCount = Long.bitCount(i);
				COMPTEUR.put(i << j*8, bitCount);
				long keyCol = 0;
				
				for(int idx = 0; idx < 8; ++idx){
					if((i & (1L << idx)) != 0){
						keyCol |= 1L << idx*8;
						
					}
				}
				
				
				COMPTEUR.put(keyCol<<j, bitCount);
			
			}
			
			
		}
		int j = 1;
		for(long i = 2L ; i<257L; i *=2L){  
			
			for(long idx = 0; idx < i ; ++idx){  
				long keyDR = 0;
				long keyDR2 = 0;
				long keyDL = 0;
				long keyDL2 = 0;
				for(int k = 0; k<i; ++k){
					if((idx & (1L << k)) != 0){
						keyDR |= 1L<< k*8 + j-k-1;
						keyDR2 |= 1L << (7-k)*8 + 8-j+k;
						keyDL |= 1L<< k*8 + 8- j+k ;
						keyDL2 |= 1L << (7-k)*8 -k+j-1;
						
						
					}
				}
				
				
				
				COMPTEUR.put(keyDR, Long.bitCount(keyDR));
				COMPTEUR.put(keyDR2, Long.bitCount(keyDR2));
				COMPTEUR.put(keyDL, Long.bitCount(keyDL));
				COMPTEUR.put(keyDL2, Long.bitCount(keyDL2));
			}
			
			++j;
		}
		
		
		//Fin de construction du compteur
		
		
		
		long key = 0;
		long value = 0;
		for (int dep = 0; dep < 64; ++dep) {
			int x = dep % 8;
			int y = dep / 8;
			int end = 0;

			// masques de lignes
			for (int i = x + 1; i < 8; ++i) {
				if (i != x) {
					end = y * 8 + i;
					key = 1L << dep | 1L << end;
					value = (dep < end) ? ((1L << end) - (1L << dep) * 2)
							: ((1L << dep) - (1L << end) * 2);
					MASK_MOVEMENT.put(key, value);
				}
			}

			// masques de colonnes
			for (int i = y + 1; i < 8; ++i) {
				if (i != y) {
					end = x + i * 8;
					key = 1L << dep | 1L << end;
					value = 0;
					if (i < y) {
						for (int idx = i + 1; idx < y; ++idx) {
							value |= 1L << idx * 8 + x;
						}
					} else {
						for (int idx = y + 1; idx < i; ++idx) {
							value |= 1L << idx * 8 + x;
						}
					}
					MASK_MOVEMENT.put(key, value);

				}
			}

			// masques de diagonales droites /

			int endX = x - 1;
			int endY = y + 1;
			while (endX >= 0 && endY < 8) {
				end = endX + endY * 8;
				key = 1L << dep | 1L << end;
				value = 0;
				int interX = x - 1;
				int interY = y + 1;
				while (interX > endX && interY < endY) {
					value |= 1L << (interX + interY * 8);
					--interX;
					++interY;
				}
				MASK_MOVEMENT.put(key, value);

				--endX;
				++endY;
			}

			// masques de diagonales gauche \

			endX = x + 1;
			endY = y + 1;
			while (endX < 8 && endY < 8) {
				end = endX + endY * 8;
				key = 1L << dep | 1L << end;
				value = 0;
				int interX = x + 1;
				int interY = y + 1;
				while (interX < endX && interY < endY) {
					value |= 1L << (interX + interY * 8);
					++interX;
					++interY;
				}
				
				MASK_MOVEMENT.put(key, value);

				++endX;
				++endY;
			}

		}

		MASK = new HashMap<Integer, Long>();
		for (int i = 0; i < 64; ++i) {
			if (i % 8 == 0) { // bord de droite
				MASK.put(i, 1L << i + 1 | 3L << i + 7
						| ((i - 8 >= 0) ? 3L << i - 8 : 0));
			} else if ((i + 1) % 8 == 0) {// bord de gauche
				MASK.put(i, (i + 7 < 64 ? 3L << i + 7 : 0) | 1L << i - 1
						| 3L << i - 9);
			} else {// general
				MASK.put(i, 1L << i + 1 | (i + 7 < 64 ? 7L << i + 7 : 0L)
						| 1L << i - 1 | (i - 9 >= 0 ? 7L << i - 9 : 0));
			}
		}
	}

	public void printGame() {
		int idx = 8;
		System.out.println("---------------------");
		System.out.print(idx+">");
		long totalPions = mPions | mPionsAdv;
		for (int i = 63; i >= 0; --i) {
			if ((totalPions & (1l << i)) == 0) {
				System.out.print("[ ]");
			} else if ((mPions & (1l << i)) == 0) {
				System.out.print("[x]");
			} else {
				System.out.print("[o]");
			}
			if (i % 8 == 0) {
				System.out.println();
				if(idx > 1)
					System.out.print(--idx+">");
			}
		}
		System.out.println("   A  B  C  D  E  F  G  H");
	}

	public void MakeMvtAndUpdate(long move)

	{
		long from = mPions & move;
	
		long to = move ^ from;
		
		mPions ^= move;
		mPionsAdv &= (-1L ^ to);
	}

	public void coupAdvAndUpdate(long move) {
		inverse();
		MakeMvtAndUpdate(move);
		inverse();
		
	}

	private void inverse() {
		long temp = mPions;
		mPions = mPionsAdv;
		mPionsAdv = temp;
	}

	
	
	
	public int getBestM( int lvl, int alpha, int beta){
		
//		 si P est une feuille alors
		if(isConnected() && isConnected(mPionsAdv))
			return -50;
		if(isConnected())
			return Integer.MAX_VALUE;
		if(isConnected(mPionsAdv))
			return Integer.MIN_VALUE;
		if(lvl == 0){
			return 0;
		}
		
//	       retourner la valeur de P
		
		// sinon
		else{
			int best = Integer.MIN_VALUE;
			ArrayList<Long> moves = generatePossibleMvt();
			for(Long mvt: moves){
				Grid advGrid = new Grid(this, mvt, true);
				int val = advGrid.getBestM(lvl-1, -alpha, -beta);
				if(val > best){
					alpha = best;
					if(alpha >= beta){
						return best;
					}
				}
			}
			return best;
			
		}
//	  
//	       Meilleur = –INFINI
//	       pour tout fils Pi de P faire
//	           Val = -ALPHABETA(Pi,-B,-A)
//	           si Val > Meilleur alors
//	               Meilleur = Val
//	               si Meilleur > A alors
//	                      A = Meilleur
//	                   si A ≥ B alors
//	                       retourner Meilleur
//	                   finsi
//	               finsi
//	           finsi 
//	       finpour 
//	       retourner Meilleur
//	   finsi
		
		
		
		//return 1L;
	}
	public void calcule(int lvl) {

	

		

	}

	

	//private static int nbcoupaleatoire = 0;

	public String getBestMove(int lvl) {

		/*this.bestMvt = 0L;
		calcule(lvl);

		if (this.bestMvt == 0L) {
			ArrayList<Long> coups = generatePossibleMvt();
			System.out.println(nbcoupaleatoire++);
			this.bestMvt = coups.get((int) (Math.random() * coups.size()));

		}*/
		ArrayList<Long> moves = generatePossibleMvt();
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		long bestMvt = 0L;
		loop : for(long mvt: moves){
			
			Grid advGrid = new Grid(this, mvt, false);
			int val = advGrid.getBestM(lvl-1, alpha, beta);
			if(val > alpha){
				alpha = val;
				bestMvt = mvt;
				System.out.println(val);
			}
		}
		

	
		long fromLong = mPions & bestMvt;
		long toLong = bestMvt ^ fromLong;
		int from = 63 - Long.numberOfLeadingZeros(fromLong);
		int to = 63 - Long.numberOfLeadingZeros(toLong);
		
		MakeMvtAndUpdate(bestMvt);
		
		char[] res = new char[4];
		res[0] = (char) ('A' + (7 - (from % 8)));
		res[1] = (char) ('1' + (from / 8));
		res[2] = (char) ('A' + (7 - (to % 8)));
		res[3] = (char) ('1' + (to / 8));

		System.out.println("" + res[0] + res[1] + res[2] + res[3]);
	

		return "" + res[0] + res[1] + res[2] + res[3];
	}



	

}
