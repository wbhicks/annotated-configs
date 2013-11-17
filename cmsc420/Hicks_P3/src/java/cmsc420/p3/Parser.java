package cmsc420.p3;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import cmsc420.exceptions.BTreeInitException;
import cmsc420.exceptions.BadCodeException;
import cmsc420.exceptions.CoordDuplicateException;
import cmsc420.exceptions.DotColorFormatException;
import cmsc420.exceptions.DotNameDuplicateException;
import cmsc420.exceptions.DotNameFormatException;
import cmsc420.exceptions.DotNameRangeException;
import cmsc420.exceptions.EmptyArrayException;
import cmsc420.exceptions.EmptyStringException;
import cmsc420.exceptions.IntersectionException;
import cmsc420.exceptions.NotFoundException;
import cmsc420.exceptions.ParseAsFormatException;
import cmsc420.exceptions.QTDuplicateException;
import cmsc420.exceptions.RangeException;

/**
 * COVERED<br>
 * Acts as a wrapper, so that the rest of the app works on commands that could be passed to it via
 * an API. I.e. the Parser class turns the rest of the app into a library with an API. Parser
 * filters input so that each command becomes an array of objects.
 * <p>
 * All methods were static, but now I want to instantiate Parser objects because I want my unit
 * tests to work on 2 Parsers: one using a ref implementation of DotWorld, and one using a
 * production version. Alternatively, I could have had one Parser instance (or else keep it as a
 * static class) and let it manipulate two DotWorlds, but it seemed cleaner to keep DotWorlds and
 * Parsers paired.
 * 
 * @author two methods by Evan Machusak (changes by wbhicks). Rest by wbhicks
 */
public class Parser {

/**
 * Each class should have one, instead of log4j or println's TODO automatically get the arg (via
 * reflection?)
 */
private static Logger lg = Logger.getLogger( "cmsc420.p3.Parser" );
private static final String LOG_LVL = Messages.getString( "Parser.LOG_LVL" );
private static final String LOG_FORMTR = Messages.getString( "Parser.LOG_FORMTR" );
private static final String PRJ_DIR = Messages.getString( "PRJ_DIR" );
private static final String LOG_PATTERN = Messages.getString( "Parser.LOG_PATTERN" );
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
	} catch ( IOException e ) {}
}

/**
 * Apart from logging tools, these are the only fields of the Parser class.
 */
private String drawMode;
private DotWorld ownedDotWorld;

/**
 * No unit test needed; too simple to break _on its own_. drawMode defaults to "TEXT"
 * 
 * @param dw The DotWorld object that this instance will use
 */
public Parser( DotWorld dw ) {
	super();
	ownedDotWorld = dw;
	drawMode = "TEXT";
}

/**
 * No unit test needed; too simple to break _on its own_.
 * 
 * @param dw The DotWorld object that this instance will use 
 * @param dm The initial state of drawMode
 */
public Parser( DotWorld dw, String dm ) {
	super();
	ownedDotWorld = dw;
	drawMode = dm;
}

/**
 * can't be static: uses drawMode. No unit test needed; too simple to break _on its own_. Used, but not tested, by the unit test
 * that tests ANIMATE_HORIZONTAL_PATH
 * 
 * @param s The new state of drawMode
 */
public void setDrawMode( String s ) {
	drawMode = s;
}

/**
 * COVERED <br> Returns true iff the string arg consists of nothing but spaces. To be more robust, it should (1)
 * work with any kind of whitespace, not just spaces, and (2) do something "appropriate" if the
 * string arg is empty. The first is not an issue in this project, because we've already replaced
 * all other kinds of ws with strings, and the second's not an issue because the token (the string
 * arg) won't be formed as an empty string.
 * 
 * TODO what if it's empty?
 * 
 * @author Evan Machusak 
 * @param s a String suspected to be a sequence of spaces 
 * @return true iff s is a sequence of spaces
 */
public static boolean isWS( String s ) {
	for ( int i = 0; i < s.length(); i++ )
		if ( s.charAt( i ) != ' ' ) return false;
	return true;
}

