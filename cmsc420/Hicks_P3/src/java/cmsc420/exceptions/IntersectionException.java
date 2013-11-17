package cmsc420.exceptions;


/**
 * Has 2 uses: (1) raised in DotWorld's mapSeg() when a true intersection is detected, and (2) raised there
 * too when mapSeg catches a MaxDepthException from QTNode's add(). The reason for (2) is that I didn't want 
 * to add a MaxDepthException to DotWorld's API, since that would affect the Parser, and yet using
 * MaxDepthException in QTNode's add() clarifies things there. Eventually I'll allow for it in the DotWorld
 * API too. In the meantime, I added a constructor here that takes a MaxDepthException and copies its fields
 * into corresponding fields here, in IntersectionException.
 * 
 * @author wbhicks
 *
 * @version 
 */
public class IntersectionException extends Exception {

		// all these fields are required merely to accommodate data salvaged from a MaxDepthException
	public String dotName;
	public int segListSize, lx, ly, ux, uy;
			
	public IntersectionException( String message, MaxDepthException mde ) {
		super( message );
	    this.dotName = mde.dotName;
	    this.segListSize = mde.segListSize;
	    this.lx = mde.lx;
	    this.ly = mde.ly;
	    this.ux = mde.ux;
	    this.uy = mde.uy;

	}
	
public IntersectionException() {
super();
// TODO Auto-generated constructor stub
}

public IntersectionException( String message ) {
super( message );
// TODO Auto-generated constructor stub
}

public IntersectionException( String message,
	Throwable cause ) {
super( message, cause );
// TODO Auto-generated constructor stub
}

public IntersectionException( Throwable cause ) {
super( cause );
// TODO Auto-generated constructor stub
}

}
