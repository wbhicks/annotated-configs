package cmsc420.p3test;

import java.io.IOException;
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
import cmsc420.p3.BPTreeViz;
import cmsc420.p3.BTreeOrder;
import cmsc420.p3.Dot;
import cmsc420.p3.DotList;
import cmsc420.p3.DotWorldVeriList;
import cmsc420.p3.FrameViz;
import cmsc420.p3.MappedArgs;
import cmsc420.p3.Messages;
import cmsc420.p3.Parser;
import cmsc420.p3.DotName;
import cmsc420.p3.DotColor;
import cmsc420.p3.Coord;
import cmsc420.p3.QTMagnitude;
import cmsc420.p3.QTViz;
import cmsc420.p3.Radius;
import cmsc420.p3.SegWithDistance;

/**
 * Test Evan's Parser class.
 * 
 * @author wbhicks
 */
public class ParserTest extends junit.framework.TestCase {

/**
 * Even this test case class needs a logger, e.g. to report the identity of System.in so I can build
 * asserts to judge it. I use a logger instead of log4j or println's. It must not be final.
 * 
 * TODO the arg to getLogger() should be automatic.
 */
private static Logger lg = Logger.getLogger( "cmsc420.p3test.ParserTest" );

/**
 * final fields must be initialized when declared; thus these can't be stuffed into setUp()
 */
private static final String LOG_LVL = Messages.getString( "ParserTest.LOG_LVL" );
private static final String LOG_FORMTR = Messages.getString( "ParserTest.LOG_FORMTR" );
private static final String PRJ_DIR = Messages.getString( "PRJ_DIR" );
private static final String LOG_PATTERN = Messages.getString( "ParserTest.LOG_PATTERN" );
private static final String LOG = PRJ_DIR + LOG_PATTERN;
private static final String REDIR_DIR = Messages.getString( "REDIR_DIR" );
private static final String REDIR = PRJ_DIR + REDIR_DIR;
private static final String REDIR_1 = REDIR + "keep_for_testing.in";
private static final String REDIR_BLUE = REDIR + "one_blue_dot.in";
private static final String REDIR_RED = REDIR + "one_red_dot.in";
private static final String REDIR_MYTH = REDIR + "SHOULD_NOT_BE.in";

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
		/*
		 * works, but many xcptns to catch
		 * 
		 * fh.setFormatter( (java.util.logging.Formatter) (Class.forName( LOG_FORMTR
		 * ).newInstance()));
		 */
		lg.addHandler( fh );
	} catch ( IOException e ) {}
}

//DotWorld todaysDW;
Parser firstParser;

protected void setUp() throws Exception {
	super.setUp();
	//todaysDW = new DotWorldPM3QT();
	//todaysDW = new DotWorldVeriList();
	//Parser.setDotWorldInstance( todaysDW );
	firstParser = new Parser( new DotWorldVeriList() );
	return;
}

/**
 * empty, for now
 */
protected void tearDown() throws Exception {
	super.tearDown();
}

/**
 * changeStandardIn is no longer a domain method. Now it's in this JUnit test case (class). Hard to
 * test, because changes to System.in must be tested by looking at several things: did the domain
 * method report success, did the string rep of System.in change too, and does a read() reveal the
 * content that proves the right file was obtained?
 * 
 * I no longer use the word "test" in this method name, because (I think) it's not a unit test.
 * Instead, this method tests a sibling method, which is itself a utility method. So, I use "quiz".
 * 
 * TODO apparently, if this method name doesn't start with test, it's not part of the test suite. Do
 * I want it to be? If so, figure out how to explicitly include it in the test suite.
 */
private static void quizChangeStandardIn() {

	/*
	 * to read initial characters in files and thus verify their identities
	 */
	java.io.InputStreamReader isr;

	/*
	 * to use assertNotSame on a pair of InputStreams
	 */
	java.io.InputStream inStrm;

	/*
	 * Does the domain method return the right booleans? And (for this, see the log) is System.in
	 * changing exactly when it should be?
	 * 
	 * TODO See if s0, s1, s2 can be replaced with InputStreams for assertNotSame() testing
	 */
	String s0 = System.in.toString();
	lg.fine( "System.in initially: " + s0 );

	assertTrue( "This file should exist", ParserTest.changeStdIn( REDIR_1 ) );
	String s1 = System.in.toString();
	lg.fine( "Should be REDIR_1: " + s1 );
	assertNotSame( "should be unequal", s0, s1 );

	assertFalse( "This file should _not_ exist", ParserTest.changeStdIn( REDIR_MYTH ) );
	String s2 = System.in.toString();
	lg.fine( "Should be unchanged: " + s2 );
	assertEquals( "Should not have changed", s1, s2 );

	/*
	 * We must test whether the domain method actually obtains the desired file, in addition to
	 * self- reporting success. To do this we use two files, the contents of which are known to us.
	 * We read the first character (actually an int) from each. One starts with 'a' (97), the other
	 * with 'b' (98).
	 */
	try { // logging omitted, for now

		// u c n a?

		assertTrue( "This file should exist", ParserTest.changeStdIn( REDIR + "starts_with_a.in" ) );
		isr = new java.io.InputStreamReader( System.in );
		assertEquals( "Should both be 97", 97, isr.read() );

		// u c a b?

		assertTrue( "This file should exist", ParserTest.changeStdIn( REDIR + "starts_with_b.in" ) );
		isr = new java.io.InputStreamReader( System.in );
		assertEquals( "Should both be 98", 98, isr.read() );
	} catch ( IOException e ) {
		lg.warning( "read() failed! See std err..." );
		e.printStackTrace();
	}

	/*
	 * The blue and red files each begin with the LIST_DOTS command. Therefore their initial chars
	 * should be identical, while System.in should change as it obtains each file in turn.
	 */
	inStrm = System.in;
	assertSame( "The concept of same should work here", inStrm, System.in );

	assertTrue( "blue file should exist", ParserTest.changeStdIn( REDIR_BLUE ) );
	lg.fine( "Should be REDIR_BLUE: " + System.in.toString() );
	assertNotSame( "System.in should have changed", inStrm, System.in );
	inStrm = System.in; // prep inStrm for next comparison
	isr = new java.io.InputStreamReader( System.in );
	int c0 = 1776, c1 = 1848; // unequal
	try {
		c0 = isr.read();
		lg.fine( "REDIR_BLUE's 1st char: " + c0 );
	} catch ( IOException e ) {
		lg.warning( "Couldn't read file! See std err..." );
		e.printStackTrace();
	}
	assertTrue( "red file should exist", ParserTest.changeStdIn( REDIR_RED ) );
	lg.fine( "Should be REDIR_RED: " + System.in.toString() );
	assertNotSame( "System.in should have changed", inStrm, System.in );
	inStrm = System.in; // for next comparison (none written yet)
	isr = new java.io.InputStreamReader( System.in );
	try {
		c1 = isr.read();
		lg.fine( "REDIR_RED's 1st char: " + c1 );
	} catch ( IOException e1 ) {
		lg.warning( "Couldn't read file! See std err..." );
		e1.printStackTrace();
	}
	assertEquals( "1st chars should be equal", c0, c1 );
}

public void testIsWS() {
	assertTrue( "Empty string --> t", Parser.isWS( "" ) );
	assertTrue( "one space --> t", Parser.isWS( " " ) );
	assertTrue( "two spaces --> t", Parser.isWS( "  " ) );
	assertTrue( "three spaces --> t", Parser.isWS( "   " ) );
	assertFalse( "non-space --> f", Parser.isWS( "A" ) );
	assertFalse( "non-spaces --> f", Parser.isWS( "ABC" ) );
	assertFalse( "non-space at end --> f", Parser.isWS( "  A" ) );
	assertFalse( "non-space at beginning --> f", Parser.isWS( "A  " ) );
	assertFalse( "non-space in middle --> f", Parser.isWS( " A " ) );
}

/**
 * This method makes calls to a couple of helper methods. These helper methods conduct loops of
 * assertions. Where the assertions are (thus) wrapped in a brief for loop, it means that I'm using
 * the loop to test a few different int args for the given scenario.
 */
