package gomoku;

public class Piece {

	int player;
	Square square;
	
	public Piece(int player, Square square) {
		
		this.player = player;
		this.square = square;
	}
	
	public Square getSquare() {
		return square;
	}
	
	public int getPlayer() {
		return player;
	}
	
	@Override
	public String toString() {
		return "P" + player + "(" + square.x + "," + square.y + ")";
	}
 }