/**
 * COVERED <br> If 1st arg is not a non-empty array of non-empty Strings, throws an appropriate exception.
 * 
 * Asserts nothing about the args themselves, except that there is the correct number of elements in
 * place to match them, and that all elements that can not be args are themselves correct.
 * 
 * If true, then all that's left is to examine the elements that are in the locations where the args
 * are expected - are they really args?
 * 
 * TODO If arg is negative, should method throw exception or just return false? (Currently, it
 * returns false.)
 * 
 * @param theCmdLine a non-empty array of non-empty Strings
 * @param expectedNumOfArgs
 * @return true iff right delimiters are in right places
 * @throws EmptyStringException
 */
public static boolean hasValidDelimiterPattern( String[] theCmdLine, int expectedNumOfArgs )
				throws EmptyArrayException, EmptyStringException {

	if ( null == theCmdLine ) { throw new NullPointerException(
					"wanted String array (poss. empty), got null ref" ); }
	if ( 0 == theCmdLine.length ) { throw new EmptyArrayException(
					"got empty array of String, was cmd line empty?" ); }
	for ( int i = 0; i < theCmdLine.length; i++ ) {
		if ( null == theCmdLine[i] ) { throw new NullPointerException(
						"wanted String (poss. empty), got null ref" ); }
		/*
		 * why is length a field for arrays but a method for Strings? What was Sun's thinking?
		 */
		if ( 0 == theCmdLine[i].length() ) { throw new EmptyStringException(); }
	}
	/*
	 * theCmdLine is now known to be a non-empty array of non-empty Strings
	 */
	if ( 0 > expectedNumOfArgs ) { return false; }
	/*
	 * meaningful only if expectedNumOfArgs nonnegative, which it's now known to be
	 */
	int correctLength = ((0 == expectedNumOfArgs) ? 3 : (expectedNumOfArgs * 2 + 2));

	if ( theCmdLine.length != correctLength ) { return false; }
	if ( !(theCmdLine[1].equals( "(" )) ) { return false; }
	// length is known to be correct, thus various
	// ways to express this
	if ( !(theCmdLine[correctLength - 1].equals( ")" )) ) { return false; }
	/*
	 * length is correct. Last comma (if any), therefore, should be at theCmdLine[ expectedNumOfArgs *
	 * 2 - 1 ]
	 */
	for ( int i = 3; i <= (expectedNumOfArgs * 2 - 1); i += 2 ) {
		if ( !(theCmdLine[i].equals( "," )) ) { return false; }
	}
	return true;
}

/**
 * COVERED <br>
 * Inspired by Integer's parseInt() et al. Probably superceded by something like generics in JDK
 * 1.5. Parse the string to create an object of the given class. Intended for use by
 * mapStringsToSig(), which needs to do this repeatedly as it assembles an array of args (which
 * mapStringsToSig() returns.) This method returns exactly one such object, so it should be invoked
 * once for each arg in the array that mapStringsToSig() must generate.
 * 
 * The custom-made exception it throws is very likely to be thrown: it will appear every time a
 * "bad" arg appears on the command line. An alternative would have been to return a pair consisting
 * of the object and a boolean success flag.
 * 
 * @param c a Class (the class of the desired object)
 * @param s a String (the string rep of the desired obj)
 * @return the desired object
 * @throws ParseAsFormatException whenever the string is "bad"
 */
public static Object parseAs( Class c, String s ) throws ParseAsFormatException {
	Object result = null;
	if ( c.equals( BTreeOrder.class ) ) {
		try {
			result = new BTreeOrder( Integer.parseInt( s ) );
		} catch ( NumberFormatException e ) {
			throw new ParseAsFormatException();
		}
	} else if ( c.equals( DotName.class ) ) {
		try {
			result = new DotName( s );
		} catch ( DotNameFormatException e ) {
			throw new ParseAsFormatException();
		}
	} else if ( c.equals( DotColor.class ) ) {
		try {
			result = new DotColor( s );
		} catch ( DotColorFormatException e ) {
			throw new ParseAsFormatException();
		}
	} else if ( c.equals( Coord.class ) ) {
		try {
			result = new Coord( Integer.parseInt( s ) );
		} catch ( NumberFormatException e ) {
			throw new ParseAsFormatException();
		}
	} else if ( c.equals( QTMagnitude.class ) ) {
		try {
			result = new QTMagnitude( Integer.parseInt( s ) );
		} catch ( NumberFormatException e ) {
			throw new ParseAsFormatException();
		}
	} else if ( c.equals( Radius.class ) ) {
		try {
			result = new Radius( Integer.parseInt( s ) );
		} catch ( NumberFormatException e ) {
			throw new ParseAsFormatException();
		}
	}
	return result;
}

