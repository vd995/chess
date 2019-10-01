package sah;

public class Move {

	Point from;
	Point to;
	boolean promotion;
	char promoteTo; // 'Q', 'R', 'B', or 'N' 
	Point enpKill; // The piece killed by en Passsan 
	char castle;
	String message;

	public Move(){
		from = new Point();
		to = new Point();
		promotion = false;
		castle=(char)0;
	}

	public Move(Point f, Point t){
		from = new Point(f.y,f.x);
		to = new Point(t.y,t.x);
		promotion = false;
		castle=(char)0;
	}

	public Move(Point f, Point t, boolean pro){
		from = new Point(f.y, f.x);
		to = new Point(t.y,t.x);
		promotion = pro;
		promoteTo = 0;
		castle=(char) 0;

	}

	public Move(Point f, Point t, char c){
		from = new Point(f.y, f.x);
		to = new Point(t.y, t.x);
		promotion = false;
		castle = c;
	}

	public Move (Point f, Point t, Point enp){
		from = new Point(f.y, f.x);
		to = new Point(t.y,t.x);

		enpKill = new Point(enp.y,enp.x);
		castle=(char) 0;
	}

	public boolean equals(Move m) {
		return (m != null && m.to.equals(to) && m.from.equals(from));

	}

	public void setMessage(String s) {
		message = s;
	}

	public String getMessage(){
		return message;
	}

	public char getCastle()	{return castle;}

	public void setCastle(char c) {castle = c;}

}
