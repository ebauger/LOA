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
	
	//les masques qui retourne tout les bits ˆ 1 sur une m�me ligne en fonction de la case i, avec 0<=i<64
	private static final Map<Integer, Long> LOA_MASK_LINES = new HashMap<Integer, Long>(64);
	private static final Map<Integer, Long> LOA_MASK_COLUMNS = new HashMap<Integer, Long>(64);
	private static final Map<Integer, Long> LOA_MASK_DR = new HashMap<Integer, Long>(64);
	private static final Map<Integer, Long> LOA_MASK_DL = new HashMap<Integer, Long>(64);
	
	
	//Contien le nombre de bit correspondant ˆ la valuer (long) de la clŽ
	private static final Map<Long, Integer> COMPTEUR = new HashMap<Long, Integer>(6000); //5628 mais c'est pas sžr

	protected Long mPions = 0L;
	protected Long mPionsAdv = 0L;

	

	

	private static final Map<Long, Long> MASK_MOVEMENT = new HashMap<Long, Long>(
			2016);

	/**
	 * env 10 micro secondes
	 * 
	 * @param g
	 */
	protected Grid(Grid g) {

		// long start = System.nanoTime();

		this.mPions =new Long(g.mPions);
		this.mPionsAdv =  new Long(g.mPionsAdv);

		/*ArrayList<ReferencedByte> lignes = new ArrayList<ReferencedByte>(8);
		ArrayList<ReferencedByte> columns = new ArrayList<ReferencedByte>(8);
		ArrayList<ReferencedByte> diagR = new ArrayList<ReferencedByte>(15);
		ArrayList<ReferencedByte> diagL = new ArrayList<ReferencedByte>(15);

		for (int i = 0; i < 8; ++i) {
			lignes.add(new ReferencedByte());
			columns.add(new ReferencedByte());
		}

		for (int i = 0; i < 15; ++i) {
			diagR.add(new ReferencedByte());
			diagL.add(new ReferencedByte());
		}*/

		
	}
	
	

	public Grid(String str, int type, int myColor) {
		//init();
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

	/**
	 * env 400 nano secondes
	 */
	protected boolean isConnected(long pions) {


		// long start = System.nanoTime();

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

		// long end = System.nanoTime();
		// float time = end - start;
		//
		// System.out.println("isConnected(pions) : mvt : time s= " + time
		// / 1000000000 + " mls=" + time / 1000000 + " mcs=" + time / 1000
		// + " ns=" + time);

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
	 * env 5 micro-secondes
	 * 
	 * @param pions
	 * @return the List of possible moves
	 */
	public ArrayList<Long> generatePossibleMvt() {
		ArrayList<Long> possMvt = new ArrayList<Long>();

		// long start = System.nanoTime();

		// parcour chaque bits (boucle de 64)
		// + condition si 1L<<i et pions != 0
		long totalPions = mPions|mPionsAdv;
		for (int i = 63; i >= 0; --i) {
			if ((1L << i & mPions) != 0) { // si on a un de nos pion
				
				int actionLine = COMPTEUR.get(totalPions & LOA_MASK_LINES.get(i));
				int actionCol = COMPTEUR.get(totalPions & LOA_MASK_COLUMNS.get(i));
				int actionDR = COMPTEUR.get(totalPions & LOA_MASK_DR.get(i));
				int actionDL = COMPTEUR.get(totalPions & LOA_MASK_DL.get(i));
				
				long movement = 0;
				
				int endI = i + actionLine;  // ˆ gauche
				if(((1L << endI) & LOA_MASK_LINES.get(i)) != 0 && (1L << endI & mPions)==0 ){
					movement = 1L << i | 1L << endI;
					
					if(movement != precedentMove & (MASK_MOVEMENT.get(movement)&mPionsAdv) == 0)
					{
						possMvt.add(movement);
					}
					
				}
				endI = i - actionLine; //mvt ˆ droite
				if(((1L << endI) & LOA_MASK_LINES.get(i)) != 0 && (1L << endI & mPions)==0 ){
					movement = 1L << i | 1L << endI;
					if(movement != precedentMove & (MASK_MOVEMENT.get(movement)&mPionsAdv) == 0)
						possMvt.add(movement);
				}
				/*if(((1L << i - actionLine) & LOA_MASK_LINES.get(i)) != 0){
					possMvt.add(1L << i | 1L << i-actionLine);
				}*/
				endI = i + 8*actionCol; //mvt en haut
				if(endI < 64 && (1L << endI & mPions)==0 ){
					movement = 1L << i | 1L << endI;
					
					if(movement != precedentMove & (MASK_MOVEMENT.get(movement)&mPionsAdv) == 0)
						possMvt.add(movement);
				}
				/*if(((1L << i + 8*actionCol) & LOA_MASK_COLUMNS.get(i)) != 0){
					possMvt.add(1L << i | 1L << i+ 8*actionCol);
				}*/
				endI = i - 8*actionCol; //mvt en bas
				if(endI>=0 && (1L << endI & mPions)==0 ){
					movement = 1L << i | 1L << endI;
					if(movement != precedentMove & (MASK_MOVEMENT.get(movement)&mPionsAdv) == 0)
						possMvt.add(movement);
				}
				/*if(((1L << i - 8*actionCol) & LOA_MASK_COLUMNS.get(i)) != 0){
					possMvt.add(1L << i | 1L << i-8*actionCol);
				}*/
				endI = i + 7*actionDR; //mvt en haut droite
				if(((1L << endI) & LOA_MASK_DR.get(i)) != 0 && (1L << endI & mPions)==0 ){
					movement = 1L << i | 1L << endI;
					if(movement != precedentMove & (MASK_MOVEMENT.get(movement)&mPionsAdv) == 0)
						possMvt.add(movement);
				}
				/*if(((1L << i + 7*actionDR) & LOA_MASK_DR.get(i)) != 0){
					possMvt.add(1L << i | 1L << i+7*actionDR);
				}*/
				endI = i - 7*actionDR; //mvt en base gauche
				if(((1L << endI) & LOA_MASK_DR.get(i)) != 0 && (1L << endI & mPions)==0 ){
					movement = 1L << i | 1L << endI;
					if(movement != precedentMove & (MASK_MOVEMENT.get(movement)&mPionsAdv) == 0)
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
					if(movement != precedentMove & (MASK_MOVEMENT.get(movement)&mPionsAdv) == 0)
						possMvt.add(movement);
				}
				/*if(((1L << i - 9*actionDL) & LOA_MASK_DL.get(i)) != 0){
					possMvt.add(1L << i | 1L << i-9*actionDL);
				}*/
				
			}
			
		}

//		 long end = System.nanoTime();
//		 float time = end - start;
//		
//		 System.out.println("generate mvt : time s= " + time / 1000000000
//		 + " mls=" + time / 1000000 + " mcs=" + time / 1000 + " ns="
//		 + time);
		
		return possMvt;

	}

	public long getmPions() {
		return mPions;
	}

	public static void init() {

		bestMovesGrid = new Stack<Long>();
		
		alphabetas = new Stack<Integer>();
		
		alphabetas.push(NO_HEURISTIQUE);
		
		
		
		//mStackMvts = new Stack<Stack<Long>>();
		//mStackGame = new Stack<Grid>();
		//ArrayList<ReferencedByte> lignes = new ArrayList<ReferencedByte>(8);
		//ArrayList<ReferencedByte> columns = new ArrayList<ReferencedByte>(8);
		//ArrayList<ReferencedByte> diagR = new ArrayList<ReferencedByte>(15);
		//ArrayList<ReferencedByte> diagL = new ArrayList<ReferencedByte>(15);
		/*for (int i = 0; i < 8; ++i) {
			lignes.add(new ReferencedByte());
			columns.add(new ReferencedByte());
		}

		for (int i = 0; i < 15; ++i) {
			diagR.add(new ReferencedByte());
			diagL.add(new ReferencedByte());
		}

		for (int i = 0; i < 64; ++i) {
			LOA_LIGNES.put(i, lignes.get(i / 8));
			LOA_COLUMNS.put(i, columns.get(i % 8));
			LOA_DIAGONAL_RIGHT.put(i, diagR.get((i / 8) + (i % 8)));
			LOA_DIAGONAL_LEFT.put(i, diagL.get((i / 8) + (7 - (i % 8))));
		}*/
		
		
		
		//DŽbut gŽnŽration de LOA MASKS
		
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
		
		
		//Fin gŽnŽration LOA_MASKS
		
		
		//DŽbut construction du compteur
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
				MASK.put(i, 1L << i + 1 |  ((i + 8 < 64) ? 3L << i + 8 : 0)
						| ((i - 8 >= 0) ? 3L << i - 8 : 0));
			} else if ((i + 1) % 8 == 0) {// bord de gauche
				MASK.put(i, (i + 7 < 64 ? 3L << i + 7 : 0) | 1L << i - 1
						|  ((i - 8 >= 0) ? 3L << i - 9 : 0));
			} else {// general
				MASK.put(i, 1L << i + 1 | (i + 7 < 64 ? 7L << i + 7 : 0L)
						| 1L << i - 1 | (i - 9 >= 0 ? 7L << i - 9 : 0));
			}
		}
	}

	public String printGame() {
		String res = "";
		int idx = 8;
		System.out.println("---------------------");
		res += "o = Mes Pions\nx = Pions Adversaire \n";
		res += idx+">";
		long totalPions = mPions | mPionsAdv;
		for (int i = 63; i >= 0; --i) {
			if ((totalPions & (1l << i)) == 0) {
				res+="[ ]";
			} else if ((mPions & (1l << i)) == 0) {
				res +="[x]";
			} else {
				res += "[o]";
			}
			if (i % 8 == 0) {
				res += "\n";
				if(idx > 1)
					res += --idx+">";
			}
		}
		res +="   A  B  C  D  E  F  G  H\n";
		System.out.println(res);
		return res;
	}

	/**
	 * 
	 * @param move
	 * @return the avdPion eated 0L else
	 */
	public void MakeMvtAndUpdate(long move)

	{
		long from = mPions & move;
	
		long to = move ^ from;
		
		mPions ^= move;
		mPionsAdv &= (-1L ^ to);
	}

	public void coupAdvAndUpdate(long move,boolean checkMove) {
		inverse();
		MakeMvtAndUpdate(move);
		inverse();
		
		if(checkMove & !bestMovesGrid.isEmpty())
		{
			if(move != bestMovesGrid.peek())
			{
				bestMovesGrid.clear();
				
				alphabetas.clear();
				alphabetas.push(NO_HEURISTIQUE);
			}
		}
	}

	protected void inverse() {
		Long temp = mPions;
		mPions = new Long(mPionsAdv);
		mPionsAdv = new Long(temp);
	}

	/*public void updateLOAs(int from, int to, boolean eat) {
		// System.out.println("from="+(63-Long.numberOfLeadingZeros(from)));
		// System.out.println("from="+from+" to="+to);
		--LOA_LIGNES.get(from).value;
		--LOA_COLUMNS.get(from).value;
		--LOA_DIAGONAL_RIGHT.get(from).value;
		--LOA_DIAGONAL_LEFT.get(from).value;

		if (!eat) {
			++LOA_LIGNES.get(to).value;
			++LOA_COLUMNS.get(to).value;
			++LOA_DIAGONAL_RIGHT.get(to).value;
			++LOA_DIAGONAL_LEFT.get(to).value;
		}
	}

	/*private class ReferencedByte {
		private byte value = 0;

		@Override
		public String toString() {
			return value + "";
		}

	}*/

	private static Stack<Integer> alphabetas;
	private static Stack<Long> bestMovesGrid;

	protected long precedentMove = 0L;
	
	public void calcule(int lvl) {

		//int ab;
		
		int beta = INVALIDE_HEURISTIQUE;

		if (!alphabetas.isEmpty()) {
			beta = alphabetas.pop();

			beta = negateHeuristique(beta);

		}
//		if(beta == INVALIDE_HEURISTIQUE)
//			System.out.println("INVALIDE_HEURISTIQUE");

		int alpha = calculHeuristique(MAX_MIN);

		if (alpha == MAX_HEURISTIQUE) {
			if (alpha > beta)
			{
//				System.out.println("bestMove Clear :");
//				System.out.println("alpha == MAX_HEURISTIQUE");
//				System.out.println("&");
//				System.out.println("alpha < beta");
//				ab = alpha;
//				System.out.println("	alpha= " +(ab==MAX_HEURISTIQUE?"MAX_HEURISTIQUE":(ab == MIN_HEURISTIQUE?"MIN_HEURISTIQUE":(ab == NO_HEURISTIQUE?"NO_HEURISTIQUE": ab))));
//				ab = beta;
//				System.out.println("	beta = " + (ab==MAX_HEURISTIQUE?"MAX_HEURISTIQUE":(ab == MIN_HEURISTIQUE?"MIN_HEURISTIQUE":(ab == NO_HEURISTIQUE?"NO_HEURISTIQUE": ab))));
//				System.out.println();
//				
				
				bestMovesGrid.clear();
			}
			
			beta = alpha;

			alphabetas.push(beta);

			return;
		}

		if (lvl <= 0) {
			
			alpha = calculHeuristique(ALL);
			
			if (beta == NO_HEURISTIQUE | alpha < beta) {
//				System.out.println("bestMove Clear :");
//				System.out.println("lvl <= 0");
//				System.out.println("&");
//				System.out.println("(");
//				System.out.println("	beta == NO_HEURISTIQUE");
//				System.out.println("	|");
//				System.out.println("	alpha < beta");
//				System.out.println(")");
//				ab = alpha;
//				System.out.println("	alpha= " +(ab==MAX_HEURISTIQUE?"MAX_HEURISTIQUE":(ab == MIN_HEURISTIQUE?"MIN_HEURISTIQUE":(ab == NO_HEURISTIQUE?"NO_HEURISTIQUE": ab))));
//				ab = beta;
//				System.out.println("	beta = " + (ab==MAX_HEURISTIQUE?"MAX_HEURISTIQUE":(ab == MIN_HEURISTIQUE?"MIN_HEURISTIQUE":(ab == NO_HEURISTIQUE?"NO_HEURISTIQUE": ab))));
//				System.out.println();
				
				
				bestMovesGrid.clear();
				
				
				beta = alpha;
			}
			
			nbfeuilles++;

		} else {

			alpha = beta;

			ArrayList<Long> coups = generatePossibleMvt();

			for (long move : coups) {

				Stack<Long> BestMoveTmp = new Stack<Long>();
				for (Long bMove : bestMovesGrid) {
					BestMoveTmp.push(new Long(bMove));
				}
				
				Grid grid = new Grid(this);
				grid.MakeMvtAndUpdate(move);

				if (alpha != MAX_HEURISTIQUE & alpha != MIN_HEURISTIQUE) {
					alphabetas.push(alpha);
				

					grid.calcule(lvl - 1);

				
					int alphaTmp = negateHeuristique(alphabetas.pop());
	
	//				if(lvl == 4){
	//					ab = alpha;
	//					System.out.println("alpha= " +(ab==MAX_HEURISTIQUE?"MAX_HEURISTIQUE":(ab == MIN_HEURISTIQUE?"MIN_HEURISTIQUE":(ab == NO_HEURISTIQUE?"NO_HEURISTIQUE": ab))));
	//					ab = alphaTmp;
	//					System.out.println("alpha= " +(ab==MAX_HEURISTIQUE?"MAX_HEURISTIQUE":(ab == MIN_HEURISTIQUE?"MIN_HEURISTIQUE":(ab == NO_HEURISTIQUE?"NO_HEURISTIQUE": ab))));
	//				}
					
					if (alpha != alphaTmp & move != precedentMove) {
						alpha = alphaTmp;
						
						
						bestMovesGrid.push(move);
						
						
						if (alpha == MAX_HEURISTIQUE) {
	
							bestMovesGrid.push(move);
	
							beta = alpha;
							
							break;
						} else if (beta != NO_HEURISTIQUE & alpha >= beta) {
							bestMovesGrid = BestMoveTmp;
							break;
						}
						
						beta = alpha;
					}
				}
			}

		}


		alphabetas.push(beta);
		
//		if(lvl <= 1)
//		{
//			ab = alpha;
//			System.out.println(lvl +"--> alpha= " +(ab==MAX_HEURISTIQUE?"MAX_HEURISTIQUE":(ab == MIN_HEURISTIQUE?"MIN_HEURISTIQUE":(ab == NO_HEURISTIQUE?"NO_HEURISTIQUE": ab))));
//			ab = beta;
//			System.out.println("     beta = " + (ab==MAX_HEURISTIQUE?"MAX_HEURISTIQUE":(ab == MIN_HEURISTIQUE?"MIN_HEURISTIQUE":(ab == NO_HEURISTIQUE?"NO_HEURISTIQUE": ab))));
//		}
		
	}
	
	private final static int MAX_HEURISTIQUE = Integer.MAX_VALUE;
	private final static int MIN_HEURISTIQUE = Integer.MIN_VALUE + 2;
	private final static int NO_HEURISTIQUE = Integer.MIN_VALUE + 1;	
	private final static int INVALIDE_HEURISTIQUE = Integer.MIN_VALUE;
	
	private final int ALL = 0;
	private final int MAX_MIN = 1;
	
	private int calculHeuristique(int typeHeuristique) {
		int heuristique = 0;

		if(typeHeuristique == ALL)
		{
			if (this.isConnected(mPions) & this.isConnected(mPionsAdv)) {
				heuristique = 0;
			}
			else
			{
				
				
				
			}
		}
		
		
		
		if(typeHeuristique == MAX_MIN || typeHeuristique == ALL)
		{
			if (this.isConnected(mPions)) { // valable aussi pour match nul!!!
				//printGame();
				heuristique = MAX_HEURISTIQUE;
			} else if (this.isConnected(mPionsAdv)) {
				//printGame();
				heuristique = MIN_HEURISTIQUE;
			}
		}
		return heuristique;
	}

	private int negateHeuristique(int heuristique) {
		if (heuristique == MAX_HEURISTIQUE) {
			return MIN_HEURISTIQUE;
		}

		if (heuristique == MIN_HEURISTIQUE)
			return MAX_HEURISTIQUE;

		if (heuristique == NO_HEURISTIQUE)
			return NO_HEURISTIQUE;

		return -heuristique;
	}

	private long countBits(long bitArray) {
		bitArray = bitArray - ((bitArray >> 1) & 5555555555555555L);
		bitArray = (bitArray & 0x33333333) + ((bitArray >> 2) & 0x33333333);
		return (((bitArray + (bitArray >> 4)) & 0x0F0F0F0F) * 0x01010101) >> 24;
	}
	
	protected static int nbcoupAleatoire = 0;
	protected static int nbfeuilles = 0;
	
	protected int nbFirstCoupAleatoire = 2;

	public String getBestMove(int lvl) {
		
		System.out.println("MAX Profondeur = "+ lvl);
		
		ArrayList<Long> coups;
		Long bestMvt;
		
		if(nbFirstCoupAleatoire >0){
			
			nbFirstCoupAleatoire--;
			
		}else if(!bestMovesGrid.isEmpty()){
			
			Grid g = new Grid(this);

			
			int i = 0; // 0 = coup adversaire - 1 = mon coup
			for (long move : bestMovesGrid) {
				if(i == 0)
				{
					g.coupAdvAndUpdate(move,false);
					
					i = 1;
				}
				else
				{
					g.MakeMvtAndUpdate(move);
					
					i = 0;
				}
			}
			System.out.println("Coups précédents repris");
			g.calcule(lvl);
			
		}
		else{
			
			nbfeuilles = 0;
			
			calcule(lvl);
		}
		
		
	
		
		
		
		if (bestMovesGrid.isEmpty()) {
			coups = generatePossibleMvt();
			bestMvt = coups.get((int) (Math.random() * coups.size()));
			++nbcoupAleatoire;
		} else {
			 bestMvt = bestMovesGrid.pop();
		}
		
		precedentMove = bestMvt;
		
		System.out.println("Feuilles parcourues  = 	" + nbfeuilles);
		System.out.println("Coup Aleatoire	     = 	" + nbcoupAleatoire);


	
		Long fromLong = mPions & bestMvt;
		Long toLong = bestMvt ^ fromLong;
		int from = 63 - Long.numberOfLeadingZeros(fromLong);
		int to = 63 - Long.numberOfLeadingZeros(toLong);
		
		
		char[] res = new char[4];
		res[0] = (char) ('A' + (7 - (from % 8)));
		res[1] = (char) ('1' + (from / 8));
		res[2] = (char) ('A' + (7 - (to % 8)));
		res[3] = (char) ('1' + (to / 8));

		// System.out.println("" + res[0] + res[1] + res[2] + res[3]);
		// System.out.println("-------------");
		// String fromletter = String.valueOf(((char) ('H' - (from%8))) +
		// ((char)'1' + (from/8)));
		// String toletter = String.valueOf(((char) ('H' - (to%8))) + ((char)'1'
		// + (to/8)));
		
   		String coup = "" + res[0] + res[1] + res[2] + res[3];
		
//		System.out.println("coup=" + coup +" from=" + from +" to="+ to + "\n" +
//				" move=     " + Long.toBinaryString(bestMvt | 1L << 63)+ "\n" +
//				" mPions=   " +Long.toBinaryString(mPions | 1L << 63) + "\n" +
//				" fromLong= " + Long.toBinaryString(fromLong | 1L << 63) + "\n" +
//				" toLong=   " + Long.toBinaryString(toLong | 1L << 63) );
//		
		
		
//		coups = generatePossibleMvt();
//		
//		System.out.println("mvtpossible (" + coups.size() +")");
//		
//		for (Long move : coups) {
//						
//			Long fromLongTmp = mPions & move;
//			Long toLongTmp = move ^ fromLongTmp;
//			int fromTmp = 63 - Long.numberOfLeadingZeros(fromLongTmp);
//			int toTmp = 63 - Long.numberOfLeadingZeros(toLongTmp);
//			
//			char[] resTmp = new char[4];
//			resTmp[0] = (char) ('A' + (7 - (fromTmp % 8)));
//			resTmp[1] = (char) ('1' + (fromTmp / 8));
//			resTmp[2] = (char) ('A' + (7 - (toTmp % 8)));
//			resTmp[3] = (char) ('1' + (toTmp / 8));
//			
//			
//			System.out.println("-->	coup= " + resTmp[0] + resTmp[1] + resTmp[2] + resTmp[3]);
//			System.out.println("	from= " + fromTmp);
//			System.out.println("	to  = " + toTmp);
//			System.out.println();
//		}
//		
//		
//		Long totalPions = mPions|mPionsAdv;
//		
//		
//		int actionLine = COMPTEUR.get(totalPions & LOA_MASK_LINES.get(from));
//		int actionCol = COMPTEUR.get(totalPions & LOA_MASK_COLUMNS.get(from));
//		int actionDR = COMPTEUR.get(totalPions & LOA_MASK_DR.get(from));
//		int actionDL = COMPTEUR.get(totalPions & LOA_MASK_DL.get(from));
//		
//		
//		
//		System.out.println(
//						" actionLine= " + actionLine +"\n"+
//						" actionCol=  " + actionCol +"\n"+
//						" actionDR=   " + actionDR +"\n"+
//						" actionDL=   " + actionDL
//				
//				);
//		
		
		MakeMvtAndUpdate(bestMvt);
		
		return coup;
	}

	@Override
	public String toString() {
		return this.printGame();
	}
}
