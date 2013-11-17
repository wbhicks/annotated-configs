package cmsc420.p3;

import cmsc420.exceptions.DotColorFormatException;

/**
 * No "default" DotColor, thus no no-arg constructor
 * 
 * @author wbhicks
 * 
 * @version
 */
public class DotColor {

private String color;

public DotColor( String s ) throws DotColorFormatException {
if ( s.equals( "RED" ) || s.equals( "GREEN" )
	|| s.equals( "BLUE" ) || s.equals( "BLACK" )
	|| s.equals( "WHITE" ) ) {
	this.setColor( s );
} else {
	DotColorFormatException dcfe = new DotColorFormatException();
    throw dcfe;
}
}

/* Private, because it should never be changed, and thus
 * this setter should be invoked only from this class'
 * constructor(s).
 * 
 * @param color The color to set.
 */
private void setColor( String color ) {
this.color = color;
}

/**
 * @return Returns the color.
 */
public String getColor() {
return color;
}

public boolean equals( DotColor other ) {
    if (null == other) {
        return false;
    } else {
        return (this.getColor().equals( other.getColor() ));
    }
}

}
