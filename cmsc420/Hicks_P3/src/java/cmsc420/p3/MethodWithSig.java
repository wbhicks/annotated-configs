package cmsc420.p3;

/**
 * The 2-arg MethodWithSig constructor is the "directory"
 * that maps cmds in the cmd-line language to method
 * calls in the DotWorld API. In the future, other
 * classes besides DotWorld could somehow "register"
 * their methods here (for now, the mapping is hard-
 * coded).
 * 
 * @author wbhicks
 * 
 * @version
 */
public class MethodWithSig {

public boolean success;
public Class[] ca;
public java.lang.reflect.Method method;

public MethodWithSig() {
super();
}

/**
 * success = true IFF the String is one of the
 * "pre-approved" command names. The constructor knows about
 * some classes, and some methods in those classes.
 * (Currently, DotWorld is the only such class. All its
 * methods are, and must be, known to this constructor since
 * it is used to translate between the command line language
 * and the DotWorld API.)
 * 
 * @param theClass
 * @param s
 */
public MethodWithSig( Class theClass, String s ) {
super();
this.success = false;

if ( DotWorld.class.equals( theClass ) && (null != s)
	&& (0 < s.length()) ) {
	try {
		if ( s.equals( "ANIMATE_HORIZONTAL_PATH" ) ) {
			this.ca = new Class[] { Coord.class,
				Coord.class, Coord.class};
			this.method = DotWorld.class
				.getMethod( "animHorizPath", this.ca );
			this.success = true;
		} else if ( s.equals( "COLOR_DOT" ) ) {
			this.ca = new Class[] { DotName.class,
				DotColor.class};
			this.method = DotWorld.class
				.getMethod( "colorDot", this.ca );
			this.success = true;
		} else if ( s.equals( "COLOR_SEGMENTS" ) ) {
			this.ca = new Class[] { Coord.class,
				Coord.class, Coord.class, Coord.class,
				DotColor.class};
			this.method = DotWorld.class
				.getMethod( "colorSegs", this.ca );
			this.success = true;
		} else if ( s.equals( "CREATE_DOT" ) ) {
			this.ca = new Class[] { DotName.class,
				Coord.class, Coord.class, Radius.class,
				DotColor.class};
			this.method = DotWorld.class
				.getMethod( "createDot", this.ca );
			this.success = true;
		} else if ( s.equals( "CREATE_PATH" ) ) {
			this.ca = new Class[] { DotName.class,
				DotName.class};
			this.method = DotWorld.class
				.getMethod( "createPath", this.ca );
			this.success = true;
		} else if ( s.equals( "DELETE_DOT" ) ) {
			this.ca = new Class[] { DotName.class};
			this.method = DotWorld.class
				.getMethod( "deleteDot", this.ca );
			this.success = true;
		} else if ( s.equals( "DELETE_PATH" ) ) {
			this.ca = new Class[] { DotName.class,
				DotName.class};
			this.method = DotWorld.class
				.getMethod( "deletePath", this.ca );
			this.success = true;
		} else if ( s.equals( "DRAW_FRAME" ) ) {
			this.ca = new Class[] { Coord.class,
				Coord.class};
			this.method = DotWorld.class
				.getMethod( "drawFrame", this.ca );
			this.success = true;
		} else if ( s.equals( "EXIT" ) ) {
			this.ca = new Class[0];
			this.method = DotWorld.class.getMethod( "exit",
				this.ca );
			this.success = true;
		} else if ( s.equals( "INIT_QUADTREE" ) ) {
			this.ca = new Class[] { QTMagnitude.class };
			this.method = DotWorld.class
				.getMethod( "initQT", this.ca );
			this.success = true;
		} else if ( s.equals( "LIST_DOTS" ) ) {
			this.ca = new Class[0];
			this.method = DotWorld.class
				.getMethod( "listDots", this.ca );
			this.success = true;
		} else if ( s.equals( "MAP_SEGMENT" ) ) {
			this.ca = new Class[] { DotName.class,
				DotName.class};
			this.method = DotWorld.class
				.getMethod( "mapSeg", this.ca );
			this.success = true;
		} else if ( s.equals( "NEAREST_SEG_TO_POINT" ) ) {
			this.ca = new Class[] { Coord.class,
				Coord.class};
			this.method = DotWorld.class
				.getMethod( "nearestSegToPoint", this.ca );
			this.success = true;
        } else if ( s.equals( "PRINT_BPTREE" ) ) {
			this.ca = new Class[0];
			this.method = DotWorld.class
				.getMethod( "printBPTree", this.ca );
			this.success = true;
		} else if ( s.equals( "PRINT_QUADTREE" ) ) {
			this.ca = new Class[0];
			this.method = DotWorld.class
				.getMethod( "printQT", this.ca );
			this.success = true;
		} else if ( s.equals( "RANGE_DOTS" ) ) {
			this.ca = new Class[] { DotName.class,
				DotName.class};
			this.method = DotWorld.class
				.getMethod( "rangeDots", this.ca );
			this.success = true;
		} else if ( s.equals( "SET_BPTREE_ORDER" ) ) {
			this.ca = new Class[] { BTreeOrder.class};
			this.method = DotWorld.class
				.getMethod( "setBPTreeOrder", this.ca );
			this.success = true;
		} else if ( s.equals( "SET_DRAW_MODE" ) ) {
			this.ca = new Class[] { String.class,
				Integer.class, Integer.class, Integer.class};
			this.method = DotWorld.class
				.getMethod( "setDrawMode", this.ca );
			this.success = true;
		} else if ( s.equals( "SHORTEST_PATH" ) ) {
			this.ca = new Class[] { DotName.class,
				DotName.class};
			this.method = DotWorld.class
				.getMethod( "shortestPath", this.ca );
			this.success = true;
		} else if ( s.equals( "UNMAP_SEGMENT" ) ) {
			this.ca = new Class[] { DotName.class,
				DotName.class};
			this.method = DotWorld.class
				.getMethod( "unmapSegment", this.ca );
			this.success = true;
		} // else success is already false
	} catch ( NoSuchMethodException e ) {
		// success is already false
	}
} // else success is already false
} // end method

} // end class
