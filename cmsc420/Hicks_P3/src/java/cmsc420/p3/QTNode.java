package cmsc420.p3;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import cmsc420.exceptions.BadCodeException;
import cmsc420.exceptions.MaxDepthException;

/**
 * All nodes for the PM1QT should be instances of this class. There are 3 kinds of nodes: internal; 
 * black leaves (leaves with data); and white leaves (empty leaves). The tree is full, so each node is
 * either a leaf, or an internal node with exactly 4 children.
 */
public class QTNode {

	private static Logger lg = Logger.getLogger( "cmsc420.p3.QTNode" );
	private static final String LOG_LVL = Messages.getString( "QTNode.LOG_LVL" );
	private static final String LOG_FORMTR = Messages.getString( "QTNode.LOG_FORMTR" );
	private static final String PRJ_DIR = Messages.getString( "PRJ_DIR" );
	private static final String LOG_PATTERN = Messages.getString( "QTNode.LOG_PATTERN" );
	private static final String LOG = PRJ_DIR + LOG_PATTERN;

	static {
		lg.setLevel( java.util.logging.Level.parse( LOG_LVL ) );
		// default handler would simply drop sub-INFO messages
		try {
			java.util.logging.Handler fh = new java.util.logging.FileHandler( LOG );
			// XML or plaintext?
			if ( LOG_FORMTR.equals( "java.util.logging.XMLFormatter" ) ) {
				fh.setFormatter( new java.util.logging.XMLFormatter() );
			} else {
				fh.setFormatter( new java.util.logging.SimpleFormatter() );
			}
			lg.addHandler( fh );
		} catch ( IOException e ) {
		}
	}
	
/**
 * 2 of the 4 corners; the others are computed. These are ints (wrapped as Coords). The intervals are 
 * closed, i.e. the area of the node includes the 4 corner points and the 4 corner-to-corner boundaries.<p><p>
 * 
 * Example: if lx is 4 and uy is 7, then the node includes the point (4.0,7.0) at its NW corner. This
 * means that an "(integer,integer)" point like (4.0,7.0) may appear in 4 nodes at once, if it serves as a
 * corner. Similarly, a Seg may lie along the boundary between two nodes, in which case both QTNodes
 * include it.<p><p>
 * 
 * The specs state that the minumum area for a node is 1x1, so the fact that Coords are integers is no more
 * constraining than are the specs - i.e. no need for lx, ly, ux, uy to hold floating-point values.<p><p>
 */
public Coord lx, ly, ux, uy;

/**
 * There are 3 kinds of nodes: internal; black leaves (leaves with data); and white leaves (empty
 * leaves).<p><p>
 * 
 * The tree is full, so each node is either a leaf, or an internal node with exactly 4 children.
 * Therefore these 4 child nodes are null refs iff this node is a leaf, else they're all non-null.
 * I.e. white nodes are instantiated, differing from black nodes only in in their dot and seg
 * fields.
 */
public QTNode nw, ne, sw, se;

/**
 * Is bigger than the minimum area permitted for a node, i.e. not too small to split
 */
public boolean canBeSplit;

/**
 * Is a leaf (and so may be black or white), as opposed to an internal node
 */
public boolean isLeaf;


/**
 * null iff node contains no Dot. This is always the case with internal nodes and white leaves, and may be
 * the case with a given black leaf (iff the black leaf contains exactly 1 q-edge that's not a self-loop).
 */
public Dot dot;

/**
 * Never null, even for an internal node. An empty list iff node has no q-edges (i.e. internal node or 
 * white leaf).
 */
public SegList segs;

/**
 * Called only when root is created.
 * 
 * @param aCoord The x (or, equivalently, y) coord for the upper-rightmost pixel. Must be a power of
 *            2. If, e.g., 32, then the QT covers an area from (0.0,0.0) to (32.0,32.0). The
 *            assumption that a node can not measure less than 1x1 is currently hard-coded, so
 *            additionally aCoord must not be less than 1 (which would imply a QT covering the area
 *            from (0.0,0.0) to (1.0,1.0)
 */
public QTNode( int aCoord ) {
	lx = new Coord( 0 );
	ly = new Coord( 0 );
	ux = new Coord( aCoord );
	uy = new Coord( aCoord );
	if ( 1 < aCoord ) {
		canBeSplit = true;
	} else {
		canBeSplit = false;
	}
	isLeaf = true;
	nw = ne = sw = se = null;
	dot = null;
	segs = new SegList();
}

/**
 * Called for every node other than the root.
 * 
 * @param lxParent
 * @param lyParent
 * @param uxParent
 * @param uyParent
 * @param placement
 * @throws BadCodeException
 */
public QTNode( Coord lxParent, Coord lyParent, Coord uxParent, Coord uyParent, String placement )
				throws BadCodeException {
	if ( 1 < (uxParent.coord - lxParent.coord) ) {
		canBeSplit = true;
	} else {
		canBeSplit = false;
	}
	isLeaf = true;
	nw = ne = sw = se = null;
	dot = null;
	segs = new SegList();
		
		// uxParent - lxParent = 2^k for some k >= 0, with 1 + 2^k slots
		// left to right inclusive in each row of the square
		//
	
		//    01   12   23   34   45   56   67   78 ...
	
		//      012       234       456       678 ...
	
		//    |_0            4_| |_4             8_||_8        12_||_12 16_| ...
	
		//    |_0                                8_||_8                 16_||_16 24_||_24 32_|
	
		//    |_0                                                       16_||_16          32_|
	
		//    |_0                                                                         32_|
		
	/* example: root is         lx 0,  ly 0,  ux 32, uy 32
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
	int xMidval = lxParent.coord + ((uxParent.coord - lxParent.coord) / 2);
	int yMidval = lyParent.coord + ((uyParent.coord - lyParent.coord) / 2);
	if ( placement.equals( "nw" ) ) {
		lx = new Coord( lxParent.coord );
		ly = new Coord( yMidval );
		ux = new Coord( xMidval );
		uy = new Coord( uyParent.coord );
	} else if ( placement.equals( "ne" ) ) {
		lx = new Coord( xMidval );
		ly = new Coord( yMidval );
		ux = new Coord( uxParent.coord );
		uy = new Coord( uyParent.coord );
	} else if ( placement.equals( "sw" ) ) {
		lx = new Coord( lxParent.coord );
		ly = new Coord( lyParent.coord );
		ux = new Coord( xMidval );
		uy = new Coord( yMidval );
	} else if ( placement.equals( "se" ) ) {
		lx = new Coord( xMidval );
		ly = new Coord( lyParent.coord );
		ux = new Coord( uxParent.coord );
		uy = new Coord( yMidval );
	} else {
		throw new BadCodeException();
	}
}

/**
 * Meant to be called only by unit tests. To honor the contract of this class, do not pass a null ref as
 * the arg for segs.
 * 
 * @param lx
 * @param ly
 * @param ux
 * @param uy
 * @param nw
 * @param ne
 * @param sw
 * @param se
 * @param canBeSplit
 * @param isLeaf
 * @param dot
 * @param segs
 */
public QTNode( int lx, int ly, int ux, int uy, QTNode nw, QTNode ne, QTNode sw, QTNode se,
				boolean canBeSplit, boolean isLeaf, Dot dot, SegList segs ) {
	super();
	this.lx = new Coord( lx );
	this.ly = new Coord( ly );
	this.ux = new Coord( ux );
	this.uy = new Coord( uy );
	this.nw = nw;
	this.ne = ne;
	this.sw = sw;
	this.se = se;
	this.canBeSplit = canBeSplit;
	this.isLeaf = isLeaf;
	this.dot = dot;
	this.segs = segs;
}

/** 
 * Turns the node from a white leaf into an interior node
 * @throws BadCodeException
 * @throws MaxDepthException if a subdivision smaller than 1x1 would be forced
 */
public void splitEmptyLeaf() throws BadCodeException, MaxDepthException {
	if ( !canBeSplit ) { 
			/*
			 * from spec: "If a subdivision smaller than 1x1 would be forced then this
			 * should also be treated as an intersection."
			 */
		throw new MaxDepthException( "this empty leaf can not be split", 
			((null == dot) ? "NIL" : dot.getName().getName() ), 
			segs.size(), lx.coord, ly.coord, ux.coord, uy.coord );
	}
	isLeaf = false;
	nw = new QTNode( lx, ly, ux, uy, "nw" );
	ne = new QTNode( lx, ly, ux, uy, "ne" );
	sw = new QTNode( lx, ly, ux, uy, "sw" );
	se = new QTNode( lx, ly, ux, uy, "se" );
	// dot remains null and segs remains an empty set
}

/**
 * Invoke only on white (empty) leaves.
 * 
 * @param theSeg 
 * @throws BadCodeException
 * @throws IntersectionException
 */
public void addToEmptyLeaf( Seg theSeg ) throws BadCodeException, MaxDepthException {

	boolean isSelfLoop = theSeg.getGreater().getName().getName().equals( theSeg.getLesser().getName().getName() );
	if ( liesOver( theSeg.getLesser() ) && liesOver( theSeg.getGreater() ) && !(isSelfLoop) ) {
		splitEmptyLeaf();
		add( theSeg );
	} else if ( liesOver( theSeg.getLesser() ) && liesOver( theSeg.getGreater() ) && isSelfLoop ) {
		dot = theSeg.getLesser(); // could have used the greater instead - same dot, after all
		segs.add( theSeg ); // segs is an empty list, never a null ref
	} else if ( liesOver( theSeg.getLesser() ) ) {
		dot = theSeg.getLesser();
		segs.add( theSeg );
	} else if ( liesOver( theSeg.getGreater() ) ) {
		dot = theSeg.getGreater();
		segs.add( theSeg );
	} else {
		segs.add( theSeg );
	}
}

/** 
 * Invoke only on a black leaf without a dot (i.e. it has 1 seg, one which is not a self-loop).
 * @throws BadCodeException
 * @throws MaxDepthException if a subdivision smaller than 1x1 would be forced
 */
public void split0Dot1EdgeLeaf() throws BadCodeException, MaxDepthException {
	if ( !canBeSplit ) { 
		/*
		 * from spec: "If a subdivision smaller than 1x1 would be forced then this
		 * should also be treated as an intersection."
		 */
		throw new MaxDepthException( "this 0Dot1Edge leaf can not be split", 
			((null == dot) ? "NIL" : dot.getName().getName() ), 
			segs.size(), lx.coord, ly.coord, ux.coord, uy.coord );
	}
	isLeaf = false; // turns from black leaf to interior node
	nw = new QTNode( lx, ly, ux, uy, "nw" );
	ne = new QTNode( lx, ly, ux, uy, "ne" );
	sw = new QTNode( lx, ly, ux, uy, "sw" );
	se = new QTNode( lx, ly, ux, uy, "se" );
	// dot remains null, but one q-edge to fwd to kids
	Seg extantSeg = (Seg) segs.first(); // prepare to empty the segs field
	segs = new SegList(); // now a proper internal node.
	add( extantSeg ); // re-insert the old q-edge, but at the child level
}

public void addTo0Dot1EdgeLeaf( Seg theSeg ) throws BadCodeException, MaxDepthException {
	split0Dot1EdgeLeaf();
	add( theSeg );
}

public void split1DotNEdgeLeaf() throws BadCodeException, MaxDepthException {
	if ( !canBeSplit ) { 
		/*
		 * from spec: "If a subdivision smaller than 1x1 would be forced then this
		 * should also be treated as an intersection."
		 */
		throw new MaxDepthException( "this 1DotNEdge leaf can not be split", 
			((null == dot) ? "NIL" : dot.getName().getName() ), 
			segs.size(), lx.coord, ly.coord, ux.coord, uy.coord );
	}
	isLeaf = false; // turns from black to grey
	nw = new QTNode( lx, ly, ux, uy, "nw" );
	ne = new QTNode( lx, ly, ux, uy, "ne" );
	sw = new QTNode( lx, ly, ux, uy, "sw" );
	se = new QTNode( lx, ly, ux, uy, "se" );
	dot = null; // dot becomes null, since it's now a non-leaf (i.e. grey)
	SegList extantSegList = segs; // prepare to empty the segs field
	segs = new SegList(); // now a proper internal node
	Iterator iter = extantSegList.iterator();
	while ( iter.hasNext() ) {
		add( (Seg) iter.next() ); // re-insert the old q-edge(s), but at the child level
	}
}

public void addTo1DotNEdgeLeaf( Seg theSeg ) throws BadCodeException, MaxDepthException {
	if ( theSeg.getLesser().equals( dot ) && theSeg.getGreater().equals( dot ) ) {
			/* shouldn't add self-loop to a non-empty leaf. This is due to the semantics that say a
			 * self-loop must be isolated, so a pre-existing dot (which by def has seg(s) already)
			 * can't acquire a self-loop. This method is for a leaf with a dot already present.
			 */
		throw new BadCodeException( "attempt to add self-loop to 1DotNEdgeLeaf" );
	} else if ( theSeg.getLesser().equals( dot ) && liesOver( theSeg.getGreater() ) ) {
		// this node's Dot is the lesser, and this square overlays the greater - must split
		split1DotNEdgeLeaf();
		add( theSeg );
	} else if ( theSeg.getGreater().equals( dot ) && liesOver( theSeg.getLesser() ) ) {
		// 	this node's Dot is the greater, and this square overlays the lesser - must split
		split1DotNEdgeLeaf();
		add( theSeg );
	} else if ( theSeg.getLesser().equals( dot ) && !liesOver( theSeg.getGreater() ) ) {
		// this node's Dot is the lesser, and this square doesn't overlay the greater - no split
		segs.add( theSeg );
	} else if ( theSeg.getGreater().equals( dot ) && !liesOver( theSeg.getLesser() ) ) {
		// this node's Dot is the greater, and this square doesn't overlay the lesser - no split
		segs.add( theSeg );
	} else if ( liesOver( theSeg.getLesser() ) && liesOver( theSeg.getGreater() ) ) {
		// the square overlays both, though neither one is the dot - must split
		split1DotNEdgeLeaf();
		add( theSeg );
	} else if ( liesOver( theSeg.getLesser() ) || liesOver( theSeg.getGreater() ) ) {
		// the square overlays exactly one, and neither one is the dot - must split
		split1DotNEdgeLeaf();
		add( theSeg );
	} else { // square overlays neither, therefore unrelated q-edge. Must split.
		split1DotNEdgeLeaf();
		add( theSeg );
	}
}

/** 
 * This low-level method must add theSeg to <i>each</i> existing node that deserves it, splitting nodes 
 * where necessary, but without attempting to combine nodes. I.e. it must be ignorant of the fact that
 * Quadtrees avoid unnecesary branches. This is vital because elsewhere I rely on the programming pattern 
 * of "split this node, then re-insert the stuff I just took from this node" in order to split non-empty
 * leaves.
 * @param theSeg
 * @throws BadCodeException
 * @throws MaxDepthException
 */
public void add( Seg theSeg ) throws BadCodeException, MaxDepthException {
	if ( liesOver( theSeg ) ) {
System.out.println( "*lies over*" + theSeg.toString() + " on " + "[lx=" + lx.coord + "ly=" + ly.coord
				+ "ux=" + ux.coord + "uy=" + uy.coord + ", dot="
				+ ((null == dot) ? "NIL" : dot.getName().getName()) + "]" );	
		if ( !isLeaf ) { // adding a seg can't make it a leaf, so 4 recursions in sequence are ok
System.out.println( "*not a leaf*" + theSeg.toString() + " on " + "[lx=" + lx.coord + "ly=" + ly.coord
				+ "ux=" + ux.coord + "uy=" + uy.coord + ", dot="
				+ ((null == dot) ? "NIL" : dot.getName().getName()) + "]" );				
			nw.add( theSeg );
			ne.add( theSeg );
			sw.add( theSeg );
			se.add( theSeg );
		} else { // it's a leaf
System.out.println( "*it's a leaf*" + theSeg.toString() + " on " + "[lx=" + lx.coord + "ly=" + ly.coord
				+ "ux=" + ux.coord + "uy=" + uy.coord + ", dot="
				+ ((null == dot) ? "NIL" : dot.getName().getName()) + "]" );							
			if ( null != dot ) { // black node with Dot, and incident q-edge(s)
System.out.println( "*here's a dot*" + theSeg.toString() + " on " + "[lx=" + lx.coord + "ly=" + ly.coord
				+ "ux=" + ux.coord + "uy=" + uy.coord + ", dot="
				+ ((null == dot) ? "NIL" : dot.getName().getName()) + "]" );								
				addTo1DotNEdgeLeaf( theSeg );
			} else { // no dot, but may have a seg
System.out.println( "*no dot*" + theSeg.toString() + " on " + "[lx=" + lx.coord + "ly=" + ly.coord
				+ "ux=" + ux.coord + "uy=" + uy.coord + ", dot="
				+ ((null == dot) ? "NIL" : dot.getName().getName()) + "]" );								
				if ( segs.isEmpty() ) { // white node
System.out.println( "*no seg*" + theSeg.toString() + " on " + "[lx=" + lx.coord + "ly=" + ly.coord
				+ "ux=" + ux.coord + "uy=" + uy.coord + ", dot="
				+ ((null == dot) ? "NIL" : dot.getName().getName()) + "]" );									
					addToEmptyLeaf( theSeg );
				} else { // black node with no Dot (and therefore exactly one q-edge)
System.out.println( "*here's a seg*" + theSeg.toString() + " on " + "[lx=" + lx.coord + "ly=" + ly.coord
				+ "ux=" + ux.coord + "uy=" + uy.coord + ", dot="
				+ ((null == dot) ? "NIL" : dot.getName().getName()) + "]" );									
					addTo0Dot1EdgeLeaf( theSeg );
				}
			}
		}
	} // else doesn't lie over the seg, so do nothing - crucial to correctness of the code above
}

/** 
 * "Collapse", for our purposes, means to turn an internal node with 4 <i>leaf</i> children into a leaf.
 * I.e. only an internal that is immediately above 4 leaves is a candidate for collapsing. The sibling 
 * method delete() accommodates this behavior, because it calls this method only <i>after</i> recursing.
 * Therefore, if an internal node is not above 4 leaves when this method is called, it means that prior
 * calls have failed to collapse one or more of the children, so there is no way to collapse the parent. 
 * 
 * When will 
 * 
 * 
 * When can a branch be collapsed? When a leaf changes from black (non-empty) to white (empty).  (A) 
 * 
 * No other deletes lead to a collapse. This is due in part to the fact that the semantics of this class
 * are for a PM<i>1</i>QT specifically. Two or more q-edges can share a node iff their common endpoint is
 * actually present in the node with them. Therefore two 0Dot1Edge nodes can never be collapsed
 * together (in a PM<i>3</i>QT, e.g., this might be different.) TODO sure they can! Same edge...
 * 
 * Two 1DotNEdge nodes can be, 
 * 
 * @return
 */
private boolean tryToCollapse() {
	return true;
}

/** 
 * The only permissible invocation of this method (other than its own recursive calls) is on the QT root.
 * It means, "remove this seg from the QT, and make the QT correct+consistent without it: i.e. remove 
 * dot(s) from the QT if necessary, and collapse branches where possible. But don't touch anything outside
 * the QT (not possible anyway, since the QT has no such references.)"<p><p>
 * @param theSeg
 * @return meaningless int, for now
 * @throws BadCodeException
 */
public int delete( Seg theSeg ) throws BadCodeException {
	if ( liesOver( theSeg ) ) { // must be first check - code below relies on it
		if ( !isLeaf ) {
			nw.delete( theSeg );
			ne.delete( theSeg );
			sw.delete( theSeg );
			se.delete( theSeg );
			tryToCollapse(); // this line is the only diff from lazyDelete()
		} else { // it's a leaf
			if ( null != dot ) { // black leaf with 1 Dot, and 1 or more incident q-edge(s)
				assert 0 < segs.size();
				assert segs.contains( theSeg );
				boolean succeeded = segs.remove( theSeg );
				assert succeeded; // asserts themselves typically shouldn't have side-effects
					// that may have been only q-edge here; if so, remove Dot too - only within the QT 
				if ( 0 == segs.size() ) { dot = null; }
			} else { // black leaf with no dot (and therefore exactly one q-edge)
				assert !segs.isEmpty(); // white leaf? impossible since it lies over the seg
				assert 1 == 	segs.size();
				assert theSeg.equals( segs.first() );
				segs.clear(); // this leaf's now white
			}
		}
	} // else doesn't lie over the seg, so do nothing - crucial to correctness of the code above
	return 888;
}	

/** 
 * The only permissible invocation of this method (other than its own recursive calls) is on the QT root.
 * It means, "remove this seg from the QT, and make the QT <i>almost</i>-correct without it: i.e. remove 
 * dot(s) from the QT if necessary, <b>except</b> don't collapse any branches. But don't touch anything 
 * outside the QT (not possible anyway, since the QT has no such references.)"<p><p>
 * 
 * The only difference from the sibling method delete() is that this method does no collapsing.
 * @param theSeg
 * @return meaningless int, for now
 * @throws BadCodeException
 */
public int lazyDelete( Seg theSeg ) throws BadCodeException {
	if ( liesOver( theSeg ) ) {
		if ( !isLeaf ) {
			nw.lazyDelete( theSeg );
			ne.lazyDelete( theSeg );
			sw.lazyDelete( theSeg );
			se.lazyDelete( theSeg );
		} else { // it's a leaf
			if ( null != dot ) { // black node with 1 Dot, and 1 or more incident q-edge(s)
				if (( 0 == segs.size() ) || !( segs.contains( theSeg ))) {
					throw new BadCodeException( "in lazyDelete: " + this.toString() + ", " + theSeg.toString() + ", " + segs.size() + ", " + ", seg isn't there." );
				} else {
					if (!(segs.remove( theSeg ))) { throw new BadCodeException( "in lazyDelete: _A_" ); }
						// that may have been the only q-edge here; if so, remove the Dot too (this is still
						// lazy; lazy merely means we don't collapse any part of the QT) - only within the QT 
					if ( 0 == segs.size() ) { dot = null; }
				}
			} else { // no dot, but may have a seg
				if ( segs.isEmpty() ) { // white node - impossible since it lies over the seg
					throw new BadCodeException( "in lazyDelete: " + this.toString() + ", " + theSeg.toString() + ", white node - impossible since it lies over the seg." );
				} else { // black node with no Dot (and therefore exactly one q-edge)
					if (( 1 != segs.size() ) || !( theSeg.equals( segs.first() ))) {
						throw new BadCodeException( "in lazyDelete: " + this.toString() + ", " + theSeg.toString() + ", " + segs.size() + ", " + segs.first() + ", seg isn't the expected one." );
					} else {
						segs.clear();
					}
				}
			}
		}
	} // else doesn't lie over the seg, so do nothing - crucial to correctness of the code above
	return 777;
}

private boolean liesOver( Dot aDot ) {
	return aDot.overlapsRect( lx, ly, ux, uy );
}

public boolean liesOver( Seg theSeg ) {
	return theSeg.overlapsRect( lx, ly, ux, uy );
}

public String toString() {
	StringBuffer sb = new StringBuffer();
	if ( !isLeaf ) {
		sb.append( "\nNW " );
		if ( null != nw ) {
			sb.append( nw.toString() );
		}
		sb.append( "\nNE " );
		if ( null != ne ) {
			sb.append( ne.toString() );
		}
		sb.append( "\nSW " );
		if ( null != sw ) {
			sb.append( sw.toString() );
		}
		sb.append( "\nSE " );
		if ( null != se ) {
			sb.append( se.toString() );
		}
	} else {
		sb.append( ((null == dot) ? "" : (dot.toString() + ":")) + segs.toString());
	}
	//sb.append( "\n" );
	return sb.toString();
}

}