public void testHasValidDelimiterPattern() {

	final int LOOP_MIN = -1;
	final int LOOP_MAX = 4;

	/*
	 * STAGE 1: null ref instead of array
	 */

	String[] saNull = null;

	for ( int i = LOOP_MIN; i < LOOP_MAX; i++ ) {
		try { // should throw NPE
			Parser.hasValidDelimiterPattern( saNull, i );
			fail();
		} catch ( Exception e ) {
			if ( (e instanceof NullPointerException) && (e.getMessage().startsWith( "wanted String array" )) ) {
				// do nothing
			} else fail();
		}
	}

	/*
	 * STAGE 2: empty array
	 */

	String[] sa0 = new String[0];

	for ( int i = LOOP_MIN; i < LOOP_MAX; i++ ) {
		try { // should throw EAE
			Parser.hasValidDelimiterPattern( sa0, i );
			fail();
		} catch ( Exception e ) {
			if ( !(e instanceof EmptyArrayException) ) fail();
		}
	}

	/*
	 * STAGE 3: non-empty array, but some or all elements are null refs
	 */

	String[] sa1ElemIsNull = new String[1]; // the elem is null

	for ( int i = LOOP_MIN; i < LOOP_MAX; i++ ) {
		try { // should throw NPE
			Parser.hasValidDelimiterPattern( sa1ElemIsNull, i );
			fail();
		} catch ( Exception e ) {
			if ( (e instanceof NullPointerException) && (e.getMessage().startsWith( "wanted String (poss" )) ) {
				// do nothing
			} else fail();
		}
	}

	String[] sa3SomeElemsNull = new String[3]; // all elems null

	for ( int i = LOOP_MIN; i < LOOP_MAX; i++ ) {
		try { // should throw NPE
			Parser.hasValidDelimiterPattern( sa3SomeElemsNull, i );
			fail();
		} catch ( Exception e ) {
			if ( (e instanceof NullPointerException) && (e.getMessage().startsWith( "wanted String (poss" )) ) {
				// do nothing
			} else fail();
		}
	}

	sa3SomeElemsNull = new String[3];
	sa3SomeElemsNull[0] = "not null";

	for ( int i = LOOP_MIN; i < LOOP_MAX; i++ ) {
		try { // should throw NPE
			Parser.hasValidDelimiterPattern( sa3SomeElemsNull, i );
			fail();
		} catch ( Exception e ) {
			if ( (e instanceof NullPointerException) && (e.getMessage().startsWith( "wanted String (poss" )) ) {
				// do nothing
			} else fail();
		}
	}

	sa3SomeElemsNull = new String[3];
	sa3SomeElemsNull[2] = "not null";

	for ( int i = LOOP_MIN; i < LOOP_MAX; i++ ) {
		try { // should throw NPE
			Parser.hasValidDelimiterPattern( sa3SomeElemsNull, i );
			fail();
		} catch ( Exception e ) {
			if ( (e instanceof NullPointerException) && (e.getMessage().startsWith( "wanted String (poss" )) ) {
				// do nothing
			} else fail();
		}
	}

	sa3SomeElemsNull = new String[3];
	sa3SomeElemsNull[0] = "not null";
	//sa3SomeElemsNull[1] = "not null either";
	sa3SomeElemsNull[2] = "me too!";

	for ( int i = LOOP_MIN; i < LOOP_MAX; i++ ) {
		try { // should throw NPE
			Parser.hasValidDelimiterPattern( sa3SomeElemsNull, i );
			fail();
		} catch ( Exception e ) {
			if ( (e instanceof NullPointerException) && (e.getMessage().startsWith( "wanted String (poss" )) ) {
				// do nothing
			} else fail();
		}
	}

	/*
	 * STAGE 4: non-empty array, but some or all elements are empty Strings
	 */

	String[] sa1ElemIsEmptyStr = new String[] { "" };

	for ( int i = LOOP_MIN; i < LOOP_MAX; i++ ) {
		try { // should throw ESE
			Parser.hasValidDelimiterPattern( sa1ElemIsEmptyStr, i );
			fail();
		} catch ( Exception e ) {
			if ( e instanceof EmptyStringException ) {
				// do nothing
			} else fail();
		}
	}

	String[] sa3SomeElemsEmpty = new String[] { "", "b", "c" };

	for ( int i = LOOP_MIN; i < LOOP_MAX; i++ ) {
		try { // should throw ESE
			Parser.hasValidDelimiterPattern( sa3SomeElemsEmpty, i );
			fail();
		} catch ( Exception e ) {
			if ( e instanceof EmptyStringException ) {
				// do nothing
			} else fail();
		}
	}

	sa3SomeElemsEmpty = new String[] { "a", "b", "" };

	for ( int i = LOOP_MIN; i < LOOP_MAX; i++ ) {
		try { // should throw ESE
			Parser.hasValidDelimiterPattern( sa3SomeElemsEmpty, i );
			fail();
		} catch ( Exception e ) {
			if ( e instanceof EmptyStringException ) {
				// do nothing
			} else fail();
		}
	}

	sa3SomeElemsEmpty = new String[] { "", "b", "" };

	for ( int i = LOOP_MIN; i < LOOP_MAX; i++ ) {
		try { // should throw ESE
			Parser.hasValidDelimiterPattern( sa3SomeElemsEmpty, i );
			fail();
		} catch ( Exception e ) {
			if ( e instanceof EmptyStringException ) {
				// do nothing
			} else fail();
		}
	}

	sa3SomeElemsEmpty = new String[] { "a", "", "" };

	for ( int i = LOOP_MIN; i < LOOP_MAX; i++ ) {
		try { // should throw ESE
			Parser.hasValidDelimiterPattern( sa3SomeElemsEmpty, i );
			fail();
		} catch ( Exception e ) {
			if ( e instanceof EmptyStringException ) {
				// do nothing
			} else fail();
		}
	}

	sa3SomeElemsEmpty = new String[] { "", "", "c" };

	for ( int i = LOOP_MIN; i < LOOP_MAX; i++ ) {
		try { // should throw ESE
			Parser.hasValidDelimiterPattern( sa3SomeElemsEmpty, i );
			fail();
		} catch ( Exception e ) {
			if ( e instanceof EmptyStringException ) {
				// do nothing
			} else fail();
		}
	}

	sa3SomeElemsEmpty = new String[] { "", "", "" };

	for ( int i = LOOP_MIN; i < LOOP_MAX; i++ ) {
		try { // should throw ESE
			Parser.hasValidDelimiterPattern( sa3SomeElemsEmpty, i );
			fail();
		} catch ( Exception e ) {
			if ( e instanceof EmptyStringException ) {
				// do nothing
			} else fail();
		}
	}

	/*
	 * STAGE 5: non-empty array of non-empty Strings, but pattern invalid for any expectedNumOfArgs
	 */
	looper1ForHVDP( new String[] { "foo" } );
	looper1ForHVDP( new String[] { "(" } );
	looper1ForHVDP( new String[] { ")" } );
	looper1ForHVDP( new String[] { "EXIT" } );
	looper1ForHVDP( new String[] { " " } );
	looper1ForHVDP( new String[] { "EXIT", "(" } );
	looper1ForHVDP( new String[] { "EXIT", ")" } );
	looper1ForHVDP( new String[] { "(", ")" } );
	looper1ForHVDP( new String[] { "EXIT", ")", "(" } );
	looper1ForHVDP( new String[] { "(", ")", "EXIT" } );
	looper1ForHVDP( new String[] { "EXIT", " ", ")" } );
	looper1ForHVDP( new String[] { "EXIT", "(", ")", " " } );
	looper1ForHVDP( new String[] { "EXIT", "(", ")", "hi" } );
	looper1ForHVDP( new String[] { " ", "EXIT", "(", ")" } );

	/*
	 * STAGE 6: non-empty array of non-empty Strings, and typical valid pattern (i.e. valid for one
	 * particular value of expectedNumOfArgs)
	 */

	// 6.1
	try {
		assertTrue( Parser.hasValidDelimiterPattern( new String[] { "EXIT", "(", ")" }, 0 ) );
		assertFalse( Parser.hasValidDelimiterPattern( new String[] { "EXIT", "(", ")" }, -1 ) );
		assertFalse( Parser.hasValidDelimiterPattern( new String[] { "EXIT", "(", ")" }, 1 ) );
		assertFalse( Parser.hasValidDelimiterPattern( new String[] { "EXIT", "(", ")" }, 2 ) );
	} catch ( Exception e ) {
		fail();
	}

	// 6.2

	try {
		assertTrue( Parser.hasValidDelimiterPattern( new String[] { "DELETE_DOT", "(", "FOO", ")" }, 1 ) );
		assertFalse( Parser.hasValidDelimiterPattern( new String[] { "DELETE_DOT", "(", "FOO", ")" }, -1 ) );
		assertFalse( Parser.hasValidDelimiterPattern( new String[] { "DELETE_DOT", "(", "FOO", ")" }, 0 ) );
		assertFalse( Parser.hasValidDelimiterPattern( new String[] { "DELETE_DOT", "(", "FOO", ")" }, 2 ) );
	} catch ( Exception e ) {
		fail();
	}

	// 6.3

	looper2ForHVDP( new String[] { "EXIT", "(", ")" }, 0 );
	looper2ForHVDP( new String[] { "FOO", "(", ")" }, 0 );
	looper2ForHVDP( new String[] { "foo_bar", "(", ")" }, 0 );

	looper2ForHVDP( new String[] { "EXIT", "(", "1", ")" }, 1 );
	looper2ForHVDP( new String[] { "FOO", "(", "3.5", ")" }, 1 );
	looper2ForHVDP( new String[] { "f_b", "(", "#$%", ")" }, 1 );

	looper2ForHVDP( new String[] { "EXIT", "(", "we", ",", "ve", ")" }, 2 );
	looper2ForHVDP( new String[] { "FOO", "(", "we", ",", "ve", ")" }, 2 );
	looper2ForHVDP( new String[] { "foo_bar", "(", " we", ",", "  ", ")" }, 2 );

	looper2ForHVDP( new String[] { "E  ", "(", "   ", ",", " 2", ",", "2", ")" }, 3 );
	looper2ForHVDP( new String[] { "_  ", "(", "we ", ",", "ve", ",", "2", ")" }, 3 );
	looper2ForHVDP( new String[] { "  -", "(", " we", ",", "  ", ",", "2", ")" }, 3 );

}

