package cmsc420.p3test;

import cmsc420.exceptions.BadCodeException;
import cmsc420.exceptions.DotColorFormatException;
import cmsc420.exceptions.DotNameFormatException;
import cmsc420.p3.Coord;
import cmsc420.p3.Dot;
import cmsc420.p3.DotColor;
import cmsc420.p3.DotName;
import cmsc420.p3.Radius;
import cmsc420.p3.Seg;
import junit.framework.TestCase;

/**
 * @author wbhicks
 *
 * @version 
 */
public class SegTest extends TestCase {

/*
 * @see TestCase#setUp()
 */
protected void setUp() throws Exception {
	super.setUp();
}

/*
 * @see TestCase#tearDown()
 */
protected void tearDown() throws Exception {
	super.tearDown();
}

public final void testSeg() {
//TODO Implement Seg().
}

public final void testGetLesser() {
//TODO Implement getLesser().
}

public final void testGetGreater() {
//TODO Implement getGreater().
}

public final void testCompareTo() {
//TODO Implement compareTo().
}

/*
 * Class under test for boolean equals(Object)
 */
public final void testEqualsObject() {
//TODO Implement equals().
}

public final void testDistance() {
//TODO Implement distance().
}

/** 
 * It's not vital this method spot all intersections, because if we chose to not call it at all, we'd still
 * find that we get a MaxDepthException whenever an impermissible (intersecting) seg is added. This method 
 * exists to save time when an obviously impossible intersection is being considered. E.g. it may not catch
 * the problem when a self-loop would lie on an existing seg. (e.g. 5,5--5,5 on top of 3,6--9,3) But it 
 * does seem to catch them!
 */
public final void testLiesBetween() {
	
		/* Stage 1
		 * 
		 * I'd like to catch self-loops that fall upon a diagonal seg - hmmm, I can!
		 */
	
	Coord xA = new Coord(3); 		Coord yA = new Coord(6);
	Coord xB = new Coord(9); 		Coord yB = new Coord(3);
	Coord xC = new Coord(5); 		Coord yC = new Coord(5); // on the seg
	Coord xD = new Coord(3); 		Coord yD = new Coord(6); // on top of A
	Coord xE = new Coord(5); 		Coord yE = new Coord(4); // not on the seg
	Coord xF = new Coord(11); 		Coord yF = new Coord(2); // not on the seg, but aligned with it
	try {
		Dot dA = new Dot( new DotName( "A" ), xA, yA, new Radius(1), new DotColor( "RED" ));
		Dot dB = new Dot( new DotName( "B" ), xB, yB, new Radius(1), new DotColor( "RED" ));
		// no need to actually create the Dots where the self-loops are imagined to be
		// Dot dC = new Dot( new DotName( "C" ), xC, yC, new Radius(1), new DotColor( "RED" ));
		Seg s1 = new Seg( dA, dB );
		assertTrue( s1.liesBetween( xC, yC, xC, yC ));
		assertTrue( s1.liesBetween( xD, yD, xD, yD ));
		assertFalse( s1.liesBetween( xE, yE, xE, yE ));
		assertFalse( s1.liesBetween( xF, yF, xF, yF ));
	} catch ( BadCodeException e ) { fail();		
	} catch ( DotNameFormatException e ) { fail();
	} catch ( DotColorFormatException e ) { fail(); }
}

public final void testOverlapsRect() {
//TODO Implement overlapsRect().
}

/*
 * Class under test for String toString()
 */
public final void testToString() {
//TODO Implement toString().
}

}
