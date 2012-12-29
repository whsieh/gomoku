package gomoku;

public class Square {
	
	Piece piece;
	int x;
	int y;
	Square[] neighbors;

	public Square(int x, int y) {
		
		this.x = x;
		this.y = y;
		piece = null;
		neighbors = new Square[8]; // needs to be set by board
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public Piece getPiece() {
		return piece;
	}
	
	public Piece removePiece() {
		Piece p = piece;
		piece = null;
		return p;
	}
	
	public Piece addPiece(int player) {
		piece = new Piece(player, this);
		return piece;
	}
	
	public Square getNeighbor(int direction) {
		return neighbors[direction];
	}
	
	public String toString() {
		return "Square[" + x + "," + y + "]";
	}
}