private static void looper1ForHVDP( String[] sa ) {

	final int LOOP_MIN = -1;
	final int LOOP_MAX = 4;

	for ( int i = LOOP_MIN; i < LOOP_MAX; i++ ) {
		try {
			assertFalse( Parser.hasValidDelimiterPattern( sa, i ) );
		} catch ( Exception e ) {
			fail();
		}
	}
}

private static void looper2ForHVDP( String[] sa, int correct ) {

	final int LOOP_MIN = -1;
	final int LOOP_MAX = 4;

	assertTrue( (LOOP_MIN <= correct) && (LOOP_MAX >= correct) );

	for ( int i = LOOP_MIN; i < LOOP_MAX; i++ ) {
		try {
			if ( i == correct ) assertTrue( Parser.hasValidDelimiterPattern( sa, i ) );
			else assertFalse( Parser.hasValidDelimiterPattern( sa, i ) );
		} catch ( Exception e ) {
			fail();
		}
	}
}

/**
 * No need to test each target method in the API, because the domain method does not have an
 * enumeration of them. Instead it uses reflection. Therefore it's enough to test only with sample
 * target methods. Domain method assumes the arg is a valid line, therefore do not test except with
 * valid lines. <br>
 * @throws BadCodeException
 * @throws IllegalArgumentException
 * @throws IllegalAccessException
 * @throws DotColorFormatException
 * @throws DotNameFormatException
 */
public void testInvokeTargetMethod() throws BadCodeException, IllegalArgumentException, 
	DotNameFormatException, DotColorFormatException, IllegalAccessException {

	/* This is the first time (in this unit test, i.e. method) I'm creating a FOO Dot, so it'll pass
	 * 
	 */
assertEquals( 
	(new Dot( new DotName( "FOO" ), new Coord( 3 ), new Coord( 7 ), new Radius( 5 ), new DotColor( "RED") )
	).toString()
	, 
	firstParser.invokeTargetMethod( 
				new String[] { "CREATE_DOT", "(", "FOO", ",", "3", ",", "7", ",", "5", ",", "RED", ")" } 
	).toString() // works because Dot overrides toString() - no casting necessary
);
	
/* Now, try to create FOO again. It'll throw this exception.
 * Must compare the name fields of the two DotNameDuplicateException objects - their "message" fields
 * may differ and that's ok (since I use such messages for debugging only)
 */
assertEquals(
	( new DotNameDuplicateException( "ignore this message", "FOO" )
	).name
	,
	((DotNameDuplicateException) firstParser.invokeTargetMethod( 
					new String[] { "CREATE_DOT", "(", "FOO", ",", "13", ",", "17", ",", "5", ",", "RED", ")" } 
	)).name // required a cast to DotNameDuplicateException
);
	

/* Now, try to land on FOO's coords with a different Dot. It'll throw a CoordDuplicateException.
 * Must compare the name fields of the two DotNameDuplicateException objects - their "message" fields
 * may differ and that's ok (since I use such messages for debugging only)
 */
assertEquals(
	( new CoordDuplicateException( "ignore this message", "FOO" )
	).squatter
	,
	((CoordDuplicateException) firstParser.invokeTargetMethod( 
					new String[] { "CREATE_DOT", "(", "NOTFOO", ",", "3", ",", "7", ",", "5", ",", "RED", ")" } 
	)).squatter // required a cast to CoordDuplicateException
);
	
}

public void testParseAs() {

	/*
	 * STAGE 0: should return null if given an unexpected class
	 */

	try {
		assertNull( Parser.parseAs( java.text.DecimalFormat.class, "hi" ) );
	} catch ( ParseAsFormatException pafe ) {
		fail();
	}

	/*
	 * test its ability to construct an instance for each class with which it's familiar, using the
	 * string. Try both valid and invalid args
	 */

	/*
	 * STAGE 1: DotName
	 */

	DotName dn1;
	try {
		dn1 = new DotName( "FOO" );
		assertTrue( dn1.equals( (DotName) Parser.parseAs( DotName.class, "FOO" ) ) );
	} catch ( DotNameFormatException e ) {
		fail();
	} catch ( ParseAsFormatException pafe ) {
		fail();
	}

	try {
		DotName dn2 = new DotName( "" );
		fail();
	} catch ( DotNameFormatException e ) {}
	try {
		Parser.parseAs( DotName.class, "" );
		fail();
	} catch ( ParseAsFormatException pafe ) {}

	/*
	 * STAGE 2: DotColor
	 */

	DotColor dc1;
	try {
		dc1 = new DotColor( "BLUE" );
		assertTrue( dc1.equals( (DotColor) Parser.parseAs( DotColor.class, "BLUE" ) ) );
	} catch ( DotColorFormatException e ) {
		fail();
	} catch ( ParseAsFormatException pafe ) {
		fail();
	}

	try {
		DotColor dc2 = new DotColor( "" );
		fail();
	} catch ( DotColorFormatException e ) {}
	try {
		Parser.parseAs( DotColor.class, "" );
		fail();
	} catch ( ParseAsFormatException pafe ) {}

	try {
		DotColor dc3 = new DotColor( "INVALIDCOLOR" );
		fail();
	} catch ( DotColorFormatException e ) {}
	try {
		Parser.parseAs( DotColor.class, "INVALIDCOLOR" );
		fail();
	} catch ( ParseAsFormatException pafe ) {}

	/*
	 * STAGE 3: Coord
	 */

	Coord coord1;
	try {
		coord1 = new Coord( 71 );
		assertTrue( coord1.equals( (Coord) Parser.parseAs( Coord.class, "71" ) ) );
	} catch ( ParseAsFormatException pafe ) {
		fail();
	}

	try {
		Parser.parseAs( Coord.class, "" );
		fail();
	} catch ( ParseAsFormatException pafe ) {}
	try {
		Parser.parseAs( Coord.class, "a" );
		fail();
	} catch ( ParseAsFormatException pafe ) {}
	try {
		Parser.parseAs( Coord.class, " " );
		fail();
	} catch ( ParseAsFormatException pafe ) {}
	try {
		Parser.parseAs( Coord.class, "%" );
		fail();
	} catch ( ParseAsFormatException pafe ) {}

	/*
	 * STAGE 4: Radius
	 */

	Radius radius1;
	try {
		radius1 = new Radius( 20 );
		assertTrue( radius1.equals( (Radius) Parser.parseAs( Radius.class, "20" ) ) );
	} catch ( ParseAsFormatException pafe ) {
		fail();
	}

	try {
		Parser.parseAs( Radius.class, "" );
		fail();
	} catch ( ParseAsFormatException pafe ) {}
	try {
		Parser.parseAs( Radius.class, "a" );
		fail();
	} catch ( ParseAsFormatException pafe ) {}
	try {
		Parser.parseAs( Radius.class, " " );
		fail();
	} catch ( ParseAsFormatException pafe ) {}
	try {
		Parser.parseAs( Radius.class, "%" );
		fail();
	} catch ( ParseAsFormatException pafe ) {}

	/*
	 * STAGE 5: BTreeOrder
	 */

	try {
		BTreeOrder bto1 = new BTreeOrder( 11 );
		assertTrue( bto1.equals( (BTreeOrder) Parser.parseAs( BTreeOrder.class, "11" ) ) );
	} catch ( ParseAsFormatException pafe ) {
		fail();
	}

	try {
		Parser.parseAs( BTreeOrder.class, "" );
		fail();
	} catch ( ParseAsFormatException pafe ) {}
	try {
		Parser.parseAs( BTreeOrder.class, "a" );
		fail();
	} catch ( ParseAsFormatException pafe ) {}
	try {
		Parser.parseAs( BTreeOrder.class, " " );
		fail();
	} catch ( ParseAsFormatException pafe ) {}
	try {
		Parser.parseAs( BTreeOrder.class, "%" );
		fail();
	} catch ( ParseAsFormatException pafe ) {}

	/*
	 * STAGE 6: QTMagnitude
	 */

	try {
		QTMagnitude qtm1 = new QTMagnitude( 5 );
		assertTrue( qtm1.equals( (QTMagnitude) Parser.parseAs( QTMagnitude.class, "5" ) ) );
	} catch ( ParseAsFormatException pafe ) {
		fail();
	}

	try {
		Parser.parseAs( QTMagnitude.class, "" );
		fail();
	} catch ( ParseAsFormatException pafe ) {}
	try {
		Parser.parseAs( QTMagnitude.class, "a" );
		fail();
	} catch ( ParseAsFormatException pafe ) {}
	try {
		Parser.parseAs( QTMagnitude.class, " " );
		fail();
	} catch ( ParseAsFormatException pafe ) {}
	try {
		Parser.parseAs( QTMagnitude.class, "%" );
		fail();
	} catch ( ParseAsFormatException pafe ) {}

}

