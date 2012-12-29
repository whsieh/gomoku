package gomoku;

public class Move {
	
	private static int[] PRIMES_X = new int[] {7927,8171,8389,8641,7933,8179,8419,
		8647,7937,8191,8423,8663,7949,8209,8429,8669,7951,8219,8431,8677};
	
	private static int[] PRIMES_Y = new int[] {104743,105023,105359,105613,104759,
		105031,105361,105619,104761,105037,105367,105649,104773,105071,105373,
		105653,104779,105097,105379,105667};
	
	private static int[] PRIMES_PLAYERS = new int[] {47279, 48611};
	
	int player;
	int x;
	int y;
	
	public Move(int player, int x, int y) {
		
		this.player = player;
		this.x = x;
		this.y = y;
	}
	
	public int getPlayer() {
		return player;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	@Override
	public String toString() {
		return "P" + player + "(" + x + "," + y + ")";
	}
	
	@Override
	public int hashCode() {
		return (x * PRIMES_X[x % PRIMES_X.length] +
			y * PRIMES_Y[y % PRIMES_Y.length]) % (PRIMES_PLAYERS[player]);
	}
	
	@Override
	public boolean equals(Object o) {
		return o.hashCode() == hashCode();
	}
}
