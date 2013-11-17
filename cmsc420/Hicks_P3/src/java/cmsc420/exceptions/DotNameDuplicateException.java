package cmsc420.exceptions;


/**
 * @author wbhicks
 *
 * @version 
 */
public class DotNameDuplicateException extends Exception {

    public String name;
    
public DotNameDuplicateException() {
super();
// TODO Auto-generated constructor stub
}

// I don't want to accidentally use this when I want to set name
//public DotNameDuplicateException( String message ) {
//super( message );
//// TODO Auto-generated constructor stub
//}

public DotNameDuplicateException( String message,
	Throwable cause ) {
super( message, cause );
// TODO Auto-generated constructor stub
}

public DotNameDuplicateException( Throwable cause ) {
super( cause );
// TODO Auto-generated constructor stub
}

public DotNameDuplicateException( String message, String name ) {
    super( message );
    this.name = name;
    }

}
