package cmsc420.p3;


/**
 * @author wbhicks
 *
 * @version 
 */
public class Coord {
    
public int coord;

public Coord( int i ) {
	this.coord = i;
}

public boolean equals( Coord other ) {
    if (null == other) {
        return false;
    } else {
        return ( this.coord == other.coord );
    }
}
}
