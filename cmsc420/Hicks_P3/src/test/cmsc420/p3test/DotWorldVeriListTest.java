package cmsc420.p3test;
import java.util.LinkedHashMap;

import cmsc420.exceptions.BadCodeException;
import cmsc420.exceptions.CoordDuplicateException;
import cmsc420.exceptions.DotColorFormatException;
import cmsc420.exceptions.DotNameDuplicateException;
import cmsc420.exceptions.DotNameFormatException;
import cmsc420.exceptions.IntersectionException;
import cmsc420.exceptions.NotFoundException;
import cmsc420.exceptions.QTDuplicateException;
import cmsc420.exceptions.RangeException;
import cmsc420.p3.Coord;
import cmsc420.p3.CoordTuple;
import cmsc420.p3.Dot;
import cmsc420.p3.DotColor;
import cmsc420.p3.DotName;
import cmsc420.p3.DotWorldVeriList;
import cmsc420.p3.QTNode;
import cmsc420.p3.Radius;
import cmsc420.p3.Seg;
import cmsc420.p3.SegList;
import junit.framework.TestCase;


/**
 * underway: mapseg + unmapsegment
 * 
 * @author wbhicks
 *
 * @version 
 */
public class DotWorldVeriListTest extends TestCase {

/** 
 * Generates and returns a DWVL holding an L-shaped graph, for use by unit tests (siblings). It happens to
 * use DWVL's IOC constructor to do this, but it could have used DWVL's no-arg constructor instead and built
 * the L-shaped graph by calling various DWVL methods (at the time, I didn't trust those methods.)
 * 
 * @return an L-shaped graph
 * @throws DotColorFormatException
 * @throws DotNameFormatException
 */
private DotWorldVeriList prefabDWVLOf3Dots() throws DotColorFormatException, DotNameFormatException {

	/*
	 * STAGE 1: set up the dwvl. Origin (0,0) is always at lower left (see QTNode).
	 * 
	 * A 
	 * | 
	 * B-C
	 * 
	 * We want A to be farther from the left than C is from the right, and we want C to be farther
	 * from the bottom than A is from the top. This is to ensure that the missing (A,C) segment,
	 * when we finally add it (in another unit test, recycling this code), slices cleanly through
	 * the NE quadrant.
	 */

	Coord xA = new Coord(5); 		Coord yA = new Coord(29);
	Coord xB = new Coord(5); 		Coord yB = new Coord(5);
	Coord xC = new Coord(29); 		Coord yC = new Coord(5);
	Dot dA = new Dot( new DotName( "A" ), xA, yA, new Radius(1), new DotColor( "RED" ));
	Dot dB = new Dot( new DotName( "B" ), xB, yB, new Radius(1), new DotColor( "RED" ));
	Dot dC = new Dot( new DotName( "C" ), xC, yC, new Radius(1), new DotColor( "RED" ));

		// make sure each dot has its adj and its seg
	
	try {   dA.addAdjacentAndSeg( dB, new Seg( dA, dB ));
			dB.addAdjacentAndSeg( dA, new Seg( dA, dB ));
			dB.addAdjacentAndSeg( dC, new Seg( dB, dC ));
			dC.addAdjacentAndSeg( dB, new Seg( dB, dC ));
	} catch ( BadCodeException e ) { fail( e.getMessage()); }

	try {	assertEquals( 1, dA.getNumOfAdjacentsAndSegs());
			assertEquals( 2, dB.getNumOfAdjacentsAndSegs());
			assertEquals( 1, dC.getNumOfAdjacentsAndSegs());
	} catch ( BadCodeException e1 ) { fail( e1.getMessage()); }

		// add the 3 dots to the objects, other than the QT, that will be passed to the dwvl constructor
	
	java.util.TreeMap tmToBeDotsByName = new java.util.TreeMap(); // by name, they're A, B, C
	tmToBeDotsByName.put( new DotName( "A" ), dA );
	tmToBeDotsByName.put( new DotName( "B" ), dB );
	tmToBeDotsByName.put( new DotName( "C" ), dC );
	java.util.TreeMap tmToBeDotsByXY = new java.util.TreeMap(); // by coord, they're B, A, C
	tmToBeDotsByXY.put( new CoordTuple( xB, yB ), dB );
	tmToBeDotsByXY.put( new CoordTuple( xA, yA ), dA );
	tmToBeDotsByXY.put( new CoordTuple( xC, yC ), dC );
	
		/*
		 * do the same with the segs. Seg constructor disregards the order of the args, but the
		 * TreeSet called segsOrderedBySpec must hold Segs in the correct order. My comments in the
		 * domain classes claim that any such TreeSet (i.e. one holding only Segs) "will do so
		 * automatically. Because it holds only Segs, it uses the natural ordering for Segs I
		 * defined via Seg.compareTo() - i.e. the ordering required by the spec"
		 * 
		 * Still, I'll add them explicitly in the correct order
		 */ 
		
	java.util.TreeSet tsToBeSegsOrderedBySpec = new java.util.TreeSet();
	try {	tsToBeSegsOrderedBySpec.add( new Seg( dA, dB )); // add this Seg first. but either order ok for the Dots
			tsToBeSegsOrderedBySpec.add( new Seg( dB, dC )); // add this Seg second. but either order ok for the Dots
	} catch ( BadCodeException surprise ) { fail(); }
	
		// prepare the 3 non-empty seglists the QT's leaves will need (NE's will be empty)
	
	SegList slForNWLeaf = new SegList();
	SegList slForSWLeaf = new SegList();
	SegList slForSELeaf = new SegList();
	
	try {
		slForNWLeaf.add( new Seg( dA, dB )); // only Seg in NW	
			// no Seg in NE
		slForSWLeaf.add( new Seg( dA, dB )); // in SW, add this Seg first
		slForSWLeaf.add( new Seg( dB, dC )); // in SW, add this Seg second
		slForSELeaf.add( new Seg( dB, dC )); // only Seg in SE
	} catch ( BadCodeException surprise ) {
		fail();
	}
	
		// prepare the 4 QT leaves
	
	/* COPIED from QTNode source:
	 * 
	 * root is                  lx 0,  ly 0,  ux 32, uy 32
	 * nw child of root will be lx 0,  ly 16, ux 16, uy 32
	 * ne child of root will be lx 16, ly 16, ux 32, uy 32
	 * sw child of root will be lx 0,  ly 0,  ux 16, uy 16
	 * se child of root will be lx 16, ly 0,  ux 32, uy 16
	 * 
	 * lx       ux
	 *  -------- <-- uy
	 * | --  -- |
	 * ||nw||ne||
	 * ||  ||  ||
	 * | --  -- |
	 * | --  -- |
	 * ||sw||se||
	 * ||  ||  ||
	 * | --  -- |
	 *  -------- <-- ly
	 * 
	 *  Thus, the picture agrees with "real-world" defs of nw, ne, sw, se
	 *  Cartesian Origin (0,0) is at lower left
	 */ 
	
	QTNode theNW = new QTNode( 0, 16, 16, 32, null, null, null, null, true, true, dA, slForNWLeaf );
	QTNode theNE = new QTNode( 16, 16, 32, 32, null, null, null, null, true, true, null, new SegList());
	QTNode theSW = new QTNode( 0, 0, 16, 16, null, null, null, null, true, true, dB, slForSWLeaf );
	QTNode theSE = new QTNode( 16, 0, 32, 16, null, null, null, null, true, true, dC, slForSELeaf );
	
		// construct the QT itself (i.e. the root). Again, we'll use a 32x32 grid
	
	// TODO the domain javadocs are screwy - does a non-leaf have an empty list, or null, for its segs?
	// here, I'll arbitrarily insist that non-leaves have a null ref for segs
//	QTNode theRoot = new QTNode( 0, 0, 32, 32, theNW, theNE, theSW, theSE, true, false, null, null );
	
	// failure a ways below! so come back here and make it an empty list TODO - does it matter?
	QTNode theRoot = new QTNode( 0, 0, 32, 32, theNW, theNE, theSW, theSE, true, false, null, new SegList());

	/* Last Stage
	 * 
	 * construct the DWVL using the IOC constructor
	 */
	
return new DotWorldVeriList( tmToBeDotsByName, tmToBeDotsByXY,
			tsToBeSegsOrderedBySpec, theRoot, 10, 10, 10, true, false );

}

/** 
 * Same approach as sibling, prefabDWVLOf3Dots(). Again happens to use DWVL's IOC constructor.
 * 
 * @return same L-shaped graph as prefabDWVLOf3Dots, plus extra dot in NE forming a quadrilateral
 * @throws DotColorFormatException
 * @throws DotNameFormatException
 */
private DotWorldVeriList prefabDWVLOf4Dots() throws DotColorFormatException, DotNameFormatException {

	/*
	 * STAGE 1: set up the dwvl. Origin (0,0) is always at lower left (see QTNode).
	 * 
	 * A-D
	 * | |
	 * B-C
	 * 
	 * We want A to be farther from the left than C is from the right, and we want C to be farther
	 * from the bottom than A is from the top. This is to ensure that the missing (A,C) segment,
	 * when we finally add it (in another unit test, recycling this code), slices cleanly through
	 * the NE quadrant and avoids the SW.
	 */

	Coord xA = new Coord(5); 		Coord yA = new Coord(29);
	Coord xB = new Coord(5); 		Coord yB = new Coord(5);
	Coord xC = new Coord(29); 		Coord yC = new Coord(5);
	Coord xD = new Coord(30); 		Coord yD = new Coord(30);
	Dot dA = new Dot( new DotName( "A" ), xA, yA, new Radius(1), new DotColor( "RED" ));
	Dot dB = new Dot( new DotName( "B" ), xB, yB, new Radius(1), new DotColor( "RED" ));
	Dot dC = new Dot( new DotName( "C" ), xC, yC, new Radius(1), new DotColor( "RED" ));
	Dot dD = new Dot( new DotName( "D" ), xD, yD, new Radius(1), new DotColor( "RED" ));

		// make sure each dot has its adj and its seg
	
	try {   dA.addAdjacentAndSeg( dB, new Seg( dA, dB ));
			dA.addAdjacentAndSeg( dD, new Seg( dA, dD ));
			dB.addAdjacentAndSeg( dA, new Seg( dA, dB ));
			dB.addAdjacentAndSeg( dC, new Seg( dB, dC ));
			dC.addAdjacentAndSeg( dB, new Seg( dB, dC ));
			dC.addAdjacentAndSeg( dD, new Seg( dC, dD ));
			dD.addAdjacentAndSeg( dA, new Seg( dA, dD ));
			dD.addAdjacentAndSeg( dC, new Seg( dC, dD ));
	} catch ( BadCodeException e ) { fail( e.getMessage()); }

	try {	assertEquals( 2, dA.getNumOfAdjacentsAndSegs());
			assertEquals( 2, dB.getNumOfAdjacentsAndSegs());
			assertEquals( 2, dC.getNumOfAdjacentsAndSegs());
			assertEquals( 2, dD.getNumOfAdjacentsAndSegs());
	} catch ( BadCodeException e1 ) { fail( e1.getMessage()); }

		// add the 4 dots to the objects, other than the QT, that will be passed to the dwvl constructor
	
	java.util.TreeMap tmToBeDotsByName = new java.util.TreeMap(); // by name, they're A, B, C, D
	tmToBeDotsByName.put( new DotName( "A" ), dA );
	tmToBeDotsByName.put( new DotName( "B" ), dB );
	tmToBeDotsByName.put( new DotName( "C" ), dC );
	tmToBeDotsByName.put( new DotName( "D" ), dD );
	java.util.TreeMap tmToBeDotsByXY = new java.util.TreeMap(); // by coord, they're B, A, C, D
	tmToBeDotsByXY.put( new CoordTuple( xB, yB ), dB );
	tmToBeDotsByXY.put( new CoordTuple( xA, yA ), dA );
	tmToBeDotsByXY.put( new CoordTuple( xC, yC ), dC );
	tmToBeDotsByXY.put( new CoordTuple( xD, yD ), dD );
	
		/*
		 * do the same with the segs. Seg constructor disregards the order of the args, but the
		 * TreeSet called segsOrderedBySpec must hold Segs in the correct order. My comments in the
		 * domain classes claim that any such TreeSet (i.e. one holding only Segs) "will do so
		 * automatically. Because it holds only Segs, it uses the natural ordering for Segs I
		 * defined via Seg.compareTo() - i.e. the ordering required by the spec"
		 * 
		 * Still, I'll add them explicitly in the correct order
		 */ 
		
	java.util.TreeSet tsToBeSegsOrderedBySpec = new java.util.TreeSet();
	try {	tsToBeSegsOrderedBySpec.add( new Seg( dA, dB )); // add this Seg first. but either order ok for the Dots
			tsToBeSegsOrderedBySpec.add( new Seg( dB, dC )); // add this Seg second. but either order ok for the Dots
			tsToBeSegsOrderedBySpec.add( new Seg( dA, dD ));
			tsToBeSegsOrderedBySpec.add( new Seg( dC, dD ));
	} catch ( BadCodeException surprise ) { fail(); }
	
	SegList slForNWLeaf = new SegList(); // prepare the 4 non-empty seglists the QT's leaves will need
	SegList slForNELeaf = new SegList();
	SegList slForSWLeaf = new SegList();
	SegList slForSELeaf = new SegList();
	
	try {	slForNWLeaf.add( new Seg( dA, dB )); // 1st
			slForNWLeaf.add( new Seg( dA, dD )); // 2nd
			slForNELeaf.add( new Seg( dA, dD )); // 1st
			slForNELeaf.add( new Seg( dC, dD )); // 2nd
			slForSWLeaf.add( new Seg( dA, dB )); // in SW, add this Seg first
			slForSWLeaf.add( new Seg( dB, dC )); // in SW, add this Seg second
			slForSELeaf.add( new Seg( dB, dC )); // 1st
			slForSELeaf.add( new Seg( dC, dD )); // 2nd
	} catch ( BadCodeException surprise ) { fail(); }
	
		// prepare the 4 QT leaves
	
	/* COPIED from QTNode source:
	 * 
	 * root is                  lx 0,  ly 0,  ux 32, uy 32
	 * nw child of root will be lx 0,  ly 16, ux 16, uy 32
	 * ne child of root will be lx 16, ly 16, ux 32, uy 32
	 * sw child of root will be lx 0,  ly 0,  ux 16, uy 16
	 * se child of root will be lx 16, ly 0,  ux 32, uy 16
	 * 
	 * lx       ux
	 *  -------- <-- uy
	 * | --  -- |
	 * ||nw||ne||
	 * ||  ||  ||
	 * | --  -- |
	 * | --  -- |
	 * ||sw||se||
	 * ||  ||  ||
	 * | --  -- |
	 *  -------- <-- ly
	 * 
	 *  Thus, the picture agrees with "real-world" defs of nw, ne, sw, se
	 *  Cartesian Origin (0,0) is at lower left
	 */ 
	
	QTNode theNW = new QTNode( 0, 16, 16, 32, null, null, null, null, true, true, dA, slForNWLeaf );
	QTNode theNE = new QTNode( 16, 16, 32, 32, null, null, null, null, true, true, dD, slForNELeaf);
	QTNode theSW = new QTNode( 0, 0, 16, 16, null, null, null, null, true, true, dB, slForSWLeaf );
	QTNode theSE = new QTNode( 16, 0, 32, 16, null, null, null, null, true, true, dC, slForSELeaf );
	
		// construct the QT itself (i.e. the root). Again, we'll use a 32x32 grid
	
	// TODO the domain javadocs are screwy - does a non-leaf have an empty list, or null, for its segs?
	// here, I'll arbitrarily insist that non-leaves have a null ref for segs
//	QTNode theRoot = new QTNode( 0, 0, 32, 32, theNW, theNE, theSW, theSE, true, false, null, null );
	
	// failure a ways below! so come back here and make it an empty list TODO - does it matter?
	QTNode theRoot = new QTNode( 0, 0, 32, 32, theNW, theNE, theSW, theSE, true, false, null, new SegList());

	/* Last Stage
	 * 
	 * construct the DWVL using the IOC constructor
	 */
	
return new DotWorldVeriList( tmToBeDotsByName, tmToBeDotsByXY,
			tsToBeSegsOrderedBySpec, theRoot, 10, 10, 10, true, false );

}

/** 
 * Unlike some sibling prefabs, this one doesn't use the DWVL IOC constructor. Instead it uses the no-arg
 * (default) constructor, then invokes DWVL methods to build the DWVL object into the desired shape. This is
 * because (1) handcrafting the objects to go into (via the IOC constructor) the fields of the DWVL object 
 * would be too much work, and (2) I now trust the DWVL methods I'll be invoking here.<p>
 * <p>
 * The QT balks at adjacent dots, even though createDot doesn't, so we'll avoid creating them.<p>
 * 
 * @return a DotWorldVeriList with 8 regions of dots-packed-in-a-grid, the 8 regions looking like the 
 * background regions of the Union Jack (UK flag). It has 960 Dots. 129 additional places where dots
 * could be are empty. (960+129=1089=33*33, there being 33 fenceposts from 0 to 64 inclusive, stepping by 2)
 * @throws DotColorFormatException
 * @throws DotNameFormatException
 * @throws CoordDuplicateException
 * @throws DotNameDuplicateException
 * @throws QTDuplicateException
 * @throws RangeException
 */
private DotWorldVeriList prefabDWVLUnionJack1() throws RangeException, QTDuplicateException, 
	DotNameDuplicateException, CoordDuplicateException, DotNameFormatException, DotColorFormatException {
	
	DotWorldVeriList dwvl = new DotWorldVeriList();
	dwvl.initQT( new cmsc420.p3.QTMagnitude( 6 )); // necessary, unless using a prefab. 64x64
	for (int x = 0; x <= 64; x += 2 ) {
		for (int y = 0; y <= 64; y += 2 ) {
			if (( x == y ) || ( x + y == 64 ) || ( 32 == x ) || ( 32 == y )) {
				// you're on one of the 8 spokes, so do nothing
			} else {
				dwvl.createDot( new DotName( "UJ" + x + "," + y ), 
					new Coord(x), new Coord(y), new Radius(1), new DotColor( "BLUE" ));
			}
		}
	}

	return dwvl;
}


private void quizPrefabDWVLOf3Dots( DotWorldVeriList dwvl2 ) {

	/*
	 * STAGE A: verify correctness of dwvl2's 2 dot-holding fields
	 */

	// confirm there are 3 dots there now
java.util.TreeMap dwvl2dbn = dwvl2.getDotsByName();
assertEquals( 3, dwvl2dbn.size());
java.util.TreeMap dwvl2dbxy = dwvl2.getDotsByXY();
assertEquals( 3, dwvl2dbxy.size());

java.util.Iterator itern = dwvl2dbn.values().iterator();
Dot dwvl2dbnDot1 = (Dot) itern.next();
Dot dwvl2dbnDot2 = (Dot) itern.next();
Dot dwvl2dbnDot3 = (Dot) itern.next();
java.util.Iterator iterxy = dwvl2dbxy.values().iterator();
Dot dwvl2dbxyDot1 = (Dot) iterxy.next();
Dot dwvl2dbxyDot2 = (Dot) iterxy.next();
Dot dwvl2dbxyDot3 = (Dot) iterxy.next();

assertEquals( "A", dwvl2dbnDot1.getName().getName());
assertEquals( "B", dwvl2dbnDot2.getName().getName());
assertEquals( "C", dwvl2dbnDot3.getName().getName());

assertEquals( "B", dwvl2dbxyDot1.getName().getName());
assertEquals( "A", dwvl2dbxyDot2.getName().getName());
assertEquals( "C", dwvl2dbxyDot3.getName().getName());

	/*
	 * STAGE B: verify correctness of dwvl2's segsOrderedBySpec field
	 */
	
		// confirm there are 2 segs there now
	java.util.TreeSet dwvl2sobs = dwvl2.getSegsOrderedBySpec();
	assertEquals( 2, dwvl2sobs.size());

	java.util.Iterator iter = dwvl2sobs.iterator();
	Seg dwvl2sobsSeg1 = (Seg) iter.next();
	Seg dwvl2sobsSeg2 = (Seg) iter.next();
	
	assertEquals( "B", dwvl2sobsSeg1.getLesser().getName().getName());
	assertEquals( "A", dwvl2sobsSeg1.getGreater().getName().getName());
	assertEquals( "B", dwvl2sobsSeg2.getLesser().getName().getName());
	assertEquals( "C", dwvl2sobsSeg2.getGreater().getName().getName());
	
	/*
	 * STAGE C: verify correctness of the segs and seg lists in dwvl2's QT _leaves_ 
	 * (ignore the question of whether the seg list of a non-leaf shall be null, or empty)
	 */
	
	QTNode dwvl2tqtr = dwvl2.getTheQTRoot();
	SegList dwvl2qtnwSegList = dwvl2tqtr.nw.segs;
	SegList dwvl2qtneSegList = dwvl2tqtr.ne.segs;
	SegList dwvl2qtswSegList = dwvl2tqtr.sw.segs;
	SegList dwvl2qtseSegList = dwvl2tqtr.se.segs;

	// confirm there is 1 seg in NW leaf
	assertEquals( 1, dwvl2qtnwSegList.size());
	// confirm there are no segs in NE leaf
	assertEquals( 0, dwvl2qtneSegList.size());
	// confirm there are 2 segs in SW leaf
	assertEquals( 2, dwvl2qtswSegList.size());
	// confirm there is 1 seg in SE leaf
	assertEquals( 1, dwvl2qtseSegList.size());

		// iterate in the NW
	java.util.Iterator iterNW = dwvl2qtnwSegList.iterator();
	Seg dwvl2qtnwSegListSeg1 = (Seg) iterNW.next();
	assertEquals( "B", dwvl2qtnwSegListSeg1.getLesser().getName().getName());
	assertEquals( "A", dwvl2qtnwSegListSeg1.getGreater().getName().getName());
	
		// iterate in the SW
	java.util.Iterator iterSW = dwvl2qtswSegList.iterator();
	Seg dwvl2qtswSegListSeg1 = (Seg) iterSW.next();
	Seg dwvl2qtswSegListSeg2 = (Seg) iterSW.next();
	assertEquals( "B", dwvl2qtswSegListSeg1.getLesser().getName().getName());
	assertEquals( "A", dwvl2qtswSegListSeg1.getGreater().getName().getName());
	assertEquals( "B", dwvl2qtswSegListSeg2.getLesser().getName().getName());
	assertEquals( "C", dwvl2qtswSegListSeg2.getGreater().getName().getName());

		// iterate in the SE
	java.util.Iterator iterSE = dwvl2qtseSegList.iterator();
	Seg dwvl2qtseSegListSeg1 = (Seg) iterSE.next();
	assertEquals( "B", dwvl2qtseSegListSeg1.getLesser().getName().getName());
	assertEquals( "C", dwvl2qtseSegListSeg1.getGreater().getName().getName());

	/*
	 * STAGE D: verify correctness of the Dots in dwvl2's QT _leaves_ 
	 */
	
	assertEquals( "A", dwvl2tqtr.nw.dot.getName().getName());
	assertEquals( null, dwvl2tqtr.ne.dot );
	assertEquals( "B", dwvl2tqtr.sw.dot.getName().getName());
	assertEquals( "C", dwvl2tqtr.se.dot.getName().getName());

	return;
}

private void quizPrefabDWVLOf4Dots( DotWorldVeriList dwvl2 ) {

	/*
	 * STAGE A: verify correctness of dwvl2's 2 dot-holding fields
	 */

	// confirm there are 4 dots there now
java.util.TreeMap dwvl2dbn = dwvl2.getDotsByName();
assertEquals( 4, dwvl2dbn.size());
java.util.TreeMap dwvl2dbxy = dwvl2.getDotsByXY();
assertEquals( 4, dwvl2dbxy.size());

java.util.Iterator itern = dwvl2dbn.values().iterator();
Dot dwvl2dbnDot1 = (Dot) itern.next();
Dot dwvl2dbnDot2 = (Dot) itern.next();
Dot dwvl2dbnDot3 = (Dot) itern.next();
Dot dwvl2dbnDot4 = (Dot) itern.next();
java.util.Iterator iterxy = dwvl2dbxy.values().iterator();
Dot dwvl2dbxyDot1 = (Dot) iterxy.next();
Dot dwvl2dbxyDot2 = (Dot) iterxy.next();
Dot dwvl2dbxyDot3 = (Dot) iterxy.next();
Dot dwvl2dbxyDot4 = (Dot) iterxy.next();

assertEquals( "A", dwvl2dbnDot1.getName().getName());
assertEquals( "B", dwvl2dbnDot2.getName().getName());
assertEquals( "C", dwvl2dbnDot3.getName().getName());
assertEquals( "D", dwvl2dbnDot4.getName().getName());

assertEquals( "B", dwvl2dbxyDot1.getName().getName());
assertEquals( "A", dwvl2dbxyDot2.getName().getName());
assertEquals( "C", dwvl2dbxyDot3.getName().getName());
assertEquals( "D", dwvl2dbxyDot4.getName().getName());

	/*
	 * STAGE B: verify correctness of dwvl2's segsOrderedBySpec field
	 */
	
		// confirm there are 4 segs there now
	java.util.TreeSet dwvl2sobs = dwvl2.getSegsOrderedBySpec();
	assertEquals( 4, dwvl2sobs.size());

	java.util.Iterator iter = dwvl2sobs.iterator();
	Seg dwvl2sobsSeg1 = (Seg) iter.next();
	Seg dwvl2sobsSeg2 = (Seg) iter.next();
	Seg dwvl2sobsSeg3 = (Seg) iter.next();
	Seg dwvl2sobsSeg4 = (Seg) iter.next();
	
	assertEquals( "B", dwvl2sobsSeg1.getLesser().getName().getName());
	assertEquals( "A", dwvl2sobsSeg1.getGreater().getName().getName());
	assertEquals( "B", dwvl2sobsSeg2.getLesser().getName().getName());
	assertEquals( "C", dwvl2sobsSeg2.getGreater().getName().getName());
	assertEquals( "A", dwvl2sobsSeg3.getLesser().getName().getName());
	assertEquals( "D", dwvl2sobsSeg3.getGreater().getName().getName());
	assertEquals( "C", dwvl2sobsSeg4.getLesser().getName().getName());
	assertEquals( "D", dwvl2sobsSeg4.getGreater().getName().getName());

	/*
	 * STAGE C: verify correctness of the segs and seg lists in dwvl2's QT _leaves_ 
	 * (ignore the question of whether the seg list of a non-leaf shall be null, or empty)
	 */
	
	QTNode dwvl2tqtr = dwvl2.getTheQTRoot();
	SegList dwvl2qtnwSegList = dwvl2tqtr.nw.segs;
	SegList dwvl2qtneSegList = dwvl2tqtr.ne.segs;
	SegList dwvl2qtswSegList = dwvl2tqtr.sw.segs;
	SegList dwvl2qtseSegList = dwvl2tqtr.se.segs;

	// confirm there are 2 segs in each leaf
	assertEquals( 2, dwvl2qtnwSegList.size());
	assertEquals( 2, dwvl2qtneSegList.size());
	assertEquals( 2, dwvl2qtswSegList.size());
	assertEquals( 2, dwvl2qtseSegList.size());

		// iterate in the NW
	java.util.Iterator iterNW = dwvl2qtnwSegList.iterator();
	Seg dwvl2qtnwSegListSeg1 = (Seg) iterNW.next();
	Seg dwvl2qtnwSegListSeg2 = (Seg) iterNW.next();
	assertEquals( "B", dwvl2qtnwSegListSeg1.getLesser().getName().getName());
	assertEquals( "A", dwvl2qtnwSegListSeg1.getGreater().getName().getName());
	assertEquals( "A", dwvl2qtnwSegListSeg2.getLesser().getName().getName());
	assertEquals( "D", dwvl2qtnwSegListSeg2.getGreater().getName().getName());

	// iterate in the NE
	java.util.Iterator iterNE = dwvl2qtneSegList.iterator();
	Seg dwvl2qtneSegListSeg1 = (Seg) iterNE.next();
	Seg dwvl2qtneSegListSeg2 = (Seg) iterNE.next();
	assertEquals( "A", dwvl2qtneSegListSeg1.getLesser().getName().getName());
	assertEquals( "D", dwvl2qtneSegListSeg1.getGreater().getName().getName());
	assertEquals( "C", dwvl2qtneSegListSeg2.getLesser().getName().getName());
	assertEquals( "D", dwvl2qtneSegListSeg2.getGreater().getName().getName());

		// iterate in the SW
	java.util.Iterator iterSW = dwvl2qtswSegList.iterator();
	Seg dwvl2qtswSegListSeg1 = (Seg) iterSW.next();
	Seg dwvl2qtswSegListSeg2 = (Seg) iterSW.next();
	assertEquals( "B", dwvl2qtswSegListSeg1.getLesser().getName().getName());
	assertEquals( "A", dwvl2qtswSegListSeg1.getGreater().getName().getName());
	assertEquals( "B", dwvl2qtswSegListSeg2.getLesser().getName().getName());
	assertEquals( "C", dwvl2qtswSegListSeg2.getGreater().getName().getName());

		// iterate in the SE
	java.util.Iterator iterSE = dwvl2qtseSegList.iterator();
	Seg dwvl2qtseSegListSeg1 = (Seg) iterSE.next();
	Seg dwvl2qtseSegListSeg2 = (Seg) iterSE.next();
	assertEquals( "B", dwvl2qtseSegListSeg1.getLesser().getName().getName());
	assertEquals( "C", dwvl2qtseSegListSeg1.getGreater().getName().getName());
	assertEquals( "C", dwvl2qtseSegListSeg2.getLesser().getName().getName());
	assertEquals( "D", dwvl2qtseSegListSeg2.getGreater().getName().getName());

	/*
	 * STAGE D: verify correctness of the Dots in dwvl2's QT _leaves_ 
	 */
	
	assertEquals( "A", dwvl2tqtr.nw.dot.getName().getName());
	assertEquals( "D", dwvl2tqtr.ne.dot.getName().getName());
	assertEquals( "B", dwvl2tqtr.sw.dot.getName().getName());
	assertEquals( "C", dwvl2tqtr.se.dot.getName().getName());

	return;
}

/** 
 * Could be toughened! TODO
 * @param dwvl
 */
private void quizPrefabDWVLUnionJack1( DotWorldVeriList dwvl ) {
	
	/*
	 * STAGE A: verify that dwvl's 2 dot-holding fields each hold 960 Dots
	 */
	
	java.util.TreeMap dwvldbn = dwvl.getDotsByName();
	assertEquals( 960, dwvldbn.size());
	java.util.TreeMap dwvldbxy = dwvl.getDotsByXY();
	assertEquals( 960, dwvldbxy.size());
	
	/*
	 * STAGE B: verify that dwvl's segsOrderedBySpec field is empty
	 */
	
	java.util.TreeSet dwvlsobs = dwvl.getSegsOrderedBySpec();
	assertEquals( 0, dwvlsobs.size());

}

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

/*
 * Class under test for void DotWorldVeriList()
 */
public final void testDotWorldVeriList() {
//TODO Implement DotWorldVeriList().
}

/*
 * Class under test for void DotWorldVeriList(java.util.TreeMap, java.util.TreeMap, java.util.TreeSet, QTNode, int, int, int, boolean, boolean)
 */
public final void testDotWorldVeriList2() {
//TODO Implement DotWorldVeriList().
}

public final void testAnimHorizPath() {
//TODO Implement animHorizPath().
}

public final void testColorDot() {
//TODO Implement colorDot().
}

public final void testColorSegs() {
//TODO Implement colorSegs().
}

public final void testCreateDot() {
//TODO Implement createDot(). Ensure such dots don't appear in the QT until a seg's been mapped.
}

public final void testCreatePath() {
//TODO Implement createPath().
}

public final void testDeleteDot() {
//TODO Implement deleteDot().
}

public final void testDeletePath() {
//TODO Implement deletePath().
}

public final void testDrawFrame() {
//TODO Implement drawFrame().
}

public final void testDrawFrameN() {
//TODO Implement drawFrameN().
}

public final void testExit() {
//TODO Implement exit().
}

public final void testInitQT() {
//TODO Implement initQT().
}

public final void testIntersectsAny() throws DotNameDuplicateException, CoordDuplicateException, 
	DotColorFormatException, NotFoundException, IntersectionException, BadCodeException, DotNameFormatException {

		/* Stage 1
		 * 
		 * J and K are to the left of the prefab seg A--B, L and M are to its right, Z1 and Z2 are upon it
		 */

	try {
		DotWorldVeriList dwvlPrefab3 = prefabDWVLOf3Dots(); // has 2 segs: 5,29--5,5--29,5
		dwvlPrefab3.createDot( new DotName( "J" ), new Coord(3), new Coord(20), new Radius(1), new DotColor( "RED" ));
		dwvlPrefab3.createDot( new DotName( "K" ), new Coord(3), new Coord(18), new Radius(1), new DotColor( "RED" ));
		dwvlPrefab3.createDot( new DotName( "L" ), new Coord(7), new Coord(20), new Radius(1), new DotColor( "RED" ));
		dwvlPrefab3.createDot( new DotName( "M" ), new Coord(7), new Coord(18), new Radius(1), new DotColor( "RED" ));
		assertFalse( dwvlPrefab3.intersectsAny( new DotName( "J" ), new DotName( "K" )));
		assertFalse( dwvlPrefab3.intersectsAny( new DotName( "L" ), new DotName( "M" )));
		assertTrue( dwvlPrefab3.intersectsAny( new DotName( "J" ), new DotName( "L" )));
		assertTrue( dwvlPrefab3.intersectsAny( new DotName( "K" ), new DotName( "L" )));
		assertTrue( dwvlPrefab3.intersectsAny( new DotName( "J" ), new DotName( "M" )));
		assertTrue( dwvlPrefab3.intersectsAny( new DotName( "K" ), new DotName( "M" )));
		assertFalse( dwvlPrefab3.intersectsAny( new DotName( "J" ), new DotName( "J" )));
		assertFalse( dwvlPrefab3.intersectsAny( new DotName( "K" ), new DotName( "K" )));
		assertFalse( dwvlPrefab3.intersectsAny( new DotName( "L" ), new DotName( "L" )));
		assertFalse( dwvlPrefab3.intersectsAny( new DotName( "M" ), new DotName( "M" )));
		dwvlPrefab3.createDot( new DotName( "Z1" ), new Coord(5), new Coord(20), new Radius(1), new DotColor( "RED" ));
		dwvlPrefab3.createDot( new DotName( "Z2" ), new Coord(5), new Coord(18), new Radius(1), new DotColor( "RED" ));
		assertTrue( dwvlPrefab3.intersectsAny( new DotName( "Z1" ), new DotName( "Z1" )));
		assertTrue( dwvlPrefab3.intersectsAny( new DotName( "Z2" ), new DotName( "Z2" )));
		assertTrue( dwvlPrefab3.intersectsAny( new DotName( "Z1" ), new DotName( "Z2" )));
		assertTrue( dwvlPrefab3.intersectsAny( new DotName( "Z2" ), new DotName( "Z1" )));
		assertTrue( dwvlPrefab3.intersectsAny( new DotName( "Z1" ), new DotName( "J" )));
		assertTrue( dwvlPrefab3.intersectsAny( new DotName( "K" ), new DotName( "Z1" )));
		assertTrue( dwvlPrefab3.intersectsAny( new DotName( "Z1" ), new DotName( "L" )));
		assertTrue( dwvlPrefab3.intersectsAny( new DotName( "M" ), new DotName( "Z1" )));
		assertTrue( dwvlPrefab3.intersectsAny( new DotName( "J" ), new DotName( "Z2" )));
		assertTrue( dwvlPrefab3.intersectsAny( new DotName( "Z2" ), new DotName( "K" )));
		assertTrue( dwvlPrefab3.intersectsAny( new DotName( "L" ), new DotName( "Z2" )));
		assertTrue( dwvlPrefab3.intersectsAny( new DotName( "Z2" ), new DotName( "M" )));
	} catch ( DotColorFormatException e ) { fail();
	} catch ( DotNameFormatException e ) { fail();
	} catch ( DotNameDuplicateException e ) { fail();
	} catch ( CoordDuplicateException e ) { fail();
	} catch ( NotFoundException e ) { fail();
	} catch ( BadCodeException e ) { fail();
	}
	
		/* Stage 2
		 * 
		 * An unexpected IEx was triggered by this code in testMapSegSelfLoops() - now it's fixed
		 */

	DotWorldVeriList forensic = prefabDWVLOf3Dots(); // has 2 segs: 5,29--5,5--29,5
	forensic.createDot( new DotName( "BC16,5" ), 
					new Coord(16), new Coord(5), new Radius(7), new DotColor( "RED" )); // was never mapped
	forensic.createDot( new DotName( "BC16,18" ),
					new Coord(16), new Coord(18), new Radius(7), new DotColor( "RED" ));
	forensic.mapSeg( new DotName( "BC16,18" ), new DotName( "BC16,18" ) ); // squats on any A--C
	forensic.createDot( new DotName( "BC18,16" ),
					new Coord(18), new Coord(16), new Radius(7), new DotColor( "RED" ));
	LinkedHashMap probe = new LinkedHashMap();
	assertFalse( forensic.intersectsAny( new DotName( "BC18,16" ), new DotName( "BC18,16" ), probe ));
	System.out.println( probe.toString() );
	forensic.mapSeg( new DotName( "BC18,16" ), new DotName( "BC18,16" ) ); // squats on any A--C 
	forensic.createDot( new DotName( "BC20,14" ),
					new Coord(20), new Coord(14), new Radius(7), new DotColor( "RED" ));
	forensic.mapSeg( new DotName( "BC20,14" ), new DotName( "BC20,14" ) ); // squats on any A--C 

}

public final void testListDots() {
//TODO Implement listDots().
}

/**
 * Test all foreseen occasions for mapSeg, except the following occasions (which have their own unit
 * tests as siblings to this one):<br>
 * 1. anything involving self loops (testMapSegSelfLoops)<br>
 * 1b. trying to lie upon, or very close to, an unrelated dot (i.e. upon a self-loop) (testMapSegSelfLoops)<br>
 * 2. mapping "the same" seg twice (testMapSegOverlay)<br>
 * 3. one seg lying along the same line as another, and (wholly or partly) covering it (testMapSegOverlay)<br>
 * 5. two parallel (or nearly parallel) lines very close together<br>
 * 6. a T junction
 * In short, this unit test should be for "obviously acceptable" mappings, and criss-cross intersections.
 * 
 * @throws DotNameDuplicateException
 * @throws CoordDuplicateException
 * @throws DotColorFormatException
 * @throws NotFoundException
 * @throws IntersectionException
 * @throws BadCodeException
 * @throws DotNameFormatException
 * @throws RangeException
 * @throws QTDuplicateException
 */
public final void testMapSeg() throws DotNameDuplicateException, CoordDuplicateException, 
	DotColorFormatException, NotFoundException, IntersectionException, BadCodeException, 
	DotNameFormatException, RangeException, QTDuplicateException {
	
		/* STAGE 1 TODO
		 * 
		 * Try some "obviously good" seg mapping and test that the segs show up in the QT's guts. I.e. for
		 * mappings that won't cause exceptions, can we observe that they produce the proper result in the QT?
		 */


		/* Stage 2
		 * 
		 * Use some "prefab" helpers to carry out some correct seg mapping. unmapSegment is also used here,
		 * which, strictly, we shouldn't do since we are in effect testing unmapSegment. But we use it to 
		 * test whether we can map on the site of a former seg.
		 * 
		 * Then repeat without relying on prefabs, except the quizzes.
		 */
	
	DotWorldVeriList dwvlPrefab3 = prefabDWVLOf3Dots();
	quizPrefabDWVLOf3Dots( dwvlPrefab3 );
	dwvlPrefab3.createDot( new DotName( "D" ), new Coord(30), new Coord(30), new Radius(1), new DotColor( "RED" ));
	dwvlPrefab3.mapSeg( new DotName( "A" ), new DotName( "D" ) );
	dwvlPrefab3.mapSeg( new DotName( "C" ), new DotName( "D" ) );
	quizPrefabDWVLOf4Dots( dwvlPrefab3 ); // it's become the 4-dot shape
	dwvlPrefab3.unmapSegment( new DotName( "A" ), new DotName( "D" ) );
	dwvlPrefab3.unmapSegment( new DotName( "C" ), new DotName( "D" ) );
	quizPrefabDWVLOf3Dots( dwvlPrefab3 ); // it's back to the 3-dot shape
	dwvlPrefab3.createDot( new DotName( "SONOFD" ), new Coord(30), new Coord(30), 
					new Radius(300), new DotColor( "BLUE" ));
    dwvlPrefab3.mapSeg( new DotName( "A" ), new DotName( "SONOFD" ) );

	DotWorldVeriList dwvl = new DotWorldVeriList(); // build an equivalent DWVL from scratch
	dwvl.initQT( new cmsc420.p3.QTMagnitude( 5 )); // necessary, unless using a prefab
	dwvl.createDot( new DotName( "A" ), new Coord(5), new Coord(29), new Radius(1), new DotColor( "RED" ));
	dwvl.createDot( new DotName( "B" ), new Coord(5), new Coord(5), new Radius(1), new DotColor( "RED" ));
	dwvl.createDot( new DotName( "C" ), new Coord(29), new Coord(5), new Radius(1), new DotColor( "RED" ));
	dwvl.mapSeg( new DotName( "A" ), new DotName( "B" ) );
	dwvl.mapSeg( new DotName( "B" ), new DotName( "C" ) );
	quizPrefabDWVLOf3Dots( dwvl ); // it's equivalent to the 3-dot prefab
	dwvl.createDot( new DotName( "D" ), new Coord(30), new Coord(30), new Radius(1), new DotColor( "RED" ));
	dwvl.mapSeg( new DotName( "A" ), new DotName( "D" ) );
	dwvl.mapSeg( new DotName( "C" ), new DotName( "D" ) );
	quizPrefabDWVLOf4Dots( dwvl ); // it's become equivalent to the 4-dot prefab

		/* Stage 3
		 * 
		 * test some mapseg error reporting
		 */
	
	try { 	dwvl.mapSeg( new DotName( "A" ), new DotName( "NONESUCH" ));
			fail();
	} catch ( NotFoundException expected ) {
		assertEquals( "raised by mapSeg, i.e. one or both names not in dotsByName", expected.getMessage() );
	} catch ( Exception surprise ) { fail(); }

	try { 	dwvl.mapSeg( new DotName( "NOBODY" ), new DotName( "NONESUCH" ));
			fail();
	} catch ( NotFoundException expected ) {
		assertEquals( "raised by mapSeg, i.e. one or both names not in dotsByName", expected.getMessage() );
	} catch ( Exception surprise ) { fail(); }
	
	dwvl.mapSeg( new DotName( "A" ), new DotName( "C" ) ); // lay groundwork for an intersection
	try { 	dwvl.mapSeg( new DotName( "B" ), new DotName( "D" )); // criss-cross intersection
			fail();
	} catch ( IntersectionException expected ) {
		assertEquals( "raised by mapSeg, i.e. intersectsAny returned true", expected.getMessage() );
	} catch ( Exception surprise ) { fail(); }
	
		/* Stage 4
		 * 
		 * Criss-cross intersections should throw IntersectionException. Try to generate Criss-cross
		 * intersections in a DWVL: get a prefabDWVLUnionJack1, then draw a slash and try to cross
		 * it.
		 * 
		 * Dots are in one another's line of fire, but that's ok; all these mappings will fail
		 * (hopefully), so they add no segs to the QT*, so each mapping will have a clear shot, so
		 * when it throws an IntersectionException it'll be because it'd cross the only seg in the
		 * QT (i.e. A-B).
		 * 
		 * (* in Stage 1 I verified that failed mappings leave a QT unchanged)
		 */
	
	DotWorldVeriList dwvl4 = prefabDWVLUnionJack1(); // has dots, but no segs

		// Stage 4.1 a sw-to-ne slash
	
	dwvl4.createDot( new DotName( "A" ), new Coord(0), new Coord(0), new Radius(1), new DotColor( "RED" ));
	dwvl4.createDot( new DotName( "B" ), new Coord(64), new Coord(64), new Radius(1), new DotColor( "RED" ));
	dwvl4.mapSeg( new DotName( "A" ), new DotName( "B" ) ); // now we have a sw-to-ne slash from 0,0 to 64,64

		// Stage 4.2 these should succeed despite the slash (commented out once tested)

	/*
	dwvl4.mapSeg( new DotName( "UJ0,4" ), new DotName( "UJ0,62" ) ); // succeeded!	
	dwvl4.mapSeg( new DotName( "UJ2,6" ), new DotName( "UJ2,34" ) ); // succeeded!	
	dwvl4.mapSeg( new DotName( "UJ2,36" ), new DotName( "UJ2,64" ) ); // succeeded!	
	dwvl4.mapSeg( new DotName( "UJ6,10" ), new DotName( "UJ6,12" ) ); // succeeded!	
	dwvl4.mapSeg( new DotName( "UJ6,14" ), new DotName( "UJ6,60" ) ); // succeeded!	
	dwvl4.mapSeg( new DotName( "UJ12,20" ), new DotName( "UJ16,20" ) ); // succeeded!	
	dwvl4.mapSeg( new DotName( "UJ12,22" ), new DotName( "UJ14,22" ) ); // succeeded!	
	dwvl4.mapSeg( new DotName( "UJ16,22" ), new DotName( "UJ18,22" ) ); // succeeded!	
	dwvl4.mapSeg( new DotName( "UJ26,30" ), new DotName( "UJ34,40" ) ); // succeeded!	
	dwvl4.mapSeg( new DotName( "UJ42,46" ), new DotName( "UJ44,50" ) ); // succeeded!	
	dwvl4.mapSeg( new DotName( "UJ58,62" ), new DotName( "UJ52,64" ) ); // succeeded!
	*/
	
		// Stage 4.3 these mappings across the slash should fail
	
	for ( int x = 0; x <= 60; x += 2 ) {
		for ( int y = x + 4; y <= 64; y += 2 ) {
			if (( x == y ) || ( x + y == 64 ) || ( 32 == x ) || ( 32 == y )) {
				// you're on one of the 8 spokes, so do nothing
			} else {
				try { // should throw IntersectionException
					dwvl4.mapSeg( new DotName( "UJ" + x + "," + y ), new DotName( "UJ" + y + "," + x ) );
					fail();
				} catch ( IntersectionException expected ) {
					assertEquals( "raised by mapSeg, i.e. intersectsAny returned true", expected.getMessage() );
				} catch ( Exception surprise ) { 
					fail(); 
				}
			}
		}
	}
}			

public final void testMapSegOverlay() throws NotFoundException, 
	IntersectionException, BadCodeException, DotNameDuplicateException, 
	CoordDuplicateException, DotNameFormatException, DotColorFormatException {
	
		/* Stage 1
		 * 
		 * Map, unmap, and remap a seg. Avoid letting either endpoint become seg-less, or unmapSegment
		 * will obliterate it. Instead, we want to reuse the same two Dots when we remap a seg.
		 */
	
	DotWorldVeriList dwvlPrefab3 = prefabDWVLOf3Dots(); // it's A--B--C
    dwvlPrefab3.mapSeg( new DotName( "A" ), new DotName( "C" ) );
    dwvlPrefab3.unmapSegment( new DotName( "A" ), new DotName( "C" ) );
    dwvlPrefab3.mapSeg( new DotName( "A" ), new DotName( "C" ) );

		/* Stage 2
		 * 
		 * Recall A is 5,29 and B is 5,5. We can create new dots along that seg without problem, but we 
		 * shouldn't succeed in using them to map segs.
		 */
	
	dwvlPrefab3.createDot( new DotName( "ABOVEA" ), 
						new Coord(5), new Coord(31), new Radius(1), new DotColor( "RED" ));
	dwvlPrefab3.createDot( new DotName( "BELOWA" ), 
						new Coord(5), new Coord(27), new Radius(1), new DotColor( "RED" ));
	dwvlPrefab3.createDot( new DotName( "ABOVEB" ), 
						new Coord(5), new Coord(7), new Radius(1), new DotColor( "RED" ));
	dwvlPrefab3.createDot( new DotName( "BELOWB" ), 
						new Coord(5), new Coord(3), new Radius(1), new DotColor( "RED" ));
	try { 
		dwvlPrefab3.mapSeg( new DotName( "ABOVEA" ), new DotName( "BELOWA" ) );
		fail();
	} catch ( IntersectionException expected ) {
		assertEquals( "raised by mapSeg, i.e. intersectsAny returned true", expected.getMessage() );
	} catch ( Exception surprise ) { 
		fail(); 
	}
	try { 
		dwvlPrefab3.mapSeg( new DotName( "ABOVEA" ), new DotName( "ABOVEB" ) );
		fail();
	} catch ( IntersectionException expected ) {
		assertEquals( "raised by mapSeg, i.e. intersectsAny returned true", expected.getMessage() );
	} catch ( Exception surprise ) { 
		fail(); 
	}
	try { 
		dwvlPrefab3.mapSeg( new DotName( "B" ), new DotName( "ABOVEA" ) ); // either order - I tested
		fail();
	} catch ( IntersectionException expected ) {
		assertEquals( "raised by mapSeg, i.e. intersectsAny returned true", expected.getMessage() );
	} catch ( Exception surprise ) { 
		fail(); 
	}
	try { 
		dwvlPrefab3.mapSeg( new DotName( "ABOVEA" ), new DotName( "BELOWB" ) );
		fail();
	} catch ( IntersectionException expected ) {
		assertEquals( "raised by mapSeg, i.e. intersectsAny returned true", expected.getMessage() );
	} catch ( Exception surprise ) { 
		fail(); 
	}
	try { 
		dwvlPrefab3.mapSeg( new DotName( "BELOWA" ), new DotName( "ABOVEB" ) );
		fail();
	} catch ( IntersectionException expected ) {
		assertEquals( "raised by mapSeg, i.e. intersectsAny returned true", expected.getMessage() );
	} catch ( Exception surprise ) { 
		fail(); 
	}
	try { 
		dwvlPrefab3.mapSeg( new DotName( "B" ), new DotName( "BELOWA" ) ); // either order - I tested
		fail();
	} catch ( IntersectionException expected ) {
		/* assertEquals( "raised by mapSeg, i.e. intersectsAny returned true", expected.getMessage() );
		 * this IE is actually a relayed MDE - AWT bug? TODO
		 */
	} catch ( Exception surprise ) { 
		fail(); 
	}
	try { 
		dwvlPrefab3.mapSeg( new DotName( "BELOWA" ), new DotName( "BELOWB" ) );
		fail();
	} catch ( IntersectionException expected ) {
		assertEquals( "raised by mapSeg, i.e. intersectsAny returned true", expected.getMessage() );
	} catch ( Exception surprise ) { 
		fail(); 
	}
	try { 
		dwvlPrefab3.mapSeg( new DotName( "B" ), new DotName( "ABOVEB" ) ); // either order - I tested
		fail();
	} catch ( IntersectionException expected ) {
		/* assertEquals( "raised by mapSeg, i.e. intersectsAny returned true", expected.getMessage() );
		 * this IE is actually a relayed MDE - AWT bug? TODO
		 */
	} catch ( Exception surprise ) { 
		fail(); 
	}
	try { 
		dwvlPrefab3.mapSeg( new DotName( "ABOVEB" ), new DotName( "BELOWB" ) );
		fail();
	} catch ( IntersectionException expected ) {
		assertEquals( "raised by mapSeg, i.e. intersectsAny returned true", expected.getMessage() );
	} catch ( Exception surprise ) { 
		fail(); 
	}
	
	dwvlPrefab3.mapSeg( new DotName( "ABOVEA" ), new DotName( "A" ) );
	dwvlPrefab3.mapSeg( new DotName( "BELOWB" ), new DotName( "B" ) );	
}

public final void testMapSegProximity() throws RangeException, QTDuplicateException, DotNameDuplicateException, 
		CoordDuplicateException, DotColorFormatException, NotFoundException, IntersectionException, 
		BadCodeException, DotNameFormatException {

		/* Stage 1
		 * 
		 * T-junctions: each should cause an Xep to be thrown, either IEx (if detected by intersectsAny) or
		 * else MDE (if the conflict isn't detected until we start to split nodes in the QT.) For 
		 * dual-purpose testing, we'll stick these along the QT frontiers.
		 */
	
	DotWorldVeriList dwvl1 = prefabDWVLUnionJack1();
	quizPrefabDWVLUnionJack1( dwvl1 );
	dwvl1.mapSeg( new DotName( "UJ2,0" ), new DotName( "UJ8,0" ) );
	try { 	dwvl1.mapSeg( new DotName( "UJ4,2" ), new DotName( "UJ4,0" ));
			fail();
	} catch ( IntersectionException expected ) {
		assertEquals( "raised by mapSeg, i.e. intersectsAny returned true", expected.getMessage() );
	} catch ( Exception surprise ) { fail(); }
	dwvl1.mapSeg( new DotName( "UJ64,62" ), new DotName( "UJ64,50" ) );
	try { 	dwvl1.mapSeg( new DotName( "UJ62,58" ), new DotName( "UJ64,58" ));
			fail();
	} catch ( IntersectionException expected ) {
		assertEquals( "raised by mapSeg, i.e. intersectsAny returned true", expected.getMessage() );
	} catch ( Exception surprise ) { fail(); }
	// quizPrefabDWVLUnionJack1( dwvl1 ); // causes a failure, as it should
	dwvl1.createDot( new DotName( "NEW0,0" ), new Coord(0), new Coord(0), new Radius(1), new DotColor( "RED" ));
	dwvl1.createDot( new DotName( "NEW0,64" ), new Coord(0), new Coord(64), new Radius(1), new DotColor( "RED" ));
	dwvl1.mapSeg( new DotName( "NEW0,0" ), new DotName( "NEW0,64" ) );
	dwvl1.mapSeg( new DotName( "UJ2,64" ), new DotName( "NEW0,64" ));
	// dwvl1.mapSeg( new DotName( "UJ6,62" ), new DotName( "NEW0,64" )); // fails: MDE (it should)
	try { 	dwvl1.mapSeg( new DotName( "UJ2,60" ), new DotName( "UJ0,60" ));
			fail();
	} catch ( IntersectionException expected ) {
		assertEquals( "raised by mapSeg, i.e. intersectsAny returned true", expected.getMessage() );
	} catch ( Exception surprise ) { fail(); }
	dwvl1.mapSeg( new DotName( "UJ20,24" ), new DotName( "UJ24,28" ) ); // try a diagonal for a change
	try {
		dwvl1.mapSeg( new DotName( "UJ22,26" ), new DotName( "UJ20,28" ) );
		fail();
	} catch ( IntersectionException expected ) {
		assertEquals( "raised by mapSeg, i.e. intersectsAny returned true", expected.getMessage() );
	} catch ( Exception surprise ) { fail(); }

		/* Stage 2
		 * 
		 * "near misses": these should throw only MDEs, and do so whenever a new seg would intrude on a
		 * 1x1 square occupied by an incompatible object.
		 */

	DotWorldVeriList dwvl2 = prefabDWVLUnionJack1();
	quizPrefabDWVLUnionJack1( dwvl2 );
	dwvl2.mapSeg( new DotName( "UJ10,6" ), new DotName( "UJ10,6" ) );
//	dwvl2.mapSeg( new DotName( "UJ10,8" ), new DotName( "UJ16,0" )); // throws MDE, good info at console
//	dwvl2.mapSeg( new DotName( "UJ10,8" ), new DotName( "UJ18,0" )); // throws MDE, good info at console
	dwvl2.mapSeg( new DotName( "UJ10,8" ), new DotName( "UJ20,0" )); // passes, as it should
	dwvl2.createDot( new DotName( "NEW8,9" ), new Coord(8), new Coord(9), new Radius(1), new DotColor( "RED" ));
	dwvl2.createDot( new DotName( "NEW12,9" ), new Coord(12), new Coord(9), new Radius(1), new DotColor( "RED" ));
//	dwvl2.mapSeg( new DotName( "NEW8,9" ), new DotName( "NEW12,9" )); // throws MDE, good info at console
	
}


/**
 * According to the newsgroup, a self loop (segment from FOO to FOO) can exist IFF no other segs use
 * FOO. For now, I'll assume that merely invoking createDot does not imply the creation of a
 * self-loop, so that there's a diff btwn (1) a dot that has no q-edges (createDot has been called
 * but no seg has yet been mapped) and (2) a dot that has a self-loop (createDot, followed by
 * mapSeg( FOO FOO ).) As long as a self loop exists, the dot can't be used in other segs, and
 * vice-versa. From specs: "It is possible that name1==name2, this should not be an error. The PM3
 * should be able to know whether any given point has a path to itself or not."
 * @throws DotColorFormatException
 * @throws DotNameFormatException
 * @throws BadCodeException
 * @throws IntersectionException
 * @throws NotFoundException
 * @throws DotNameFormatException
 * @throws DotColorFormatException
 * @throws CoordDuplicateException
 * @throws DotNameDuplicateException
 *  
 */
public final void testMapSegSelfLoops() throws NotFoundException, IntersectionException, BadCodeException, 
		DotNameDuplicateException, CoordDuplicateException, DotNameFormatException, DotColorFormatException {

		/* Stage 1
		 * 
		 * Map a self-loop, ensure it blocks subsequent segs incident to that dot, and ensure a
		 * self-loop can't be added to a dot with a seg (i.e. self-loops and other segs don't mix)
		 */

	DotWorldVeriList dwvlPrefab3 = prefabDWVLOf3Dots();
	quizPrefabDWVLOf3Dots( dwvlPrefab3 );
	dwvlPrefab3.createDot( new DotName( "D" ), new Coord(30), new Coord(30), new Radius(1), new DotColor( "RED" ));
	dwvlPrefab3.mapSeg( new DotName( "D" ), new DotName( "D" ) ); // this self-loop should pass
	try { 
		dwvlPrefab3.mapSeg( new DotName( "A" ), new DotName( "D" ) ); // should throw IEx - D's now a self-loop
		fail();
	} catch ( IntersectionException expected ) {
		assertEquals( "raised by mapSeg, i.e. intersectsAny returned true", expected.getMessage() );
	} catch ( Exception surprise ) { 
		fail(); 
	}
	try { 
		dwvlPrefab3.mapSeg( new DotName( "B" ), new DotName( "D" ) ); // should throw IEx - D's now a self-loop
		fail();
	} catch ( IntersectionException expected ) {
		assertEquals( "raised by mapSeg, i.e. intersectsAny returned true", expected.getMessage() );
	} catch ( Exception surprise ) { 
		fail(); 
	}
	try { 
		dwvlPrefab3.mapSeg( new DotName( "D" ), new DotName( "D" ) ); // should throw IEx - D's now a self-loop
		fail();
	} catch ( IntersectionException expected ) {
		assertEquals( "raised by mapSeg, i.e. intersectsAny returned true", expected.getMessage() );
	} catch ( Exception surprise ) { 
		fail(); 
	}
	try { 
		dwvlPrefab3.mapSeg( new DotName( "C" ), new DotName( "C" ) ); // should throw IEx - C has a seg already
		fail();
	} catch ( IntersectionException expected ) {
		assertEquals( "raised by mapSeg, i.e. intersectsAny returned true", expected.getMessage() );
	} catch ( Exception surprise ) { 
		fail(); 
	}

		/* Stage 2
		 * 
		 * Map, unmap and remap a self-loop. Note that Dots with self-loops have a seg count of 1, so
		 * they're susceptible to the "feature" (as per the specs?) whereby using unmapSegment to remove
		 * the final seg on a Dot will obliterate the Dot from the DotWorld, too. Therefore you can't
		 * remap a self-loop until you re-createDot, because the Dot has vanished from the DotWorld.
		 */

		// D--D has been mapped already
	dwvlPrefab3.unmapSegment( new DotName( "D" ), new DotName( "D" ) ); // D itself's now gone, too
	quizPrefabDWVLOf3Dots( dwvlPrefab3 );
	try {
		dwvlPrefab3.deleteDot( new DotName( "D" ) ); // should fail
		fail();
	} catch ( NotFoundException expected ) { // do nothing
	} catch ( Exception surprise ) { 
		fail(); 
	}
	dwvlPrefab3.createDot( new DotName( "SonOfD" ), new Coord(30), new Coord(30), new Radius(1), new DotColor( "RED" ));
	dwvlPrefab3.mapSeg( new DotName( "SonOfD" ), new DotName( "SonOfD" ) );
	dwvlPrefab3.unmapSegment( new DotName( "SonOfD" ), new DotName( "SonOfD" ) );
	quizPrefabDWVLOf3Dots( dwvlPrefab3 );
	try {
		dwvlPrefab3.deleteDot( new DotName( "SonOfD" ) ); // should fail
		fail();
	} catch ( NotFoundException expected ) { // do nothing
	} catch ( Exception surprise ) { 
		fail(); 
	}	
	
		/* Stage 3
		 * 
		 * Confirm that a self-loop can't lie in the middle of an (unrelated, obv.) seg, and vice-versa
		 */
	
	dwvlPrefab3.createDot( new DotName( "BC16,5" ), 
					new Coord(16), new Coord(5), new Radius(7), new DotColor( "RED" ));
	try { 
		dwvlPrefab3.mapSeg( new DotName( "BC16,5" ), new DotName( "BC16,5" ) ); // should throw IEx: on B--C
		fail();
	} catch ( IntersectionException expected ) {
		assertEquals( "raised by mapSeg, i.e. intersectsAny returned true", expected.getMessage() );
	} catch ( Exception surprise ) { 
		fail(); 
	}	
	dwvlPrefab3.createDot( new DotName( "BC16,18" ),
					new Coord(16), new Coord(18), new Radius(7), new DotColor( "RED" ));
	dwvlPrefab3.mapSeg( new DotName( "BC16,18" ), new DotName( "BC16,18" ) ); // squats on any A--C 
	dwvlPrefab3.createDot( new DotName( "BC18,16" ),
					new Coord(18), new Coord(16), new Radius(7), new DotColor( "RED" ));
	dwvlPrefab3.mapSeg( new DotName( "BC18,16" ), new DotName( "BC18,16" ) ); // squats on any A--C 
	dwvlPrefab3.createDot( new DotName( "BC20,14" ),
					new Coord(20), new Coord(14), new Radius(7), new DotColor( "RED" ));
	dwvlPrefab3.mapSeg( new DotName( "BC20,14" ), new DotName( "BC20,14" ) ); // squats on any A--C 
	try { 
		dwvlPrefab3.mapSeg( new DotName( "A" ), new DotName( "C" ) ); // fails: 3 self-loops in way
		fail();
	} catch ( IntersectionException expected ) {
		assertEquals( "raised by mapSeg, i.e. intersectsAny returned true", expected.getMessage() );
	} catch ( Exception surprise ) { 
		fail(); 
	}
	dwvlPrefab3.unmapSegment( new DotName( "BC16,18" ), new DotName( "BC16,18" ) );
//dwvlPrefab3.deleteDot( new DotName( "BC16,18" )); // works just as well in the case of self-loops
	try { 
		dwvlPrefab3.mapSeg( new DotName( "A" ), new DotName( "C" ) ); // fails: 2 self-loops in way
		fail();
	} catch ( IntersectionException expected ) {
		assertEquals( "raised by mapSeg, i.e. intersectsAny returned true", expected.getMessage() );
	} catch ( Exception surprise ) { 
		fail(); 
	}
	dwvlPrefab3.unmapSegment( new DotName( "BC18,16" ), new DotName( "BC18,16" ) );
//dwvlPrefab3.deleteDot( new DotName( "BC18,16" )); // works just as well in the case of self-loops
	try { 
		dwvlPrefab3.mapSeg( new DotName( "A" ), new DotName( "C" ) ); // fails: 1 self-loop in way
		fail();
	} catch ( IntersectionException expected ) {
		assertEquals( "raised by mapSeg, i.e. intersectsAny returned true", expected.getMessage() );
	} catch ( Exception surprise ) { 
		fail(); 
	}
	dwvlPrefab3.unmapSegment( new DotName( "BC20,14" ), new DotName( "BC20,14" ) );
//dwvlPrefab3.deleteDot( new DotName( "BC20,14" )); // works just as well in the case of self-loops
	dwvlPrefab3.mapSeg( new DotName( "A" ), new DotName( "C" ) ); // succeeds, on 4th try: road has been cleared
}

public final void testNearestSegToPoint() {
//TODO Implement nearestSegToPoint().
}

public final void testPrintBPTree() {
//TODO Implement printBPTree().
}

public final void testPrintQT() {
//TODO Implement printQT().
}

public final void testRangeDots() {
//TODO Implement rangeDots().
}

public final void testSetBPTreeOrder() {
//TODO Implement setBPTreeOrder().
}

public final void testSetDrawMode() {
//TODO Implement setDrawMode().
}

public final void testShortestPath() {
//TODO Implement shortestPath().
}

/**
 * create empty dot world and try to unmap a segment. expect "no such dot". This is the kind of
 * method for which the IOC ("set-all") constructor of DotWorldVeriList was written.
 */
public final void testUnmapSegment1() {

	/* The boolean bptHasBeenInitialized is irrelevant here - false or true both ok <br>
	 * The quadtree will have a 32x32 size - important to remember when sizing the kids, later <br>
	 * 
	 * Apart from the set-all constructor (invoked here), the only method that sets a value for
	 * (i.e. makes an assignment to) theQTRoot, or flips qtHasBeenInitialized, is initQT(). Therefore 
	 * we will make sure, here, that (qtHasBeenInitialized is true IFF theQTRoot is non-null). Since
	 * the specs say that initQT "will always precede the first command which requires the quadtree",
	 * we'll opt for (qtHasBeenInitialized is true AND theQTRoot is non-null).
	 */
	DotWorldVeriList dwvl1 = new DotWorldVeriList( new java.util.TreeMap(), new java.util.TreeMap(),
		new java.util.TreeSet(), new QTNode( 32 ), // must be a power of 2. the tree is now effectively empty
		10, 10, 10, true, false );
	try {
		dwvl1.unmapSegment( new DotName( "FOO" ), new DotName( "BAR" ) );
		fail();
	} catch ( NotFoundException expected ) {
		if ( expected.getMessage().equals( "dot" ) ) {
			assertTrue( true );
		} else {
			fail();
		}
	} catch ( Exception surprise ) {
		fail();
	}
}

/**
 * Obtain a dot world (from prefabDWVLOf3Dots) with 3 dots and 2 segs (like an "L"), then unmap its
 * segs. To write this method, I needed knowledge of what prefabDWVLOf3Dots does, e.g. what the dots
 * are named.
 * 
 * @throws DotNameFormatException
 * @throws DotColorFormatException
 */
public final void testUnmapSegment2() throws DotNameFormatException, DotColorFormatException {

	DotWorldVeriList dwvl2 = prefabDWVLOf3Dots();
	quizPrefabDWVLOf3Dots( dwvl2 ); // confirm prefabDWVLOf3Dots still works
	
	try { // try to unmap a segment btwn nonexistent dots. expect "no such dot"
		dwvl2.unmapSegment( new DotName( "DOESNTEXIST" ), new DotName( "NORDOI" ));
		fail();
	} catch ( NotFoundException expected ) {
		if ( expected.getMessage().equals( "dot" )) {
			assertTrue( true );
		} else {
			fail();
		}
	} catch ( Exception surprise ) { fail(); }
	
	try { // try to unmap the 3rd (i.e. nonexistent) segment of the triangle. expect "no such segment"
		dwvl2.unmapSegment( new DotName( "A" ), new DotName( "C" ));
		fail();
	} catch ( NotFoundException expected ) {
		if ( expected.getMessage().equals( "segment" )) {
			assertTrue( true );
		} else {
			fail( expected.getMessage());
		}
	} catch ( Exception surprise ) { fail(); }

	/*
	 * STAGE 4: still using dwvl2, unmap the 2 actual segments of the triangle, twice each. 
	 *          expect an array with the 2 DotName args _in the same order as passed to the method_,
	 * 		    followed by "no such dot" when the same command is repeated (because A, then B&C 
	 *          disappear).
	 */

	quizPrefabDWVLOf3Dots( dwvl2 ); // it should be unchanged
	
	/*
	 * STAGE 4 MAIN PART
	 */
	
	DotName[] dnaExpected1 = new DotName[] { new DotName( "A" ), new DotName( "B" ) };
	
	try {		
		DotName[] dnaActual1 = dwvl2.unmapSegment( new DotName( "A" ), new DotName( "B" ));
		assertEquals( 2, dnaActual1.length );
		assertEquals( dnaExpected1[0], dnaActual1[0] );
		assertEquals( dnaExpected1[1], dnaActual1[1] );
	} catch ( BadCodeException feared ) {
		fail( "BCE: [" + feared.getMessage() + "]" );
	} catch ( NotFoundException surprise ) {
		fail( surprise.getMessage());
	}
	
	try {
		dwvl2.unmapSegment( new DotName( "A" ), new DotName( "B" )); // just removed
		fail();
	} catch ( NotFoundException expected ) {
		if ( expected.getMessage().equals( "dot" )) { // because A no longer exists
			assertTrue( true );
		} else {
			fail( expected.getMessage());
		}
	} catch ( Exception surprise ) {
		fail();
	}

	DotName[] dnaExpected2 = new DotName[] { new DotName( "B" ), new DotName( "C" ) };

	try {		
		DotName[] dnaActual2 = dwvl2.unmapSegment( new DotName( "B" ), new DotName( "C" ));
		assertEquals( 2, dnaActual2.length );
		assertEquals( dnaExpected2[0], dnaActual2[0] );
		assertEquals( dnaExpected2[1], dnaActual2[1] );
	} catch ( BadCodeException feared ) {
		fail( "BCE: [" + feared.getMessage() + "]" );
	} catch ( NotFoundException surprise ) {
		fail( surprise.getMessage());
	}

	try {
		dwvl2.unmapSegment( new DotName( "B" ), new DotName( "C" )); // just removed
		fail();
	} catch ( NotFoundException expected ) {
		if ( expected.getMessage().equals( "dot" )) { // because B and C no longer exist
			assertTrue( true );
		} else {
			fail( expected.getMessage());
		}
	} catch ( Exception surprise ) {
		fail();
	}
	
	/*
	 * STAGE 4 CLEANUP: dwvl2 should now be empty, so verify emptiness of the lists (incl. in 
	 * dwvl2's QT _leaves_ (ignore the question of whether the seg list of a non-leaf shall be 
	 * null, or empty)
	 */

	/*
	 * STAGE 4 CLEANUP A: dwvl2's 2 fields that handle its dots should be empty, since the dots were
	 * removed.
	 */
	
	assertEquals( 0, dwvl2.getDotsByName().size());
	assertEquals( 0, dwvl2.getDotsByXY().size()); 

	/*
	 * STAGE 4 CLEANUP B: verify correctness of dwvl2's segsOrderedBySpec field - confirm there 
	 * are no segs there now
	 */
	
	java.util.TreeSet dwvl2sobs2 = dwvl2.getSegsOrderedBySpec();
	assertEquals( 0, dwvl2sobs2.size());

	/*
	 * STAGE 4 CLEANUP C: verify there are no segs remaining in the seg lists in dwvl2's QT _leaves_. 
	 * (ignore the question of whether the seg list of a non-leaf shall be null, or empty)
	 * The leaves themselves haven't been deleted, because so far DWVL code relies on QTNode's 
	 * lazyDelete(), which never prunes the tree.
	 */
	
	QTNode dwvl2tqtr = dwvl2.getTheQTRoot();
	assertEquals( 0, dwvl2tqtr.nw.segs.size());
	assertEquals( 0, dwvl2tqtr.ne.segs.size());
	assertEquals( 0, dwvl2tqtr.sw.segs.size());
	assertEquals( 0, dwvl2tqtr.se.segs.size());

	/*
	 * STAGE 4 CLEANUP D: verify no Dots in dwvl2's QT _leaves_ 
	 */
	
	assertEquals( null, dwvl2tqtr.nw.dot );
	assertEquals( null, dwvl2tqtr.ne.dot );
	assertEquals( null, dwvl2tqtr.sw.dot );
	assertEquals( null, dwvl2tqtr.se.dot );
	

	
	/* The JUnit tool assertEquals() relies on equals(), except the assertion passes given
	 * 2 null args. Make sure the type of the args overrides equals()!
	 */
}
	
/**
 * Obtain a quadrilateral dot world (from prefabDWVLOf4Dots), then unmap 2 of the 4 segs.
 * 
 * @throws DotNameFormatException
 * @throws DotColorFormatException
 */
public final void testUnmapSegment3() throws DotNameFormatException, DotColorFormatException {

	DotWorldVeriList dwvl = prefabDWVLOf4Dots();
	quizPrefabDWVLOf4Dots( dwvl ); // confirm prefabDWVLOf4Dots still works

	DotName[] dnaExpected1 = new DotName[] { new DotName( "D" ), new DotName( "A" ) };
	try {	DotName[] dnaActual1 = dwvl.unmapSegment( new DotName( "D" ), new DotName( "A" ));
			assertEquals( 2, dnaActual1.length );
			assertEquals( dnaExpected1[0], dnaActual1[0] );
			assertEquals( dnaExpected1[1], dnaActual1[1] );
	} catch ( BadCodeException feared ) { fail( "BCE: [" + feared.getMessage() + "]" );
	} catch ( NotFoundException surprise ) { fail( surprise.getMessage()); }

	DotName[] dnaExpected2 = new DotName[] { new DotName( "C" ), new DotName( "D" ) };
	try {	DotName[] dnaActual2 = dwvl.unmapSegment( new DotName( "C" ), new DotName( "D" ));
			assertEquals( 2, dnaActual2.length );
			assertEquals( dnaExpected2[0], dnaActual2[0] );
			assertEquals( dnaExpected2[1], dnaActual2[1] );
	} catch ( BadCodeException feared ) { fail( "BCE: [" + feared.getMessage() + "]" );
	} catch ( NotFoundException surprise ) { fail( surprise.getMessage()); }
	
	// now the trick: dwvl should now look just like a prefab from quizPrefabDWVLOf3Dots()
	quizPrefabDWVLOf3Dots( dwvl );
	
}

}