public void testMapStringsToSig() throws DotNameFormatException, DotColorFormatException {

	// STAGE 1: a good 1-arg cmd line

	DotName ma3dn = new DotName( "BAR" );
	MappedArgs ma3 = new MappedArgs();
	ma3.args = new Object[] { ma3dn };
	Class[] ma4ca = new Class[] { DotName.class };
	String[] ma4sa = new String[] { "X", "<", "BAR", ">" };

	MappedArgs ma4 = Parser.mapStringsToSig( ma4ca, ma4sa );
	assertTrue( ma4.success );

	assertTrue( ma3.args[0] instanceof DotName );
	assertTrue( ma4.args[0] instanceof DotName );
	assertTrue( ((DotName) (ma3.args[0])).getName().equals( "BAR" ) );
	assertTrue( ((DotName) (ma4.args[0])).getName().equals( "BAR" ) );

	// do the name fields of the DotNames agree?
	assertTrue( ((DotName) (ma4.args[0])).getName().equals( ((DotName) (ma3.args[0])).getName() ) );

	// do the DotNames themselves agree?
	assertTrue( ((DotName) (ma4.args[0])).equals( ((DotName) (ma3.args[0])) ) );

	/*
	 * Arrays.equals returns false because it uses the equals() method defined for the _declared
	 * class_ of the array. Since someMappedArgs.args is declared as an array of _Objects_,
	 * Arrays.equals can't take advantage of the equals() methods for DotName, Radius, etc.
	 * 
	 * UPDATE: this equals flipped from false to true once I made DotName implement Comparable. Why?
	 * TODO
	 */
	//assertFalse( java.util.Arrays.equals( ma3.args, ma4.args ));
	assertTrue( java.util.Arrays.equals( ma3.args, ma4.args ) );

	//STAGE 2: a good 4-arg cmd line

	DotName ma5dn = new DotName( "FOO" );
	DotColor ma5dc = new DotColor( "BLUE" );
	Coord ma5c = new Coord( 3 );
	Radius ma5r = new Radius( 7 );

	MappedArgs ma5 = new MappedArgs();
	ma5.args = new Object[] { ma5dn, ma5dc, ma5c, ma5r };

	Class[] ma6ca = new Class[] { DotName.class, DotColor.class, Coord.class, Radius.class };
	String[] ma6sa = new String[] { "X", "<", "FOO", "comma", "BLUE", "comma", "3", "comma", "7", ">" };

	MappedArgs ma6 = Parser.mapStringsToSig( ma6ca, ma6sa );
	assertTrue( ma6.success );

	assertEquals( 4, ma5.args.length );
	assertEquals( 4, ma6.args.length );

	// do the DotNames agree?
	assertTrue( ((DotName) (ma5.args[0])).equals( ((DotName) (ma6.args[0])) ) );

	// shows that casting is no accident
	try {
		assertTrue( ((DotColor) (ma5.args[0])).equals( ((DotColor) (ma6.args[0])) ) );
		fail();
	} catch ( ClassCastException e ) {}

	// do the DotColors agree?
	assertTrue( ((DotColor) (ma5.args[1])).equals( ((DotColor) (ma6.args[1])) ) );

	// do the Coords agree?
	assertTrue( ((Coord) (ma5.args[2])).equals( ((Coord) (ma6.args[2])) ) );

	// do the Radii agree?
	assertTrue( ((Radius) (ma5.args[3])).equals( ((Radius) (ma6.args[3])) ) );

	// STAGE 3: some bad 1-arg cmd lines

	Class[] ma8ca = new Class[] { DotColor.class };
	String[] ma8sa = new String[] { "X", "<", "NOCOLOR", ">" };
	MappedArgs ma8 = Parser.mapStringsToSig( ma8ca, ma8sa );
	assertFalse( ma8.success );

	Class[] ma10ca = new Class[] { Coord.class };
	String[] ma10sa = new String[] { "X", "<", "NOCOORD", ">" };
	MappedArgs ma10 = Parser.mapStringsToSig( ma10ca, ma10sa );
	assertFalse( ma10.success );

	Class[] ma12ca = new Class[] { Radius.class };
	String[] ma12sa = new String[] { "X", "<", "NORADIUS", ">" };
	MappedArgs ma12 = Parser.mapStringsToSig( ma12ca, ma12sa );
	assertFalse( ma12.success );

	// STAGE 4: some bad 2-arg cmd lines

	Class[] ma14ca = new Class[] { DotColor.class, Radius.class };
	String[] ma14sa = new String[] { "X", "<", "NOCOLOR", "comma here", "7", ">" };
	MappedArgs ma14 = Parser.mapStringsToSig( ma14ca, ma14sa );
	assertFalse( ma14.success );

	Class[] ma16ca = new Class[] { DotColor.class, Radius.class };
	String[] ma16sa = new String[] { "X", "<", "BLUE", "comma here", "", ">" };
	MappedArgs ma16 = Parser.mapStringsToSig( ma16ca, ma16sa );
	assertFalse( ma16.success );

	// STAGE 5: a null ref as Class array should throw NPE

	try {
		Class[] caNullRef = null;
		Parser.mapStringsToSig( caNullRef, new String[0] );
		fail();
	} catch ( NullPointerException e ) {}

	try {
		Class[] caNullRef = null;
		String[] saNullRef = null; // irrelevant
		Parser.mapStringsToSig( caNullRef, saNullRef );
		fail();
	} catch ( NullPointerException e ) {}

	try {
		Class[] caNullRef = null;
		String[] sa = new String[] { "not", "relevant" };
		Parser.mapStringsToSig( caNullRef, sa );
		fail();
	} catch ( NullPointerException e ) {}

	/*
	 * STAGE 6:
	 * 
	 * If classArray is empty, then success field of returned object is true and args field of
	 * returned object is an empty array.
	 */

	MappedArgs maTrivial1 = Parser.mapStringsToSig( new Class[0], new String[0] );
	assertTrue( maTrivial1.success );
	assertEquals( 0, maTrivial1.args.length );

	String[] saNullRef = null; // irrelevant
	MappedArgs maTrivial2 = Parser.mapStringsToSig( new Class[0], saNullRef );
	assertTrue( maTrivial2.success );
	assertEquals( 0, maTrivial2.args.length );

	String[] saIrrelevant = new String[] { "not", "relevant" };
	MappedArgs maTrivial3 = Parser.mapStringsToSig( new Class[0], saIrrelevant );
	assertTrue( maTrivial3.success );
	assertEquals( 0, maTrivial3.args.length );

}

