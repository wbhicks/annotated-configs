package cmsc420.exceptions;

/**
 * To be thrown only from QTNode, for now. Means the tree's bottomed out (some node's "too small to split").
 * Catch, and turn into an IntersectionException. Keep out of the DotWorld API. (Parser expects only 
 * IntersectionExceptions.)
 * 
 * @author wbhicks
 *
 * @version 
 */
public class MaxDepthException extends Exception {

	public String dotName;
	public int segListSize, lx, ly, ux, uy;
			
	public MaxDepthException() { super();	}
	
	public MaxDepthException( String message ) { super( message );	}
	
	public MaxDepthException( String message, String dotName, int segListSize, int lx, int ly, int ux, int uy ) {
	    super( message );
	    this.dotName = dotName;
	    this.segListSize = segListSize;
	    this.lx = lx;
	    this.ly = ly;
	    this.ux = ux;
	    this.uy = uy;
    }
	
	public MaxDepthException( String message, Throwable cause ) { super( message, cause ); 	}
	
	public MaxDepthException( Throwable cause ) { super( cause ); 	}
}
