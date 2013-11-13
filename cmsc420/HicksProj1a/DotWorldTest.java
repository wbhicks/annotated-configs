// only two test methods used so far - ignore

import junit.framework.TestCase;

public class DotWorldTest extends TestCase {

    int a;
    double b;
    
	protected void setUp() throws Exception {
		super.setUp();
        a = 1;
        b = 2.0;
        DotWorld.createDot( "FACTUAL_DOT", 17543, 1236, 500, "RED");
        DotWorld.createDot( "SETUP11", 51, 51, 0, "BLUE" );
        DotWorld.createDot( "SETUP12", 51, 52, 10, "BLUE" );
        DotWorld.createDot( "SETUP21", 52, 51, 20, "BLUE" );
        DotWorld.createDot( "SETUP22", 52, 52, 0, "BLUE" );
        DotWorld.createDot( "SETUP31", 53, 51, 10, "BLUE" );
        DotWorld.createDot( "SETUP32", 53, 52, 20, "BLUE" );
        DotWorld.createDot( "SETUP33", 53, 53, 0, "BLUE" );
        DotWorld.createDot( "SETUP13", 51, 53, 0, "BLUE" );
        DotWorld.createDot( "SETUP23", 52, 53, 0, "BLUE" );
        DotWorld.createDot( "SETUP40", 54, 50, 0, "BLUE" );
        DotWorld.createDot( "SETUP04", 50, 54, 0, "BLUE" );
        DotWorld.createDot( "SETUP41", 54, 51, 0, "BLUE" );
        DotWorld.createDot( "SETUP42", 54, 52, 0, "BLUE" );
        DotWorld.createDot( "SETUP43", 54, 53, 0, "BLUE" );
        DotWorld.createDot( "SETUP44", 54, 54, 0, "BLUE" );
        DotWorld.createDot( "SETUP14", 51, 54, 0, "BLUE" );
        DotWorld.createDot( "SETUP24", 52, 54, 0, "BLUE" );
        DotWorld.createDot( "SETUP34", 53, 54, 0, "BLUE" );

	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testColorDot() {
        ColorDotReturnPair pair;
        pair = DotWorld.colorDot( "FICTIONAL_DOT", "RED" );
        assertFalse( pair.isSuccessful() );
        pair = DotWorld.colorDot( "FICTIONAL_DOT", "SILLY_COLOR" );
        assertFalse( pair.isSuccessful() );
        pair = DotWorld.colorDot( "FACTUAL_DOT", "RED" );
        assertTrue( pair.isSuccessful() );
        // should work even if color is the same:
        pair = DotWorld.colorDot( "FACTUAL_DOT", "RED" );
        assertTrue( pair.isSuccessful() );
        // Until we create an enumeration of legal colors, should accept:
        pair = DotWorld.colorDot( "FACTUAL_DOT", "SILLY_COLOR" );
        // TODO assertFalse as soon as I write a color enumeration:
        assertTrue( pair.isSuccessful() );
	}

	public void testCreateDot() {
        CreateDotReturnPair pair;
        pair = DotWorld.createDot( "A", 1, 1, 500, "RED");
        assertTrue( pair.isSuccessful() );
        assertEquals( "YOUSHOULDNOTSEETHIS", pair.getFailureCode());
        pair = DotWorld.createDot( "A", 1, 1, 500, "RED");
        assertFalse( pair.isSuccessful() );
        assertEquals( "AE", pair.getFailureCode());
        pair = DotWorld.createDot( "A", 2, 1, 500, "RED");
        assertFalse( pair.isSuccessful() );
        assertEquals( "AE", pair.getFailureCode());
        pair = DotWorld.createDot( "A", 1, 2, 500, "RED");
        assertFalse( pair.isSuccessful() );
        assertEquals( "AE", pair.getFailureCode());
        pair = DotWorld.createDot( "A", 2, 2, 500, "RED");
        assertFalse( pair.isSuccessful() );
        assertEquals( "AE", pair.getFailureCode());
        pair = DotWorld.createDot( "B", 1, 1, 500, "RED");
        assertFalse( pair.isSuccessful() );
        assertEquals( "DC", pair.getFailureCode());
        pair = DotWorld.createDot( "B", 1, 2, 500, "RED");
        assertTrue( pair.isSuccessful() );
        assertEquals( "YOUSHOULDNOTSEETHIS", pair.getFailureCode());
        pair = DotWorld.createDot( "C", 2, 1, 500, "RED");
        assertTrue( pair.isSuccessful() );
        assertEquals( "YOUSHOULDNOTSEETHIS", pair.getFailureCode());
        pair = DotWorld.createDot( "D", 2, 2, 500, "RED");
        assertTrue( pair.isSuccessful() );
        assertEquals( "YOUSHOULDNOTSEETHIS", pair.getFailureCode());
        pair = DotWorld.createDot( "D", 3, 3, 400, "RED");
        assertFalse( pair.isSuccessful() );
        assertEquals( "AE", pair.getFailureCode());
        pair = DotWorld.createDot( "E", 0, 0, 500, "RED");
        assertTrue( pair.isSuccessful() );
        assertEquals( "YOUSHOULDNOTSEETHIS", pair.getFailureCode());
        pair = DotWorld.createDot( "F", 0, 0, 400, "RED");
        assertFalse( pair.isSuccessful() );
        assertEquals( "DC", pair.getFailureCode());
 	}

	public void testCreatePath() {
	}

	public void testDeleteDot() {
	}

	public void testDeletePath() {
	}

	public void testExit() {
	}

	public void testListDots() {
        
	}

	public void testShortestPath() {
	}

}