/**
 * The domain method must generate the expected user-readable String when passed a particular return
 * value from a target method. To help it accomplish this, it needs the original command line too,
 * and a success flag. The domain method is <i>not </i> expected to make sense of the command line,
 * because it isn't called until after the target method has done its work. So, this unit test gives
 * the domain method an Object (supposed to be the object returned by some target method), together
 * with the flag and command line, and checks the resulting String against the output required by
 * the spec. <br>
 * <br>
 * 1st arg = vetted( theLine )<br>
 * <br>
 * 1st arg false --> other 2 args not examined
 * 
 * @throws BadCodeException
 * @throws DotColorFormatException
 * @throws DotColorFormatException
 * @throws DotNameFormatException
 */
public void testToReport() throws BadCodeException, DotNameFormatException, DotColorFormatException {

	/*
	 * STAGE 1: Show that if 1st arg false, (other args make no difference and EIC string is
	 * returned)
	 */

	String[] saValid1 = new String[] { "EXIT", "(", ")" };
	String[] saValid2 = new String[] { "COLOR_DOT", "(", "A", ",", "BLUE", ")" };
	String[] saInvalid1 = new String[] { "FOO", "(", ")" };
	String[] saInvalid2 = new String[] { "COLOR_DOT", "(", "A", ",", "NOCOLOR", ")" };

	assertEquals( "*****\nError: Invalid Command.", firstParser.toReport( false, null, null ) );
	assertEquals( "*****\nError: Invalid Command.", firstParser.toReport( false, saValid1, null ) );
	assertEquals( "*****\nError: Invalid Command.", firstParser.toReport( false, saInvalid1, null ) );
	assertEquals( "*****\nError: Invalid Command.", firstParser.toReport( false, saValid2, null ) );
	assertEquals( "*****\nError: Invalid Command.", firstParser.toReport( false, saInvalid2, null ) );
	assertEquals( "*****\nError: Invalid Command.", firstParser.toReport( false, null, "irrelevant" ) );
	assertEquals( "*****\nError: Invalid Command.", firstParser.toReport( false, saValid1, "irrelevant" ) );
	assertEquals( "*****\nError: Invalid Command.", firstParser.toReport( false, saInvalid1, "irrelevant" ) );
	assertEquals( "*****\nError: Invalid Command.", firstParser.toReport( false, saValid2, "irrelevant" ) );
	assertEquals( "*****\nError: Invalid Command.", firstParser.toReport( false, saInvalid2, "irrelevant" ) );

	/*
	 * STAGE 2: 1st arg is true, 2nd arg "looks like" a vettable line to toReport(). toReport()
	 * doesn't invoke vetted; it relies on the 1st arg instead. So, it suffices to give it merely a
	 * 1-element array as the 2nd arg, whose sole element will be used by toReport() to find the
	 * correct case
	 * 
	 * Think of the ~ as meaning, "If I weren't a cmd-line stub, my remaining elems would go here" :)
	 */

	/*
	 * ANIMATE_HORIZONTAL_PATH
	 */

	// is the ANIMATE_HORIZONTAL_PATH report sensitive
	// to drawMode?
	String[] saStubAHP1 = new String[] { "ANIMATE_HORIZONTAL_PATH", "~" };
	firstParser.setDrawMode( "DRAW" );
	FrameViz[] fvaAHP1 = new FrameViz[] { new FrameViz( "[_ some frame 1 _]" ), new FrameViz(
					"[_ some frame 2 _]" ) };
	assertEquals( "\n*****\n==> ANIMATE_HORIZONTAL_PATH~\n" + "\nAnimation Complete.", firstParser.toReport(
					true, saStubAHP1, fvaAHP1 ) );

	// is the ANIMATE_HORIZONTAL_PATH report sensitive
	// to drawMode?
	String[] saStubAHP2 = new String[] { "ANIMATE_HORIZONTAL_PATH", "~" };
	firstParser.setDrawMode( "TEXT" );
	FrameViz[] fvaAHP2 = new FrameViz[] { new FrameViz( "[_ some frame 1 _]" ), new FrameViz(
					"[_ some frame 2 _]" ) };
	assertEquals( "\n*****\n==> ANIMATE_HORIZONTAL_PATH~\n" + "[_ some frame 1 _]\n" + "[_ some frame 2 _]\n"
					+ "\nAnimation Complete.", firstParser.toReport( true, saStubAHP2, fvaAHP2 ) );

	/*
	 * COLOR_SEGMENTS
	 */

	String[] saStubCoSeg = new String[] { "COLOR_SEGMENTS", "~" };
	assertEquals( "\n*****\n==> COLOR_SEGMENTS~\n" + "Update complete.  Found 123 segments.", firstParser
					.toReport( true, saStubCoSeg, new Integer( 123 ) ) );

	/*
	 * CREATE_DOT
	 */

	String[] saStubCrDot = new String[] { "CREATE_DOT", "~" };

	// should succeed
	Dot dotCrDot = new Dot( new DotName( "MOE" ), new Coord( 17 ), new Coord( 76 ), new Radius( 3000 ),
					new DotColor( "BLUE" ) );

	assertEquals( "\n*****\n==> CREATE_DOT~\n" + "Created dot MOE at (17,76) color:BLUE.", firstParser
					.toReport( true, saStubCrDot, dotCrDot ) );

	// should complain as though name already in use
	DotNameDuplicateException dndeCrDot = new DotNameDuplicateException( "some mssg", "LARRY" );
	assertEquals( "\n*****\n==> CREATE_DOT~\n" + "Error: Dot LARRY already exists.", firstParser.toReport(
					true, saStubCrDot, dndeCrDot ) );

	// should complain as though coords already in use
	CoordDuplicateException cdeCrDot = new CoordDuplicateException( "some mssg", "CURLY" );
	assertEquals( "\n*****\n==> CREATE_DOT~\n" + "Error: Dot CURLY already exi"
					+ "sts at the specified coordinates.", firstParser.toReport( true, saStubCrDot, cdeCrDot ) );

	/*
	 * DELETE_DOT
	 */

	String[] saStubDelDot = new String[] { "DELETE_DOT", "~" };

	// should succeed
	DotName dnDelDot = new DotName( "DELME" );
	assertEquals( "\n*****\n==> DELETE_DOT~\n" + "Deleted dot DELME.", firstParser.toReport( true,
					saStubDelDot, dnDelDot ) );

	// should complain as though dot not found
	NotFoundException nfeDelDot = new NotFoundException();
	assertEquals( "\n*****\n==> DELETE_DOT~\n" + "Error: The specified dot does not exist.", firstParser
					.toReport( true, saStubDelDot, nfeDelDot ) );

	/*
	 * DRAW_FRAME
	 */

	/*
	 * Is the DRAW_FRAME report sensitive to drawMode? From the spec: "TEXT will be the mode used
	 * when grading"
	 */
	String[] saStubDrawF1 = new String[] { "DRAW_FRAME", "~" };
	firstParser.setDrawMode( "DRAW" );
	assertEquals( "\n*****\n==> DRAW_FRAME~\n" + "\nDraw Frame Complete.", firstParser.toReport( true,
					saStubDrawF1, new FrameViz( "YOU SHOULDN'T SEE THIS" ) ) );

	// is the DRAW_FRAME report sensitive to drawMode?
	String[] saStubDrawF2 = new String[] { "DRAW_FRAME", "~" };
	firstParser.setDrawMode( "TEXT" );
	assertEquals( "\n*****\n==> DRAW_FRAME~\n" + "[_ frame would go here _]" + "\nDraw Frame Complete.",
					firstParser.toReport( true, saStubDrawF2, new FrameViz( "[_ frame would go here _]" ) ) );

	/*
	 * EXIT
	 */

	String[] saStubExit = new String[] { "EXIT", "~" };
	assertEquals( "\n*****\n==> EXIT~\nHave a nice day!", firstParser.toReport( true, saStubExit, null ) );
	// by contrast, saValid1 is more than a stub
	assertEquals( "\n*****\n==> EXIT()\nHave a nice day!", firstParser.toReport( true, saValid1, null ) );

	/*
	 * INIT_QUADTREE
	 */

	String[] saStubIQT = new String[] { "INIT_QUADTREE", "~" };

	// should succeed
	assertEquals( "\n*****\n==> INIT_QUADTREE~\nQuadtree initialized.", firstParser.toReport( true, saStubIQT,
					null ) );

	// should complain as though out of range
	RangeException reIQT = new RangeException();
	assertEquals( "\n*****\n==> INIT_QUADTREE~\n" + "Error: size out of range.", firstParser.toReport( true,
					saStubIQT, reIQT ) );

	// should complain as though QT already init'd
	QTDuplicateException qtdeIQT = new QTDuplicateException();
	assertEquals( "\n*****\n==> INIT_QUADTREE~\n" + "Error: The Quadtree has already been initialized.",
					firstParser.toReport( true, saStubIQT, qtdeIQT ) );

	/*
	 * MAP_SEGMENT
	 */

	String[] saStubMapSeg = new String[] { "MAP_SEGMENT", "~" };

	// should succeed
	DotName[] dnArrMapSeg = new DotName[2];
	dnArrMapSeg[0] = new DotName( "LEWIS" );
	dnArrMapSeg[1] = new DotName( "CLARK" );
	assertEquals( "\n*****\n==> MAP_SEGMENT~\n" + "Mapped segment (LEWIS,CLARK).", firstParser.toReport( true,
					saStubMapSeg, dnArrMapSeg ) );

	// should complain as though dot(s) didn't exist
	NotFoundException nfeMapSeg = new NotFoundException();
	assertEquals( "\n*****\n==> MAP_SEGMENT~\n" + "Error: The specified dot does not exist.", firstParser
					.toReport( true, saStubMapSeg, nfeMapSeg ) );

	// should complain as though intersection detected
	IntersectionException ieMapSeg = new IntersectionException();
	// TODO restore once the QT is working right
	//assertEquals( "\n*****\n==> MAP_SEGMENT~\n" + "Error: Intersection detected.",
	// firstParser.toReport(
	// true,
	//				saStubMapSeg, ieMapSeg ) );

	/*
	 * NEAREST_SEG_TO_POINT
	 */

	String[] saStubNSTP = new String[] { "NEAREST_SEG_TO_POINT", "~" };

	// should succeed
	SegWithDistance swdNSTP1 = new SegWithDistance( new DotName( "ABE" ), new DotName( "BOB" ), 7.0 );
	assertEquals( "\n*****\n==> NEAREST_SEG_TO_POINT~\n" + "Nearest segment: (ABE,BOB). Distance: 7.000.",
					firstParser.toReport( true, saStubNSTP, swdNSTP1 ) );

	// should succeed
	SegWithDistance swdNSTP2 = new SegWithDistance( new DotName( "ABE" ), new DotName( "BOB" ),
					1234567.123444 );
	assertEquals(
					"\n*****\n==> NEAREST_SEG_TO_POINT~\n"
									+ "Nearest segment: (ABE,BOB). Distance: 1234567.123.", firstParser
									.toReport( true, saStubNSTP, swdNSTP2 ) );

	// should succeed
	SegWithDistance swdNSTP3 = new SegWithDistance( new DotName( "ABE" ), new DotName( "BOB" ), 123.000444 );
	assertEquals( "\n*****\n==> NEAREST_SEG_TO_POINT~\n" + "Nearest segment: (ABE,BOB). Distance: 123.000.",
					firstParser.toReport( true, saStubNSTP, swdNSTP3 ) );

	// should complain as if tree is empty
	NotFoundException nfeNSTP = new NotFoundException();
	assertEquals( "\n*****\n==> NEAREST_SEG_TO_POINT~\n" + "Tree is empty.", firstParser.toReport( true,
					saStubNSTP, nfeNSTP ) );

	/*
	 * PRINT_BPTREE
	 */

	String[] saStubPBPT = new String[] { "PRINT_BPTREE", "~" };

	// should succeed
	BPTreeViz bptvPBPT = new BPTreeViz( "[_ some imaginary bpt viz _]" );
	assertEquals( "\n*****\n==> PRINT_BPTREE~\n" + "[_ some imaginary bpt viz _]", firstParser.toReport( true,
					saStubPBPT, bptvPBPT ) );

	// should complain as if tree is empty
	NotFoundException nfePBPT = new NotFoundException();
	assertEquals( "\n*****\n==> PRINT_BPTREE~\n" + "Tree is empty.", firstParser.toReport( true, saStubPBPT,
					nfePBPT ) );

	/*
	 * PRINT_QUADTREE
	 */

	String[] saStubPQT = new String[] { "PRINT_QUADTREE", "~" };

	// should succeed
	QTViz qtvPQT = new QTViz( "[_ some qt viz would go here _]" );
	assertEquals( "\n*****\n==> PRINT_QUADTREE~\n" + "[_ some qt viz would go here _]", firstParser.toReport( true,
					saStubPQT, qtvPQT ) );

	// should complain as if tree is empty
	NotFoundException nfePQT = new NotFoundException();
	assertEquals( "\n*****\n==> PRINT_QUADTREE~\n" + "Tree is empty.", firstParser.toReport( true, saStubPQT,
					nfePQT ) );

	/*
	 * RANGE_DOTS
	 */

	String[] saStubRangeDots = new String[] { "RANGE_DOTS", "~" };

	// should succeed
	DotList dotListForRangeDots = new DotList();
	dotListForRangeDots.add( new Dot( new DotName( "ADAM" ), new Coord( 10 ), new Coord( 100 ), new Radius(
					33 ), new DotColor( "RED" ) ) );

	dotListForRangeDots.add( new Dot( new DotName( "BOB" ), new Coord( 20 ), new Coord( 200 ),
					new Radius( 33 ), new DotColor( "GREEN" ) ) );

	dotListForRangeDots.add( new Dot( new DotName( "CARL" ), new Coord( 30 ), new Coord( 300 ), new Radius(
					33 ), new DotColor( "BLUE" ) ) );

	assertEquals( "\n*****\n==> RANGE_DOTS~\n" + "ADAM at (10,100) color:RED\n"
					+ "BOB at (20,200) color:GREEN\n" + "CARL at (30,300) color:BLUE\n", firstParser
					.toReport( true, saStubRangeDots, dotListForRangeDots ) );

	// should complain as though no dots matched
	DotNameRangeException dnreForRangeDots = new DotNameRangeException();
	assertEquals( "\n*****\n==> RANGE_DOTS~\n" + "No matching dots found.", firstParser.toReport( true,
					saStubRangeDots, dnreForRangeDots ) );

	/*
	 * SET_BPTREE_ORDER
	 */

	String[] saStubSBPTO = new String[] { "SET_BPTREE_ORDER", "~" };

	// should succeed
	BTreeOrder btoSBPTO = new BTreeOrder( 5 );
	assertEquals( "\n*****\n==> SET_BPTREE_ORDER~\n" + "Order set to 5.", firstParser.toReport( true,
					saStubSBPTO, btoSBPTO ) );

	// should complain as though B Tree already init'd
	BTreeInitException btieSBPTO = new BTreeInitException();
	assertEquals( "\n*****\n==> SET_BPTREE_ORDER~\n" + "Error: B+ tree already initialized.", firstParser
					.toReport( true, saStubSBPTO, btieSBPTO ) );

	/*
	 * SET_DRAW_MODE
	 */

	String[] saStubSDM = new String[] { "SET_DRAW_MODE", "~" };
	assertEquals( "\n*****\n==> SET_DRAW_MODE~\n", firstParser.toReport( true, saStubSDM, null ) );

	/*
	 * UNMAP_SEGMENT
	 */

	String[] saStubUnmapSeg = new String[] { "UNMAP_SEGMENT", "~" };

	// should succeed -- n.b. Segment's capitalized here,
	// but not for MAP_SEGMENT
	DotName[] dnArrUnmapSeg = new DotName[2];
	dnArrUnmapSeg[0] = new DotName( "SLEWIS" );
	dnArrUnmapSeg[1] = new DotName( "SCLARK" );
	assertEquals( "\n*****\n==> UNMAP_SEGMENT~\n" + "Unmapped Segment (SLEWIS,SCLARK).", firstParser.toReport(
					true, saStubUnmapSeg, dnArrUnmapSeg ) );

	// should complain as though dot(s) didn't exist
	NotFoundException dotnfeUnmapSeg = new NotFoundException( "dot" );
	assertEquals( "\n*****\n==> UNMAP_SEGMENT~\n" + "Error: The specified dot does not exist.", firstParser
					.toReport( true, saStubUnmapSeg, dotnfeUnmapSeg ) );

	// should complain as though segment didn't exist
	NotFoundException segnfeUnmapSeg = new NotFoundException( "segment" );
	assertEquals( "\n*****\n==> UNMAP_SEGMENT~\n" + "Error: The specified segm"
					+ "ent was not found on the map.", firstParser.toReport( true, saStubUnmapSeg,
					segnfeUnmapSeg ) );

}

