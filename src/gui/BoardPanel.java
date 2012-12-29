package gui;

import gomoku.Board;
import gomoku.Move;
import gomoku.Piece;
import gomoku.Player;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import util.Pair;

import ai.GomokuAI;

public class BoardPanel extends JPanel {
	
	public static final BoardPanel panel = new BoardPanel();
	
	public static final int DEFAULT_DIM_X = 16;
	public static final int DEFAULT_DIM_Y = 16;
	
	public static final int DEFAULT_SIZE_SQUARE = 32;
	public static final int DEFAULT_OFFSET_SQUARE = DEFAULT_SIZE_SQUARE/2;
	public static final int DEFAULT_SIZE_X = DEFAULT_SIZE_SQUARE * DEFAULT_DIM_X;
	public static final int DEFAULT_SIZE_Y = DEFAULT_SIZE_SQUARE * DEFAULT_DIM_Y;
	
	public static final int TITLE_BAR_THICKNESS = 30;
	
	PiecePainter whitePiecePainter;
	PiecePainter blackPiecePainter;
	
	Board board;
	
	private int drawX;
	private int drawY;
	private int victory = -1;
	private Move lastMove = null;
	
	public BoardPanel() {
		
		board = new Board(DEFAULT_DIM_X, DEFAULT_DIM_Y, Player.BLACK);
		whitePiecePainter = new PiecePainter("img/white.gif");
		blackPiecePainter = new PiecePainter("img/black.gif");
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 1) {
					int x = (e.getX() - DEFAULT_OFFSET_SQUARE) / DEFAULT_SIZE_SQUARE;
					int y = (e.getY() - DEFAULT_OFFSET_SQUARE) / DEFAULT_SIZE_SQUARE;
					if (victory == -1 && board.doMove(x,y)) {
						if (GomokuAI.evaluate(board)[Player.WHITE] > 5000) {
							// White wins
							victory = Player.WHITE;
						} else {
							Pair<Move,Integer> aiResults = GomokuAI.minimax(board,2);
							board.doMove(aiResults.getFirst());
							lastMove = aiResults.getFirst();
							if (GomokuAI.evaluate(board)[Player.BLACK] > 5000) {
								// Black wins
								victory = Player.BLACK;
							}
						}
					}
					repaint();
				}
				System.out.println("Black eval: " + GomokuAI.evaluate(board,Player.BLACK));
				System.out.println("White eval: " + GomokuAI.evaluate(board, Player.WHITE));
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter(){
			@Override
			public void mouseMoved(MouseEvent e) {
				int x = (e.getX() - DEFAULT_OFFSET_SQUARE) / DEFAULT_SIZE_SQUARE;
				int y = (e.getY() - DEFAULT_OFFSET_SQUARE) / DEFAULT_SIZE_SQUARE;
				drawX = x;
				drawY = y;
				repaint();
			}
		});
	}
	
	@Override
	public void paintComponent(Graphics g) {
		
		Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
        
		super.paintComponent(g);
		
		g.drawString("(" + drawX + "," + drawY + ")",5,25);
		
		// Draw vertical lines
		for (int i = 0; i < DEFAULT_DIM_X*DEFAULT_SIZE_SQUARE+DEFAULT_OFFSET_SQUARE; i+=DEFAULT_SIZE_SQUARE) {
			g.drawLine(i, 0, i, DEFAULT_SIZE_Y);
		}
		// Draw horizontal lines
		for (int i = 0; i < DEFAULT_DIM_Y*DEFAULT_SIZE_SQUARE+DEFAULT_OFFSET_SQUARE; i+=DEFAULT_SIZE_SQUARE) {
			g.drawLine(0, i, DEFAULT_SIZE_X, i);
		}
		for (Piece piece : board.getPieces(Player.WHITE)) {
			whitePiecePainter.paint(g, DEFAULT_OFFSET_SQUARE + piece.getSquare().getX()*DEFAULT_SIZE_SQUARE,
					DEFAULT_OFFSET_SQUARE + piece.getSquare().getY()*DEFAULT_SIZE_SQUARE);
		}
		for (Piece piece : board.getPieces(Player.BLACK)) {
			blackPiecePainter.paint(g, DEFAULT_OFFSET_SQUARE + piece.getSquare().getX()*DEFAULT_SIZE_SQUARE,
					DEFAULT_OFFSET_SQUARE + piece.getSquare().getY()*DEFAULT_SIZE_SQUARE);
		}
		int player = lastMove.getPlayer();
		Color defaultColor = g.getColor();
		Stroke defaultStroke = g2d.getStroke();
		g2d.setStroke(new BasicStroke((float) 2.5));
		g.setColor(Color.RED);
		g.drawOval(DEFAULT_OFFSET_SQUARE + lastMove.getX()*DEFAULT_SIZE_SQUARE,
				DEFAULT_OFFSET_SQUARE + lastMove.getY()*DEFAULT_SIZE_SQUARE,
				30, 30);
		g.setColor(defaultColor);
		g2d.setStroke(defaultStroke);
		Font defaultFont = g.getFont();
		
		switch(victory) {
		
		case Player.BLACK:
			g.setFont(new Font(defaultFont.getFontName(),1,30));
			g.drawString("AI (BLACK) wins!",50,50);
			g.setFont(defaultFont);
			break;
			
		case Player.WHITE:
			g.setFont(new Font(defaultFont.getFontName(),1,30));
			g.drawString("HUMAN (WHITE) wins!",50,50);
			g.setFont(defaultFont);
			break;
			
		default:
			break;
		}
		
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_OFF);
	}

	public static void main(String[] args) {
		
		JFrame frame = new JFrame("Get 5 pieces in a row!!!");
		frame.add(panel);
		frame.setSize(DEFAULT_SIZE_X + DEFAULT_OFFSET_SQUARE,
				DEFAULT_SIZE_Y + TITLE_BAR_THICKNESS + DEFAULT_OFFSET_SQUARE);
		frame.setResizable(false);
		frame.setVisible(true);
		Move firstMove = GomokuAI.bestMove(panel.board).getFirst();
		panel.board.doMove(firstMove);
		panel.lastMove = firstMove;
		panel.repaint();
		frame.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	
}
