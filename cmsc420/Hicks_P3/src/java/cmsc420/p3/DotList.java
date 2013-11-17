package cmsc420.p3;

import java.util.Iterator;


/**
 * Used for those return values in the API that hold
 * two (or, rarely, more) Dots.
 * 
 * @author wbhicks
 *
 * @version 
 */
public class DotList extends java.util.LinkedList {

public DotList() {
	super();
}

public String toString() {
    StringBuffer sb = new StringBuffer();
	Iterator iter = this.iterator();
    while ( iter.hasNext() ) {
        sb.append( iter.next().toString() );
        sb.append( "\n" );
    }
    return sb.toString();
}

}
