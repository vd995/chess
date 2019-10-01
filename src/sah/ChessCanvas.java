package sah;

import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class ChessCanvas extends Canvas implements MouseListener, MouseMotionListener{
	
	private int delx, dely;
	@SuppressWarnings("unused")
	private int left, top;
	private Chess parent;

	private Point from;
	private Point to;
	private boolean dragging;
	private Image buffer;
	private Graphics gContext; 

	public ChessCanvas(Chess c)	{

		left = 0;
		top = 0;
		delx = 25;
		dely = 25;
		parent = c;
		from = new Point();
		to = new Point();
		addMouseMotionListener(this);
		addMouseListener(this);
		dragging = false;
		setSize(8*delx+1,8*dely+1);

	}

	public ChessCanvas(Chess c, int l, int t, int dx, int dy){

		left = l;
		top = t;
		delx  = dx;
		dely = dy;
		parent = c;
		from = new Point();
		to = new Point();
		addMouseMotionListener(this);
		addMouseListener(this);
		dragging = false;
		setSize(8*delx+1,8*dely+1);
	}

	public void paint(Graphics g){
		int rank,file;
		ChessBoard board;
		Point p= new Point();
		Piece piece;

		buffer = createImage(8*delx+1,8*dely+1);
		gContext = buffer.getGraphics();


		board = parent.getBoard();

		/* Draw the board */
		for (rank=0;rank<8;rank++){
			for (file=7;file>=0;file--){
				/* Color in the dark squares */
				if ((rank+file)%2 == 1)
					gContext.setColor(Color.gray);

				else
					gContext.setColor(Color.lightGray);

				gContext.fillRect(file*delx,rank*dely,delx,dely);
			}	
		}


		/* Draw the pieces */
		for (rank = 0;rank<8;rank++){
			for (file = 7;file >=0; file --) {
				/* If the square is occupied, draw the piece */
				p.x = file;
				p.y = rank;
				if ((piece = board.getPiece(p)) != null)	{
					if (dragging && p.equals(from))
						gContext.setColor(Color.green);
					else if (piece.getColor())
						gContext.setColor(Color.white);
					else
						gContext.setColor(Color.black);
					gContext.setFont(new Font("Serif",Font.BOLD,20));
					piece.drawPiece(file*delx,(8-rank)*dely,gContext);
				}	
			}
		}
		g.drawImage(buffer,0,0,this);
	}

	/* Override the update method so that the board is not erased */
	public void update(Graphics g) {paint(g);}

	public void mouseReleased(MouseEvent e){
		
		if (dragging){
			to.y = 7 - (int) e.getY()/dely;
			to.x = e.getX()/delx;
			parent.moveHandler(new Move(from,to));
			dragging = false;
		}
		repaint();
	}


	public void mouseDragged(MouseEvent e) {}


	public void mousePressed(MouseEvent e) {
		int r,f;
		r = 7 - (int) e.getY()/dely;
		f = e.getX()/delx;
		Point p = new Point(r,f);
		ChessBoard b = parent.getBoard();

		if (b.isOccupied(p) &&
				b.getPiece(p).getColor() == b.getTurn() &&
				parent.isHuman(b.getTurn())  )
		{
			from.x = f;
			from.y = r;
			dragging = true;
			repaint();
		}
	}

	public void mouseExited(MouseEvent e) {dragging = false;}

	/* Unused MouseListener methods */
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}


	/* Unused MouseMotionListener method */
	public void mouseMoved(MouseEvent e) {}
}