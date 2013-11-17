package cmsc420.p3;

import cmsc420.exceptions.DotNameFormatException;


/**
 * Evan Machusak's parser code ensures there is no whitespace
 * in the String. That was the only restriction on names
 * mentioned by the spec -- I must check the newsgroup for
 * any others.
 * 
 * @author wbhicks
 *
 * @version 
 */
public class DotName implements Comparable {
    
private String name;

/**
 * There may be other naming restrictions imposed by the
 * specs, but for now we'll balk only at a null ref or an
 * empty string
 * 
 * @param s the desired name, as a String
 * @throws DotNameFormatException
 */
public DotName( String s ) throws DotNameFormatException {
if (( null == s ) || ( "".equals( s ))) {
    DotNameFormatException dnfe 
        = new DotNameFormatException();
    throw dnfe;    
} else {
    this.setName( s );    
}
}

/* Private, because it should never be changed, and thus
 * this setter should be invoked only from this class'
 * constructor(s).
 * 
 * @param name The name to set.
 */
private void setName( String name ) {
this.name = name;
}

/**
 * @return Returns the name.
 */
public String getName() {
return name;
}

public int compareTo( Object o ) throws ClassCastException {
if ( o instanceof DotName ) {
    DotName otherDotName = (DotName) o;
    return name.compareTo( otherDotName.getName());
} else {
    throw new ClassCastException();
}
}

public boolean equals( Object o ) {
//    return ( 0 == compareTo( o ));
    if ( o instanceof DotName ) {
    	return name.equals( ((DotName) o).name );
    } else return false;
}

}