/**
 * dispatched can't be meaningfully tested in those cases where its arg is unvettable. When
 * dispatched gets its arg, it passes it on to vetted. If vetted returns false, then dispatched
 * never tries to invoke the right target method. Therefore the strongest test for dispatched is to
 * make sure it behaves correctly when given a vettable arg. Note that vetted is static, because it
 * can be, while dispatched must be a per-instance method.
 */
public void testBothVettedAndDispatched() throws IllegalArgumentException, IllegalAccessException,
				BadCodeException {

	String[] saNull = null;
	assertFalse( Parser.vetted( saNull ) );
	assertFalse( firstParser.dispatched( saNull ) );

	assertFalse( Parser.vetted( new String[0] ) );
	assertFalse( firstParser.dispatched( new String[0] ) );

	assertFalse( Parser.vetted( new String[] { "" } ) );
	assertFalse( firstParser.dispatched( new String[] { "" } ) );

	assertFalse( Parser.vetted( new String[] { " " } ) );
	assertFalse( firstParser.dispatched( new String[] { " " } ) );

	assertFalse( Parser.vetted( new String[] { "STRANGE_CMD" } ) );
	assertFalse( firstParser.dispatched( new String[] { "STRANGE_CMD" } ) );

	assertFalse( Parser.vetted( new String[] { "STRANGE_CMD", "(" } ) );
	assertFalse( firstParser.dispatched( new String[] { "STRANGE_CMD", "(" } ) );

	assertFalse( Parser.vetted( new String[] { "STRANGE_CMD", "(", ")" } ) );
	assertFalse( firstParser.dispatched( new String[] { "STRANGE_CMD", "(", ")" } ) );

	assertFalse( Parser.vetted( new String[] { "STRANGE_CMD", "(", "FOO", ",", "RED", ")" } ) );
	assertFalse( firstParser.dispatched( new String[] { "STRANGE_CMD", "(", "FOO", ",", "RED", ")" } ) );

	/*
	 * COLOR_DOT isn't in 3e - so dispatched() should throw a BCE
	 */

	assertTrue( Parser.vetted( new String[] { "COLOR_DOT", "(", "FOO", ",", "RED", ")" } ) );
	// not in 3e, so let's throw a BCE if we try to dispatch
	//assertTrue( firstParser.dispatched( new String[] {
	//    "COLOR_DOT", "(", "FOO", ",", "RED", ")" }, todaysDW ));

	assertFalse( Parser.vetted( new String[] { "COLOR_DOT", "(", "FOO", ",", "NOCOLOR", ")" } ) );
	//assertFalse( firstParser.dispatched( new String[] {
	//    "COLOR_DOT", "(", "FOO", ",", "NOCOLOR", ")" } ));

	assertFalse( Parser.vetted( new String[] { "COLOR_DOT", "(", "FOO", ",", "RED", ",", "7", ")" } ) );
	//assertFalse( firstParser.dispatched( new String[] {
	//    "COLOR_DOT", "(", "FOO", ",", "RED", ",", "7", ")" } ));

	/*
	 * ANIMATE_HORIZONTAL_PATH
	 */

	assertTrue( Parser.vetted( new String[] { "ANIMATE_HORIZONTAL_PATH", "(", "1", ",", "2", ",", "3", ")" } ) );
	assertTrue( firstParser
					.dispatched( new String[] { "ANIMATE_HORIZONTAL_PATH", "(", "1", ",", "2", ",", "3", ")" } ) );

	/*
	 * COLOR_SEGMENTS
	 */

	assertTrue( Parser
					.vetted( new String[] { "COLOR_SEGMENTS", "(", "1", ",", "2", ",", "10", ",", "20", ",", "RED", ")" } ) );
	assertTrue( firstParser
					.dispatched( new String[] { "COLOR_SEGMENTS", "(", "1", ",", "2", ",", "10", ",", "20", ",", "RED", ")" } ) );

	/*
	 * CREATE_DOT
	 */

	assertTrue( Parser
					.vetted( new String[] { "CREATE_DOT", "(", "FOO", ",", "3", ",", "7", ",", "5", ",", "RED", ")" } ) );
	assertTrue( firstParser
					.dispatched( new String[] { "CREATE_DOT", "(", "FOO", ",", "3", ",", "7", ",", "5", ",", "RED", ")" } ) );

	assertFalse( Parser
					.vetted( new String[] { "CREATE_DOT", "(", "FOO", ",", "3", ",", "NONUM", ",", "5", ",", "RED", ")" } ) );
	assertFalse( firstParser
					.dispatched( new String[] { "CREATE_DOT", "(", "FOO", ",", "3", ",", "NONUM", ",", "5", ",", "RED", ")" } ) );

	assertFalse( Parser
					.vetted( new String[] { "CREATE_DOT", "(", "FOO", ",", "3", ",", "", ",", "5", ",", "RED", ")" } ) );
	assertFalse( firstParser
					.dispatched( new String[] { "CREATE_DOT", "(", "FOO", ",", "3", ",", "", ",", "5", ",", "RED", ")" } ) );

	assertFalse( Parser
					.vetted( new String[] { "CREATE_DOT", "(", "FOO", ",", "3", ",", "$#^", ",", "5", ",", "RED", ")" } ) );
	assertFalse( firstParser
					.dispatched( new String[] { "CREATE_DOT", "(", "FOO", ",", "3", ",", "$#^", ",", "5", ",", "RED", ")" } ) );

	/*
	 * DELETE_DOT
	 */

	assertTrue( Parser.vetted( new String[] { "DELETE_DOT", "(", "DOROTHY", ")" } ) );
	assertTrue( firstParser.dispatched( new String[] { "DELETE_DOT", "(", "DOROTHY", ")" } ) );

	/*
	 * DRAW_FRAME
	 */

	assertTrue( Parser.vetted( new String[] { "DRAW_FRAME", "(", "1", ",", "3", ")" } ) );
	assertTrue( firstParser.dispatched( new String[] { "DRAW_FRAME", "(", "1", ",", "3", ")" } ) );

	/*
	 * EXIT
	 */

	assertTrue( Parser.vetted( new String[] { "EXIT", "(", ")" } ) );
	assertTrue( firstParser.dispatched( new String[] { "EXIT", "(", ")" } ) );

	assertFalse( Parser.vetted( new String[] { "EXIT", "(", "7", ")" } ) );
	assertFalse( firstParser.dispatched( new String[] { "EXIT", "(", "7", ")" } ) );

	/*
	 * INIT_QUADTREE
	 */

	// out-of-range val should be vetted anyway
	assertTrue( Parser.vetted( new String[] { "INIT_QUADTREE", "(", "1", ")" } ) );
	assertTrue( firstParser.dispatched( new String[] { "INIT_QUADTREE", "(", "1", ")" } ) );

	assertTrue( Parser.vetted( new String[] { "INIT_QUADTREE", "(", "13", ")" } ) );
	assertTrue( firstParser.dispatched( new String[] { "INIT_QUADTREE", "(", "13", ")" } ) );

	assertTrue( Parser.vetted( new String[] { "INIT_QUADTREE", "(", "14", ")" } ) );
	assertTrue( firstParser.dispatched( new String[] { "INIT_QUADTREE", "(", "14", ")" } ) );

	assertTrue( Parser.vetted( new String[] { "INIT_QUADTREE", "(", "31", ")" } ) );
	assertTrue( firstParser.dispatched( new String[] { "INIT_QUADTREE", "(", "31", ")" } ) );

	/*
	 * MAP_SEGMENT
	 */

	assertTrue( Parser.vetted( new String[] { "MAP_SEGMENT", "(", "ABEMS", ",", "BOBMS", ")" } ) );
	assertTrue( firstParser.dispatched( new String[] { "MAP_SEGMENT", "(", "ABEMS", ",", "BOBMS", ")" } ) );

	/*
	 * NEAREST_SEG_TO_POINT
	 */

	assertTrue( Parser.vetted( new String[] { "NEAREST_SEG_TO_POINT", "(", "10", ",", "13", ")" } ) );
	assertTrue( firstParser.dispatched( new String[] { "NEAREST_SEG_TO_POINT", "(", "10", ",", "13", ")" } ) );

	/*
	 * PRINT_BPTREE
	 */

	assertTrue( Parser.vetted( new String[] { "PRINT_BPTREE", "(", ")" } ) );
	assertTrue( firstParser.dispatched( new String[] { "PRINT_BPTREE", "(", ")" } ) );

	/*
	 * PRINT_QUADTREE
	 */

	assertTrue( Parser.vetted( new String[] { "PRINT_QUADTREE", "(", ")" } ) );
	assertTrue( firstParser.dispatched( new String[] { "PRINT_QUADTREE", "(", ")" } ) );

	/*
	 * RANGE_DOTS
	 */

	assertTrue( Parser.vetted( new String[] { "RANGE_DOTS", "(", "ABERD", ",", "BOBRD", ")" } ) );
	assertTrue( firstParser.dispatched( new String[] { "RANGE_DOTS", "(", "ABERD", ",", "BOBRD", ")" } ) );

	assertTrue( Parser.vetted( new String[] { "RANGE_DOTS", "(", "GABERD", ",", "BOBRD", ")" } ) );
	assertTrue( firstParser.dispatched( new String[] { "RANGE_DOTS", "(", "GABERD", ",", "BOBRD", ")" } ) );

	/*
	 * SET_BPTREE_ORDER
	 */

	assertTrue( Parser.vetted( new String[] { "SET_BPTREE_ORDER", "(", "7", ")" } ) );
	assertTrue( firstParser.dispatched( new String[] { "SET_BPTREE_ORDER", "(", "7", ")" } ) );

	assertTrue( Parser.vetted( new String[] { "SET_BPTREE_ORDER", "(", "5", ")" } ) );
	assertTrue( firstParser.dispatched( new String[] { "SET_BPTREE_ORDER", "(", "5", ")" } ) );

	/*
	 * SET_DRAW_MODE
	 */

	assertTrue( Parser
					.vetted( new String[] { "SET_DRAW_MODE", "(", "TEXT", ",", "1", ",", "2", ",", "3", ")" } ) );
	assertTrue( firstParser
					.dispatched( new String[] { "SET_DRAW_MODE", "(", "TEXT", ",", "1", ",", "2", ",", "3", ")" } ) );

	/*
	 * UNMAP_SEGMENT
	 */

	assertTrue( Parser.vetted( new String[] { "UNMAP_SEGMENT", "(", "ABEUS", ",", "BOBUS", ")" } ) );
	assertTrue( firstParser.dispatched( new String[] { "UNMAP_SEGMENT", "(", "ABEUS", ",", "BOBUS", ")" } ) );

}