/**
 * COVERED <br>
 * Assumes the String array has valid delimiter pattern for the given Class array. Use
 * hasValidDelimiterPattern first to ensure this.
 * 
 * Transforms an array of Strings (stringArray) into the array of objects needed as args by some
 * other method, in accordance with the pattern supplied by the array of Classes (classArray).
 * 
 * If classArray is a null ref, throws unchecked exception.
 * 
 * If classArray is empty, then success field of returned object is true and args field of returned
 * object is an empty array.
 * 
 * Iff classArray is a non-empty array, then stringArray is examined. Unspecified behavior if
 * stringArray is "wrong" length. Call this method only with a stringArray of the "right" length.
 * 
 * TODO improve error handling
 * 
 * @param classArray, not null, possibly empty
 * @param stringArray
 * @return a MappedArgs object. Iff its success field is true, then its args field is guaranteed to
 *         hold an array of objects that can be passed to the target method. Otherwise, i.e. success
 *         is false, value of the returned object's args field is unspecified.
 */
public static MappedArgs mapStringsToSig( Class[] classArray, String[] stringArray ) {
	MappedArgs ma = new MappedArgs();
	ma.success = true;
	int cIt, sIt;
	ma.args = new Object[classArray.length];
	for ( cIt = 0; cIt < classArray.length; cIt++ ) {
		sIt = (cIt + 1) * 2;
		try {
			ma.args[cIt] = parseAs( classArray[cIt], stringArray[sIt] );
		} catch ( ParseAsFormatException pafe ) {
			ma.success = false;
		}
	}
	return ma;
}

/**
 * COVERED by testBothVettedAndDispatched()
 * 
 * @param theLine
 * @return true iff the supplied cmd line is a flawless, good-to-go, syntactically correct cmd line that is
 * guaranteed to generate a valid API call.
 */
public static boolean vetted( String[] theLine ) {
	boolean result = true;

	if ( (null == theLine) || (0 == theLine.length) ) {
		result = false;
	} else {
		MethodWithSig mws = new MethodWithSig( DotWorld.class, theLine[0] );
		if ( !mws.success ) {
			result = false;
		} else {
			try {
				result = hasValidDelimiterPattern( theLine, mws.ca.length );
			} catch ( EmptyArrayException e ) {
				result = false;
			} catch ( EmptyStringException e ) {
				result = false;
			}
			if ( result ) {
				MappedArgs ma = mapStringsToSig( mws.ca, theLine );
				if ( !ma.success ) {
					result = false;
				}
			}
		}
	}
	return result;
}

/**
 * COVERED <br>
 * (can't be static: uses drawMode.) <br>
 * toReport is invoked after the actual work has been done by the target method. Its job is to
 * generate the application's command line response to the user, which means principally to
 * transform the object that was returned by the target method into an informational String
 * appropriate for printing. In order to do this, it needs to know which target method got invoked,
 * and sometimes it needs to "cite" one or more of the original args to the target method. So, it is
 * passed the original command line too. It is also passed a flag for valid vs. invalid commands.
 * <br>
 * <br>
 * vetted == false --> other 2 args are ignored <br>
 * <br>
 * vetted == true --> theLine was good-to-go <br>
 * <br>
 * o is a null ref iff target method returns void <br>
 * <br>
 * 
 * @param vetted, true iff theLine was good-to-go (i.e. a flag for valid commands)
 * 
 * @param theLine, a non-empty array of Strings for echoing and for determining which cmd it was.
 *            Examined iff vetted == true.
 * 
 * @param o, the retn val of the target method
 * 
 * @return an informational String appropriate for printing
 * 
 * @throws Exception. This means a coding error - vetted didn't work correctly
 */
