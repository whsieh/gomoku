package ai;

import gomoku.Board;
import gomoku.Move;
import gomoku.Piece;
import gomoku.Player;
import gomoku.Square;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import util.Pair;

public class GomokuAI {
	
	private GomokuAI() {}
	
	/**
	 * Given a Board, attempts to return the best Move for the current
	 * player, as well as the score difference. Large negative score
	 * difference is good for WHITE, large positive score difference
	 * is good for BLACK.
	 */
	public static Pair<Move,Integer> bestMove(Board board) {
		if (board.getPieces(Player.BLACK).size() == 0 && 
				board.getPieces(Player.WHITE).size() == 0) {
			return new Pair<Move,Integer>(
					new Move(Player.BLACK,board.getDimX()/2-1,board.getDimY()/2-1),1);
		}
		Set<Move> possibleMoves = collectReplies(
				findSequences(board, Player.BLACK),findSequences(board, Player.WHITE),board.getCurrentPlayer());
		int bestDelta = board.getCurrentPlayer() == Player.BLACK ?
				Integer.MIN_VALUE : Integer.MAX_VALUE;
		Move bestMove = null;
		
		for (Move move : possibleMoves) {
			
			board.doMove(move);
			int[] result = evaluate(board);
			int difference = result[Player.BLACK]-result[Player.WHITE];
			
			if (board.getCurrentPlayer() == Player.BLACK) {
				
				if (difference < bestDelta) {
					bestMove = move;
					bestDelta = difference;
				}
				
			} else if (board.getCurrentPlayer() == Player.WHITE){
				
				if (difference > bestDelta) {
					bestMove = move;
					bestDelta = difference;
				}
			}
			board.undoMove();
		}
		return new Pair<Move,Integer>(bestMove,bestDelta);
	}
	
	/**
	 * An implementation of the minimax algorithm.
	 */
	public static Pair<Move,Integer> minimax(Board board, int depth) {
		if (depth > 0) {
			Set<Move> children = collectReplies(
					findSequences(board, Player.BLACK),
					findSequences(board, Player.WHITE),
					board.getCurrentPlayer());
			int bestDelta = board.getCurrentPlayer()==Player.BLACK ?
					Integer.MIN_VALUE : Integer.MAX_VALUE;
			Move bestMove = null;
			if (board.getCurrentPlayer() == Player.BLACK) {
				
				for (Move child : children) {
					
					board.doMove(child);
					Pair<Move,Integer> result = minimax(board,depth-1);
					if (result.getSecond() > bestDelta) {
						bestMove = child;
						bestDelta = result.getSecond();
					}
					board.undoMove();
				}
				
			} else if (board.getCurrentPlayer() == Player.WHITE){
				
				for (Move child : children) {
					
					board.doMove(child);
					Pair<Move,Integer> result = minimax(board,depth-1);
					if (result.getSecond() < bestDelta) {
						bestMove = child;
						bestDelta = result.getSecond();
					}
					board.undoMove();
				}
			}
			return new Pair<Move,Integer>(bestMove,bestDelta);
		} else {
			return bestMove(board);
		}
	}
	
	
	/**
	 * Evaluates the board, considering both players' pieces. Returns an array
	 * containing the scores for both players.
	 */
	public static int[] evaluate(Board board) {
		
		return new int[]{
				evaluate(board, Player.BLACK).getFirst(),
				evaluate(board, Player.WHITE).getFirst()
				};
	}
	
	public static Set<Move> collectReplies(Set<Sequence> seq0, Set<Sequence> seq1, int player) {
		Set<Move> replies = new HashSet<Move>();
		for (Sequence s : seq0) {
			replies.addAll(s.adjacentMoves(player));
		}
		for (Sequence s : seq1) {
			replies.addAll(s.adjacentMoves(player));
		}
		return replies;
	}
	
