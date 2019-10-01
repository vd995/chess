package sah;

import java.util.*; 
import java.awt.*;

abstract public class Piece{
	
	public final static boolean WHITE = true;
	public final static boolean BLACK = false;
	
	private boolean color;
	private int value;
	
	public Piece(boolean c) {
		color = c;
		value = 0;	
	}
	
	@SuppressWarnings("rawtypes")
	abstract public Vector getLegalMoves(Point from, ChessBoard b);
	
	/* draw a piece given the lower left corner of the square */
	abstract public void drawPiece(int x, int y, Graphics g);
	
	public boolean getColor() {return color;}
	public int getValue() {return value;}
	protected void setValue(int v) {value = v;}
		
	
}

class King extends Piece{
	public King(boolean c){	
		super(c);
		setValue(1000);
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getLegalMoves(Point from, ChessBoard b){
		Vector v = new Vector();
		int dx,dy;
		Point tempPoint=new Point();
		
		/* Check for castling */
		if (b.canCastle(b.getTurn(),'Q'))
			v.addElement(new Move(from,new Point(from.y,2),'Q'));
		
		if (b.canCastle(b.getTurn(),'K'))
			v.addElement(new Move(from, new Point(from.y,6),'K'));
		
		/* Check normal moves */
		if (getColor() == b.getTurn())
			for (dx=-1;dx<=1;dx++)
				for (dy=-1;dy<=1;dy++){
					tempPoint.x = from.x + dx;
					tempPoint.y = from.y + dy;
				
					if (!tempPoint.onBoard());
					else if (!(b.isOccupied(tempPoint)) || 
						(b.getPiece(tempPoint).getColor() != getColor()) )
			 			v.addElement(new Move(from,tempPoint));
				}
		return v;
	}
	
	
	public void drawPiece(int x,int y, Graphics g){	
		int []X = {10,20,22,22,24,25,25,24,22,20,18,17,15,
					13,12,10,8,6,5,5,6,8,8,10};
		int []Y = {5,5,6,10,13,15,17,18,20,20,18,22,23,
				    22,18,20,20,18,17,15,13,10,6,5};
		int i;
		for (i=0;i<X.length;i++)
		{
			X[i] += x;
			Y[i] = y-Y[i];
		}
									   
		g.fillPolygon(X,Y,X.length);
		g.drawLine(x+13,y-25,x+17,y-25);
		g.drawLine(x+15,y-27,x+15,y-23);
	}
}

class Queen extends Piece{
	public Queen(boolean c)	{	
		super(c);
		setValue(9);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getLegalMoves(Point from, ChessBoard b)
	{
		
		Vector v = new Vector();
		int dx,dy;
		Point tempPoint=new Point();
		int distance;
		boolean blocked;
		
		if (getColor() == b.getTurn())
			for (dx=-1;dx<=1;dx++)
				for (dy=-1;dy<=1;dy++)
				{
					distance = 1;
					blocked = false;
					while (!blocked)
					{
						tempPoint.x = from.x + dx*distance;
						tempPoint.y = from.y + dy*distance;
				
						if (!tempPoint.onBoard())
							blocked = true;
						else if (!(b.isOccupied(tempPoint)))
							v.addElement(new Move(from,tempPoint));
						else if (b.getPiece(tempPoint).getColor() != getColor()) 
						{
							v.addElement(new Move(from,tempPoint));
							blocked = true;
						}
						else
							blocked = true;
						distance++;
					}
				}
		return v;
	}
	
	
	public void drawPiece(int x,int y, Graphics g){	
		
		int []X = {10,20,20,28,19,20,15,10,11,2,10,10};
		int []Y = {5,5,10,18,14,25,16,25,14,18,10,5};
		int i;
		for (i=0;i<X.length;i++){
			X[i] += x;
			Y[i] = y-Y[i];
		}
									   
		g.fillPolygon(X,Y,X.length);
	}
}


class Bishop extends Piece
{
	public Bishop(boolean c){	
		super(c);
		setValue(3);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getLegalMoves(Point from, ChessBoard b)	{
	
		Vector v = new Vector();
		int dx,dy;
		Point tempPoint=new Point();
		int distance;
		boolean blocked;
		
		if (getColor() == b.getTurn())
			for (dx=-1;dx<=1;dx+=2)
				for (dy=-1;dy<=1;dy+=2)	{
					distance = 1;
					blocked = false;
					while (!blocked){
						tempPoint.x = from.x + dx*distance;
						tempPoint.y = from.y + dy*distance;
				
						if (!tempPoint.onBoard())
							blocked = true;
						else if (!(b.isOccupied(tempPoint)))
							v.addElement(new Move(from,tempPoint));
						else if (b.getPiece(tempPoint).getColor() != getColor()) {
							v.addElement(new Move(from,tempPoint));
							blocked = true;
						}
						else
							blocked = true;
						distance++;
					}
				}
		return v;
	}
	
	
	public void drawPiece(int x,int y, Graphics g){	
		int []X = {5,13,17,25,25,17,18,20,20,19,17,18,14,13,12,11,10,10,11,12,5,5};
		int []Y = {5,7,7,5,8,8,10,15,17,20,25,16,25,23,21,18,15,12,10,8,8,5};
		int i;
		for (i=0;i<X.length;i++){
			X[i] += x;
			Y[i] = y - Y[i];
		}
		g.fillPolygon(X,Y,X.length);
	}
}


class Rook extends Piece {
	public Rook(boolean c)
	{	
		super(c);
		setValue(5);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getLegalMoves(Point from, ChessBoard b)
	{
		
		Vector v = new Vector();
		int dx,dy;
		Point tempPoint=new Point();
		int distance;
		boolean blocked;
		
		if (getColor() == b.getTurn())
		{
			/* Check the horizontals */
			for (dx=-1;dx<=1;dx+=2)
			{
				distance = 1;
				blocked	= false;
				while (!blocked)
				{
					tempPoint.x = from.x + dx*distance;
					tempPoint.y = from.y;
				
					if (!tempPoint.onBoard())
						blocked	= true;
					else if	(!(b.isOccupied(tempPoint)))
						v.addElement(new Move(from,tempPoint));
					else if	(b.getPiece(tempPoint).getColor() != getColor()) 
					{
						v.addElement(new Move(from,tempPoint));
						blocked	= true;
					}
					else
						blocked	= true;
					distance++;
				}
			}
			/* Check the verticals */
			for (dy=-1;dy<=1;dy+=2)
			{
				distance = 1;
				blocked	= false;
				while (!blocked)
				{
					tempPoint.x = from.x;
					tempPoint.y = from.y + dy*distance;
				
					if (!tempPoint.onBoard())
						blocked	= true;
					else if	(!(b.isOccupied(tempPoint)))
						v.addElement(new Move(from,tempPoint));
					else if	(b.getPiece(tempPoint).getColor() != getColor()) 
					{
						v.addElement(new Move(from,tempPoint));
						blocked	= true;
					}
					else
						blocked	= true;
					distance++;
				}
			}
		}
		return v;
	}
	
	
	public void drawPiece(int x,int y, Graphics g)
	{	
		Polygon polyrook = new Polygon();
		polyrook.addPoint(5+x,-25+y);
		polyrook.addPoint(5+x,-18+y);
		polyrook.addPoint(10+x,-18+y);
		polyrook.addPoint(10+x,-8+y);
		polyrook.addPoint(5+x,-8+y);
		polyrook.addPoint(5+x,-5+y);
		polyrook.addPoint(25+x,-5+y);
		polyrook.addPoint(25+x,-8+y);
		polyrook.addPoint(20+x,-8+y);
		polyrook.addPoint(20+x,-18+y);
		polyrook.addPoint(25+x,-18+y);
		polyrook.addPoint(25+x,-25+y);
		polyrook.addPoint(22+x,-25+y);
		polyrook.addPoint(22+x,-22+y);
		polyrook.addPoint(18+x,-22+y);
		polyrook.addPoint(18+x,-25+y);
		polyrook.addPoint(12+x,-25+y);
		polyrook.addPoint(12+x,-22+y);
		polyrook.addPoint(8+x,-22+y);
		polyrook.addPoint(8+x,-25+y);
		polyrook.addPoint(5+x,-25+y);
		
		g.fillPolygon(polyrook);
		
	}	
}



class Knight extends Piece 
{
	public Knight(boolean c)
	{	
		super(c);
		setValue(3);
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getLegalMoves(Point from, ChessBoard b)
	{
		Vector v = new Vector();
		int dx,dy;
		Point tempPoint=new Point();
		
		if (getColor() == b.getTurn())
		{	
			int i;
			for (i=1;i<=8;i++)
			{
				switch(i)
				{
				case 1: dx = -1; dy = -2; break;
				case 2: dx = -2; dy = -1; break;
				case 3: dx = -2; dy = 1; break;
				case 4: dx = -1; dy = 2; break;
				case 5: dx = 1; dy = 2; break;
				case 6: dx = 2; dy = 1; break;
				case 7: dx = 2; dy = -1; break;
				case 8: dx = 1; dy = -2; break;
				default: dx=0;dy=0;
				}
			
				tempPoint.x = from.x + dx;
				tempPoint.y = from.y + dy;
				
				if (!tempPoint.onBoard());
				else if (!(b.isOccupied(tempPoint)) || 
					(b.getPiece(tempPoint).getColor() != getColor()) )
					v.addElement(new Move(from,tempPoint));
			}
		}
		return v;
	}
	
	
	public void drawPiece(int x,int y, Graphics g)
	{	
		int i;
		int []X = {11,25,24,23,22,20,17,14,12,12,10,11,8,6,5,7,9,
					12,14,15,16,15,14,13,11};
		int []Y = {-5,-5,-12,-16,-19,-21,-23,-23,-25,-23,-24,-22,-17,
					-15,-14,-13,-12,-15,-17,-16,-16,-13,-11,-8,-5};
		for (i=0;i<X.length;i++)
		{
			X[i] += x;
			Y[i] += y;
		}
		
		g.fillPolygon(X,Y,X.length);
		
		
	}
}

class Pawn extends Piece
{
	public Pawn(boolean c)
	{	
		super(c);
		setValue(1);
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Vector getLegalMoves(Point from, ChessBoard b)
	{
		Vector v = new Vector();
		int dx,dy;
		Point tempPoint=new Point();
		
		if (getColor() == b.getTurn())
		{
			/* Find the direction for the pawn */
			if (b.getTurn() == WHITE)
				dy = 1;
			else
				dy = -1;
			
			/* Check for straight ahead move */
			tempPoint.x = from.x;
			tempPoint.y = from.y + dy;
				
			if (!(b.isOccupied(tempPoint)))
			{
				/* forward promotion */
				if (tempPoint.y == 0 || tempPoint.y == 7)
					v.addElement(new Move(from,tempPoint,true));
				
				else
					v.addElement(new Move(from,tempPoint));
				
				/* Also check for double move */
				tempPoint.y = from.y + 2*dy;
				tempPoint.x = from.x;
				if (tempPoint.onBoard() && !(b.isOccupied(tempPoint))
					&& (from.y == 1 || from.y == 6))
					v.addElement(new Move(from,tempPoint));
			}
			
			/* Check for attack */
			for (dx = -1; dx <=1; dx+=2)
			{
				tempPoint.x = from.x + dx;
				tempPoint.y = from.y + dy;
				
				if (tempPoint.onBoard() && b.isOccupied(tempPoint) 
						&& b.getPiece(tempPoint).getColor() != getColor())
				{
					if (tempPoint.y > 0 && tempPoint.y < 7)
			 			v.addElement(new Move(from,tempPoint, true));
					
					/* capture promotion */
					else
						v.addElement(new Move(from,tempPoint, true));
					
				}
				
				/* Check for en passant */
				else if (getColor() == BLACK && tempPoint.y == 2)
				{
					Point capPoint = new Point(3,tempPoint.x);
					Move lastMove = b.getLastMove();
					if (tempPoint.onBoard() && b.isOccupied(capPoint) 
						 && b.getPiece(capPoint).getColor() == WHITE
						 && b.getPiece(capPoint).getValue() == 1
						 && lastMove.equals(new Move(new Point(1,tempPoint.x),
													 capPoint)))
						v.addElement(new Move(from,tempPoint, capPoint));
				}
				else if (getColor() == WHITE && b.allMoves.size() > 0 && tempPoint.y == 5)
				{
					Point capPoint = new Point(4,tempPoint.x);
					Move lastMove = b.getLastMove();
					if (tempPoint.onBoard() && b.isOccupied(capPoint) 
						 && b.getPiece(capPoint).getColor() == BLACK
						 && b.getPiece(capPoint).getValue() == 1
						 && lastMove.equals(new Move(new Point(6,tempPoint.x),
													 capPoint)))
						v.addElement(new Move(from,tempPoint, capPoint));
				}
					
				
			}
		}
		return v;
	}
	
	
	public void drawPiece(int x,int y, Graphics g)
	{	
		int []X = {5,25,23,21,18,20,20,18,22,20,17,
					13,10,8,12,10,10,12,9,7,5};
		int []Y = {5,5,7,8,9,11,14,16,16,17,18,
					18,17,16,16,14,11,9,8,7,5};
		int i;
		for (i=0;i<X.length;i++)
		{
			X[i] += x;
			Y[i] = y-Y[i];
		}
									   
		g.fillPolygon(X,Y,X.length);
		g.fillOval(x+12,y-23,6,6);
		
	}
}

