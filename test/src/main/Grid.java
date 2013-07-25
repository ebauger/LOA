package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


public abstract class Grid {// implements Runnable{

	private static Map<Integer, Long> MASK;
	public static int TYPE_DECODE_SERVER = 0;
	//private Stack<Stack<Long>> mStackMvts;
	//private Stack<Grid> mStackGame;
	
	//les masques qui retourne tout les bits ˆ 1 sur une m�me ligne en fonction de la case i, avec 0<=i<64
	private static final Map<Integer, Long> LOA_MASK_LINES = new HashMap<Integer, Long>(64);
	private static final Map<Integer, Long> LOA_MASK_COLUMNS = new HashMap<Integer, Long>(64);
	private static final Map<Integer, Long> LOA_MASK_DR = new HashMap<Integer, Long>(64);
	private static final Map<Integer, Long> LOA_MASK_DL = new HashMap<Integer, Long>(64);
	
	protected final static HashMap<String, ArrayList<Long>> moves =new HashMap<String, ArrayList<Long>>(10000);
	
	//Contien le nombre de bit correspondant ˆ la valuer (long) de la clŽ
	private static final Map<Long, Integer> COMPTEUR = new HashMap<Long, Integer>(6000); //5628 mais c'est pas sžr

	protected long mPions = 0L;
	protected long mPionsAdv = 0L;

	protected int nbMPions;
	protected int nbPionsAdv;
	
	
	protected static int lvlparcouru;
	
	
	protected static int nbMovesRepris = 0;

	public final static int WHITE = 1;
	public final static int BLACK = 2;
	
	public char myColor;
	public char colorAdv;

	protected boolean whitePions;
	
	
	protected static int nbcoupAleatoire = 0;
	protected static int nbfeuilles = 0;

	protected static int MaxLvl = 0;
	
	protected static boolean whiteOriginColor;
		
