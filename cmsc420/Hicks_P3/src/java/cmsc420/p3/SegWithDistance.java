package cmsc420.p3;

import java.text.DecimalFormat;

public class SegWithDistance {
    
    public DotName nameOfLesser;
    public DotName nameOfGreater;
    public double dist;

    
/* @param nameOfLesser name of the lesser dot
/* @param nameOfGreater name of the greater dot
/* @param dist
 */
public SegWithDistance( 
    DotName nameOfLesser, DotName nameOfGreater, double dist ) {
super();
this.nameOfLesser = nameOfLesser;
this.nameOfGreater = nameOfGreater;
this.dist = dist;
}

public String toString() {
    DecimalFormat df = new DecimalFormat( "0.000" );
    String s = df.format( dist );
	return "Nearest segment: (" + nameOfLesser.getName() + "," 
        + nameOfGreater.getName() + "). Distance: " + s + ".";
}
}
