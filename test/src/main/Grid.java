package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Grid {// implements Runnable{

	private static Map<Integer, Long> MASK;
	public static int TYPE_DECODE_SERVER = 0;
	private Stack<Stack<Long>> mStackMvts;
	private Stack<Grid> mStackGame;
	
	//les masques qui retourne tout les bits � 1 en fonction de la case i, avec 0<=i<64
	private static final Map<Integer, Long> LOA_MASK_LINES = new HashMap<Integer, Long>(64);
	private static final Map<Integer, Long> LOA_MASK_COLUMNS = new HashMap<Integer, Long>(64);
	private static final Map<Integer, Long> LOA_MASK_DR = new HashMap<Integer, Long>(64);
	private static final Map<Integer, Long> LOA_MASK_DL = new HashMap<Integer, Long>(64);
	
	
	//Contien le nombre de bit correspondant � la valuer (long) de la cl�
	private static final Map<Long, Integer> COMPTEUR = new HashMap<Long, Integer>(6000); //5628 mais c'est pas s�r

	private long mPions = 0;
	private long mPionsAdv = 0;

	

	/*private final Map<Integer, ReferencedByte> LOA_LIGNES = new HashMap<Integer, ReferencedByte>(
			64);
	private final Map<Integer, ReferencedByte> LOA_COLUMNS = new HashMap<Integer, ReferencedByte>(
			64);
	private final Map<Integer, ReferencedByte> LOA_DIAGONAL_RIGHT = new HashMap<Integer, ReferencedByte>(
			64);
	private final Map<Integer, ReferencedByte> LOA_DIAGONAL_LEFT = new HashMap<Integer, ReferencedByte>(
			64);*/

	private static final Map<Long, Long> MASK_MOVEMENT = new HashMap<Long, Long>(
			2016);

	/**
	 * env 10 micro secondes
	 * 
	 * @param g
	 */
	private Grid(Grid g) {

		// long start = System.nanoTime();

		this.mPions = g.mPionsAdv;
		this.mPionsAdv = g.mPions;

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
		}

		for (int i = 0; i < 64; ++i) {
			LOA_LIGNES.put(i, lignes.get(i / 8));
			LOA_COLUMNS.put(i, columns.get(i % 8));
			LOA_DIAGONAL_RIGHT.put(i, diagR.get((i / 8) + (i % 8)));
			LOA_DIAGONAL_LEFT.put(i, diagL.get((i / 8) + (7 - (i % 8))));
		}

		for (int i = 0; i < 8; ++i) {
			this.LOA_LIGNES.get(i * 8).value = g.LOA_LIGNES.get(i * 8).value;
			this.LOA_COLUMNS.get(i).value = g.LOA_COLUMNS.get(i).value;
		}

		for (int i = 0; i < 8; ++i) {
			this.LOA_DIAGONAL_RIGHT.get(i * 8).value = g.LOA_DIAGONAL_RIGHT
					.get(i * 8).value;
			this.LOA_DIAGONAL_RIGHT.get(63 - i).value = g.LOA_DIAGONAL_RIGHT
					.get(63 - i).value;

			this.LOA_DIAGONAL_LEFT.get(i).value = g.LOA_DIAGONAL_LEFT.get(i).value;
			this.LOA_DIAGONAL_LEFT.get(i * 8).value = g.LOA_DIAGONAL_LEFT
					.get(i * 8).value;
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
					/*++LOA_LIGNES.get(63 - offset).value;
					++LOA_COLUMNS.get(63 - offset).value;
					++LOA_DIAGONAL_RIGHT.get(63 - offset).value;
					++LOA_DIAGONAL_LEFT.get(63 - offset).value;*/

				} else if (c == '2') {
					pionsBlack |= (long) 1 << 63 - offset;
					/*++LOA_LIGNES.get(63 - offset).value;
					++LOA_COLUMNS.get(63 - offset).value;
					++LOA_DIAGONAL_RIGHT.get(63 - offset).value;
					++LOA_DIAGONAL_LEFT.get(63 - offset).value;*/
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
					/*++LOA_LIGNES.get(63 - offset).value;
					++LOA_COLUMNS.get(63 - offset).value;
					++LOA_DIAGONAL_RIGHT.get(63 - offset).value;
					++LOA_DIAGONAL_LEFT.get(63 - offset).value;*/

				} else if (c == '2') {
					mPionsAdv |= (long) 1 << 63 - offset;
					/*++LOA_LIGNES.get(63 - offset).value;
					++LOA_COLUMNS.get(63 - offset).value;
					++LOA_DIAGONAL_RIGHT.get(63 - offset).value;
					++LOA_DIAGONAL_LEFT.get(63 - offset).value;*/
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
	private boolean isConnected(long pions) {


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
		System.out.println(COMPTEUR.get(1L<<62 | 1L<<55));
		System.out.println(COMPTEUR.get(1L<<38 | 1L<<45));
		System.out.println(COMPTEUR.get(1L<<39 | 1L<<60));
		System.out.println(COMPTEUR.get(1L<<47 | 1L<<61));

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
			if ((1L << i & mPions) != 0) {
				
				int actionLine = COMPTEUR.get(totalPions & LOA_MASK_LINES.get(i));
				int actionCol = COMPTEUR.get(totalPions & LOA_MASK_COLUMNS.get(i));
				int actionDR = COMPTEUR.get(totalPions & LOA_MASK_DR.get(i));
				int actionDL = COMPTEUR.get(totalPions & LOA_MASK_DL.get(i));
				if(((1L << i + actionLine) & LOA_MASK_LINES.get(i)) != 0){
					possMvt.add(1L << i | 1L << i+actionLine);
				}
				if(((1L << i - actionLine) & LOA_MASK_LINES.get(i)) != 0){
					possMvt.add(1L << i | 1L << i-actionLine);
				}
				
				if(((1L << i + 8*actionCol) & LOA_MASK_COLUMNS.get(i)) != 0){
					possMvt.add(1L << i | 1L << i+ 8*actionCol);
				}
				if(((1L << i - 8*actionCol) & LOA_MASK_COLUMNS.get(i)) != 0){
					possMvt.add(1L << i | 1L << i-8*actionCol);
				}
				
				if(((1L << i + 7*actionDR) & LOA_MASK_DR.get(i)) != 0){
					possMvt.add(1L << i | 1L << i+7*actionDR);
				}
				if(((1L << i - 7*actionDR) & LOA_MASK_DR.get(i)) != 0){
					possMvt.add(1L << i | 1L << i-7*actionDR);
				}
				
				if(((1L << i + 9*actionDL) & LOA_MASK_DL.get(i)) != 0){
					possMvt.add(1L << i | 1L << i+9*actionDL);
				}
				if(((1L << i - 9*actionDL) & LOA_MASK_DL.get(i)) != 0){
					possMvt.add(1L << i | 1L << i-9*actionDL);
				}
				
			}
			/*if ((1L << i & mPions) != 0) {

				int currentY = i / 8;
				int currentX = i % 8;
				int position;
				// //lignes
				// //� droite
				position = i - LOA_LIGNES.get(i).value;
				if (position >= currentY * 8) { // on d�passe pas
					if ((mPions & (1L << position)) == 0) {
						// System.out.println(i+" to "+position);
						if ((mPionsAdv & MASK_MOVEMENT.get(1L << i
								| 1L << position)) == 0) {
							possMvt.add(1L << i | 1L << position);
						}
					}
				}

				// � gauche
				position = i + LOA_LIGNES.get(i).value;
				if (position < currentY * 8 + 8) {
					if ((mPions & (1L << position)) == 0) {
						if ((mPionsAdv & MASK_MOVEMENT.get(1L << i
								| 1L << position)) == 0) {
							possMvt.add(1L << i | 1L << position);
						}
					}
				}

				// le long de la colonne
				// en haut
				position = i + LOA_COLUMNS.get(i).value * 8;
				if (position < 64) {
					if ((mPions & (1L << position)) == 0) {
						if ((mPionsAdv & MASK_MOVEMENT.get(1L << i
								| 1L << position)) == 0) {
							possMvt.add(1L << i | 1L << position);
						}
					}
				}
				// en bas
				position = i - LOA_COLUMNS.get(i).value * 8;
				if (position >= 0) {
					if ((mPions & (1L << position)) == 0) {
						if ((mPionsAdv & MASK_MOVEMENT.get(1L << i
								| 1L << position)) == 0) {
							possMvt.add(1L << i | 1L << position);
						}
					}
				}

				// diagonale droite '/'
				// en haut droite
				int endX = currentX - LOA_DIAGONAL_RIGHT.get(i).value;
				int endY = currentY + LOA_DIAGONAL_RIGHT.get(i).value;
				if (endX >= 0 && endY < 8) {
					position = endY * 8 + endX;
					if ((mPions & (1L << position)) == 0) {
						if ((mPionsAdv & MASK_MOVEMENT.get(1L << i
								| 1L << position)) == 0) {
							possMvt.add(1L << i | 1L << position);
						}
					}
				}

				// en bas gauche
				endX = currentX + LOA_DIAGONAL_RIGHT.get(i).value;
				endY = currentY - LOA_DIAGONAL_RIGHT.get(i).value;
				if (endX < 8 && endY >= 0) {
					position = endY * 8 + endX;
					if ((mPions & (1L << position)) == 0) {
						if ((mPionsAdv & MASK_MOVEMENT.get(1L << i
								| 1L << position)) == 0) {
							possMvt.add(1L << i | 1L << position);
						}
					}
				}

				// Diagonale gauche '\'
				// en haut gauche

				endX = currentX + LOA_DIAGONAL_LEFT.get(i).value;
				endY = currentY + LOA_DIAGONAL_LEFT.get(i).value;
				if (endX < 8 && endY < 8) {
					position = endY * 8 + endX;
					if ((mPions & (1L << position)) == 0) {
						if ((mPionsAdv & MASK_MOVEMENT.get(1L << i
								| 1L << position)) == 0) {
							possMvt.add(1L << i | 1L << position);
						}
					}
				}

				// en bas droite

				endX = currentX - LOA_DIAGONAL_LEFT.get(i).value;
				endY = currentY - LOA_DIAGONAL_LEFT.get(i).value;
				if (endX >= 0 && endY >= 0) {
					position = endY * 8 + endX;
					if ((mPions & (1L << position)) == 0) {
						if ((mPionsAdv & MASK_MOVEMENT.get(1L << i
								| 1L << position)) == 0) {
							possMvt.add(1L << i | 1L << position);
						}
					}
				}

				// possMvt.add(maskPion);

			}*/
		}

		// long end = System.nanoTime();
		// float time = end - start;
		//
		// System.out.println("generate mvt : time s= " + time / 1000000000
		// + " mls=" + time / 1000000 + " mcs=" + time / 1000 + " ns="
		// + time);
		
		return possMvt;

	}

	public long getmPions() {
		return mPions;
	}

	public static void init() {

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
		
		
		//D�but g�n�ration de LOA MASKS
		
		//72340172838076673L = 0000000100000001000000010000000100000001000000010000000100000001
		for(int i = 0; i< 64; ++i){
			LOA_MASK_LINES.put(i, 255L<<i*8);
			//printBits(255L<<i*8);
			LOA_MASK_COLUMNS.put(i,72340172838076673L << i%8);
			
		}
		long result = 0;
		for(int i = 0; i<8; ++i){
			
			//for(int idx = 0; idx < 8; ++idx){
				result = result << 8 | 1L << i;
				//System.out.println(result);
				//printBits(result);
			//}
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
			
			//for(int idx = 0; idx < 8; ++idx){
				result = result >>> 8 | 1L << i;
				//System.out.println(result);
				//printBits(result);
			//}
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
			
			//for(int idx = 0; idx < 8; ++idx){
				result = result << 8 | 1L << i;
				//System.out.println(result);
				//printBits(result);
			//}
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
			
			//for(int idx = 0; idx < 8; ++idx){
				result = result >>> 8 | 1L << i;
				//System.out.println(result);
				//printBits(result);
			//}
			int endX = i%8;
			int endY = 7;
			while (endX >=0 && endY >=0) {
				int pos = endX + endY * 8;
				
				
				
				LOA_MASK_DL.put(pos, result);

				--endX;
				--endY;
			
			}
		
		}
		
		
		//Fin g�n�ration LOA_MASKS
		
		
		//D�but construction du compteur
		//ajout de lignes et de colonnes
		for(long i = 0L; i <256L; ++i){
			
			for(int j = 0; j<8; ++j){
				int bitCount = Long.bitCount(i);
				COMPTEUR.put(i << j*8, bitCount);
				long keyCol = 0;
				//long keyDR = 0;
				//long keyDR2 = 0;
				//long keyDL = 0;
				for(int idx = 0; idx < 8; ++idx){
					if((i & (1L << idx)) != 0){
						keyCol |= 1L << idx*8;
						//keyDR |= 1L >>> idx;
						//keyDR2 |= 1L << idx;
					}
				}
				
				
				COMPTEUR.put(keyCol<<j, bitCount);
				/*keyDR = keyDR<<j & LOA_MASK_DR.get(j);
				COMPTEUR.put(keyDR, Long.bitCount(keyDR));
				keyDR2 = keyDR2<<j & LOA_MASK_DR.get(j);
				COMPTEUR.put(keyDR2, Long.bitCount(keyDR2));
				//COMPTEUR.put(keyDL, bitCount);
				printBits(keyDR);*/
			}
			
			
		}
		int j = 1;
		for(long i = 2L ; i<257L; i *=2L){  //2, 4, 8, 16 etc  ex 32
			//System.out.println(i+" "+j);
			for(long idx = 0; idx < i ; ++idx){  // de 0 � 32-1
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
						//System.out.println((7-k)*8 + 8-j+k);
						
					}
				}
				
				
				//printBits(keyDR2);
				COMPTEUR.put(keyDR, Long.bitCount(keyDR));
				COMPTEUR.put(keyDR2, Long.bitCount(keyDR2));
				COMPTEUR.put(keyDL, Long.bitCount(keyDL));
				COMPTEUR.put(keyDL2, Long.bitCount(keyDL2));
			}
			//keyDR = keyDR & LOA_MASK_DR.get(j);
			
			//keyDR2 = keyDR2<<j & LOA_MASK_DR.get(j);
			//COMPTEUR.put(keyDR2, Long.bitCount(keyDR2));
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
		System.out.println("---------------------");
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
			}
		}
	}

	/**
	 * 
	 * @param move
	 * @return the avdPion eated 0L else
	 */
	public void MakeMvtAndUpdate(long move)

	{
		long from = mPions & move;
		// printBits(mPions);
		long to = move ^ from;
		//updateLOAs(63 - Long.numberOfLeadingZeros(from),
				//63 - Long.numberOfLeadingZeros(to), (mPionsAdv & to) != 0);
		mPions ^= move;
		mPionsAdv &= (-1L ^ to);
	}

	public void coupAdvAndUpdate(long move) {
		inverse();
		MakeMvtAndUpdate(move);
		inverse();
		// run();
	}

	private void inverse() {
		long temp = mPions;
		mPions = mPionsAdv;
		mPionsAdv = temp;
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
	private static Stack<Long> bestMoves;

	public void calcule(int lvl) {

	}

	private final static int MAX_HEURISTIQUE = Integer.MAX_VALUE;
	private final static int MIN_HEURISTIQUE = Integer.MIN_VALUE + 1;
	private final static int NO_HEURISTIQUE = Integer.MIN_VALUE;

	private int calculHeuristique() {
		int heuristique = 0;

		if (this.isConnected(mPions) & this.isConnected(mPionsAdv)) {
			heuristique += 50;
		} else if (this.isConnected(mPions)) { // valable aussi pour match nul
												// !!!
			heuristique += 100;
		} else if (this.isConnected(mPionsAdv)) {
			heuristique -= 100;
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

	private static int nbcoupNONaleatoire = 0;
	private static int nbfeuilles;

	public String getBestMove(int lvl) {
		nbfeuilles = 0;
		alphabetas = new Stack<Integer>();
		// nbNoeuds = 0L;

		calcule(lvl);

		System.out.println("nbfeuilles=" + nbfeuilles);

		Long bestMvt = bestMoves.pop();

		if (bestMoves.isEmpty()) {
			ArrayList<Long> coups = generatePossibleMvt();
			bestMvt = coups.get((int) (Math.random() * coups.size()));
		} else {
			System.out.println("nbcoupNONaleatoire=" + ++nbcoupNONaleatoire);
		}
		// System.out.println("-------------");
		// printBits(bestMvt);
		// printBits(mPions);
		// printBits(mPionsAdv);
		// pringLOAs();
		long fromLong = mPions & bestMvt;
		long toLong = bestMvt ^ fromLong;
		int from = 63 - Long.numberOfLeadingZeros(fromLong);
		int to = 63 - Long.numberOfLeadingZeros(toLong);
		
		MakeMvtAndUpdate(bestMvt);
		// System.out.println("from=" + from + " to=" + to);

		// updateLOAs(63-Long.numberOfLeadingZeros(from),
		// 63-Long.numberOfLeadingZeros(to));
		// mPions ^= bestMvt;
		// mPionsAdv &= (-1L^to);
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

		return "" + res[0] + res[1] + res[2] + res[3];
	}

	// @Override
	// public void run() {
	// bestMvt = calcule(1);
	// //MakeMvtAndUpdate(bestMvt);
	//
	// }

}
