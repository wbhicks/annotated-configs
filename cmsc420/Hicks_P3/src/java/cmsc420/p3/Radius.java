package cmsc420.p3;


/**
 * @author wbhicks
 *
 * @version 
 */
public class Radius {
    
public int radius;

public Radius( int i ) {
	this.radius = i;
}

public boolean equals( Radius other ) {
    if (null == other) {
        return false;
    } else {
        return ( this.radius == other.radius );
    }
}

}
