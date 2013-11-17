package cmsc420.exceptions;


/**
 * @author wbhicks
 *
 * @version 
 */
public class CoordDuplicateException extends Exception {

    public String squatter;
    
public CoordDuplicateException() {
super();
// TODO Auto-generated constructor stub
}

// commented out to avoid confusion with the one that
// sets squatter
//public CoordDuplicateException( String message ) {
//super( message );
//// TODO Auto-generated constructor stub
//}

public CoordDuplicateException( String message,
	Throwable cause ) {
super( message, cause );
// TODO Auto-generated constructor stub
}

public CoordDuplicateException( Throwable cause ) {
super( cause );
// TODO Auto-generated constructor stub
}

public CoordDuplicateException( 
    String message, String squatter ) {
    super( message );
    this.squatter = squatter;
    }
}