public void testTopLevel() throws IllegalArgumentException, IOException, IllegalAccessException,
				BadCodeException {

	assertTrue( "This file should exist", ParserTest.changeStdIn( REDIR + "primary_s1.input" ) );
	assertTrue( "This file should exist", ParserTest.changeStdOut( REDIR + "primary_s1_my_out.txt" ) );
	firstParser.topLevel( null );
	assertTrue( "This file should exist", ParserTest.changeStdIn( REDIR + "keep_for_testing.in" ) );
	assertTrue( "This file should exist", ParserTest.changeStdOut( REDIR + "extraneous_my_out.txt" ) );

	/*
	 * CREATE_DOT
	 */
	//
	//        // should succeed
	//    String[] saStubCrDot1 = new String[] {
	//        "CREATE_DOT", "~" };
	//    Dot dotCrDot1 = new Dot( "MOE", 17, 76, 3000, "BLUE" );
	//    assertEquals(
	//        "\n*****\n==> CREATE_DOT~\n"
	//        + "Created dot MOE at (17,76) color:BLUE.",
	//        firstParser.toReport( true, saStubCrDot1, dotCrDot1 ));
	//
	//// should complain - name already in use
	//    String[] saStubCrDot2 = new String[] {
	//        "CREATE_DOT", "~" };
	//    Dot dotCrDot2 = new Dot( "MOE", 1700, 7600, 3000, "BLUE" );
	//    assertEquals(
	//        "\n*****\n==> CREATE_DOT~\n"
	//        + "Created dot MOE at (17,76) color:BLUE.",
	//        firstParser.toReport( true, saStubCrDot2, dotCrDot2 ));
	//
	//        // should complain - coords already in use
	//    String[] saStubCrDot3 = new String[] {
	//        "CREATE_DOT", "~" };
	//    Dot dotCrDot3 = new Dot( "LARRY", 17, 76, 3000, "BLUE" );
	//    assertEquals(
	//        "\n*****\n==> CREATE_DOT~\n"
	//        + "Created dot MOE at (17,76) color:BLUE.",
	//        firstParser.toReport( true, saStubCrDot3, dotCrDot3 ));
	//
	//        // should complain about name, not coords (as per BNF)
	//    String[] saStubCrDot4 = new String[] {
	//    "CREATE_DOT", "~" };
	//    Dot dotCrDot4 = new Dot( "MOE", 17, 76, 3000, "BLUE" );
	//    assertEquals(
	//    "\n*****\n==> CREATE_DOT~\n"
	//    + "Created dot MOE at (17,76) color:BLUE.",
	//    firstParser.toReport( true, saStubCrDot4, dotCrDot4 ));
}

