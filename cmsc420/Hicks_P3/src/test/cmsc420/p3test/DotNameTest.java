package cmsc420.p3test;

import cmsc420.exceptions.DotNameFormatException;
import cmsc420.p3.DotName;
import junit.framework.TestCase;


/**
 * @author wbhicks
 *
 * @version 
 */
public class DotNameTest extends TestCase {

protected void setUp() throws Exception {
super.setUp();
}

protected void tearDown() throws Exception {
super.tearDown();
}

public final void testDotName() {

try {
    String sNull = null;
    DotName dn0 = new DotName( sNull );
    fail();
} catch ( DotNameFormatException e ) {
    assertTrue( true );
}

try {
    DotName dn1 = new DotName( "" );
    fail();
} catch ( DotNameFormatException e ) {
    assertTrue( true );
}

try {
	DotName dn2 = new DotName( "MOE" );
    assertTrue( true );
} catch ( DotNameFormatException e ) {
	fail();
}

}

public final void testGetName() {
    
    try {
        DotName dn = new DotName( "MOE" );
        assertEquals( "MOE", dn.getName() );
    } catch ( DotNameFormatException e ) {
        fail();
    }

}

/*
 * Class under test for boolean equals(DotName)
 */
public final void testEqualsDotName() {
    try {
        DotName dn0 = new DotName( "MOE" );
        DotName dn1 = new DotName( "MOE" );
        DotName dn2 = new DotName( "LARRY" );
        assertTrue( dn0.equals( dn1 ));
        assertFalse( dn0.equals( dn2 ));
    } catch ( DotNameFormatException e ) {
        fail();
    }

}

}
