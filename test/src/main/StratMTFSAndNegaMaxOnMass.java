package main;

import java.util.ArrayList;

public class StratMTFSAndNegaMaxOnMass extends Grid {

	private static ArrayList<Integer> keysPionsPlusAdv = new ArrayList<Integer>();
	private int mNbPions = 0;
	private int mNbPionsAdv = 0;

	public static void init() {
		for (int i = 1; i < 13; ++i)
			;
		// TODO TODO
	}

	public StratMTFSAndNegaMaxOnMass(String str, int type, boolean myColor) {
		super(str, type, myColor);
	}

	public StratMTFSAndNegaMaxOnMass(Grid g, long mvt, boolean inverse) {
		super(g, mvt, inverse);

	}

	private int massHeuristic() {
		int sumX = 0;
		int sumY = 0;
		int cpt = 0;

		for (int i = 0; i < 64; ++i) {
			if ((1L << i & mPions) != 0) {
				++cpt;
				sumX += i % 8;
				sumY += i / 8;
			}

		}
		sumX /= cpt;
		sumY /= cpt;
		int gap = 0;
		for (int i = 0; i < 64; ++i) {
			if ((1L << i & mPions) != 0) {
				gap += Math.pow(sumX - i % 8, 2) + Math.pow(sumY - i / 8, 2);
			}

		}

		return 1000000 - gap;

	}

	private int negaMax(int lvl, int lvlsDone, int alpha, int beta) {
		if (isConnected())
			return Integer.MAX_VALUE - lvlsDone;
		if (advIsConnected())
			return INT_MIN_VALUE + lvlsDone;
		if (lvl == 0) {
			return massHeuristic() - lvlsDone;
		} else {
			ArrayList<Long> moves = generatePossibleMvt();
			for (Long move : moves) {
				StratMTFSAndNegaMaxOnMass advGrid = new StratMTFSAndNegaMaxOnMass(
						this, move, true);
				int val = -advGrid
						.negaMax(lvl - 1, lvlsDone + 1, -beta, -alpha);
				if (val >= beta)
					return val;
				if (val > alpha) {
					alpha = val;

				}
			}
		}

		/*
		 * function negamax(node, depth, α, β, color) if node is a terminal
		 * node or depth = 0 return color * the heuristic value of node else
		 * foreach child of node val := -negamax(child, depth - 1, -β, -α,
		 * -color) if val ≥ β return val if val > α α := val return α
		 */

		return alpha;
	}

	@Override
	protected long getBestMove(int lvl) {
		ArrayList<Long> moves = generatePossibleMvt();
		long bestMvt = moves.get(0);
		int alpha = INT_MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		// int best = INT_MIN_VALUE;

		for (Long move : moves) {
			StratMTFSAndNegaMaxOnMass advGrid = new StratMTFSAndNegaMaxOnMass(
					this, move, true);
			int val = -advGrid.negaMax(lvl, 0, -beta, -alpha);
			// System.out.println(val);
			if (val >= beta)
				return move;
			if (val > alpha) {
				alpha = val;
				bestMvt = move;
			}
		}

		/*
		 * int center = 0; int max = Integer.MAX_VALUE; int min = INT_MIN_VALUE;
		 * 
		 * for(Long move: moves){ if(center == min)
		 * 
		 * 
		 * beta = center+1; else beta = center; StratMTFSAndNegaMaxOnMass
		 * advGrid = new StratMTFSAndNegaMaxOnMass(this, move, true); center = -
		 * advGrid.negaMax(lvl, 0, -beta, -beta+1); if(center <beta){ max =
		 * center; } else{ min = center; bestMvt = move; }
		 */
		/*
		 * function MTDF(root, f, d) g := f upperBound := +∞ lowerBound :=
		 * -∞ while lowerBound < upperBound if g = lowerBound then β := g+1
		 * else β := g g := AlphaBetaWithMemory(root, β-1, β, d) if g < β
		 * then upperBound := g else lowerBound := g return g
		 */
		// }
		return bestMvt;
	}

}
