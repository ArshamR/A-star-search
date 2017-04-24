package connectWise;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

public class Main {
	
	final static String DESK = "-";
	final static String WALL = "X";
	final static String COFFEE = "C";
	
	public static void main(String[] args) {
		int numRows = 3;
		int numColumns = 4;
		Tuple<Integer, Integer> desk = new Tuple<>(1,0);
		List<Tuple<Integer,Integer>> coffeeLocations = new ArrayList<>();
		List<Tuple<Integer,Integer>> walls = new ArrayList<>();
		
		coffeeLocations.add(new Tuple<>(0,2));
		coffeeLocations.add(new Tuple<>(2,1));
		
		walls.add(new Tuple<>(1,1));
		walls.add(new Tuple<>(1,2));
		walls.add(new Tuple<>(2,0));
		
		System.out.println(DistanceToCoffee(numRows,numColumns,desk,coffeeLocations,walls));
	}
	
	//Given a starting location this function finds the shortest path to all coffee machines and returns the shortest one
	public static int DistanceToCoffee(int numRows, int numColumns, 
					  Tuple<Integer,Integer> deskLocation, List<Tuple<Integer,Integer>> 
					  coffeeLocations, List<Tuple<Integer,Integer>> walls)
	{
		int min = Integer.MAX_VALUE;
		OfficeTile[][] office = initOffice(numRows,numColumns, walls, coffeeLocations);
		office = setHeuristic(office,coffeeLocations.get(0));
		
		//For each coffee machine find the shortest path
		for(Tuple<Integer,Integer> e : coffeeLocations){
			int dstX = (int) e.getX();
			int dstY = (int) e.getY();
			boolean found = false;
			
			//Stores the unexplored nodes and sorts them based on their f values
			PriorityQueue<OfficeTile> frontier = new PriorityQueue<OfficeTile>(1,new Comparator<OfficeTile>(){
				public int compare(OfficeTile t1, OfficeTile t2){
					if(t1.getF() < t2.getF()){
						return -1;
					}
					if(t1.getF() > t2.getF()){
						return 1;
					}
					return 0;
				}
			});
			HashSet<OfficeTile> exploredSet = new HashSet<>();
			
			//Start node
			OfficeTile startDesk = office[deskLocation.getX()][deskLocation.getY()];
			startDesk.setG(0);
			startDesk.setF(startDesk.getH());
			frontier.add(startDesk);
			
			Tuple<Integer,Integer> start = startDesk.getLocation();
			int startX = start.getX();
			int startY = start.getY();
			
			while(!frontier.isEmpty() && !found){
				OfficeTile temp = frontier.poll();
				
				
				if(temp == office[dstX][dstY]){
					min = tracePath(temp, startX, startY, min);
					found = true;
				}
				else{
					exploredSet.add(temp);
					checkNeighbor(exploredSet, frontier, temp, office);
				}
			}
		}
		return min;									
	}
	
	//Once goal has been found trace path back to source and determine path cost
	public static int tracePath(OfficeTile goal, int startX, int startY, int min){
		Tuple<Integer,Integer> goalLocation = goal.getLocation();
		int count = 0;
		
		int tileX = goalLocation.getX();
		int tileY = goalLocation.getY();
		OfficeTile parent;
		parent = goal.getParent();
		
		while(!(tileX == startX && tileY == startY)){
			count++;
			tileX = parent.getLocation().getX();
			tileY = parent.getLocation().getY();
			parent = parent.getParent();
		}
		if(count < min){
			min = count;
		}
		return min;
		
	}
	
	//Check neighbors and add to frontier if they aren't walls haven't been explored and are within bounds.
	public static PriorityQueue<OfficeTile> checkNeighbor(HashSet<OfficeTile> exploredSet, PriorityQueue<OfficeTile> frontier, OfficeTile space, OfficeTile[][] office){
		Tuple<Integer,Integer> tempLocation = space.getLocation();
		int tempX = tempLocation.getX();
		int tempY = tempLocation.getY();
		
		if(tempX + 1 < office.length){
			if(!office[tempX+1][tempY].getType().equals(WALL)){
				if(!exploredSet.contains(office[tempX+1][tempY])){
					office[tempX+1][tempY].setParent(space);
					office[tempX+1][tempY].updateG();
					office[tempX+1][tempY].calculateF();
					frontier.add(office[tempX+1][tempY]);
				}
			}
			
		}
		if(tempX -1 >= 0){
			if(!office[tempX-1][tempY].getType().equals(WALL)){
				if(!exploredSet.contains(office[tempX-1][tempY])){
					office[tempX-1][tempY].setParent(space);
					office[tempX-1][tempY].updateG();
					office[tempX-1][tempY].calculateF();
					frontier.add(office[tempX-1][tempY]);	
				}
			}
		}
		if(tempY+1 < office[0].length){
			if(!office[tempX][tempY+1].getType().equals(WALL)){
				if(!exploredSet.contains(office[tempX][tempY+1])){
					office[tempX][tempY+1].setParent(space);
					office[tempX][tempY+1].updateG();
					office[tempX][tempY+1].calculateF();
					frontier.add(office[tempX][tempY+1]);	
				}
			}
		}
		if(tempY-1 >= 0){
			if(!office[tempX][tempY-1].getType().equals(WALL)){
				if(!exploredSet.contains(office[tempX][tempY-1])){
					office[tempX][tempY-1].setParent(space);
					office[tempX][tempY-1].updateG();
					office[tempX][tempY-1].calculateF();
					frontier.add(office[tempX][tempY-1]);	
				}
			}
		}
		return frontier;		
	}
	
	//The heuristic is the straight line distance to the goal.
	//This ensures f(n) = g(n) + h(n) never overestimates the actual cost
	//i.e the heuristic is admissible 
	public static OfficeTile[][] setHeuristic(OfficeTile[][] office,Tuple<Integer,Integer> coffeeLocation){
		
		int dstX = coffeeLocation.getX();
		int dstY = coffeeLocation.getY();
		for(int i=0;i<office.length;i++){
			for(int j=0;j<office[0].length;j++){
				if(office[i][j].getType() != WALL){
				office[i][j].setH(distance(i, j, dstX, dstY)); //Straight line distance to goal. 
				}
			}
		}
		
		return office;
	}
	
	public static double distance(double x1, double y1, double x2, double y2){
		double result =0;
		
		result = Math.sqrt(Math.pow((x2-x1), 2) + Math.pow((y2-y1), 2));
		
		return result;
	}
	
	//Creates the grid and sets the tiles to -, W or C depending of the given locations
	@SuppressWarnings("rawtypes")
	public static OfficeTile[][] initOffice(int numRows, int numColumns,List<Tuple<Integer,Integer>> walls, List<Tuple<Integer,Integer>> coffeeLocations){
		OfficeTile[][] office = new OfficeTile[numRows][numColumns];
		
		boolean set = false;
		for(int i =0;i<numRows;i++){
			for(int j=0;j<numColumns;j++){
				office[i][j] = new OfficeTile(DESK);
				office[i][j].setLocation(i, j);
				
				for(Tuple e : walls){
					int tempX = (int) e.getX();
					int tempY = (int) e.getY();
					if(i == tempX && j == tempY){
						office[i][j].setType(WALL);
						set = true;
					}
				}
				//Only enter if it isn't a wall
				if(!set){
					for(Tuple e : coffeeLocations){
						int tempX = (int) e.getX();
						int tempY = (int) e.getY();
						if(i == tempX && j == tempY){
							office[i][j].setType(COFFEE);
						}
					}
				}
				set = false;
			}
		}
		return office;
	}
}