package sah;

import java.awt.*;

import java.applet.*;
import java.awt.event.*;
import java.util.*; 


@SuppressWarnings("serial")
public class Chess extends Applet{
	
	public static final boolean WHITE = true;
	public static final boolean BLACK = false;
	private ChessBoard board;
	private ChessCanvas canvas;
	private Button btnNewGame;
	private Button btnTakeBack;
	private Choice blackCh;
	private Choice whiteCh;
	private AITimer timer;
	
	private boolean thinking;  
	
	private Move tempMove;
	public Label messageLbl;
	private boolean isPromoting;
	
	@SuppressWarnings("deprecation")
	public void init(){
		
		canvas = new ChessCanvas(this,0,0,30,30);
		add(canvas);
		
		this.setBackground(Color.blue);
		canvas.repaint();	   
		
		btnNewGame = new Button("New Game");
		add(btnNewGame);
		btnNewGame.addActionListener(new NewGameListener(this));
		
		btnTakeBack = new Button("Take Back");
		add(btnTakeBack);
		btnTakeBack.addActionListener(new TakeBackListener(this));
		
		whiteCh = new Choice();
		whiteCh.add("Human");
		whiteCh.add("Random");
		whiteCh.add("1 move");
		whiteCh.add("2 moves");
		whiteCh.add("3 moves");
		whiteCh.setForeground(Color.white);
		whiteCh.setBackground(Color.gray);
			
		blackCh = new Choice();
		blackCh.add("Human");
		blackCh.add("Random");
		blackCh.add("1 move");
		blackCh.add("2 moves");
		blackCh.add("3 moves");
		blackCh.setForeground(Color.black);
		blackCh.setBackground(Color.gray);
		add(whiteCh);
		add(blackCh);
		whiteCh.addItemListener(new PlayerChoiceListener(this));
		blackCh.addItemListener(new PlayerChoiceListener(this));
		
		messageLbl = new Label("Ready to play");
			
		add(messageLbl);
		messageLbl.setBounds(30,320,0,0);
			
		messageLbl.setSize(120,20);
		canvas.addKeyListener(new PromotionListener(this));
		addKeyListener(new PromotionListener(this));
		
		timer = new AITimer(this);
		timer.start();
		timer.suspend();
		newGame();
		
	}
	
	@SuppressWarnings("deprecation")
	public void newGame()	{
		board = new ChessBoard();
		messageLbl.setText("");
		repaint();
		canvas.repaint();
		if (!isHuman(WHITE) || !isHuman(BLACK) )
		
			timer.resume();
		
		board.findAllLegalMoves();
		thinking = false;
	}
	
	
	public void moveHandler(Move m){
		if (isPromoting)
			return;
		
		if (board.isLegal(m) && board.isCheckLegal(m))	{
			
			if (m.promotion){
				tempMove = new Move(m.from,m.to);
				messageLbl.setText("Enter 'Q','R','B', or 'N'");
				messageLbl.repaint();
				isPromoting = true;
			}
			
			else			{
				board.makeMove(m);
				readyNextMove();				
			}
		}
		else messageLbl.setText(m.getMessage());
	}
	
	@SuppressWarnings("deprecation")
	public void readyNextMove()	{
		board.findAllLegalMoves();
	
		messageLbl.setSize(120,20);
		canvas.repaint(50);
		if (board.getAllLegalMoves().isEmpty())		{
			if (board.isInCheck(board.getTurn()))
				messageLbl.setText("Checkmate!");
			else
				messageLbl.setText("Stalemate!");
			timer.suspend();
		}
		else if (board.isInCheck(board.getTurn()))
			messageLbl.setText("Check");
		else if (board.getTurn())
			messageLbl.setText("white's move...");
		else
			messageLbl.setText("black's move...");
	}
	
	public ChessBoard getBoard(){
		return board;
	}
	
	public void takeBack(){
		board.takeBackMove();
		readyNextMove();
	}
	
	public void promotionHandler(char c){
		if (isPromoting){	
			messageLbl.setText("");
			isPromoting = false;
			
			tempMove.promoteTo = c;
			tempMove.promotion = true;
			
			board.makeMove(tempMove);
			readyNextMove();
		}
			
	}
	
	public boolean isHuman(boolean t){
		if (t)
			return (whiteCh.getSelectedItem().equals("Human"));
		else
			return (blackCh.getSelectedItem().equals("Human"));
	}
	
	public void timerHandler(){
		boolean t = board.getTurn();
		
		if (!isHuman(t) && !thinking)	{
			thinking = true;
			playAI();
			thinking = false;
		}
			
	}
	@SuppressWarnings("deprecation")
	public void choiceHandler(){
		if (isHuman(WHITE) && isHuman(BLACK))
			timer.suspend();
		else
			timer.resume();
	}
	
	private void playAI(){
		@SuppressWarnings("rawtypes")
		Vector v;
		/* Random Play */
		if ((board.getTurn() && whiteCh.getSelectedItem().equals("Random"))
		 || (!board.getTurn()&& blackCh.getSelectedItem().equals("Random")) )
     			v = board.getAllLegalMoves();
						

		else if (board.getTurn() && whiteCh.getSelectedIndex() > 1)
			v = board.getBestMoves(whiteCh.getSelectedIndex() - 2);
		else if (!board.getTurn() && blackCh.getSelectedIndex() > 1) 
			v = board.getBestMoves(blackCh.getSelectedIndex()-2);
		
		else
			return;
			
		Move m = (Move) v.elementAt((int)(Math.random()*v.size()));
	
		board.makeMove(m);
		readyNextMove();

	}

}

class PromotionListener implements KeyListener{
	Chess parent;
	
	public PromotionListener(Chess c){
		parent = c;
		
	}
		
	public void keyTyped(KeyEvent e){
		char c = Character.toUpperCase(e.getKeyChar());
		
		parent.messageLbl.setText("typed: " + c);
		if (c=='Q' || c=='N' || c=='R' || c=='B')									   
			parent.promotionHandler(c);
	}
	
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
}


class NewGameListener implements ActionListener{
	Chess parent;
	
	public NewGameListener(Chess c)	{
		parent = c;
		
	}
	
	public void actionPerformed(ActionEvent e)	{
		parent.newGame();
	}

}

class TakeBackListener implements ActionListener{
	Chess parent;
	
	public TakeBackListener(Chess c)	{
		parent = c;
	}
	
	public void actionPerformed(ActionEvent e)	{
		parent.takeBack();		
	}
}							

@SuppressWarnings("serial")
class PromotionFrame extends Frame{
	public PromotionFrame(String s)	{
		super(s);
	}
	
	public void paint(Graphics g)	{
		g.setColor(Color.black);
		g.drawString("Type 'Q', 'R', 'B', 'N'",25,25);
	}
}

class AITimer extends Thread{
	private Chess parent;
	
	public AITimer(Chess c)	{
		parent = c;
	}
	
	public void run()	{
		while(true)		{
			try	{
				sleep(2000);
			}
		
			catch(InterruptedException e)	{
				parent.showStatus(e.toString());
			}
		
			finally	{
				parent.timerHandler();
			}
		}
	}
}

class PlayerChoiceListener implements ItemListener{
	private Chess parent;
	
	public PlayerChoiceListener(Chess c){
		parent = c;
	}
	
	public void itemStateChanged(ItemEvent e){
		parent.choiceHandler();
	}
}