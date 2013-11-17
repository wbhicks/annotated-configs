package cmsc420.p3;

/**
 * For now, hold a pair of Coords. Used for the keys in one
 * of the Dot maps.
 * 
 * @author wbhicks
 * 
 * @version
 */
public class CoordTuple implements Comparable {

private int x, y;

public CoordTuple( Coord xCoord, Coord yCoord ) {
super();
x = xCoord.coord;
y = yCoord.coord;
}

public int compareTo( Object o ) throws ClassCastException {
// by the spec, x dominates
if ( o instanceof CoordTuple ) {
	CoordTuple other = (CoordTuple) o;
	if ( x != other.x ) {
		return (x - other.x);
	} else if ( y != other.y ) {
		return (y - other.y);
	} else {
		return 0; // same coords
	}
} else {
	throw new ClassCastException();
}
}

public boolean equals( Object o ) {
return (0 == compareTo( o ));
}

}
