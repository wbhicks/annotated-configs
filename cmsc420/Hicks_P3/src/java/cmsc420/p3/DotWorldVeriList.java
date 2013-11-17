package cmsc420.p3;

import java.math.BigInteger;
import java.util.LinkedHashMap;

import cmsc420.exceptions.BTreeInitException;
import cmsc420.exceptions.BadCodeException;
import cmsc420.exceptions.CoordDuplicateException;
import cmsc420.exceptions.DotColorFormatException;
import cmsc420.exceptions.DotNameDuplicateException;
import cmsc420.exceptions.DotNameFormatException;
import cmsc420.exceptions.DotNameRangeException;
import cmsc420.exceptions.IntersectionException;
import cmsc420.exceptions.MaxDepthException;
import cmsc420.exceptions.NotFoundException;
import cmsc420.exceptions.QTDuplicateException;
import cmsc420.exceptions.RangeException;

/**
 * For testing only! A brute-force implementation of
 * DotWorld. This is not intended as an implementation of
 * the project. Meant to verify output, thus "Veri".
 * 
 * @author wbhicks
 * 
 * @version
 */
public class DotWorldVeriList implements DotWorld {

private java.util.TreeMap dotsByName = new java.util.TreeMap();
private java.util.TreeMap dotsByXY = new java.util.TreeMap();

/**
 * Because it holds only Segs, it uses the natural ordering
 * for Segs I defined via Seg.compareTo() - i.e. the
 * ordering required by the spec
 */
private java.util.TreeSet segsOrderedBySpec = new java.util.TreeSet();
private QTNode theQTRoot;

/**
 * setDrawMode() sets all 3 of these at once, but it's mandated by the API.
 */
private int drawModeXSize, drawModeYSize, drawModeStep;

private boolean qtHasBeenInitialized = false;
private boolean bptHasBeenInitialized = false;

/**
 * This no-arg constructor must be explicitly defined (here) because there exists another 
 * constructor (below). 
 */
public DotWorldVeriList() {
	super();
}

/**
 * This constructor exists to support the Inversion Of Control programming pattern, specifically in
 * the JUnit test class that covers this class. I want to be able to construct an object with 
 * "ready-made" values for its fields, then perform a unit test on one of its methods. <br>These 
 * settings override any initializers above.
 * 
 * @param dotsByName
 * @param dotsByXY
 * @param segsOrderedBySpec
 * @param theQTRoot
 * @param drawModeXSize
 * @param drawModeYSize
 * @param drawModeStep
 * @param qtHasBeenInitialized
 * @param bptHasBeenInitialized
 */
public DotWorldVeriList(
	java.util.TreeMap dotsByName,
	java.util.TreeMap dotsByXY,
	java.util.TreeSet segsOrderedBySpec,
	QTNode theQTRoot,
	int drawModeXSize, int drawModeYSize, int drawModeStep,
	boolean qtHasBeenInitialized, boolean bptHasBeenInitialized ) {
	
	super();
	 this.dotsByName = dotsByName;
	 this.dotsByXY = dotsByXY;
	 this.segsOrderedBySpec = segsOrderedBySpec;
	 this.theQTRoot = theQTRoot;
	 this.drawModeXSize = drawModeXSize;
	 this.drawModeYSize = drawModeYSize;
	 this.drawModeStep = drawModeStep;
	 this.qtHasBeenInitialized = qtHasBeenInitialized;
	 this.bptHasBeenInitialized = bptHasBeenInitialized;

}

public FrameViz[] animHorizPath( Coord xminCoord, Coord xmaxCoord, Coord yposCoord ) {
int xmin = xminCoord.coord;
int xmax = xmaxCoord.coord;
int ypos = yposCoord.coord;
// specs say drawModeStep will be > 0 anyway
int step = ((drawModeStep > 0) ? drawModeStep : 1);
int numOfFrames = (1 // first frame
// other frames
+ ((xmax - xmin) / step)
// a final frame, iff the xmax wasn't stepped on
+ ((((xmax - xmin) % step) != 0) ? 1 : 0));
FrameViz[] arr = new FrameViz[numOfFrames];
// all but the last frame march in step
for ( int i = 0; i < (arr.length - 1); i++ ) {
	arr[i] = drawFrameN( (xmin + (step * i)), ypos, i );
}
arr[arr.length - 1] = drawFrameN( xmax, ypos, (arr.length - 1) );
return arr;
}

/*
 * why doesn't this retn anything? -- because it's not in
 * the 3e spec. Implemented here as a utility function
 * (called from colorDotsByName), but remains public to
 * comply with DotWorld iface.
 */
public boolean colorDot( DotName dn, DotColor dc ) {

if ( dotsByName.containsKey( dn ) ) {
	(( Dot ) ( dotsByName.get(dn) )).setColor( dc );
    return true;
} else {
	return false;
}
}

/**
 * @param sublist
 *            a view of only those Dots to be recolored.
 *            Since this is a view, they all exist, and
 *            there's no point in counting how many get
 *            recolored (they all do).
 * @param dc
 *            the desired color
 */
private void colorDots( DotList sublist, DotColor dc ) {
java.util.Iterator iter = sublist.iterator();
Dot currentDot = null;
while ( iter.hasNext() ) {
	currentDot = (Dot) iter.next();
	currentDot.setColor( dc );
}
}

private int colorDotsByName( DotName[] targetDots, DotColor dc ) {
int numberOfTargetsRecolored = 0;
for ( int i = 0; i < targetDots.length; i++ ) {
	if ( colorDot( targetDots[i], dc ) ) {
		numberOfTargetsRecolored++;
	}
}
return numberOfTargetsRecolored;
}

public int colorSegs( Coord lx, Coord ly, Coord ux, Coord uy, DotColor dc ) {

int count = 0;
java.util.Iterator iter = segsOrderedBySpec.iterator();
Seg currentSeg = null;
while ( iter.hasNext() ) {
	currentSeg = (Seg) iter.next();
	if ( currentSeg.overlapsRect( lx, ly, ux, uy ) ) {
		currentSeg.getLesser().setColor( dc );
		currentSeg.getGreater().setColor( dc );
		count++;
	}
}
return count;
}

/**
 * Don't add to QT, merely to dotsByName and dotsByXY
 */
public Dot createDot( DotName name, Coord x, Coord y, Radius radius, DotColor color )
				throws DotNameDuplicateException, CoordDuplicateException, DotNameFormatException,
				DotColorFormatException {

	CoordTuple xy = new CoordTuple( x, y );
		// specs say to complain about dupe name, not dupe coords, if both are the case
	if ( dotsByName.containsKey( name ) ) {
		throw new DotNameDuplicateException( "unused message", name.getName() );
	} else if ( dotsByXY.containsKey( xy ) ) {
		throw new CoordDuplicateException( "unused message", ((Dot) (dotsByXY.get( xy ))).getName().getName() );
	} else {
		try {
			// if this throws, there's bad code elsewhere
			Dot d = new Dot( name, x, y, radius, color );
			dotsByName.put( name, d );
			dotsByXY.put( xy, d );
			return d;
		} catch ( DotNameFormatException e ) {
			throw new DotNameFormatException();
		} catch ( DotColorFormatException e ) {
			throw new DotColorFormatException();
		}
	}
}

/**
 * why doesn't this retn anything? -- because it's not in
 * the 3e spec
 */
public void createPath( DotName string, DotName string2 ) {
return;
}

/**
 * Since I've already coded unmapSegment, I'll implement deleteDot by invoking unmapSegment
 * repeatedly, on each Seg of the Dot. The final invocation will automatically eliminate the Dot
 * from the DotWorld. Therefore don't manipulate any field directly from here.
 * 
 * The (awkward?) use of an array instead of an iterator is because we can't modify the map while
 * traversing a view of it.
 */
public DotName deleteDot( DotName theName ) throws NotFoundException, BadCodeException {
    if (!dotsByName.containsKey( theName )) {
        throw new NotFoundException();
    } else {
        Dot theDot = (Dot) dotsByName.get( theName );
        DotName[] dna = theDot.getAllAdjacentNames();
        for (int i = 0; i < dna.length; i++ ) {
        		unmapSegment( theName, dna[i] );
        		/* this was the 1st implementation: the line below was the body of the for loop. Did it ever work?
             * theDot.removeAdjacentAndSeg( dna[i] );
             */
        }
        return theName;
    }
}

/*
 * why doesn't this retn anything? -- because it's not in
 * the 3e spec
 */
public void deletePath( DotName string, DotName string2 ) {
return;
}

public FrameViz drawFrame( Coord coordCenterX, Coord coordCenterY ) {
	return drawFrameN( coordCenterX.coord, coordCenterY.coord, 0 );
}

public FrameViz drawFrameN( int centerX, int centerY, int frameNum ) {
	StringBuffer sb = new StringBuffer( "Frame " + frameNum + "\n" );
	java.util.TreeSet so = selectOverlappers( centerX, centerY );
	java.util.Iterator iter = so.iterator();
	Seg currentSeg = null;
	while ( iter.hasNext() ) {
		currentSeg = (Seg) iter.next();
		sb.append( "(" + currentSeg.getLesser().getName().getName() + ","
						+ currentSeg.getGreater().getName().getName() + ")" );
	}
	return new FrameViz( sb.toString() );
}

public void exit() {
	return;
}

/**
 * No unit test needed; too simple to break _on its own_. <br>
 * Makes unit testing easier. There is no setter.
 * @return Returns bptHasBeenInitialized.
 */
public boolean getBptHasBeenInitialized() {
	return bptHasBeenInitialized;
}

/**
 * No unit test needed; too simple to break _on its own_. <br>
 * Makes unit testing easier. There is no setter.
 * @return Returns dotsByName.
 */
public java.util.TreeMap getDotsByName() {
	return dotsByName;
}

/**
 * No unit test needed; too simple to break _on its own_. <br>
 * Makes unit testing easier. There is no setter.
 * @return Returns dotsByXY.
 */
public java.util.TreeMap getDotsByXY() {
	return dotsByXY;
}

/**
 * No unit test needed; too simple to break _on its own_. <br>
 * Makes unit testing easier. There is no setter.
 * @return Returns drawModeStep.
 */
public int getDrawModeStep() {
	return drawModeStep;
}

/**
 * No unit test needed; too simple to break _on its own_. <br>
 * Makes unit testing easier. There is no setter.
 * @return Returns drawModeXSize.
 */
public int getDrawModeXSize() {
	return drawModeXSize;
}

/**
 * No unit test needed; too simple to break _on its own_. <br>
 * Makes unit testing easier. There is no setter.
 * @return Returns drawModeYSize.
 */
public int getDrawModeYSize() {
	return drawModeYSize;
}

/**
 * No unit test needed; too simple to break _on its own_. <br>
 * Makes unit testing easier. There is no setter.
 * @return Returns qtHasBeenInitialized.
 */
public boolean getQtHasBeenInitialized() {
	return qtHasBeenInitialized;
}

/**
 * No unit test needed; too simple to break _on its own_. <br>
 * Makes unit testing easier. There is no setter.
 * @return Returns segsOrderedBySpec.
 */
public java.util.TreeSet getSegsOrderedBySpec() {
	return segsOrderedBySpec;
}

/**
 * No unit test needed; too simple to break _on its own_. <br>
 * Makes unit testing easier. There is no setter.
 * @return Returns theQTRoot.
 */
public QTNode getTheQTRoot() {
	return theQTRoot;
}

/**
 * By the spec, a bad magnitude is not like a garbage color. Instead, it provokes its own error
 * response. Therefore the range checking occurs here, not in the QTMagnitude constructor.
 */
public void initQT( QTMagnitude qtm ) throws RangeException, QTDuplicateException {
    /* from specs: "If size is less than 2 or more than 30 print an error."
     */
if ( (2 > qtm.n) || (30 < qtm.n) ) {
	throw new RangeException();
} else if ( qtHasBeenInitialized ) {
    throw new QTDuplicateException();
} else {
	qtHasBeenInitialized = true;
	BigInteger temp2 = new BigInteger( "2" );
	BigInteger tempPow = temp2.pow( qtm.n );
	int tempResult = tempPow.intValue();
	theQTRoot = new QTNode( tempResult ); 
    return;
}
}

/** why doesn't this retn anything? -- because it's not in the 3e spec
 */
public void listDots() {
return;
}

/** 
 * When no probe is required, call this one - it calls the probed version with a throwaway probe
 * @param a
 * @param b
 * @return <code>true</code> iff segsOrderedBySpec has an unrelated seg that liesBetween the Dots
 * @throws NotFoundException iff one or both DotNames are not in dotsByName
 * @throws BadCodeException
 */
public boolean intersectsAny( DotName a, DotName b ) throws NotFoundException, BadCodeException {
	return intersectsAny( a, b, new LinkedHashMap() );
}

/** 
 * This is a utility method and should be private. It's not in the DotWorld API. It's called only from the
 * sibling method mapSeg. I've temporarily made it public for unit-testing.<p>
 * <p>
 * liesBetween works given 1 self-loop, but not 2. This method (which calls liesBetween) goes further and
 * handles correctly comparisons btwn 2 self-loops (which happens whenever a DotWorld acquires its 2nd
 * self-loop).<p>
 * <p>
 * The problem is that, if you have a self-loop in hand, you can't simply iterate through all the segs in
 * the DotWorld and rely on liesBetween to tell you if the prospective self-loop conflicts with any segs.
 * The reason: each existing self-loop in segsOrderedBySpec <i>will</i> cause liesBetween to return 
 * <code>true</code> when it's compared to the prospective self-loop. This "false positive" is unacceptable - 
 * it would prevent DotWorld from holding more than 1 self-loop at a time!<p>
 * <p>
 * False negatives, by contrast, are ok - whenever intersectsAny returns <code>false</code>, it means at
 * worst that a conflict will be discovered when the QT throws an MDE.
 * @param a
 * @param b
 * @param probe for testing
 * @return <code>true</code> (usually) if segsOrderedBySpec has an unrelated seg that liesBetween the Dots,
 * though it doesn't catch all conflicts. <code>true</code> constitutes a guarantee that the mapping would
 * conflict. <code>false</code> guarantees nothing.
 * @throws NotFoundException iff one or both DotNames are not in dotsByName
 */
public boolean intersectsAny( DotName a, DotName b, LinkedHashMap probe ) throws 
		NotFoundException, BadCodeException {
	if (!( dotsByName.containsKey( a ) && dotsByName.containsKey( b ))) {
	    throw new NotFoundException();
	}
	Coord ax = ((Dot) dotsByName.get( a )).getX();
	Coord ay = ((Dot) dotsByName.get( a )).getY();
	Coord bx = ((Dot) dotsByName.get( b )).getX();
	Coord by = ((Dot) dotsByName.get( b )).getY();
	if ( a.equals( b ) && (0 < ((Dot) dotsByName.get( a )).getNumOfAdjacentsAndSegs()) ) {
			// It's a prospective self-loop for a dot that already has seg(s).
		probe.put( "message", "self-loop has seg(s) already" );
	    return true;
	} else if ( a.equals( b )) {
			/* It's the other kind of prospective self-loop (i.e. for a segless dot). The only reason
			 * for this method to return true in such a case is if the dot lies in the middle of a seg.
			 * liesBetween will wrongly return true whenever we iterate upon a self-loop (because it's
			 * then comparing 2 self-loops), so we'll correct for that. No other self-loop can obstruct
			 * this one, because that would imply that two dots can occupy one point.
			 */
	    java.util.Iterator iter = segsOrderedBySpec.iterator();
	    Seg currentSeg = null;
	    while ( iter.hasNext() ) {
	        currentSeg = (Seg) iter.next();
	        if ( currentSeg.liesBetween( ax, ay, ax, ay ) // self-loop, so might as well use only a
	        				// ... and currentSeg is not self-loop
	        			&& !(currentSeg.getGreater().getName().equals( currentSeg.getLesser().getName()))) {
	        		probe.put( "message", "another Seg liesBe	tween" );
	        		probe.put( "currentSeg", currentSeg.toString() );
	        		return true;
	        }
	    }
	    return false;
	} else if ( (!(a.equals( b ))) && // not self-loop, and ...
				(   (((Dot) dotsByName.get( a )).isAdjacentTo( a )) || // a has self-loop, or ...
					(((Dot) dotsByName.get( b )).isAdjacentTo( b )) // b has self-loop
				)) {
			 // this seg is predestined to be thwarted by a self-loop at one of the ends
		probe.put( "message", "non-self-loop, but self-loop(s) at endpoint(s)" );
	    return true;
	} else {
			/* It's not a self-loop, and neither endpoint has a self-loop. If there's a self-loop
			 * obstructing it in its middle, that will be discovered exactly like a criss-cross by
			 * liesBetween: i.e. liesBetween works correctly unless _both_ segs compared are self-loops. 
			 */
	    java.util.Iterator iter = segsOrderedBySpec.iterator();
	    Seg currentSeg = null;
	    while ( iter.hasNext() ) {
	        currentSeg = (Seg) iter.next();
	        if ( currentSeg.liesBetween( ax, ay, bx, by ) // not self-loop, so a and b are distinct
		            && (!(currentSeg.getLesser().getName().equals( a )))
		            && (!(currentSeg.getGreater().getName().equals( b )))
		            && (!(currentSeg.getLesser().getName().equals( b )))
		            && (!(currentSeg.getGreater().getName().equals( a )))) {
	        		probe.put( "message", "another Seg liesBe	tween" );
	        		probe.put( "currentSeg", currentSeg.toString() );
	        		return true;
	        }
	    }
	    return false;
	}
}

public DotName[] mapSeg( DotName aName, DotName bName ) 
    		throws NotFoundException, IntersectionException, BadCodeException {
	if (!( dotsByName.containsKey( aName ) && dotsByName.containsKey( bName ))) {
	    throw new NotFoundException( "raised by mapSeg, i.e. one or both names not in dotsByName" );
	} else if ( intersectsAny( aName, bName )) { // works for self-loops too
	    throw new IntersectionException( "raised by mapSeg, i.e. intersectsAny returned true" );
	} else {
	    Dot aDot = (Dot) dotsByName.get( aName );
	    Dot bDot = (Dot) dotsByName.get( bName );
	    Seg theSeg = new Seg( aDot, bDot ); // Seg constructor will disregard their order here
	    try {
	        theQTRoot.add( theSeg );	
	    } catch ( MaxDepthException mde ) {
	        throw new IntersectionException( 
				"raised by mapSeg( " + aName.getName() + ", " + bName.getName() + " ) to relay an MDE: ", 
				mde );
	    }
	    segsOrderedBySpec.add( theSeg );
	    aDot.addAdjacentAndSeg( bDot, theSeg ); // even a self-loop needs a dot-to-dot pointer
	    if (!( aName.equals( bName ))) { // not a self-loop
	    		bDot.addAdjacentAndSeg( aDot, theSeg ); // the 2nd of the 2 typical pointers
	    }
	        // specs say to return names in same order as given in command line
	    DotName[] report = new DotName[] { aName, bName };
	    return report;
	}
}

public SegWithDistance nearestSegToPoint( Coord xCoord, Coord yCoord ) throws NotFoundException {
    int x = xCoord.coord;
    int y = yCoord.coord;
    double theWinningDistance = -1.0; // bogus initialization
    Seg theWinningSeg = null; // bogus initialization
    java.util.Iterator iter = segsOrderedBySpec.iterator();
    Seg currentSeg = null;
        // initialize the winners
    if ( iter.hasNext() ) {
        currentSeg = (Seg) iter.next();
        theWinningSeg = currentSeg;
    	theWinningDistance = currentSeg.distance( x, y );
    } else {
    	throw new NotFoundException();
    }
    while ( iter.hasNext() ) {
        currentSeg = (Seg) iter.next();
        if ( currentSeg.distance( x, y ) < theWinningDistance ) {
            theWinningSeg = currentSeg;
            theWinningDistance = currentSeg.distance( x, y );
        }
    }
    return new SegWithDistance( theWinningSeg.getLesser().getName(),
                    theWinningSeg.getGreater().getName(), theWinningDistance );
}

/** not implemented for now TODO
 */
public BPTreeViz printBPTree() throws NotFoundException {
	return new BPTreeViz( "this is obviously the wrong bptv constructor" );
}

/** not implemented for now - or is this line it? TODO
 */
public QTViz printQT() throws NotFoundException {
	return new QTViz( theQTRoot.toString() );
}

/**
 * very inefficient, but for unit testing only TODO
 */
public DotList rangeDots( DotName dn1, DotName dn2 ) throws DotNameRangeException {
		// from spec: "If name1 > name2 The dots must be listed in reverse strcmp"
	DotList result = new DotList();
	boolean listMustBeReversed = (dn1.compareTo( dn2 ) > 0);
	//java.util.Iterator iter = dotsByName.entrySet().iterator();
	java.util.Iterator iter = dotsByName.values().iterator();
	if ( null == dotsByName || 0 == dotsByName.size() ) { throw new DotNameRangeException(); }
	Dot currentDot = null;
	while ( iter.hasNext() ) {
		currentDot = (Dot) iter.next(); // CCE!!!!!!
		// from spec: dn1, dn2 are inclusive
		if ( (currentDot.getName().compareTo( dn1 ) >= 0) && (currentDot.getName().compareTo( dn2 ) <= 0) ) {
			// add it
			if ( listMustBeReversed ) {
				result.addFirst( currentDot );
			} else {
				result.addLast( currentDot ); // identical to add()
			}
		}
	}
	if ( 0 == result.size() ) {
		throw new DotNameRangeException();
	} else {
		return result;
	}
}

private java.util.TreeSet selectOverlappers( Coord lx, Coord ly, Coord ux, Coord uy ) {

java.util.TreeSet overlappers = new java.util.TreeSet();

// iterate over segsOrderedBySpec, not overlappers
java.util.Iterator iter = segsOrderedBySpec.iterator();
Seg currentSeg = null;
while ( iter.hasNext() ) {
	currentSeg = (Seg) iter.next();
	if ( currentSeg.overlapsRect( lx, ly, ux, uy ) ) {
		overlappers.add( currentSeg );
	}
}
return overlappers;
}

private java.util.TreeSet selectOverlappers( int centerX, int centerY ) {

Coord lx = new Coord( centerX - drawModeXSize );
Coord ly = new Coord( centerY - drawModeYSize );
Coord ux = new Coord( centerX + drawModeXSize );
Coord uy = new Coord( centerY + drawModeYSize );
return selectOverlappers( lx, ly, ux, uy );
}

public BTreeOrder setBPTreeOrder( BTreeOrder bto ) throws BTreeInitException {
	if ( bptHasBeenInitialized ) {
	
		throw new BTreeInitException();
	} else {
	
		bptHasBeenInitialized = true;
		// this is a verification (reference implementation) class, so do nothing - no B+T
		return bto;
	}

}

/**
 * specs say to echo only, therefore void. Mode controlled within Parser, not DotWorld, so ignored
 * here. Note that this is not really a setter in the conventional sense, since it sets three fields
 * at once. This class has no setters, although it has a set-all constructor and getters for unit 
 * testing.
 */
public void setDrawMode( String mode, Integer xsize, Integer ysize, Integer step ) {
drawModeXSize = xsize.intValue();
drawModeYSize = ysize.intValue();
drawModeStep = step.intValue();
return;
}

/**
 * why doesn't this retn anything? -- because it's not in
 * the 3e spec
 */
public void shortestPath( DotName string, DotName string2 ) {
return;
}

public DotName[] unmapSegment( DotName aName, DotName bName ) throws NotFoundException, BadCodeException {
	Dot aDot = (Dot) dotsByName.get( aName ); // aDot is null if no such dot exists - not an error
	Dot bDot = (Dot) dotsByName.get( bName ); // ditto
	    // by spec, dot error must have priority
	if (!( dotsByName.containsKey( aName ) && dotsByName.containsKey( bName ))) {
	    throw new NotFoundException( "dot" );
	        /*
			 * We need a ref to the Seg in order to remove it. That is, if we can't get a ref to "the"
			 * Seg, then it doesn't exist and there's no way to ask the TreeSet about it. (i.e.
			 * segsOrderedBySpec is a Set not a Map). However, merely to see if it exists we can ask one
			 * of the Dots if the other is an adjacent. Below, we could have used the two names in
			 * either order in the expression; if one has a ref to the other we'll trust the other has a
			 * ref to the first, too.
			 */
	} else if (!aDot.isAdjacentTo( bName )) { // either order ok
	    	throw new NotFoundException( "segment" );
	} else { // the Seg exists, and might be self-loop

			/* Where is the Seg referenced? (1) segsOrderedBySpec in this class; (2) the bDot's
			 * segsByName, (3) the aDot's segsByName, (4) the QT. Must remove from each.
			 * TODO refactor segsOrderedBySpec so it's a map, not a set. Then we won't need
	         * to get a ref to the Seg to remove it from segsOrderedBySpec, just some kind of key.
	         */
	    Seg theSeg = aDot.getSegByName( bName ); // either order ok
	    if (!segsOrderedBySpec.remove( theSeg )) { throw new BadCodeException( "_A_" + theSeg.toString() ); }
	    
	    int aLinksPre;
		try { aLinksPre = aDot.getNumOfAdjacentsAndSegs(); } catch ( BadCodeException e ) { throw new BadCodeException( "_1_" + e.getMessage()); }
		if (( aDot.removeAdjacentAndSeg( bName ) != (aLinksPre - 1) ) || ( aLinksPre < 1 )) { throw new BadCodeException( "_B_" + theSeg.toString() + "; alpr=" + aLinksPre ); }
		int aLinksPost;
		try { aLinksPost = aDot.getNumOfAdjacentsAndSegs(); } catch ( BadCodeException e2 ) { throw new BadCodeException( "_3_" + e2.getMessage()); }
	    if (( aLinksPost < 0 ) || ( aLinksPost != (aLinksPre - 1))) { throw new BadCodeException( "_D_" + theSeg.toString() + "; alpr=" + aLinksPre + "; alpo=" + aLinksPost ); }
		
		    	/* Since we just invoked removeAdjacentAndSeg, it's possible that one or both Dots must be
			 * eliminated. If a Dot must be removed, where is it referenced? (1) dotsByName (here), (2)
			 * dotsByXY (here). The links count is zero, so there are no others.
			 */
	    if ( aLinksPost == 0 ) {
	        dotsByName.remove( aName );
	        CoordTuple aDotXY = new CoordTuple( aDot.getX(), aDot.getY() );
	        dotsByXY.remove( aDotXY );
	    }
	
	    if (!aName.equals( bName )) { // not a self-loop
			int bLinksPre;
			try { bLinksPre = bDot.getNumOfAdjacentsAndSegs(); } catch ( BadCodeException e1 ) { throw new BadCodeException( "_2_" + e1.getMessage()); }
			if (( bDot.removeAdjacentAndSeg( aName ) != (bLinksPre - 1) ) || ( bLinksPre < 1 )) { throw new BadCodeException( "_C_" + theSeg.toString() + "; alpr=" + aLinksPre + "; blpr=" + bLinksPre ); }
		    int bLinksPost;
		    try { bLinksPost = bDot.getNumOfAdjacentsAndSegs(); } catch ( BadCodeException e3 ) { throw new BadCodeException( "_4_" + e3.getMessage()); }
		    if (( bLinksPost < 0 ) || ( bLinksPost != (bLinksPre - 1))) { throw new BadCodeException( "_E_" + theSeg.toString() + "; alpr=" + aLinksPre + "; blpr=" + bLinksPre + "; alpo=" + aLinksPost + "; blpo=" + bLinksPost ); }

			    	/* Since we just invoked removeAdjacentAndSeg, it's possible that one or both Dots must be
				 * eliminated. If a Dot must be removed, where is it referenced? (1) dotsByName (here), (2)
				 * dotsByXY (here). The links count is zero, so there are no others.
				 */
		    if ( bLinksPost == 0 ) {
		        dotsByName.remove( bName );
		        CoordTuple bDotXY = new CoordTuple( bDot.getX(), bDot.getY() );
		        dotsByXY.remove( bDotXY );
		    }
	    }
	    try { theQTRoot.lazyDelete( theSeg ); } catch ( BadCodeException e4 ) { throw new BadCodeException( "_F_" + e4.getMessage()); }
	        // specs say to return names in same order as given in command line
	    DotName[] report = new DotName[] { aName, bName };
	    return report;
	}
}

}
