	private static final Map<Long, Long> MASK_MOVEMENT = new HashMap<Long, Long>(
			2016);

//	private static final 

	
	/**
	 * env 10 micro secondes
	 * 
	 * @param g
	 */
	protected Grid(Grid g) {

		// long start = System.nanoTime();

		this.mPions =g.mPions;
		this.mPionsAdv =  g.mPionsAdv;
		myColor = g.myColor;
		colorAdv = g.colorAdv;
		
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
		int nbWhitePions =0;
		int nbBlackPions = 0;
		
		Long pionsWhite = 0L;
		Long pionsBlack = 0L;
		
		if (type == TYPE_DECODE_SERVER) {

			for (char c : str.toCharArray()) {
				if (c == '4') {
					pionsWhite |= (long) 1 << 63 - offset;
					++nbWhitePions;

				} else if (c == '2') {
					pionsBlack |= (long) 1 << 63 - offset;
					++nbBlackPions;
				}

				++offset;
			}


		} else {

			for (char c : str.toCharArray()) {
				if (c == '1') {
					pionsWhite |= (long) 1 << 63 - offset;
					++nbWhitePions;

				} else if (c == '2') {
					pionsBlack |= (long) 1 << 63 - offset;
					++nbBlackPions;
				}

				++offset;
			}
			


		}
		
		if (myColor == Grid.WHITE) {
			mPions = pionsWhite;
			mPionsAdv = pionsBlack;
			this.myColor = 'W';
			this.colorAdv = 'B';
			this.nbMPions = nbWhitePions;
			this.nbPionsAdv = nbBlackPions;
			this.whitePions = true;
			whiteOriginColor = true;
		} else if (myColor == Grid.BLACK){
			mPions = pionsBlack;
			mPionsAdv = pionsWhite;
			this.myColor = 'B';
			this.colorAdv = 'W';
			this.nbMPions = nbBlackPions;
			this.nbPionsAdv = nbWhitePions;
			this.whitePions = false;
			whiteOriginColor = false;
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


	/**
	 * env 5 micro-secondes
	 * 
	 * @param pions
	 * @return the List of possible moves
	 */
	public ArrayList<Long> generatePossibleMvt() {
		ArrayList<Long> possMvt = new ArrayList<Long>();
		
		
		
		// long start = System.nanoTime();
		
//		String key = Long.toBinaryString(mPions) + Long.toBinaryString(mPionsAdv);
//		
//		ArrayList<Long> lstMove = moves.get(key);
//		
//		if(lstMove != null)
//		{
//			
//			possMvt = lstMove;
//			++nbMovesRepris;
//
////			 System.out.println("\nmouvement repris-------------------");
//			
//		}
//		else
//		{

		

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
					
					if((MASK_MOVEMENT.get(movement)&mPionsAdv) == 0)
					{
						possMvt.add(movement);
					}
					
				}
				endI = i - actionLine; //mvt ˆ droite
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

//		moves.put(key, possMvt);
//		
//		
//		}
		
//		 long end = System.nanoTime();
//		 float time = end - start;
//		
//		 System.out.println("generate mvt : time " + time / 1000000 + "micro");
		
		
		return possMvt;

	}

	
	public long getmPions() {
		return mPions;
	}

	
	public static void init() {
	
		
		
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
		res += "\n";
		res += 	myColor + " = Mes Pions		" + Long.toBinaryString(mPions|(1L<<63))+
				"\n"+
				colorAdv + " = Pions Adversaire 	" + Long.toBinaryString(mPionsAdv|(1L<<63)) +"\n";
		res+="\nStrategie : " + this.getClass().getName();
		res += "\n"+ idx+">";
		for (int i = 63; i >= 0; --i) {
			
			if ((mPions & (1l << i)) != 0) {
				res +="["+ myColor +"]";
			} else if( (mPionsAdv & (1l << i)) != 0)
				res +="["+ colorAdv +"]";
			else{
				res+="[ ]";
			} 

			if (i % 8 == 0) {
				res += "\n";
				if(idx > 1)
					res += --idx+">";
			}
		}
		res +="   A  B  C  D  E  F  G  H\n";
//		res+= "heuristique=" + this.heuristique;
		System.out.println(res);
		return res;
	}

//	protected int heuristique;
	
	
	/**
	 * 
	 * @param move
	 * @return the avdPion eated 0L else
	 */
	public void MakeMvtAndUpdate(long move,boolean clean)

	{
		long from = mPions & move;
	
		long to = move ^ from;
		
		mPions ^= move;
		
		mPionsAdv &=(-1L ^ to);
		
//		long mPionsAdvTmp = mPionsAdv & (-1L ^ to);
//		
//		if(mPionsAdvTmp != mPionsAdv)
//			--nbPions;
//		
//		mPionsAdv = mPionsAdvTmp;
	}

	public void coupAdvAndUpdate(long move,boolean clean) {
		inverse();
		MakeMvtAndUpdate(move,clean);
		inverse();
		
	}

	protected void inverse() {
		Long temp = mPions;
		mPions = new Long(mPionsAdv);
		mPionsAdv = new Long(temp);
		
		char colorTmp = myColor;
		myColor = colorAdv;
		colorAdv = colorTmp;
	}
		//int ab;
	abstract protected long getBestMove();

	public String getBestMoveAsString(int maxLvl) {

		MaxLvl = maxLvl;
		
		long bestMvt = getBestMove();

	
		long fromLong = mPions & bestMvt;
		long toLong = bestMvt ^ fromLong;
		int from = 63 - Long.numberOfLeadingZeros(fromLong);
		int to = 63 - Long.numberOfLeadingZeros(toLong);
		
		MakeMvtAndUpdate(bestMvt,true);
		
//		printGame();
		
		char[] res = new char[4];
		res[0] = (char) ('A' + (7 - (from % 8)));
		res[1] = (char) ('1' + (from / 8));
		res[2] = (char) ('A' + (7 - (to % 8)));
		res[3] = (char) ('1' + (to / 8));

//		System.out.println("" + res[0] + res[1] + res[2] + res[3]);
	

		return "" + res[0] + res[1] + res[2] + res[3];
	}
}
