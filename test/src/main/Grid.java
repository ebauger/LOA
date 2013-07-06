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

	private long mPions = 0;
	private long mPionsAdv = 0;

	/*
	 * private static final int LINES = 0; private static final int COLUMNS = 1;
	 * private static final int DIAGONAL_RIGHT = 2; // '/' private static final
	 * int DIAGONAL_LEFT = 3; // '\'
	 */
	// byte[][] mLinesOfActon;

	private final Map<Integer, ReferencedByte> LOA_LIGNES = new HashMap<Integer, ReferencedByte>(
			64);
	private final Map<Integer, ReferencedByte> LOA_COLUMNS = new HashMap<Integer, ReferencedByte>(
			64);
	private final Map<Integer, ReferencedByte> LOA_DIAGONAL_RIGHT = new HashMap<Integer, ReferencedByte>(
			64);
	private final Map<Integer, ReferencedByte> LOA_DIAGONAL_LEFT = new HashMap<Integer, ReferencedByte>(
			64);

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

		ArrayList<ReferencedByte> lignes = new ArrayList<ReferencedByte>(8);
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
		}

		// long end = System.nanoTime();
		// float time = end - start;
		//
		// System.out.println("new Grid(Grid g) : mvt : time s= " + time
		// / 1000000000 + " mls=" + time / 1000000 + " mcs=" + time / 1000
		// + " ns=" + time);

	}

	public Grid(String str, int type, int myColor) {
		init();
		int offset = 0;

		if (type == TYPE_DECODE_SERVER) {

			long pionsWhite = 0;
			long pionsBlack = 0;

			for (char c : str.toCharArray()) {
				if (c == '4') {
					pionsWhite |= (long) 1 << 63 - offset;
					++LOA_LIGNES.get(63 - offset).value;
					++LOA_COLUMNS.get(63 - offset).value;
					++LOA_DIAGONAL_RIGHT.get(63 - offset).value;
					++LOA_DIAGONAL_LEFT.get(63 - offset).value;

				} else if (c == '2') {
					pionsBlack |= (long) 1 << 63 - offset;
					++LOA_LIGNES.get(63 - offset).value;
					++LOA_COLUMNS.get(63 - offset).value;
					++LOA_DIAGONAL_RIGHT.get(63 - offset).value;
					++LOA_DIAGONAL_LEFT.get(63 - offset).value;
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
					++LOA_LIGNES.get(63 - offset).value;
					++LOA_COLUMNS.get(63 - offset).value;
					++LOA_DIAGONAL_RIGHT.get(63 - offset).value;
					++LOA_DIAGONAL_LEFT.get(63 - offset).value;

				} else if (c == '2') {
					mPionsAdv |= (long) 1 << 63 - offset;
					++LOA_LIGNES.get(63 - offset).value;
					++LOA_COLUMNS.get(63 - offset).value;
					++LOA_DIAGONAL_RIGHT.get(63 - offset).value;
					++LOA_DIAGONAL_LEFT.get(63 - offset).value;
				}

				++offset;
			}
		}
		// System.out.println("dŽbut partie, nos pions:");
		// printBits(mPions);
		// System.out.println();

	}

	public void printBits(Long arrayBits) {
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

		// printBits(pions);

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

	public void printBits() {

		// System.out.println(Long.toBinaryString(mPions));

		// printBits(1L<<24|1L<<31)
		// printBits(1L<<7|1L<<35);
		// printBits(MASK_MOVEMENT.get(1L<<7|1L<<35));
		// System.out.println();
		// printBits(1L<<21|1L<<35);
		// printBits(MASK_MOVEMENT.get(1L<<21|1L<<35));
		// System.out.println();
		// printBits(1L<<14|1L<<35);
		// printBits(MASK_MOVEMENT.get(1L<<14|1L<<35));
		// System.out.println();
		// printBits(1L<<7|1L<<21);
		// printBits(MASK_MOVEMENT.get(1L<<7|1L<<21));
		// System.out.println();
		// printBits(1L<<7|1L<<56);
		// printBits(MASK_MOVEMENT.get(1L<<7|1L<<56));
		// System.out.println();
		// printBits(1L<<28|1L<<56);
		// printBits(MASK_MOVEMENT.get(1L<<28|1L<<56));
		// System.out.println();
		//
		// System.out.println(Long.toBinaryString(1L<<5|1L<<1| 1L<<63));
		// System.out.println(Long.toBinaryString(MASK_MOVEMENT.get(1L<<5|1L<<1)|
		// 1L<<63));
		// System.out.println();
		//
		// System.out.println(Long.toBinaryString(1L<<27|1L<<43| 1L<<63));
		// System.out.println(Long.toBinaryString(MASK_MOVEMENT.get(1L<<27|1L<<43)|
		// 1L<<63));
		// System.out.println();
		// System.out.println(Long.toBinaryString(1L<<26|1L<<2| 1L<<63));
		// System.out.println(Long.toBinaryString(MASK_MOVEMENT.get(1L<<26|1L<<2)|
		// 1L<<63));*/
		//
		/*
		 * System.out.println();
		 * 
		 * System.out.println(Long.toBinaryString(1L<<28|1L<<56| 1L<<63));
		 * System
		 * .out.println(Long.toBinaryString(MASK_MOVEMENT.get(1L<<28|1L<<56)|
		 * 1L<<63));
		 * 
		 * System.out.println();
		 * 
		 * System.out.println(Long.toBinaryString(1L<<27|1L<<41| 1L<<63));
		 * System
		 * .out.println(Long.toBinaryString(MASK_MOVEMENT.get(1L<<27|1L<<41)|
		 * 1L<<63));
		 * 
		 * System.out.println();
		 * 
		 * System.out.println(Long.toBinaryString(1L<<5|1L<<23| 1L<<63));
		 * System.
		 * out.println(Long.toBinaryString(MASK_MOVEMENT.get(1L<<5|1L<<23)|
		 * 1L<<63));
		 * 
		 * System.out.println();
		 * 
		 * System.out.println(Long.toBinaryString(1L<<16|1L<<61| 1L<<63));
		 * System
		 * .out.println(Long.toBinaryString(MASK_MOVEMENT.get(1L<<16|1L<<61)|
		 * 1L<<63));
		 * 
		 * System.out.println(MASK_MOVEMENT.size());
		 */

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

		for (int i = 63; i >= 0; --i) {

			// long postionPion = 1L << i;

			// long move;
			if ((1L << i & mPions) != 0) {

				int currentY = i / 8;
				int currentX = i % 8;
				int position;
				// //lignes
				// //à droite
				position = i - LOA_LIGNES.get(i).value;
				if (position >= currentY * 8) { // on dépasse pas
					if ((mPions & (1L << position)) == 0) {
						// System.out.println(i+" to "+position);
						if ((mPionsAdv & MASK_MOVEMENT.get(1L << i
								| 1L << position)) == 0) {
							possMvt.add(1L << i | 1L << position);
						}
					}
				}

				// à gauche
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

			}
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

	public void init() {

		bestMoves = new Stack<Long>();

		mStackMvts = new Stack<Stack<Long>>();
		mStackGame = new Stack<Grid>();
		ArrayList<ReferencedByte> lignes = new ArrayList<ReferencedByte>(8);
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
					// if(MASK_MOVEMENT.containsKey(key)){
					// System.out.println("lines inserts existing key");
					// printBits(key);
					// printBits(value);
					// }
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
					// if(MASK_MOVEMENT.containsKey(key)){
					// System.out.println("columns inserts existing key");
					// printBits(key);
					// printBits(value);
					// }
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
				// if(MASK_MOVEMENT.containsKey(key)){
				// System.out.println("DR inserts existing key -> x="+x+" y=" +
				// y + " endx=" +endX + " endy="+endY);
				// printBits(key);
				// printBits(value);
				// }
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
				// if(MASK_MOVEMENT.containsKey(key)){
				// System.out.println("DL inserts existing key -> x="+x+" y=" +
				// y + " endx=" +endX + " endy="+endY);
				// printBits(key);
				// printBits(value);
				// }
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
	public long MakeMvtAndUpdate(long move)

	{
		long from = mPions & move;
		// printBits(mPions);
		long to = move ^ from;

		long eatingAdvPion = mPionsAdv & to;

		updateLOAs(63 - Long.numberOfLeadingZeros(from),
				63 - Long.numberOfLeadingZeros(to), (eatingAdvPion) != 0);
		mPions ^= move;
		mPionsAdv &= (-1L ^ to);

		return eatingAdvPion;

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

	public void updateLOAs(int from, int to, boolean eat) {
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

	private void replaceAdvPion(long advPion) {
		if (advPion != 0) {

			this.mPionsAdv |= advPion;

			int toUpdate = 63 - Long.numberOfLeadingZeros(advPion);
			++LOA_LIGNES.get(toUpdate).value;
			++LOA_COLUMNS.get(toUpdate).value;
			++LOA_DIAGONAL_RIGHT.get(toUpdate).value;
			++LOA_DIAGONAL_LEFT.get(toUpdate).value;

		}

	}

	private class ReferencedByte {
		private byte value = 0;

		@Override
		public String toString() {
			return value + "";
		}

	}

	// nbNoeuds++;

	// System.out.println("lvl=" + lvl);

	// int taille = coups.size();
	// int random = (int) (Math.random()*taille);
	// System.out.println("bestMvt random:");
	// printBits(coups.get(random));
	//
	// if (alphabeta == 100) {
	// return;
	// }

	// if (this.isConnected(mPions)) { // valable aussi pour match nul !!!
	// this.alphabeta = 100;
	// return;
	// } else if (this.isConnected(mPionsAdv)) {
	// this.alphabeta = -100;
	// return;
	// } else {
	// this.alphabeta = 0;
	// }
	//
	// ArrayList<Long> coups = generatePossibleMvt();
	//
	// if (lvl == 0) {
	// return;
	// }
	//
	// for (long move : coups) {
	//
	// Grid gridAdv = new Grid(this);
	//
	// gridAdv.coupAdvAndUpdate(move);
	//
	// this.alphabeta = ((-gridAdv.alphabeta) < this.alphabeta) ?
	// gridAdv.alphabeta
	// : this.alphabeta;
	// // System.out.println("alphabeta=" + this.alphabeta);
	//
	// if (this.alphabeta == 100) {
	// this.bestMvt = move;
	//
	// return;
	// }
	//
	// if (this.alphabeta == -100) {
	// return;
	// }
	//
	// if (this.alphabeta == 0) {
	// gridAdv.calcule(lvl - 1);
	//
	// }
	//
	// }
	private static Stack<Integer> alphabetas;

	private static Stack<Long> bestMoves;

	// private static long nbNoeuds;

	public void calcule(int lvl) {

		int beta = NO_HEURISTIQUE;

		if (!alphabetas.isEmpty()) {
			beta = alphabetas.pop();

			if (beta != NO_HEURISTIQUE)
				beta = -beta;

		}

		int alpha = calculHeuristique();

		if (alpha == MAX_HEURISTIQUE | alpha == MIN_HEURISTIQUE) {
			if (alpha < beta)
				bestMoves.clear();

			beta = alpha;

			alphabetas.push(beta);

			return;
		}

		if (lvl <= 0) {
			if (beta == NO_HEURISTIQUE | alpha < beta) {

				bestMoves.clear();
				nbfeuilles++;

			}

		} else {

			alpha = NO_HEURISTIQUE;

			ArrayList<Long> coups = generatePossibleMvt();

			for (long move : coups) {

				// application du mouvement
				long pionAdvEating = this.MakeMvtAndUpdate(move);

				if (alpha != MAX_HEURISTIQUE & alpha != MIN_HEURISTIQUE) {
					alphabetas.push(alpha);
				}

				this.inverse();

				calcule(lvl - 1);

				this.inverse();

				this.MakeMvtAndUpdate(move);

				this.replaceAdvPion(pionAdvEating);

				int alphaTmp = -alphabetas.pop();

				if (alpha != alphaTmp) {
					alpha = alphaTmp;
					bestMoves.push(move);

					if (beta != NO_HEURISTIQUE & alpha > beta)
						break;
				}

				if (alpha == MAX_HEURISTIQUE | alpha == MIN_HEURISTIQUE) {

					bestMoves.push(move);

					break;
				}

			}

		}

		beta = alpha;

		alphabetas.push(beta);
	}

	private final static int MAX_HEURISTIQUE = Integer.MAX_VALUE;
	private final static int MIN_HEURISTIQUE = Integer.MIN_VALUE + 1;
	private final static int NO_HEURISTIQUE = Integer.MIN_VALUE;

	private int calculHeuristique() {
		int heuristique = 0;

		if (this.isConnected(mPions)) { // valable aussi pour match nul !!!
			heuristique += 100;
		} else if (this.isConnected(mPionsAdv)) {
			heuristique -= 100;
		}

		return heuristique;
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
		/*
		 * long from = mPions & move;
		 * 
		 * long to = move^from;
		 */
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

	public void pringLOAs() {
		System.out.println();
		System.out.println(LOA_COLUMNS);
		System.out.println(LOA_LIGNES);
		System.out.println(LOA_DIAGONAL_RIGHT);
		System.out.println(LOA_DIAGONAL_LEFT);
	}

}