public String toReport( boolean vetted, String[] theLine, Object o ) throws BadCodeException {

	if ( !vetted ) {
		/*
		 * this is the only case where we don't echo the (erroneous) command and so don't use
		 * theLine
		 */
		return "*****\nError: Invalid Command.";
	} else {
			/* The newline that precedes the 5 asterisks below is not something I gleaned from the 
			 * specs, but rather inserted to match the sample output.
			 */
		StringBuffer echo = new StringBuffer( "\n*****\n==> " );
		for ( int i = 0; i < theLine.length; i++ ) {
			echo.append( theLine[i] );
		}
		echo.append( "\n" );
		if ( "ANIMATE_HORIZONTAL_PATH".equals( theLine[0] ) ) {
			StringBuffer optFVA = new StringBuffer( "" );
			if ( ("TEXT".equals( drawMode )) || ("BOTH".equals( drawMode )) ) {
				FrameViz[] fva = (FrameViz[]) o;
				for ( int i = 0; i < fva.length; i++ ) {
					optFVA.append( fva[i].toString() ).append( "\n" );
				}
			}
			return echo.toString() + optFVA + "\nAnimation Complete.";
		} else if ( "COLOR_DOT".equals( theLine[0] ) ) {
			throw new BadCodeException(); // not in 3e
		} else if ( "COLOR_SEGMENTS".equals( theLine[0] ) ) {
			return echo + "Update complete.  Found " + ((Integer) o) + " segments.";
		} else if ( "CREATE_DOT".equals( theLine[0] ) ) {
			if ( o instanceof Dot ) {
				Dot d = ((Dot) o);
				return echo + "Created dot " + d.toString() + ".";
			} else if ( o instanceof DotNameDuplicateException ) {
				return echo + "Error: Dot " + ((DotNameDuplicateException) o).name + " already exists.";
			} else if ( o instanceof CoordDuplicateException ) {
				return echo + "Error: Dot " + ((CoordDuplicateException) o).squatter
								+ " already exists at the speci" + "fied coordinates.";
			} else throw new BadCodeException();
		} else if ( "CREATE_PATH".equals( theLine[0] ) ) {
			throw new BadCodeException(); // not in 3e
		} else if ( "DELETE_DOT".equals( theLine[0] ) ) {
			if ( o instanceof DotName ) {
				DotName dn = ((DotName) o);
				return echo + "Deleted dot " + dn.getName() + ".";
			} else if ( o instanceof NotFoundException ) {
				return echo + "Error: The specified dot does not exist.";
			} else throw new BadCodeException();
		} else if ( "DELETE_PATH".equals( theLine[0] ) ) {
			throw new BadCodeException(); // not in 3e
		} else if ( "DRAW_FRAME".equals( theLine[0] ) ) {
			String optFV = "";
			if ( ("TEXT".equals( drawMode )) || ("BOTH".equals( drawMode )) ) {
				optFV = ((FrameViz) o).toString();
			}
			return echo + optFV + "\nDraw Frame Complete.";
		} else if ( "EXIT".equals( theLine[0] ) ) {
			return echo + "Have a nice day!";
		} else if ( "INIT_QUADTREE".equals( theLine[0] ) ) {
			if ( null == o ) {
				return echo + "Quadtree initialized.";
			} else if ( o instanceof RangeException ) {
				return echo + "Error: size out of range.";
			} else if ( o instanceof QTDuplicateException ) {
				return echo + "Error: The Quadtree has alre" + "ady been initialized.";
			} else throw new BadCodeException();
		} else if ( "LIST_DOTS".equals( theLine[0] ) ) {
			throw new BadCodeException(); // not in 3e
		} else if ( "MAP_SEGMENT".equals( theLine[0] ) ) {
			if ( o instanceof DotName[] ) {
				DotName[] dnArr = ((DotName[]) o);
				return echo + "Mapped segment (" + dnArr[0].getName() + "," + dnArr[1].getName() + ").";
			} else if ( o instanceof NotFoundException ) {
				return echo + "Error: The specified dot does not exist.";
			} else if ( o instanceof IntersectionException ) {
				return echo + "Error: Intersection detected.";
				// debug version below. TODO restore to line above
				//		    return echo + "Error: Intersection detected."
				//		    	+ ((IntersectionException) o).getMessage();
			} else {
				System.out.println( o.toString() );
				throw new BadCodeException();
			}
		} else if ( "NEAREST_SEG_TO_POINT".equals( theLine[0] ) ) {
			if ( o instanceof SegWithDistance ) {
				SegWithDistance swd = ((SegWithDistance) o);
				return echo + swd.toString();
			} else if ( o instanceof NotFoundException ) {
				return echo + "Tree is empty.";
			} else throw new BadCodeException();
		} else if ( "PRINT_BPTREE".equals( theLine[0] ) ) {
			if ( o instanceof BPTreeViz ) {
				BPTreeViz bptv = ((BPTreeViz) o);
				return echo + bptv.toString();
			} else if ( o instanceof NotFoundException ) {
				return echo + "Tree is empty.";
			} else throw new BadCodeException();
		} else if ( "PRINT_QUADTREE".equals( theLine[0] ) ) {
			if ( o instanceof QTViz ) {
				QTViz qtv = ((QTViz) o);
				return echo + qtv.toString();
			} else if ( o instanceof NotFoundException ) {
				return echo + "Tree is empty.";
			} else throw new BadCodeException( "BCE at pqt:" + o.toString() );
		} else if ( "RANGE_DOTS".equals( theLine[0] ) ) {
			if ( o instanceof DotList ) {
				DotList dl = ((DotList) o);
				return echo + dl.toString();
			} else if ( o instanceof DotNameRangeException ) {
				return echo + "No matching dots found.";
			} else throw new BadCodeException( " --reason is-- " + o.toString() );
		} else if ( "SET_BPTREE_ORDER".equals( theLine[0] ) ) {
			if ( o instanceof BTreeOrder ) { //BTreeOrder
				BTreeOrder bto = ((BTreeOrder) o);
				return echo + "Order set to " + bto.n + ".";
			} else if ( o instanceof BTreeInitException ) {
				return echo + "Error: B+ tree already initialized.";
			} else {
				System.out.println( ">>>" + o.toString() );
				throw new BadCodeException();
			}
		} else if ( "SET_DRAW_MODE".equals( theLine[0] ) ) {
			return echo.toString(); // specs say: echo only
		} else if ( "SHORTEST_PATH".equals( theLine[0] ) ) {
			throw new BadCodeException(); // not in 3e
		} else if ( "UNMAP_SEGMENT".equals( theLine[0] ) ) {
			if ( o instanceof DotName[] ) {
				DotName[] dn = ((DotName[]) o);
				return echo + "Unmapped Segment (" + dn[0].getName() + "," + dn[1].getName() + ").";
			} else if ( (o instanceof NotFoundException) && (((NotFoundException) o).getMessage().equals("dot")) ) {
				return echo + "Error: The specified dot does not exist.";
			} else if ( (o instanceof NotFoundException)
							&& (((NotFoundException) o).getMessage().equals("segment")) ) {
				return echo + "Error: The specified segme" + "nt was not found on the map.";
			} else throw new BadCodeException();

		} else {
			throw new BadCodeException( "unknown cmd was vetted" );
		}
	} // end else
} // end method

