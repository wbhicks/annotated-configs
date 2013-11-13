/*
 * Created on Jun 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

/**
 * @author wbhicks
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class DotValue {

    private CoordTuple coords;
    private int radius;
    private String name;
    private String color;
    private java.util.TreeMap neighbors;
    
	public DotValue(String name, CoordTuple coords, int radius, String color) {
        this.name = name;
        this.coords = coords;
		this.radius = radius;
		this.color = color;
        neighbors = new java.util.TreeMap();
	}
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
    public CoordTuple getCoords() {
        return coords;
    }
    public void setCoords(CoordTuple coords) {
        this.coords = coords;
    }
	public java.util.TreeMap getNeighbors() {
		return neighbors;
	}
	public void setNeighbors(java.util.TreeMap neighbors) {
		this.neighbors = neighbors;
	}
}
