package ai;

public final class Direction {

	private Direction() {}
	
	public final static int EAST = 0;
	public final static int NORTH_EAST = 1;
	public final static int NORTH = 2;
	public final static int NORTH_WEST = 3;
	public final static int WEST = 4;
	public final static int SOUTH_WEST = 5;
	public final static int SOUTH = 6;
	public final static int SOUTH_EAST = 7;
	
	public final static int[] DIRECTIONS = {0,1,2,3,4,5,6,7};
	public final static int[] PARTIAL_DIRECTIONS = {0,1,2,3};
	
	public static int opposite(int direction) {
		return (direction + 4) % 8;
	}
	
	public static int shiftX(int x, int direction) {
		switch (direction) {
			case SOUTH_EAST:
			case EAST:
			case NORTH_EAST:
				return x+1;
			case NORTH_WEST:
			case WEST:
			case SOUTH_WEST:
				return x-1;
			default:
				return x;
		}
	}
	
	public static int shiftY(int y, int direction) {
		switch (direction) {
			case NORTH_EAST:
			case NORTH:
			case NORTH_WEST:
				return y-1;
			case SOUTH_WEST:
			case SOUTH:
			case SOUTH_EAST:
				return y+1;
			default:
				return y;
		}
	}
	
	public static String toString(int direction) {
		switch(direction) {
		case NORTH:
			return "North";
		case NORTH_EAST:
			return "Northeast";
		case EAST:
			return "East";
		case SOUTH_EAST:
			return "Southeast";
		case SOUTH:
			return "South";
		case SOUTH_WEST:
			return "Southwest";
		case WEST:
			return "West";
		case NORTH_WEST:
			return "Northwest";
		default:
			return "";	
		}
	}
	
}
