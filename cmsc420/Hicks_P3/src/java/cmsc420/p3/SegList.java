package cmsc420.p3;

import java.util.Iterator;
import java.util.TreeSet;


/**
 * @author wbhicks
 *
 * @version 
 */
public class SegList extends TreeSet { // java.util.LinkedList {

    public String toString() {
        StringBuffer sb = new StringBuffer();
        Iterator iter = this.iterator();
        while ( iter.hasNext() ) {
            sb.append( iter.next().toString() );
        }
        
        return sb.toString();
    }


}
