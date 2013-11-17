package cmsc420.p3;


/**
 * @author wbhicks
 *
 * @version 
 */
public class QTMagnitude {

public int n;
    
/**
 * For now, it is not the job of a QTMagnitude
 * constructor to refuse bad magnitudes. That's because,
 * by the BNF, a bad magnitude isn't garbage - instead,
 * it calls for its own error response. Thus the method
 * initQT() should handle bad magnitudes. If this
 * constructor threw an exception, and parseAs() caught
 * it, then it would have the same effect as "FOO" for
 * a color, contra the BNF.
 * 
 * @param requested
 */
public QTMagnitude( int requested ) {
    super();
    this.n = requested;
}


public boolean equals( QTMagnitude other ) {
    if (null == other) {
        return false;
    } else {
        return ( this.n == other.n );
    }
}


}