/**
 * No unit test needed; too simple to break _on its own_.
 * 
 * Receives a String to print as output, outputs it, and returns true. The String is informational
 * and is based on the object that was returned by the target method.
 * 
 * @param object
 */
public static boolean reported( String s ) {
	System.out.println( s );
	//lg.fine( s );
	return true;
}

/**
 * COVERED <br>
 * Called only if theLine has been vetted - i.e. the line is translatable into an API call. Can't be
 * static: uses ownedDotWorld. On the field called ownedDotWorld, this method invokes the
 * appropriate target method, with the args extracted from theLine. It uses the MethodWithSig
 * constructor and mapStringsToSig() as helpers.
 * @param theLine. Must be vetted first.
 * @return the same object returned by whatever target method it invoked. May be an exception thrown
 *         by the target method; this is ok and is considered to be another kind of return value
 *         from the target method.
 * @throws IllegalArgumentException
 * @throws IllegalAccessException
 */
public Object invokeTargetMethod( String[] theLine ) throws IllegalArgumentException, IllegalAccessException {
	MethodWithSig mws = new MethodWithSig( DotWorld.class, theLine[0] );
	java.lang.reflect.Method desiredMethod = mws.method;
	MappedArgs ma = mapStringsToSig( mws.ca, theLine );
	Object targetResult = null;
	try {
		//    System.out.println("apple" + desiredMethod + "mac");
		targetResult = desiredMethod.invoke( ownedDotWorld, ma.args );
		//    System.out.println("bapple");
	} catch ( InvocationTargetException ite ) {
		//    System.out.println("capple");
		targetResult = ite.getCause();
		//    System.out.println("dapple");
	}
	return targetResult;
}

