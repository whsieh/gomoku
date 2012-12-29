package gomoku;

import java.util.LinkedList;
import java.util.List;

import ai.Direction;

public class Board {

	Square[][] squares;
	LinkedList<Piece> whitePieces;
	LinkedList<Piece> blackPieces;
	int currentPlayer;
	LinkedList<Move> moveHistory;
	LinkedList<Piece>[] pieces;
	int dimX;
	int dimY;
	
	/**
	 * Constructs an empty dimX-by-dimY board with firstPlayer
	 * as the first to make a move.
	 */
	public Board(int dimX, int dimY, int firstPlayer) {
		
		this.dimX = dimX;
		this.dimY = dimY;
		currentPlayer = firstPlayer;
		squares = new Square[dimX][dimY];
		for (int i = 0; i < dimX; i++) {
			for (int j = 0; j < dimY; j++) {
				squares[i][j] = new Square(i, j);
			}
		}
		moveHistory = new LinkedList<Move>();
		whitePieces = new LinkedList<Piece>();
		blackPieces = new LinkedList<Piece>();
		pieces = (LinkedList<Piece>[])(new LinkedList[] {blackPieces,whitePieces});
		
		// Connect all squares to one another
		int shiftX,shiftY;
		for (int i = 0; i < dimX; i++) {
			for (int j = 0; j < dimY; j++) {
				for (int direction : Direction.DIRECTIONS) {
					Square sq = squares[i][j];
					shiftX = Direction.shiftX(sq.x, direction);
					shiftY = Direction.shiftY(sq.y, direction);
					if (shiftX >= 0 && shiftX < dimX && shiftY >= 0 && shiftY < dimY) {
						sq.neighbors[direction] = squares[shiftX][shiftY];
					}
				}
			}
		}
	}
	
	/**
	 * Returns the current player
	 */
	public int getCurrentPlayer() {
		return currentPlayer;
	}
	
	/**
	 * Given a player (black or white) returns the corresponding
	 * list of pieces belonging to that player. 
	 */
	public List<Piece> getPieces(int side) {
		return pieces[side];
	}
	
	/**
	 * Given coordinates, returns the square at that coordinate.
	 */
	public Square getSquare(int x, int y) {
		return squares[x][y];
	}
	
	/**
	 * Reverses the latest move recorded in moveHistory.
	 */
	public void undoMove() {
		if (!moveHistory.isEmpty()) {
			Move lastMove = moveHistory.removeLast();
			// System.err.println("Undoing " + lastMove.player + "'s previous move.");
			squares[lastMove.x][lastMove.y].removePiece();
			pieces[lastMove.player].removeLast();
			currentPlayer = Player.other(currentPlayer);
		}
	}
	
	/**
	 * Convenience method for doMove (see below)
	 */
	public boolean doMove(int x, int y) {
		return doMove(new Move(currentPlayer,x,y));
	}
	
	public int getDimX() {
		return dimX;
	}
	
	public int getDimY() {
		return dimY;
	}
	
	/**
	 * Given a Move, updates the board accordingly.
	 * If the Move is valid, executes the move and returns true.
	 * If the Move is invalid, makes no changes and returns false.
	 */
	public boolean doMove(Move move) {
		boolean valid = isValid(move);
		if (valid) {
			pieces[move.player].add(squares[move.x][move.y].addPiece(move.player));
			moveHistory.add(move);
			// System.out.print("Player " + currentPlayer + " moved at (" + move.x + "," + move.y + ")");
			currentPlayer = Player.other(currentPlayer);
			// System.out.println("...currently player " + currentPlayer + "'s turn.");
		}
		return valid;
	}
	
	/**
	 * Given a Move, returns true if the Move is valid, and
	 * false otherwise.
	 * Checks that (1) the current player is correct, (2) both
	 * x and y coordinates are valid and (3) the target square
	 * is not already occupied by a piece.
	 */
	public boolean isValid(Move move) {
		return (move.player == currentPlayer &&
				move.x >= 0 && move.x < dimX &&
				move.y >= 0 && move.y < dimY && 
				squares[move.x][move.y].piece == null);
	}	
	
}
