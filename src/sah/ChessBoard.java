package sah;
import java.util.*;

public class ChessBoard{
	
	public static final boolean WHITE = true;
	public static final boolean BLACK = false;

	private Piece contents[][];
	private boolean turn;
	@SuppressWarnings("rawtypes")
	private Vector legalMoves;
	@SuppressWarnings("rawtypes")
	public Vector  allMoves;

	private Point blackKing;
	private Point whiteKing;

	public boolean whiteKingMoved;
	public boolean blackKingMoved;
	public boolean whiteKRookMoved;
	public boolean whiteQRookMoved;
	public boolean blackKRookMoved;
	public boolean blackQRookMoved;

	@SuppressWarnings({ "rawtypes" })
	public ChessBoard(){
		contents = new Piece[8][8];
		turn = WHITE;
		allMoves = new Vector();

		newBoard();

		whiteKing = new Point(0,4);
		blackKing = new Point(7,4);
		whiteKingMoved = false;
		blackKingMoved = false;
		whiteKRookMoved = false;
		whiteQRookMoved = false;
		blackKRookMoved=false;
		blackQRookMoved=false;
		legalMoves = new Vector();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ChessBoard(ChessBoard b){
		int rank,file;
		Point p=new Point();
		contents = new Piece[8][8];
		turn = b.getTurn();
		for (rank = 0;rank < 8;rank++)
			for (file=0;file<8;file++){
				p.y = rank;
				p.x = file;
				if (b.isOccupied(p))
					contents[rank][file] = b.getPiece(p);
			}
		whiteKing = b.getKing(WHITE);
		blackKing = b.getKing(BLACK);

		whiteKingMoved = b.whiteKingMoved;
		whiteKRookMoved = b.whiteKRookMoved;
		whiteQRookMoved = b.whiteQRookMoved;
		blackKingMoved = b.blackKingMoved;
		blackKRookMoved = b.blackKRookMoved;
		blackQRookMoved = b.blackQRookMoved;

		/* copy the moves vector */
		allMoves = new Vector();
		Enumeration enm = b.allMoves.elements();
		while (enm.hasMoreElements())
			allMoves.addElement(enm.nextElement());

		/* copy the legal moves vector */
		legalMoves = new Vector();
		enm = b.legalMoves.elements();
		while (enm.hasMoreElements())
			legalMoves.addElement(enm.nextElement());

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void findAllLegalMoves(){
		int rank,file;
		Point temp = new Point();
		Vector v;
		Enumeration enm;
		Move m;
		if (legalMoves != null)
			legalMoves.removeAllElements();
		for (rank = 0;rank < 8;rank++)
			for (file = 0;file < 8; file++)	{
				temp.y = rank; temp.x = file;
				if (isOccupied(temp) && getPiece(temp).getColor() == turn){
					v = contents[rank][file].getLegalMoves(temp,this);
					enm = v.elements();
					while (enm.hasMoreElements()) {
						m = (Move) enm.nextElement();
						if (isCheckLegal(m))
							legalMoves.addElement(m);
					}

				}
			}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void takeBackMove()	{
		/* This function clears the board and starts over */
		/* Not the best way, but easy */
		Vector v= new Vector();
		Enumeration enm;
	
		newBoard();
		if (!allMoves.isEmpty()){
			allMoves.removeElementAt(allMoves.size()-1);
			enm = allMoves.elements();
			while (enm.hasMoreElements())
				v.addElement(enm.nextElement());

			/* This is necessary because makeMove adds the moves onto the vector */
			allMoves.removeAllElements();

			enm = v.elements();	
			while (enm.hasMoreElements())
				makeMove((Move)enm.nextElement());
		}
		
		allMoves = v;
	}

	public void newBoard(){
		int i;
		/* erase pieces from middle */
		int rank,file;
		for (rank = 2;rank < 6;rank++)
			for (file = 0;file < 8; file ++)
				contents[rank][file] = null;

		/* pawns */
		for (i=0;i<8;i++){
			contents[1][i] = new Pawn(WHITE);
			contents[6][i] = new Pawn(BLACK);
		}

		contents[0][0] = new Rook(WHITE);
		contents[0][1] = new Knight(WHITE);
		contents[0][2] = new Bishop(WHITE);
		contents[0][3] = new Queen(WHITE);
		contents[0][4] = new King(WHITE);
		contents[0][5] = new Bishop(WHITE);
		contents[0][6] = new Knight(WHITE);
		contents[0][7] = new Rook(WHITE);

		contents[7][0] = new Rook(BLACK);
		contents[7][1] = new Knight(BLACK);
		contents[7][2] = new Bishop(BLACK);
		contents[7][3] = new Queen(BLACK);
		contents[7][4] = new King(BLACK);
		contents[7][5] = new Bishop(BLACK);
		contents[7][6] = new Knight(BLACK);
		contents[7][7] = new Rook(BLACK);

		turn = WHITE;

	}

	public boolean getTurn() { return turn;}

	public Point getKing(boolean which)	{
	
		if (which)
			return new Point(whiteKing.y,whiteKing.x);
		else
			return new Point(blackKing.y,blackKing.x);
	}

	@SuppressWarnings("rawtypes")
	public boolean isLegal(Move m)	{
		Vector v;
		Enumeration enm;
		boolean value = false;
		Move temp;

		v = contents[m.from.y][m.from.x].getLegalMoves(m.from,this);
		enm = v.elements();
		while (enm.hasMoreElements() && !value)	{
			temp = (Move) enm.nextElement();
			if ( m.equals(temp)){
				value = true; 
				/* if move is en passant, then the capture must be added */
				if (temp.enpKill != null){
					m.enpKill = new Point(temp.enpKill);
				}

				/* if move is promotion */
				if (temp.promotion)
					m.promotion = true;
				/* if move is castling */
				m.setCastle(temp.getCastle());


			}
		}
		return value;
	}

	public boolean isOccupied(Point p)	{
		return (contents[p.y][p.x] != null);
	}

	@SuppressWarnings("unchecked")
	public void makeMove(Move m)
	{
		contents[m.to.y][m.to.x]=contents[m.from.y][m.from.x];

		/* check for castling */
		if (m.getCastle() != (char) 0)
		{
			int homerank = m.from.y;
			int dx = m.getCastle() == 'Q' ? -1 : 1;

			/* move the king */
			contents[homerank][4+2*dx] = contents[homerank][4];
			contents[homerank][4] = null;

			/* move the rook */
			contents[homerank][4+dx] = contents[homerank][(7*dx+7)/2];
			contents[homerank][(7*dx + 7)/2] = null;
		}

		/* check for en Passant */
		if (m.enpKill != null)
			contents[m.enpKill.y][m.enpKill.x] = null;

		/* check for promotion */
		if (m.promotion)
			/* The AI does automatic promotion to Queen */
			switch(m.promoteTo)
			{
			case 'Q': contents[m.to.y][m.to.x] = new Queen(turn);
			break;
			case 'B': contents[m.to.y][m.to.x] = new Bishop(turn);
			break;
			case 'R': contents[m.to.y][m.to.x] = new Rook(turn);
			break;
			case 'N': contents[m.to.y][m.to.x] = new Knight(turn);
			break;
			default: contents[m.to.y][m.to.x] = new Queen(turn);
			}

		/* Check if the King or a Rook moved (castling purposes)*/
		if (m.from.equals(new Point(0,4)))
			whiteKingMoved = true;
		if (m.from.equals(new Point(0,0)))
			whiteQRookMoved = true;
		if (m.from.equals(new Point(0,7)))
			whiteKRookMoved = true;

		if (m.from.equals(new Point(7,4)))
			blackKingMoved = true;
		if (m.from.equals(new Point(7,0)))
			blackQRookMoved = true;
		if (m.from.equals(new Point(7,7)))
			blackKRookMoved = true;

		allMoves.addElement(m);

		if (m.from.equals(getKing(WHITE))){
			whiteKing.y = m.to.y;
			whiteKing.x = m.to.x;
		}
		if (m.from.equals(getKing(BLACK))){
			blackKing.y = m.to.y;
			blackKing.x = m.to.x;
		}

		turn = !turn;

		contents[m.from.y][m.from.x] = null;
	}

	public boolean isCheckLegal(Move m){
		boolean returnValue = false;
		ChessBoard temp = new ChessBoard(this);

		if (m.getCastle() == (char) 0)	{
			temp.makeMove(m);
			if (temp.isInCheck(!temp.getTurn()))
				m.setMessage("Move results in Check!");
			else
				returnValue = true;
		}

		else{
			int homerank = turn==WHITE ? 0 : 7;
			int dx = (m.getCastle() == 'Q') ? -1 : 1;
			if (!temp.isInCheck(turn))	{
				temp.makeMove(new Move(new Point(homerank,4),new Point(homerank,4+dx)));
				
				if (!temp.isInCheck(turn))	{
					temp.makeMove(new Move(new Point(homerank,4+dx),new Point(homerank,4+2*dx)));
					
					if (!temp.isInCheck(turn))
						returnValue = true;
				}

			}
		}	
		return returnValue;
	}

	public Move getLastMove()	{
		if (allMoves.isEmpty())
			return null;
		else
			return (Move) allMoves.elementAt(allMoves.size()-1);
	}

	public Piece getPiece(Point p)	{
		return contents[p.y][p.x];
	}

	public boolean isInCheck(boolean t){
		int rank,file;
		boolean value = false;
		Point from = new Point();
		boolean switched = false;

		if (turn == t)		{
			switched = true;
			turn = !turn;
		}

		for (rank = 0; rank < 8; rank++)
			for (file = 0;file < 8; file ++)			{
				from.y = rank; from.x = file;

				if (isOccupied(from)  && getPiece(from).getColor() == turn
						&& isLegal(new Move(from,getKing(t))))
					value = true;
			}

		if (switched)
			turn = !turn;
		
		return value;
	}

	public boolean canCastle(boolean t, char side)	{
		boolean value=true;

		if (t==WHITE && side == 'K')	{
			if (whiteKingMoved || whiteKRookMoved 
					|| isOccupied(new Point(0,5)) || isOccupied(new Point(0,6)))
				value = false;
			else
				value = true;
		}
		else if (t==WHITE && side == 'Q')	{
			if (whiteKingMoved || whiteQRookMoved
					|| isOccupied(new Point(0,3)) || isOccupied(new Point(0,2))
					|| isOccupied(new Point(0,1)))
				value = false;
			else
				value = true;
		}

		else if (t==BLACK && side == 'K')	{
			if (blackKingMoved || blackKRookMoved
					|| isOccupied(new Point(7,5)) || isOccupied(new Point(7,6)))
				value = false;
			else
				value = true;
		}
		else if (t==BLACK && side == 'Q')	{
			if (blackKingMoved || blackQRookMoved
					|| isOccupied(new Point(7,3)) || isOccupied(new Point(7,2))
					|| isOccupied(new Point(7,1)))
				value = false;
			else
				value = true;	
		}
		return value;
	}

	@SuppressWarnings("rawtypes")
	public Vector getAllLegalMoves() {return legalMoves;}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getBestMoves(int depth){

		Vector v = new Vector();
		int ratings[];
		ratings = new int[legalMoves.size()];
		int index=0;
		int max = -1000; 
		Enumeration enm = legalMoves.elements();
		Move m;

		while (enm.hasMoreElements())	{
			m = (Move) enm.nextElement();
			ratings[index] = rateMove(m,depth);
			if (ratings[index] > max)
				max = ratings[index];
			index++;
		}

		for (int i=0;i<index;i++)
			if (ratings[i] == max)
				v.addElement(legalMoves.elementAt(i));

		return v;
	}	

	@SuppressWarnings("rawtypes")
	private int rateMove(Move m, int depth)	{
		int value=0;
		/* base case */
		if (depth == 0)		{

			if (m.promotion)
				value += 90;

			if (isOccupied(m.to))
				value += 10*getPiece(m.to).getValue();

			else if (m.enpKill != null)
				value += 10;

			else if (m.castle != (char) 0)
				value += 50;

			else if (getPiece(m.from).getValue() == 1000)
				value -= 10;

			value += (Math.abs(7-m.to.y) + Math.abs(7-m.to.x)
			- Math.abs(7-m.from.y) - Math.abs(7-m.from.x))/2;

			return value;
		}

		else{
			ChessBoard copy = new ChessBoard(this);

			int nominal = rateMove(m,0);

			copy.makeMove(m);

			copy.findAllLegalMoves();

			if (copy.getAllLegalMoves().size() == 0){
				if (copy.isInCheck(copy.getTurn()))
					return 1000+depth; 
				else
					return copy.isAhead(turn) ? -100 : 100;
			}

			else{
				int maxRating = -1000;
				int rating;
				Enumeration enm = copy.getAllLegalMoves().elements();
				while (enm.hasMoreElements())	{
					rating = copy.rateMove((Move)enm.nextElement(),depth-1);
					if (rating > maxRating)
						maxRating = rating;
				}

				return (nominal - maxRating);
			}			
		}
	}

	public boolean isAhead(boolean t) {
		int rank,file,white=0,black=0;
		Point temp = new Point();
		Piece p;
		for (rank = 0;rank < 8; rank++)
			for (file = 0; file<8; file++)	{
				temp.y = rank; temp.x = file;
				if (isOccupied(temp)){
					if ((p=getPiece(temp)).getColor())
						white += p.getValue();
					else
						black += p.getValue();
				}
			}

		return (white > black);
	}

}