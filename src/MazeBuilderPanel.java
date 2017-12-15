import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MazeBuilderPanel extends JPanel implements MazeConstants, MouseListener, ActionListener
{
	private MazeCell[][] theGrid;
	private int selectionMode;
	private JLabel statusLabel;
	private Place startLoc;
	private Place endLoc;
	private Stack<Place> optimal;
	private Stack<Place> mazebuilder;
	private Stack<Place> mainStack;
	
	public MazeBuilderPanel()
	{
		super();
		theGrid = new MazeCell[NUM_ROWS][NUM_COLS];
		for (int r=0; r<NUM_ROWS; r++)
			for (int c=0; c<NUM_COLS; c++)
				theGrid[r][c] = new MazeCell(r,c);
		startLoc = new Place(1,1);
		theGrid[1][1].setStart(true);
		endLoc = new Place(NUM_ROWS-2,NUM_COLS-2);
		theGrid[NUM_ROWS-2][NUM_COLS-2].setEnd(true);
		addMouseListener(this);
		optimal = new Stack<Place>();
		selectionMode = END_MODE;
		mazebuilder= new Stack<Place>();
		mainStack = new Stack<Place>();
	}
	
	public void actionPerformed(ActionEvent aEvt)
	{
		System.out.println("action.");
		repaint();
	}
	
	
	/**
	 * determines whether the given place location is within the maze, but not on one of the edges.
	 *        0123456
	 *       +-------+
	 *    0  |       |
	 *    1  | XXXXX |
	 *    2  | XXXXX |
	 *    3  | XXXXX |
	 *    4  | XXXXX |
	 *    5  |       |
	 *       +-------+
	 * @param p - a place to consider.
	 * @return whether the place corresponds to one of the "X" locations above
	 */
	public boolean inBounds(Place p)
	{
		boolean isInBounds = false;
		//TODO: Insert your code here.
		if(p.column() >0 && p.column() < NUM_COLS-1  && p.row()> 0 && p.row()< NUM_ROWS-1   )
			isInBounds = true;
		
		//---------------------------
		return isInBounds;
		
	}
	
	/**
	 * gets the "MazeCell" item in theGrid in the given location
	 * @param p - the location of the cell we want to get.
	 * @return - the MazeCell at this location.
	 */
	public MazeCell cellAt(Place p)
	{
		MazeCell mc =  theGrid[p.row()][p.column()];
		// TODO: Insert your code here.
	    
		//----------------------------
	    return mc;
	}
	
	
	/**
	 * selects one Place from the list, "choices", at random, removes it from the list and returns it.
	 * @param choices - a list of Places.
	 * @return one of the items on the list of places; if the list was initially empty, returns null.
	 * postcondition: if the list had any items, it now has one fewer item than before - the item removed
	 *                 from this list is what is returned.
	 */
	public Place pickPlaceOffList(List<Place> choices)
	{
		
		Place chosenItem = null;
		// TODO: Insert your code here.
		int cho = (int)(Math.random()*(choices.size()));
		if (choices.size()> 0){
		chosenItem = choices.get(cho);
		choices.remove(cho);
		}
		
			
		//-----------------------------
		return chosenItem;
	}
	
	
	/**
	 * returns a list of the in-bounds places immediately next to the given place where there are cells with
	 * the given SOLID/HOLLOW state.
	 * @param p - the center of the neighborhood under consideration
	 * @param state - which type of blocks (SOLID/HOLLOW) that we want to collect.
	 * @return - a list of places that a) are in-bounds, b) have the given state, and c)  are neighbors of "p."
	 */
	public List<Place> getNeighborsOfState(Place p, int state)
	{
		ArrayList<Place> result = new ArrayList<Place>();
		// TODO: Insert your code here.
		if(p.north().isNeighbor(p)== true && inBounds(p.north()) == true && cellAt(p.north()).getStatus() == state)
			result.add(p.north());
		if(p.south().isNeighbor(p)== true && inBounds(p.south()) == true && cellAt(p.south()).getStatus() == state)
			result.add(p.south());
		if(p.east().isNeighbor(p)== true && inBounds(p.east()) == true && cellAt(p.east()).getStatus() == state)
			result.add(p.east());
		if(p.west().isNeighbor(p)== true && inBounds(p.west()) == true && cellAt(p.west()).getStatus() == state)
			result.add(p.west());
		
		
		//------------------------------------------------
		return result;
	}
	

	/**
	 * fills in the entire maze with solid rock. Typically done before generating a whole new maze.
	 * 
	 */
	public void resetMazeToSolid()
	{
		for (int r=0; r<NUM_ROWS; r++)
			for (int c=0; c<NUM_COLS; c++)
				theGrid[r][c].setStatus(SOLID);
		resetSolveStates();
		repaint();
		setStatus("Maze refilled.");
		
		
	}
	
	/**
	 * resets all the "solve" states from the last time we solved the maze.
	 */
	public void resetSolveStates()
	{
		for (int r=0;r<NUM_ROWS; r++)
			for (int c=0; c<NUM_COLS; c++)
			{
				theGrid[r][c].setPopped(false);
				theGrid[r][c].setPushed(false);
			}
		optimal.clear();
		mainStack.clear();
		repaint();
	}
	
	
	
	
	/**
	 * clears the entire maze and reconstructs it. 
	 */
	public void doRebuild()
	{
		resetMazeToSolid();
		// TODO: insert your code here.....
		mazebuilder.push(startLoc);
		
		while(!mazebuilder.isEmpty())
		{
			Place topStack = (mazebuilder.pop());
		
			
			List<Place> solidneighbors=getNeighborsOfState(topStack, SOLID);
			List<Place> hollowneighbors = getNeighborsOfState(topStack, HOLLOW);
			if(hollowneighbors.size()<2){
				cellAt(topStack).setStatus(HOLLOW);
				while(solidneighbors.size()!=0){
					Place pickedPlace = pickPlaceOffList(solidneighbors);
				
					mazebuilder.push(pickedPlace);
				}
			}
		
				
		}
		
		
		//-------------------------------
		setStatus("Maze rebuilt.");
	}
	


	/**
	 * attempts to find a path from the start location to the end location.
	 */
	public void doSolve()
	{
		resetSolveStates();
		setStatus("Searching maze");
		// TODO: insert your code here.
		mainStack.push(startLoc);
		optimal.push(startLoc);
		cellAt(startLoc).setPushed(true);
		while(cellAt(endLoc).isPopped()==false){
			Place topStack= mainStack.pop(); 
			cellAt(topStack).setPopped(true);
//			else 
////				topStack = optimal.peek();
			//cellAt(topStack).setPopped(true);
			
			List<Place> hollowNeighbors = getNeighborsOfState(topStack, HOLLOW);	
			for(int i = 0; i< hollowNeighbors.size(); i++)
			{
				if(cellAt(hollowNeighbors.get(i)).isPushed()!=true && cellAt(hollowNeighbors.get(i)).isPopped() !=true){
			
			
				mainStack.push(hollowNeighbors.get(i));
				
				cellAt(hollowNeighbors.get(i)).setPushed(true);
				
				}
			}
			
			
				while(!optimal.isEmpty()&&optimal.peek().isNeighbor(topStack) == false ){
					optimal.pop();
				}
			
					optimal.push(topStack);
					
				
			
//			boolean optimalNeighbor = false;
//			while(optimalNeighbor = false)
//			{
//				if(!optimal.isEmpty()){
//				if(topStack.isNeighbor(optimal.peek())==false)
//					optimal.pop();
//				else
//					optimalNeighbor = true;
//				}
//				else
//					optimalNeighbor = true;
//	
//			}
//			if(!optimal.isEmpty()&&topStack.isNeighbor(optimal.peek()) == true)
//			{optimal.push(topStack);
//			
//			System.out.println(optimal.peek().toString());
//			}
//			System.out.println(topStack.toString()+cellAt(topStack).toString());

		}
		
		//--------------------------------------
		setStatus("Yay we got it!"); // you may want to add a happier message elsewhere in your code!
		return;
	}
	
	/**
	 * draws the maze in the main part of the window.
	 */
	public void paintComponent(Graphics g)
	{
		System.out.println("painting.");
		for (int r=0; r<NUM_ROWS; r++)
			for (int c=0; c<NUM_COLS; c++)
			{
				theGrid[r][c].drawSelf(g);
				
				// ---------------- Only used in part D ---------------------
				// draws a red circle in all "optimal" cells.
				if (optimal.contains(new Place(r,c)))
				{
					g.setColor(Color.RED);
					g.drawOval(c*CELL_SIZE+CELL_SIZE/2-OPTIMAL_PATH_CIRCLE_RADIUS, 
							   r*CELL_SIZE+CELL_SIZE/2-OPTIMAL_PATH_CIRCLE_RADIUS, 
							   2*OPTIMAL_PATH_CIRCLE_RADIUS,
							   2*OPTIMAL_PATH_CIRCLE_RADIUS);
				}
				// ----------------------------------------------------------
			}
	}

	
// ---------------------------   GUI stuff  ---------------------------------------------
	/**
	 * used by the frame to tell this class about the status label. You shouldn't need to mess with this.
	 * @param lab
	 */
	public void attachStatusLabel(JLabel lab)
	{
		statusLabel = lab;
	}
	/**
	 * Allows the program to display a short status message in the bottom of the window.
	 * @param stat
	 */
	public void setStatus(String stat)
	{
		statusLabel.setText(stat);
	}
	
	/**
	 * accessor/modifier for the selectionMode. If this is in START_MODE, then clicking in the maze should reset the
	 * starting location in the maze. If this is in END_MODE, then clicking in the maze should reset the ending
	 * location in the maze.
	 * @return
	 */
	public int getSelectionMode()
	{
		return selectionMode;
	}
	public void setSelectionMode(int selectionMode)
	{
		System.out.println("MBP: setting selection mode: "+selectionMode);
		this.selectionMode = selectionMode;
	}
	
	// ---------------------------- used MouseListener methods -------------------------
	@Override
	public void mouseReleased(MouseEvent e)
	{
		System.out.println("dealing with mouse click in panel.");
		// TODO Auto-generated method stub
		int r = e.getY()/CELL_SIZE;
		int c = e.getX()/CELL_SIZE;
		Place clickedPlace = new Place(r,c);
		System.out.println(clickedPlace);
		if (! inBounds(clickedPlace))
		{
			setStatus("Invalid location");
			return; // this isn't eligible
		}
		if (selectionMode==START_MODE)
		{
			if (startLoc.equals(clickedPlace))
				return; // no change.
			theGrid[startLoc.row()][startLoc.column()].setStart(false);
			theGrid[clickedPlace.row()][clickedPlace.column()].setStart(true);
			startLoc = clickedPlace;
			setStatus("Start moved: "+startLoc);
			resetSolveStates();
			repaint();
		}
		else if (selectionMode==END_MODE)
		{
			if (endLoc.equals(clickedPlace))
				return; // no change.
			theGrid[endLoc.row()][endLoc.column()].setEnd(false);
			theGrid[clickedPlace.row()][clickedPlace.column()].setEnd(true);
			endLoc = clickedPlace;
			setStatus("End moved: "+endLoc);
			resetSolveStates();
			repaint();
		}
	}
	
	// ------------------------------------  unused MouseListener methods.-----------------------------
	@Override
	public void mouseClicked(MouseEvent e)
	{	}
	@Override
	public void mousePressed(MouseEvent e)
	{	}
	@Override
	public void mouseEntered(MouseEvent e)
	{ }
	@Override
	public void mouseExited(MouseEvent e)
	{ }
	
	

}
