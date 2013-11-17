package cmsc420.p3test;

import cmsc420.p3.DotColor;
import cmsc420.p3.DotName;
import cmsc420.p3.DotWorld;
import cmsc420.p3.MethodWithSig;
import junit.framework.TestCase;


/**
 * @author wbhicks
 *
 * @version 
 */
public class MethodWithSigTest extends TestCase {

protected void setUp() throws Exception {
super.setUp();
}

protected void tearDown() throws Exception {
super.tearDown();
}

/*
 * Class under test for void MethodWithSig()
 */
public final void testMethodWithSig() {
}

/*
 * Class under test for void MethodWithSig(Class, String)
 */
public final void testMethodWithSigClassString() {
    
/* STAGE 0:
 * success = false if given a class other than DotWorld
 */
    
String sNull = null;
MethodWithSig mws0 = new MethodWithSig( 
    java.text.DecimalFormat.class, sNull );
assertFalse( mws0.success );

MethodWithSig mws1 = new MethodWithSig( 
    java.text.DecimalFormat.class, "" );
assertFalse( mws1.success );

MethodWithSig mws2 = new MethodWithSig( 
    java.text.DecimalFormat.class, "blah" );
assertFalse( mws2.success );

/* STAGE 1:
 * success = false if given DotWorld, but String is a null
 * ref or is empty
 */

String sNull2 = null;
MethodWithSig mws3 = new MethodWithSig( 
    DotWorld.class, sNull2 );
assertFalse( mws3.success );

MethodWithSig mws4 = new MethodWithSig( 
    DotWorld.class, "" );
assertFalse( mws4.success );

/* STAGE 2:
 * success = false if given DotWorld, but String is an
 * unrecognized command
 */

MethodWithSig mws5 = new MethodWithSig( 
    DotWorld.class, "STRANGE_CMD" );
assertFalse( mws5.success );

/* STAGE 3: success for each pre-approved command
 */

// just to show that getMethod works
try {
	DotWorld.class.getMethod( 
	    "exit", new Class[0] );
} catch ( NoSuchMethodException e1 ) {
	fail();
} 

// just to show that getMethod works
try {
    DotWorld.class.getMethod( 
        "colorDot", new Class[] { 
            DotName.class, DotColor.class } );
} catch ( NoSuchMethodException e1 ) {
    fail();
} 

// COLOR_DOT

MethodWithSig mwsCoD = new MethodWithSig( 
    DotWorld.class, "COLOR_DOT" );
Class[] mwsCoDcaExpected =
    new Class[] { DotName.class, DotColor.class };
java.lang.reflect.Method mwsCoDmethodExpected = null;
try {
    mwsCoDmethodExpected = DotWorld.class.getMethod( 
        "colorDot", mwsCoDcaExpected );
} catch ( NoSuchMethodException e ) {
    fail();
}
assertTrue( mwsCoD.success );
assertTrue( java.util.Arrays.equals( mwsCoD.ca,
    mwsCoDcaExpected ));
assertEquals( mwsCoD.method, mwsCoDmethodExpected );

// EXIT

MethodWithSig mwsX = new MethodWithSig( 
    DotWorld.class, "EXIT" );
Class[] mwsXcaExpected = new Class[0];
java.lang.reflect.Method mwsXmethodExpected = null;
try {
    mwsXmethodExpected = DotWorld.class.getMethod( 
        "exit", mwsXcaExpected );
} catch ( NoSuchMethodException e ) {
    fail();
}
assertTrue( mwsX.success );
assertTrue( java.util.Arrays.equals( mwsX.ca,
    mwsXcaExpected ));
assertEquals( mwsX.method, mwsXmethodExpected );

// TODO the others

}

}