/*
 * Pass it a filename (actually, on the Mac at least, you need to give it a fully-qualified path).
 * It returns its success in resetting System.in to be your file.
 * 
 * Ideally the filename would be passed as an arg to main(), which would pass it on here. But given
 * the specs, that can't be, so until we devise a config file we must recompile each time the input
 * file is renamed. Still, this encapsulates David Anderson's approach.
 * 
 * @author wbhicks, implementing David Anderson's posted idea
 */
private static boolean changeStdIn( String theFileAsString ) {

	java.io.FileInputStream fis;
	try {
		fis = new java.io.FileInputStream( theFileAsString );
		lg.fine( fis.toString() );
		System.setIn( fis );
		lg.fine( "Redirected." );
		return true;
	} catch ( java.io.FileNotFoundException e ) {
		lg.fine( "Couldn't redirect!" );
		return false;
	} catch ( SecurityException e ) {
		lg.fine( "Not permitted!" );
		return false;
	}
}

/*
 * See changeStdIn
 * 
 * @param theFileAsString @return whether it succeeded
 */
private static boolean changeStdOut( String theFileAsString ) {

	java.io.PrintStream fos;
	try {
		fos = new java.io.PrintStream( new java.io.FileOutputStream( theFileAsString ) );
		lg.fine( fos.toString() );
		System.setOut( fos );
		lg.fine( "Redirected." );
		return true;
	} catch ( java.io.FileNotFoundException e ) {
		lg.fine( "Couldn't redirect!" );
		return false;
	} catch ( SecurityException e ) {
		lg.fine( "Not permitted!" );
		return false;
	}
}

}
