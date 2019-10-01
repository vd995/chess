package sah;

public class Point {
	
	public int x;
	public int y;
	
	public Point(int r, int f){
		x = f;
		y = r;
	}
	
	public Point(Point p){
		x = p.x;
		y = p.y;
	}
	
	public Point(){
		x = y = 0;
	}
	
	public boolean onBoard(){
		return (x >= 0 && x < 8 && y >= 0 && y <8);
	}
	
	public boolean equals(Point p){
		return (p != null && p.x == x && p.y == y);
	}
}