/**
 * COVERED by testBothVettedAndDispatched() <br>
 * <br>
 * can't be static: uses invokeTargetMethod() <br>
 * <br>
 * The work of this method is in its side-effect. Its return value is merely the return value of
 * vetted(), which it calls. It uses this value to decide whether to invoke invokeTargetMethod(). In
 * either case, it calls toReport() and reported().
 * @param theLine, a non-empty array of Strings
 * @return A boolean that is currently unused within this class (used only for unit testing). FYI,
 *         this boolean is the same value as that returned by vetted() when called on the first line
 *         of this method.
 * @throws IllegalArgumentException
 * @throws IllegalAccessException
 * @throws BadCodeException from toReport()
 */
public boolean dispatched( String[] theLine ) throws IllegalArgumentException, IllegalAccessException,
				BadCodeException {
	if ( vetted( theLine ) ) {
		reported( toReport( true, theLine, invokeTargetMethod( theLine ) ) );
		return true;
	} else {
		reported( toReport( false, null, null ) );
		return false;
	}
}

/**
 * COVERED <br>
 * can't be static: uses dispatched() <br>
 * 
 * Evan Machusak's method. Some changes by me.
 * 
 * @param args is never used @throws IOException @throws IllegalArgumentException @throws
 * IllegalAccessException @throws BadCodeException, from toReport()
 */
public void topLevel( String[] args ) throws IOException, IllegalArgumentException, IllegalAccessException,
				BadCodeException {

	// Evan's timer. Used once, at the end of this method.
	long timeStamp = System.currentTimeMillis();

	// Evan's setup to get someLineAsString
	java.io.InputStreamReader isr = new java.io.InputStreamReader( System.in );
	java.io.BufferedReader theBufferedReader = new java.io.BufferedReader( isr );
	String someLineAsString;

	while ( (someLineAsString = theBufferedReader.readLine()) != null ) {

		// from Evan's message of June 10
		someLineAsString = someLineAsString.replaceAll( "\t", " " );

		// from Dr. Hugue's suggestion of June 10
		someLineAsString = someLineAsString.toUpperCase();

		// ---- BEGIN don't mess with -----

		int i = 0;
		String[] line_ws = null;
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer( someLineAsString, "(,) ", true );
		line_ws = new String[tokenizer.countTokens()];
		while ( tokenizer.hasMoreTokens() )
			line_ws[i++] = tokenizer.nextToken();
		int ws = 0;
		for ( i = 0; i < line_ws.length; i++ )
			if ( isWS( line_ws[i] ) == false ) ws++;
		String[] someLineAsArrayOfStrings = new String[ws];
		ws = 0;
		for ( i = 0; i < line_ws.length; i++ )
			if ( isWS( line_ws[i] ) == false ) someLineAsArrayOfStrings[ws++] = line_ws[i];

		// ---- END don't mess with -----

		/*
		 * You now have an array of strings, slaaos[], which contains only relevant data and the
		 * punctuation () and ,
		 * 
		 * example tokens: [ CREATE_DOT ] [ ( ] [ name ] [ , ] [ 10 ] [ , ] [ 20 ] [ , ] [ 30 ] [ , ] [
		 * red ] [ ) ]
		 */

		if ( someLineAsArrayOfStrings.length > 0 ) {
			dispatched( someLineAsArrayOfStrings );
		}
	} // end while

	// see first instruction
	lg.fine( "Leaving main. Running time (ms): " + (System.currentTimeMillis() - timeStamp) );
} // end method main

} // end class Parser
