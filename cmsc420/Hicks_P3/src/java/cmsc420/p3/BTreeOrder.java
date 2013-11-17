package cmsc420.p3;


/**
 * @author wbhicks
 *
 * @version 
 */
public class BTreeOrder {

public int n = 3;    
    
/**
 * From Dr. Hugue's specs: "The order will never be less 
 * than 3.  You should check for this  condition to avoid 
 * crashing horribly on a bad input, but I won't add a 
 * specific error for this part."
 * 
 * If given <3, uses 3.
 */
public BTreeOrder( int requested) {
super();
this.n = (( 3 > requested ) ? 3 : requested ); 

}


public boolean equals( BTreeOrder other ) {
    if (null == other) {
        return false;
    } else {
        return ( this.n == other.n );
    }
}


}
