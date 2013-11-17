package our

class SimpleTileG {

	static int candidateXPosition = 0
	static int candidateYPosition = 0
	int xPosition
	int yPosition
	java.awt.Color shade = java.awt.Color.MAGENTA
	
	SimpleTileG( int xPosition = candidateXPosition, 
			int yPosition = candidateYPosition) {
/*		println "in stg const before: " + xPosition + ", " + yPosition +
				"; " + this.xPosition + ", " + this.yPosition
*/		this.xPosition = xPosition
		this.yPosition = yPosition
/*		println "in stg const after : " + xPosition + ", " + yPosition +
				"; " + this.xPosition + ", " + this.yPosition
*/		if (   xPosition == candidateXPosition 
			&& yPosition == candidateYPosition ) 
			updateCandidatePositions()
	}
	
	void updateCandidatePositions() {
		/*
		 * Foolishly assumes (x+1,y+1) is free. This is
		 * dangerous if the no-arg constructor is called.
		 * Also, tiles will race toward the northeast.
		 */	
		candidateXPosition++
		candidateYPosition++
	}
	
	boolean equals( SimpleTileG a ) {
		return ( xPosition == a.xPosition ) && ( yPosition == a.yPosition )
	}
	
}
