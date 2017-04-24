package connectWise;

public class OfficeTile {

	private String type;
	private double h;
	private double g;
	private double f;
	private Tuple<Integer,Integer> location;
	OfficeTile parent;
	
	public OfficeTile(String t){
		type =t;
		g = 0;
	}
	
	public void setLocation(int x, int y){
		location = new Tuple<Integer,Integer>(x,y);
	}
	
	public Tuple<Integer,Integer> getLocation(){
		return location;
	}
	
	public void setType(String val){
		type = val;
	}
	
	public String getType(){
		return type;
	}
	
	public void setH(double v){
		h = v;
	}
	
	public void setG(double v){
		g = v;
	}
	
	public void setF(double v){
		f =v;
	}
	
	public double getH(){
		return h;
	}
	public double getG(){
		return g;
	}
	public double getF(){
		return f;
	}
	
	public void setParent(OfficeTile p){
		parent = p;
	}
	public OfficeTile getParent(){
		return parent;
	}
	public void updateG(){
		//All step costs will be 1
		g = getParent().getG() + 1;
	
	}
	
	public void calculateF(){
		f = g + h;
		//System.out.print("\nF value : " + f );
	}
}