	public static Set<Sequence> findSequences(Board board, int player) {
		
		Set<Sequence> sequences = new HashSet<Sequence>();
		LinkedList<Square> squares;
		LinkedList<Piece> pieces;
		Square currentSquare, nextSquare;
		int space,pcs;
		
		// For each of the player's pieces...
		for (Piece piece : board.getPieces(player)) {
			// For each of four non-opposite directions...
			for (int direction : Direction.PARTIAL_DIRECTIONS) {
				// Reset values for seq, space, and squares.
				space = 1;
				pcs = 1;
				squares = new LinkedList<Square>();
				pieces = new LinkedList<Piece>();
				squares.add(piece.getSquare());
				pieces.add(piece);
				// For the original direction and its opposite direction...
				for (int dir = 0; dir < 2; dir++) {
					currentSquare = piece.getSquare();
					// For 3 spaces in that direction...
					for (int count = 0; count < 4; count++) {
						// Find the next square.
						nextSquare = currentSquare.getNeighbor(direction);
						// If we hit the edge of the board...
						if (nextSquare == null) {
							// ...then stop going in this direction and decrement space
							break;
						} else {
							Piece nextPiece = nextSquare.getPiece();
							// If we see an empty space...
							if (nextPiece == null) {
								if (currentSquare.getPiece() == null && pcs > 2) {
									space++;
									break;
								} else {
									space++;
									currentSquare = nextSquare;
									if (dir == 0) {
										squares.addLast(nextSquare);
									} else {
										squares.addFirst(nextSquare);
									}
								}
							// If the next piece is occupied by an opponent's piece...
							} else if (nextPiece.getPlayer() != player) {
								// ...then stop going in this direction
								if (currentSquare.getPiece() == null) {
									space -= 1;
								} else {
									space -= (count+1);
								}
								break;
							// If the next piece is occupied by one of your pieces...
							} else if (nextPiece.getPlayer() == player) {
								space++;
								pcs++;
								currentSquare = nextSquare;
								if (dir == 0) {
									squares.addLast(nextSquare);
									pieces.addLast(nextPiece);
								} else {
									squares.addFirst(nextSquare);
									pieces.addFirst(nextPiece);
								}
							}
						}
					}
					// Reverse direction
					direction = Direction.opposite(direction);
				}
				// System.out.println("    Receiving sequence of size=" + pcs + " and space=" + space);
				if ((pcs < 3 && space > 5) || (pcs == 3 && space > 4) || (pcs > 3 && space >= 1)) {
					// Check for "false" three-in-a-row's
					if (pcs == 3) {
						Square first = pieces.get(0).getSquare();
						Square second = pieces.get(1).getSquare();
						Square third = pieces.get(2).getSquare();
						int deltaX_1 = Math.abs(first.getX()-second.getX());
						int deltaX_2 = Math.abs(second.getX()-third.getX());
						int deltaY_1 = Math.abs(first.getY()-second.getY());
						int deltaY_2 = Math.abs(second.getY()-third.getY());
						if ((deltaX_1 != 2 || deltaX_2 != 2) &&
								(deltaX_1 != 3 && deltaX_2 != 3)) {
							if ((deltaY_1 != 2 || deltaY_2 != 2) &&
									(deltaY_1 != 3 && deltaY_2 != 3)) {
								sequences.add(new Sequence(pieces,direction));
							}
						}
					} else if (pcs == 5) {
						Square first;
						Square second;
						int deltaX,deltaY;
						boolean win = true;
						for (int i = 0; i < 4; i++) {
							first = pieces.get(i).getSquare();
							second = pieces.get(i+1).getSquare();
							deltaX = Math.abs(first.getX()-second.getX());
							deltaY = Math.abs(first.getY()-second.getY());
							if (deltaX > 1 || deltaY > 1) {
								win = false;
								break;
							}
						}
						if (win) {
							sequences.add(new Sequence(pieces,direction));
						}
					} else {
						sequences.add(new Sequence(pieces,direction));
					}
				}
			}
		}
		return sequences;
	}
	
	/**
	 * Evaluates the board for a given player. Returns an integer representing the
	 * score, as well as the set of sequences for the player.
	 */
	public static Pair<Integer,Set<Sequence>> evaluate(Board board, int player) {
		
		Set<Sequence> sequences = findSequences(board, player);
		int currentPlayer = board.getCurrentPlayer();
		int attacks = 0, finalscore = 0;
		boolean victory = false;
		
		for (Sequence s : sequences) {
			
			switch(s.pieces.size()) {
			// One piece sequence
			case 1:
				finalscore++;
				break;
			// Two piece sequence
			case 2:
				if (s.isConsecutive()) {
					finalscore += 5;
				} else {
					finalscore += 3;
				}
				break;
			// Three piece sequence
			case 3:
				if (s.blocked() == 0) {
					finalscore += 5;
					attacks++;
					if (currentPlayer == player) {
						finalscore += 500;
					}
				}
				break;
			// Four piece sequence
			case 4:
				finalscore += 10;
				attacks++;
				if (currentPlayer == player) {
					finalscore += 1500;
				} else if (s.blocked() == 0 && s.isConsecutive()) {
					finalscore += 750;
				}
				break;
			// Five piece sequence
			default:
				if (s.isConsecutive()) {
					finalscore += 5000;
				}
				break;
			}
		}
		if (attacks >= 2 && finalscore < 1000) {
			finalscore += 500;
		}
		return new Pair<Integer,Set<Sequence>>(finalscore,sequences);
	}
	
	
	/** OLD CODE
	public static Pair<Integer,Set<Sequence>> evaluate(Board board, int player) {
		
		Set<Sequence> sequences = findSequences(board, player);
		
		int[] seq = new int[5];
		for (Sequence s : sequences) {
			if (s.pieces.size() == 4) {
				if (!s.isBlocked() && s.isConsecutive()) {
					seq[3]+=2;
				}
			}
			if (s.pieces.size() >= 5) {
				seq[4]++;
			} else {
				seq[s.pieces.size()-1]++;
			}
		}
		seq[1]*=15;
		seq[2]*=10;
		seq[3]*=10;
		int finalscore = (seq[0]>15 ? 15 : seq[0]) 
			+ (seq[1]>150 ? 150 : seq[1])
			+ (board.getCurrentPlayer()==player ? 
					((seq[2]+seq[3])>0 ? 750 : seq[2]+seq[3]) : ((seq[2]+seq[3])>10 ? 250 : seq[2]+seq[3]))
			+ (1000*seq[4]);
		if (finalscore > 1000)
			finalscore = 1000;
		return new Pair<Integer,Set<Sequence>>(finalscore,sequences);
	}
	*/
	
	public static String toString(Set<Sequence> sequences) {
		
		StringBuilder s = new StringBuilder("Sequence count: " + sequences.size() + "\n");
		for (Sequence seq : sequences) {
			s.append("  size=" + seq.pieces.size() + "; " + seq + "\n");
		}
		return s.substring(0,s.length()-1).toString();
	}
}
