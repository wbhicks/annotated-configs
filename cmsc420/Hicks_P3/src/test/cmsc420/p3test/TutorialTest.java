package cmsc420.p3test;

import junit.framework.TestCase;


/**
 * A TestCase to hold any unit tests that are actually tutorials or self-help exercises, rather than
 * testing some domain methods. I find JUnit assertions to be a great way to learn Java.
 * 
 * @author wbhicks
 * 
 * @version 1.0
 */
public class TutorialTest extends TestCase {

protected void setUp() throws Exception {
	super.setUp();
}

protected void tearDown() throws Exception {
	super.tearDown();
}

/**
 * I need to understand degenerate kinds of arrays of Strings, and comparisons that can be performed
 * on them. So, this test method serves as a (re)fresher course in arrays.
 */
public void testTutorialOnArrays() {

	/*
	 * TOPIC 1: Variables of array type that are null refs
	 * 
	 * _Variables_, not objects, are initialized. You _can_ initialize with null (as long as the
	 * var's of ref type, which is true of all arrays.) By contrast, _objects_, not variables, are
	 * instantiated. Null is not an object.
	 * 
	 * Below, anr holds a null reference, not a ref to an array (null is merely a "special value of
	 * reference type", so anr refers to nothing - not even to an "empty array"). Null is not even
	 * an object, so it can not be an array (which is a kind of object).
	 * 
	 * (BTW these just happen to be arrays of Strings, rather than of, say, Floats)
	 */
	String[] anr = null; // decl'n + init'n
	String[] anrTwin; // declared but not yet initialized
	anrTwin = null; // now it's initialized (with null)
	// nothing's been instantiated yet

	/*
	 * TOPIC 2: Four ways to compare two arrays
	 * 
	 * (1) GOOD: use java.util.Arrays.equals() (2) GOOD: use == (3) POOR: use equals() (4) POOR: use
	 * JUnit's assertEquals()
	 * 
	 * (1) GOOD: java.util.Arrays.equals() returns true if both array references are null, false if
	 * exactly one array reference is null. Otherwise it tests by trying to place the two arrays'
	 * elements in 1-to-1 correspondence, such that each pairing must be either 2 null refs, or else
	 * 2 objects a[i], b[i] such that a[i].equals( b[i] ).
	 * 
	 * This is a good way to compare arrays, because it utilizes any overriding of equals() in the
	 * elements' class definition.
	 */
	assertTrue( java.util.Arrays.equals( anr, anrTwin ) );

	/*
	 * (2) GOOD: the == operator for operands of reference type. It's a much stricter test than
	 * Arrays.equals(). Like Arrays.equals(), it returns true if both array references are null,
	 * false if exactly one array reference is null. Otherwise it returns true iff they refer to the
	 * same array (i.e. the same object in memory.)
	 * 
	 * This is a good way to compare arrays, because (a) it returns true, rather than throwing an
	 * NPE, when given two null refs of the same array type -- unlike equals() -- and (b) it
	 * triggers a compile-time error, rather than returning false, when given two operands of
	 * incompatible array types -- unlike equals() and JUnit's assertEquals().
	 */
	assertTrue( anr == anrTwin );

	/*
	 * (3) POOR: equals(), when invoked on an object of array type, behaves as defined by the class
	 * Object. (This is because array types can't override methods.) Therefore it relies on ==,
	 * except that it's deficient in two respects: it throws NPE if invoked on a null ref, and it
	 * won't complain at compile time if you compare two arrays of incompatible array types.
	 * 
	 * So, use == instead. "A compile-time error occurs if it is impossible to convert the type of
	 * either operand to the type of the other by a casting conversion." (from Sun)
	 * 
	 * By default, java.lang.Object's equals() returns the same as the == operator (for operands of
	 * reference type). Array types can not override it. (See JLS 10.7, "Array Members".) However,
	 * equals() can't be invoked on a null ref while == accepts null operand(s).
	 * 
	 * 2 null refs: equals() throws NPE, == returns true
	 * 
	 * 1 null ref: aNonNullRef.equals( aNullRef ) returns false, so does == (obviously)
	 * 
	 * Here, we can't invoke equals(), e.g.:
	 */
	// boolean willCauseNPE = anr.equals( anrTwin );
	/*
	 * (4) POOR: The JUnit tool assertEquals() relies on equals(), except the assertion passes given
	 * 2 null args. But array types can't override equals(), so this is no better than assertTrue(
	 * anArray == anotherArray ) -- worse, in fact, because when the two arrays are of incompatible
	 * types, it compiles, and merely causes the test method to fail when run.
	 * 
	 * Here, they're both null refs, so they pass this test:
	 */
	assertEquals( anr, anrTwin );

	/*
	 * TOPIC 3: Comparing variables that refer to empty arrays
	 * 
	 * Now let's compare two refs to "empty arrays", i.e. to actual arrays of length 0. Such an
	 * array is an object, but has no elements, can't be indexed, and is empty:
	 */
	String[] a0 = new String[0];
	String[] a0Twin = new String[] {}; // alternative style

	// No elems, so 1-to-1 pairing (of elems) succeeds
	assertTrue( java.util.Arrays.equals( a0, a0Twin ) );
	assertFalse( a0 == a0Twin ); // distinct (separate) objects
	assertFalse( a0.equals( a0Twin ) ); // distinct objects
	//assertEquals( a0, a0Twin ); // distinct objects

	/*
	 * TOPIC 4: Comparing a null ref with a ref to an empty array
	 * 
	 * All 4 ways of comparison will return false. Additionally, == compiles iff operands are of
	 * compatible array types.
	 */

	assertFalse( java.util.Arrays.equals( a0, anr ) );
	assertFalse( a0 == anr );
	assertFalse( a0.equals( anr ) ); // notice the order
	//assertEquals( a0, anr ); // fails

	/*
	 * TOPIC 5: Comparing 2 empty arrays of different, incompatible types
	 * 
	 * Instead of an array of Strings, here's one of BigDecimals, but it too is empty.
	 */
	java.math.BigDecimal[] a0BigDecimal = new java.math.BigDecimal[0];

	// No elems, so 1-to-1 pairing (of elems) succeeds.
	// The array types differ but that's OK
	assertTrue( java.util.Arrays.equals( a0, a0BigDecimal ) );
	// incompatible operand types, so won't compile
	//assertFalse( a0 == a0ButOfFloats );
	assertFalse( a0.equals( a0BigDecimal ) ); // distinct objs
	//assertEquals( a0, a0ButOfFloats ); // distinct objs

	/*
	 * TOPIC 6: Comparing 2 non-empty arrays of Strings (with equivalent elements)
	 * 
	 * Only Arrays.equals() returns true, because "hi".equals( "hi" ) returns true. The other 3 fail
	 * because the 2 arrays are distinct (separate) objects.
	 */

	String[] a1 = new String[] { "hi"};
	String[] a1Twin = { "hi"}; // shorthand

	// "hi".equals( "hi" );
	assertTrue( java.util.Arrays.equals( a1, a1Twin ) );
	assertFalse( a1.equals( a1Twin ) );
	assertFalse( a1 == a1Twin );
	//assertEquals( a1, a1Twin );

	/*
	 * TOPIC RECAP: Arrays.equals Object.equals == assertEquals | | | |
	 * 
	 * 2 null refs T throws NPE T T 1 empty, 1 null F F F F 2 empty, same type T F F F 2 empty,
	 * incompat T F won't compile F { "x" }, { "x" } T F F F
	 */
}

/**
 * Now that we've seen array comparisons, here's a (re)fresher course in Strings and arrays of
 * Strings:
 */
public void testTutorialOnStrings() {

	/*
	 * Below, nullIsNoString holds a null reference, not a ref to a String object. Null is not even
	 * an object, so it can not be a String.
	 * 
	 * anEmptyString, by contrast, refers to an empty string, which therefore has length 0:
	 */
	String nullIsNoString = null; // declared + init'd
	String anEmptyString = ""; // declared + init'd
	assertFalse( anEmptyString.equals( nullIsNoString ) );

	/*
	 * If Foo is a reference type, then the default init'n of a Foo array sets its elements (Foo
	 * variables) to be null references.
	 */
	String[] sa1null = new String[1]; // declared + init'd
	String[] sa1nullAlso = new String[] { nullIsNoString}; // declared + init'd
	// each array holds a null ref, not an empty string
	assertTrue( java.util.Arrays.equals( sa1null, sa1nullAlso ) );

	String[] sa3nulls = new String[3];
	String[] sa3nullsAlso = new String[] { nullIsNoString, nullIsNoString, nullIsNoString};
	// each array holds 3 null refs, not 3 empty strings
	assertTrue( java.util.Arrays.equals( sa3nulls, sa3nullsAlso ) );

	// arrays of diff lengths, even though all elems null
	assertFalse( java.util.Arrays.equals( sa1null, sa3nullsAlso ) );

	// to get an array of empty strings, we do this
	String[] sa2empties = new String[2]; // declared + init'd
	sa2empties[0] = ""; // changes an element: was null, now ""
	sa2empties[1] = "";
	String[] sa2emptiesToo = new String[] { "", ""};
	assertTrue( java.util.Arrays.equals( sa2empties, sa2emptiesToo ) );

	/*
	 * an empty array: {} 
	 * a non-empty array of null refs: { iAmNull, meToo } 
	 * a non-empty array of empty Strings: { "", "" }
	 */

	String[] sa0a = {};
	String[] sa0b = new String[] {};
	String[] sa0c = new String[0];

	String[] sa1a = { "hey"};
	String[] sa1b = new String[] { "hey"};

	String[] sa2a = { "hi", "bye"};
	String[] sa2b = new String[] { "hi", "bye"};
	String[] sa2c = new String[2];
	sa2c[0] = "hi";
	sa2c[1] = "bye";

	// But as mere assignment, these are illegal:
	// sa0a = {};
	// sa0a = { "blah" };

	assertTrue( java.util.Arrays.equals( sa0a, sa0b ) );
	assertTrue( java.util.Arrays.equals( sa0a, sa0c ) );
	assertTrue( java.util.Arrays.equals( sa0b, sa0c ) );
	assertTrue( java.util.Arrays.equals( sa1a, sa1b ) );
	assertTrue( java.util.Arrays.equals( sa2a, sa2b ) );
	assertTrue( java.util.Arrays.equals( sa2a, sa2c ) );
	assertTrue( java.util.Arrays.equals( sa2b, sa2c ) );
}




}
