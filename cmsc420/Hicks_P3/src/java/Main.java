import java.io.IOException;

import cmsc420.exceptions.BadCodeException;
import cmsc420.p3.DotWorldVeriList;
import cmsc420.p3.Parser;

/**
 * @author wbhicks
 *
 * @version 
 */
public class Main {
	
public static void main( String[] args ) throws IllegalArgumentException, IOException, 
		IllegalAccessException, BadCodeException {	
	Parser p = new Parser( new DotWorldVeriList() );
	p.topLevel( null );
}
}
