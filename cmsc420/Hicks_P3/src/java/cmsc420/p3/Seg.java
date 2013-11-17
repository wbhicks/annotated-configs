package cmsc420.p3;

import cmsc420.exceptions.BadCodeException;

/**
 * @author wbhicks
 * 
 * @version
 */
public class Seg implements Comparable {

private Dot theLesserDot, theGreaterDot;

// used in overlapsRect, liesBetween, distance
private java.awt.geom.Line2D.Float asL2DF;

public Seg( Dot tweedleDum, Dot tweedleDee ) throws BadCodeException {
if ( (null != tweedleDum) && (null != tweedleDee) ) {
	if ( (tweedleDum.compareByCoords( tweedleDee )) <= 0 ) {
		theLesserDot = tweedleDum;
		theGreaterDot = tweedleDee;
	} else {
		theLesserDot = tweedleDee;
		theGreaterDot = tweedleDum;
	}
	asL2DF = new java.awt.geom.Line2D.Float( theLesserDot.getX().coord, theLesserDot.getY().coord,
					theGreaterDot.getX().coord, theGreaterDot.getY().coord );
} else {
	throw new BadCodeException();
}
}

public Dot getLesser() {
return theLesserDot;
}

public Dot getGreater() {
return theGreaterDot;
}

public int compareTo( Object o ) throws ClassCastException {
if ( o instanceof Seg ) {
	Seg otherSeg = (Seg) o;
	/*
	 * By the spec, we must know which of my Dots is the
	 * lesser, and so too for the other Seg: "... the
	 * endpoints of each segment should be sorted in
	 * increasing coordinate order ..."
	 */
	if ( getLesser().compareByCoords( otherSeg.getLesser() ) != 0 ) {
		return getLesser().compareByCoords( otherSeg.getLesser() );
	} else if ( getGreater().compareByCoords( otherSeg.getGreater() ) != 0 ) {
		return getGreater().compareByCoords( otherSeg.getGreater() );
	} else {
		/*
		 * the 2 Segs overlap perfectly, thus this is a
		 * reliable basis for the sibling method equals()
		 */
		return 0;
	}
} else {
	throw new ClassCastException();
}
}

public boolean equals( Object o ) throws ClassCastException {
return (0 == compareTo( o ));
}

public double distance( int x, int y ) {
    return asL2DF.ptSegDist( x, y );
}

/**
 * Pass it the coords of the endpoints of a line; it says if that line intersects this one.<p>
 * <p>
 * It's not vital this method spot all intersections, because if we chose to not call it at all,
 * we'd still find that we get a MaxDepthException whenever an impermissible (intersecting) seg is
 * added. This method exists to save time when an obviously impossible intersection is being
 * considered.<p>
 * <p>
 * It catches the problem when a self-loop would lie on an existing seg (e.g. 5,5--5,5 on top of
 * 3,6--9,3) or vice-versa, but it does NOT work correctly when two self-loops are compared (e.g.
 * 2,7--2,7 and 5,1--5,1) - it always returns true in such cases. (This is because Sun's 
 * intersectsLine, on which it depends, has that bug/feature.) DotWorld's intersectsAny, which calls
 * liesBetween, corrects for this problem.
 * @param axCoord
 * @param ayCoord
 * @param bxCoord
 * @param byCoord
 * @return <code>true</code> iff this line and the line passed intersect
 */
public boolean liesBetween( Coord axCoord, Coord ayCoord, Coord bxCoord, Coord byCoord ) {
int ax = axCoord.coord;
int ay = ayCoord.coord;
int bx = bxCoord.coord;
int by = byCoord.coord;
return asL2DF.intersectsLine( ax, ay, bx, by );
}

public boolean overlapsRect( Coord lxCoord, Coord lyCoord, Coord uxCoord, Coord uyCoord ) {

int lx = lxCoord.coord;
int ly = lyCoord.coord;
int ux = uxCoord.coord;
int uy = uyCoord.coord;

java.awt.Rectangle rect = new java.awt.Rectangle( lx, ly, (ux - lx), (uy - ly) );

/*
 * Unfortunately, this tests only to see if it intersects
 * the _interior_ of the Rectangle - but it's a 1st step.
 */
if ( asL2DF.intersects( rect ) ) {
	return true;
	/*
	 * next 4 steps: see if it intersects any of the
	 * boundary lines. Unknown: what if it intersects a
	 * corner only -- will that be detected? TODO
	 */
} else if ( asL2DF.intersectsLine( lx, ly, ux, ly ) ) {
	return true;
} else if ( asL2DF.intersectsLine( ux, ly, ux, uy ) ) {
	return true;
} else if ( asL2DF.intersectsLine( ux, uy, lx, uy ) ) {
	return true;
} else if ( asL2DF.intersectsLine( lx, uy, lx, ly ) ) {
	return true;
} else {
	return false;
}
}

public String toString() {

    return "(" + theLesserDot.getName().getName() + "," 
    + theGreaterDot.getName().getName() + ")";   
}

} // end class
