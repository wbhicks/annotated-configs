package our

import our.P
import java.awt.Color
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

public class SimpleTileGTest{
	
	def stMap // will be a java.util.LinkedHashMap once initialized
	static boolean isFirstSetUp = true // helps restrain logging
				
	@BeforeClass
	static void setUpBeforeClass() throws Exception{	}
	
	@AfterClass
	static void tearDownAfterClass() throws Exception{}
	
	@Before
	void setUp() throws Exception{
		stMap = [ 't35a': new SimpleTileG( 3, 5 ), 
		          't35b': new SimpleTileG( 3, 5 ), 
		          't37' : new SimpleTileG( 3, 7 ), 
		          't75' : new SimpleTileG( 7, 5 ),
		          't53' : new SimpleTileG( 5, 3 ), 
		          't22' : new SimpleTileG( 2, 2 ), 
		          't04a': new SimpleTileG( 0, 4 ), 
		          't04b': new SimpleTileG( 0, 4 ),
		          't00a': new SimpleTileG( 0, 0 ), 
		          't00b': new SimpleTileG( 0, 0 ) ]
		if ( isFirstSetUp ) {
				// stMap happens to be a java.util.LinkedHashMap:
			P.t( stMap.getClass() ) // can't use .class on maps
				// java.util.LinkedHashMap's each method takes 1 arg, a closure:
			stMap.each { 
				SimpleTileG stg = it.value
				P.t it.key + " is at " + stg.xPosition + "," + stg.yPosition
			}
			isFirstSetUp = false
		}
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	void tearDown() throws Exception{
	}
	
	/**
	 * Suite method to start jUnit4-Test with a jUnit3-Runner. 
	This is used by tools not supporting jUnit4 properly.
	 */
	static final junit.framework.Test suite() {
		return new junit.framework.JUnit4TestAdapter(SimpleTileGTest.class);
	}
	
	/* (non-Javadoc)
	 * @see our.SimpleTileG#SimpleTileG(int, int)
	 */
/*
	@Test
	final void testSimpleTileG( int n1, int n2 ) {
		//TODO Implement the Testmethod SimpleTileG
	
		fail("Not yet implemented")
	}
*/

	/* (non-Javadoc)
	 * @see our.SimpleTileG#SimpleTileG(int)
	 */
/*	@Test
	final void testSimpleTileG( int n ) {
		//TODO Implement the Testmethod SimpleTileG
	
		fail("Not yet implemented")
	}
*/

	/* (non-Javadoc)
	 * @see our.SimpleTileG#SimpleTileG()
	 */
	@Test
	final void testSimpleTileG() {
		//TODO Implement the Testmethod SimpleTileG
	
		Assert.fail("Not yet implemented")
	}
	
	/* (non-Javadoc)
	 * @see our.SimpleTileG#updateCandidatePositions()
	 */
	@Test
	final void testUpdateCandidatePositions() {
		//TODO Implement the Testmethod UpdateCandidatePositions
	
		Assert.fail("Not yet implemented")
	}
	
	/* (non-Javadoc)
	 * @see our.SimpleTileG#invokeMethod(java.lang.String, java.lang.Object)
	 */
	@Test
	final void testInvokeMethod() {
		//TODO Implement the Testmethod InvokeMethod
	
		Assert.fail("Not yet implemented")
	}
	
	/* (non-Javadoc)
	 * @see our.SimpleTileG#getProperty(java.lang.String)
	 */
	@Test
	final void testGetProperty() {
		//TODO Implement the Testmethod GetProperty
	
		Assert.fail("Not yet implemented")
	}
	
	/* (non-Javadoc)
	 * @see our.SimpleTileG#setProperty(java.lang.String, java.lang.Object)
	 */
	@Test
	final void testSetProperty() {
		//TODO Implement the Testmethod SetProperty
	
		Assert.fail("Not yet implemented")
	}
	
	/* (non-Javadoc)
	 * @see our.SimpleTileG#getCandidateXPosition()
	 */
	@Test
	final void testGetCandidateXPosition() {
		//TODO Implement the Testmethod GetCandidateXPosition
	
		Assert.fail("Not yet implemented")
	}
	
	/* (non-Javadoc)
	 * @see our.SimpleTileG#setCandidateXPosition(int)
	 */
	@Test
	final void testSetCandidateXPosition() {
		//TODO Implement the Testmethod SetCandidateXPosition
	
		Assert.fail("Not yet implemented")
	}
	
	/* (non-Javadoc)
	 * @see our.SimpleTileG#getCandidateYPosition()
	 */
	@Test
	final void testGetCandidateYPosition() {
		//TODO Implement the Testmethod GetCandidateYPosition
	
		Assert.fail("Not yet implemented")
	}
	
	/* (non-Javadoc)
	 * @see our.SimpleTileG#setCandidateYPosition(int)
	 */
	@Test
	final void testSetCandidateYPosition() {
		//TODO Implement the Testmethod SetCandidateYPosition
	
		Assert.fail("Not yet implemented")
	}
	
	/* (non-Javadoc)
	 * @see our.SimpleTileG#getXPosition()
	 */
	@Test
	final void testGetXPosition() {
		//TODO Implement the Testmethod GetXPosition
	
		Assert.fail("Not yet implemented")
	}
	
	/* (non-Javadoc)
	 * @see our.SimpleTileG#setXPosition(int)
	 */
	@Test
	final void testSetXPosition() {
		//TODO Implement the Testmethod SetXPosition
	
		Assert.fail("Not yet implemented")
	}
	
	@Test
	final void testGetYPosition() {
		assert 0
	}
	
	@Test
	final void testSetYPosition() {
		assert -16
	}
	
	@Test
	final void testGetShade() {
		assert stMap.t75.shade == java.awt.Color.MAGENTA
	}
	
	@Test
	final void testSetShade() {
		assert 0.4
	}
	
	@Test
	final void testEquals() {
	/* won't work: seems to test for identity, not equality:
	 * Assert.assertEquals( stg1, stg2 )
	 * 
	 * won't work: Eclipse shows red dot when parens are missing:
	 * assert stg1.equals stg2
	 * 
	 * won't work: Eclipse shows red dot when parens are missing:
	 * assert stMap.t35a.equals stMap.t35b
	 *
	 * TODO: use JUnit-specific assert___ methods, not Groovy assert
	 */	
	 	assert stMap.t35a.equals( stMap.t35b )
	 	assert stMap.t04a.equals( stMap.t04b )
	 	assert stMap.t00a.equals( stMap.t00b )
	 		// remove the dupes:
	 	stMap.remove( "t35b" )
	 	stMap.remove( "t04b" )
	 	stMap.remove( "t00b" )
	 	stMap.each { 
	 		def k1 = it.key
	 		def v1 = it.value
	 		stMap.each {
	 		// TODO: use P.t :-)
	 			print "# " + k1 + "=(" + v1.xPosition + "," + v1.yPosition + 
	 					") " + it.key + "=(" + it.value.xPosition + "," +
	 					it.value.yPosition + ")"
	 			if ( it.key != k1 ) {
	 				println " DIFF"
	 					// works only because we've removed dupes:
	 				assert !(it.value.equals( v1 ))
	 			} else {
	 				println " SAME"
	 				assert it.value.equals( v1 )
	 			}
	 		}
	 	}
	}
	
	@Test
	final void testToString() { Assert.fail("Not yet implemented") }
	
	@Test
	final void testFoo() { assert 2 == 1 }
}