package ai;

import gomoku.Move;
import gomoku.Piece;
import gomoku.Square;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Sequence {
	
	private static int[] PRIMES_X = new int[] {7927,8171,8389,8641,7933,8179,8419,
		8647,7937,8191,8423,8663,7949,8209,8429,8669,7951,8219,8431,8677};
	
	private static int[] PRIMES_Y = new int[] {104743,105023,105359,105613,104759,
		105031,105361,105619,104761,105037,105367,105649,104773,105071,105373,
		105653,104779,105097,105379,105667};
	
	LinkedList<Piece> pieces;
	int direction;
	
	private int hashCode;
	
	public Sequence(LinkedList<Piece> pieces, int direction) {
		this.direction = direction;
		this.hashCode = -1;
		this.pieces = pieces;
	}
	
	public Set<Move> adjacentMoves(int player) {
		
//		System.out.println("Getting adjecent moves for " + this + ":");
		Set<Move> moves = new HashSet<Move>();
		Square last = pieces.getLast().getSquare(), nextLast;
		Square first = pieces.getFirst().getSquare(), nextFirst;
		nextLast = last.getNeighbor(direction);
		nextFirst = first.getNeighbor(Direction.opposite(direction));
		
		if (nextLast != null && nextLast.getPiece() == null) {
			moves.add(new Move(player,nextLast.getX(),nextLast.getY()));
			nextLast = last.getNeighbor(direction);
			if (nextLast != null && nextLast.getPiece() == null) {
				moves.add(new Move(player,nextLast.getX(),nextLast.getY()));
			}
//			System.out.println("\tmove: " + nextLast.getX() + "," + nextLast.getY());
		}
		if (nextFirst != null && nextFirst.getPiece() == null) {
			moves.add(new Move(player,nextFirst.getX(),nextFirst.getY()));
			nextFirst = first.getNeighbor(Direction.opposite(direction));
			if (nextFirst != null && nextFirst.getPiece() == null) {
				moves.add(new Move(player,nextFirst.getX(),nextFirst.getY()));
			}
//			System.out.println("\tmove: " + nextFirst.getX() + "," + nextFirst.getY());
		}
		
		Square currentSquare = first;
		while (currentSquare != last) {
			if (currentSquare.getPiece() == null) {
				moves.add(new Move(player,
						currentSquare.getX(),currentSquare.getY()));
//				System.out.println("\tmove: " + currentSquare.getX() + "," + currentSquare.getY());
			}
			currentSquare = currentSquare.getNeighbor(direction);
		}
		return moves;
	}
	
	public boolean isConsecutive() {
		boolean consec = true;
		Square last = pieces.getLast().getSquare();
		Square first = pieces.getFirst().getSquare();
		Square currentSquare = first;
		while (currentSquare != last) {
			if (currentSquare.getPiece() == null) {
				consec = false;
				break;
			}
			currentSquare = currentSquare.getNeighbor(direction);
		}
		return consec;
	}
	
	public int blocked() {
		Square last = pieces.getLast().getSquare();
		Square first = pieces.getFirst().getSquare();
		last = last.getNeighbor(direction);
		first = first.getNeighbor(Direction.opposite(direction));
		int blocked = 0;
		if (last == null) {
			blocked++;
		} else if (last.getPiece() != null) {
			blocked++;
		}
		if (first == null) {
			blocked++;
		} else if (first.getPiece() != null) {
			blocked++;
		}
		return blocked;
	}
	
	public int getDirection() {
		return direction;
	}
	
	@Override
	public boolean equals(Object s) {
		return s.hashCode() == hashCode();
	}
	
	/**
	 * Override to correctly map any two sequences using the same pieces
	 * to the same hashcode
	 */
	@Override
	public int hashCode() {
		if (hashCode != -1) {
			return hashCode;
		} else {
			hashCode = 0;
			for (Piece p : pieces) {
				int x = p.getSquare().getX();
				int y = p.getSquare().getY();
				hashCode += x * PRIMES_X[x % PRIMES_X.length] +
					y * PRIMES_Y[y % PRIMES_Y.length];
			}
			return hashCode;
		}
	}
	
	@Override
	public String toString() {
		return pieces.toString();
	}
}
