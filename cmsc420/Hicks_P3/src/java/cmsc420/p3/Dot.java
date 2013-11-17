package cmsc420.p3;

import cmsc420.exceptions.BadCodeException;
import cmsc420.exceptions.DotColorFormatException;
import cmsc420.exceptions.DotNameFormatException;

/**
 * There should be no no-arg constructor, i.e. no "default"
 * Dot. Doesn't yet implement Comparable, because I don't
 * yet need to iterate any of the structures holding Dots in
 * any particular order.
 * 
 * @author wbhicks
 * 
 * @version
 */
public class Dot {

    private DotName name; 
    private Coord x;
    private Coord y;
    private Radius radius;
    private DotColor color; 
    private java.util.TreeMap adjacentsByName;
    private java.util.TreeMap segsByName;

    
public Dot( 
    DotName name, Coord x, Coord y, 
    Radius radius, DotColor color ) 
    throws DotNameFormatException, DotColorFormatException {
    
    super();
    this.setName( name );
    this.setX( x );
    this.setY( y );
    this.setRadius( radius );
    this.setColor( color );
    adjacentsByName = new java.util.TreeMap();
    segsByName = new java.util.TreeMap();
}

/**
 * @param name The name to set.
 */
private void setName( DotName name ) {
this.name = name;
}

/**
 * @return Returns the name.
 */
public DotName getName() {
return name;
}

/**
 * @param x The x to set.
 */
private void setX( Coord x ) {
this.x = x;
}

/**
 * @return Returns the x.
 */
public Coord getX() {
return x;
}

/**
 * @param y The y to set.
 */
private void setY( Coord y ) {
this.y = y;
}

/**
 * @return Returns the y.
 */
public Coord getY() {
return y;
}

/**
 * @param radius The radius to set.
 */
private void setRadius( Radius radius ) {
this.radius = radius;
}

/**
 * @return Returns the radius.
 */
public Radius getRadius() {
return radius;
}

/* This should be the only public setter, because color is 
 * the only attribute that should be changeable (by the spec)
 * 
 * @param color The color to set.
 */
public void setColor( DotColor color ) {
this.color = color;
}

/**
 * @return Returns the color.
 */
public DotColor getColor() {
return color;
}

public int compareByCoords( Dot other ) {
        // by the spec, x dominates
	if ( getX().coord != other.getX().coord ) {
	   return ( getX().coord - other.getX().coord );
    } else if ( getY().coord != other.getY().coord ) {
        return ( getY().coord - other.getY().coord );
    } else {
    	return 0; // same coords
    }
}

public int getNumOfAdjacentsAndSegs() throws BadCodeException {
    int n1 = adjacentsByName.size();
    int n2 = segsByName.size();
    if (( n1 != n2 ) || ( n1 < 0 )) {
        throw new BadCodeException( "this Dot's name=" + name.getName() 
        				+ ", adjacentsByName.size()=" + n1 
						+ ", segsByName.size()=" + n2 );
    } else {
    	return n1;
    }
}

public void addAdjacentAndSeg( Dot other, Seg theSeg ) throws BadCodeException {

if ( adjacentsByName.containsKey( other.name ) || segsByName.containsKey( other.name ) ) {
	throw new BadCodeException();
} else {
	adjacentsByName.put( other.name, other );
	segsByName.put( other.name, theSeg );
	return;
}
}    

/* @param dn a ref to the DotName of the to-be-disconnected neighbor
/* @return the number of Segs remaining, which == the number of Adjacents remaining
 */
public int removeAdjacentAndSeg( DotName dn ) throws BadCodeException {
    int before = getNumOfAdjacentsAndSegs();
    segsByName.remove( dn );
    adjacentsByName.remove( dn );
    int after = getNumOfAdjacentsAndSegs();
    if (( after != ( before - 1 )) || ( after < 0 )) {
        throw new BadCodeException( "before=" + before + ", after=" + after );
    } else {
    	return after;
    }
}

public boolean isAdjacentTo( DotName otherName ) {
    return ( adjacentsByName.containsKey( otherName ));
}    

public Seg getSegByName( DotName otherName ) throws BadCodeException {
    if (!( segsByName.containsKey( otherName ))) {
        throw new BadCodeException(); 
    } else {
    	return (Seg)( segsByName.get( otherName ));
    }
}

public DotName[] getAllAdjacentNames() {
    int numOfNames = adjacentsByName.size();
    DotName[] dna = new DotName[numOfNames];
    int dnaIndex = 0;
    java.util.Iterator iter = adjacentsByName.keySet().iterator();
    while ( iter.hasNext() ) {
        dna[dnaIndex++] = (DotName) iter.next();
    }
    return dna; 
}

public boolean overlapsRect( Coord lxCoord, Coord lyCoord, Coord uxCoord, Coord uyCoord ) {

int lx = lxCoord.coord;
int ly = lyCoord.coord;
int ux = uxCoord.coord;
int uy = uyCoord.coord;
java.awt.Rectangle rect = new java.awt.Rectangle( lx, ly, (ux - lx), (uy - ly) );
/*
 * Unfortunately, this (I think, from looking at Sun's source code) tests only to see if it intersects
 * the _interior_ of the Rectangle - but it's a 1st step.
 */
if ( rect.contains( x.coord, y.coord ) ) {
	return true;
	/*
	 * next 4 steps: see if it intersects any of the
	 * boundary lines.
	 */
} else if ( ( x.coord == lx || x.coord == ux ) && y.coord >= ly && y.coord <= uy ) {
	return true;
} else if ( ( y.coord == ly || y.coord == uy ) && x.coord >= lx && x.coord <= ux ) {
	return true;
} else {
	return false;
}
}


public String toString() {
	return this.getName().getName() + " at (" + this.getX().coord + ","
        + this.getY().coord + ") color:" + this.getColor().getColor();
}

}
















