/**
 * The purpose of this file is to hold the class of the same name.
 * Created Dec 18, 2008.
 */
package our;

/**
 * Will be hex tiles, for now.
 */
public class SimpleTileJ implements Tile {

/**
 * nice column for the next tile to be constructed
 */
static int candidateXPosition = 0;

/**
 * nice row for the next tile to be constructed
 */
static int candidateYPosition = 0;
		
/**
 * 0 indicates the leftmost column
 */
int xPosition;

/**
 * 0 indicates the bottom row 
 */
int yPosition;

/**
 * TODO clear (i.e. transparent) should be the default
 */
java.awt.Color color = java.awt.Color.MAGENTA;

/**
 * @param theXPosition Which column?
 * @param theYPosition Which row?
 */
public SimpleTileJ(int theXPosition, int theYPosition) {
	xPosition = theXPosition;
	yPosition = theYPosition;
	if (   xPosition == candidateXPosition 
		&& yPosition == candidateYPosition ) 
	{
		updateCandidatePositions();
	}
}

/**
 * the no-arg constructor
 */
public SimpleTileJ() {
	xPosition = candidateXPosition;
	yPosition = candidateYPosition;
	updateCandidatePositions();
}



/**
 * Called IFF the candidate location has 
 * just been filled, and we need a new one.
 */
private void updateCandidatePositions() {
	/*
	 * Foolishly assumes (x+1,y+1) is free. This is
	 * dangerous if the no-arg constructor is called.
	 * Also, tiles will race toward the northeast.
	 */	
	candidateXPosition++;
	candidateYPosition++;
	
}

	
}
