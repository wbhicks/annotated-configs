/**
 * The purpose of this file is to hold the class of the same name.
 * Created Dec 22, 2008.
 */
package our

/**
 * makeshift printing+logging class
 */
public class P{

	static void l( x ) {
		println " " + (8226 as char) + " " + x
	}
	
	static void t( x ) { // t for trace, our lowest level
		println " " + (8224 as char) + " " + x
	}
}
