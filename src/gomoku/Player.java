package gomoku;

public final class Player {
	
	private Player() {}
	
	public final static int BLACK = 0;
	public final static int WHITE = 1;
	
	public static int other(int player) {
		return 1 - player;
	}
}
