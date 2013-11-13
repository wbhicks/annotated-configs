import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * 
 * @author Evan Machusak (and wbhicks)
 *
 * This is the class Evan posted on the newsgroup, with 
 * method contributions by me (wbhicks).
 */

public class Parser {

	private static void printError() {
		System.out.println("*****");
		System.out.println("Error: Invalid Command");
		System.out.println();
	}

	private static boolean isWS(String s) {
		for (int i = 0; i < s.length(); i++)
			if (s.charAt(i) != ' ')
				return false;
		return true;
	}

	public static void main(String[] args) {

		long start = System.currentTimeMillis();

		InputStreamReader rd = new InputStreamReader(System.in);
		BufferedReader console = new BufferedReader(rd);
		String inline;

		try {
			while ((inline = console.readLine()) != null) {

				// TEST
				// System.out.println( "hi" );

				// from Evan's message of June 10
				inline = inline.replaceAll("\t", " ");

				// from Dr. Hugue's suggestion of June 10
				inline = inline.toUpperCase();

				// ---- BEGIN don't mess with -----
				int i = 0;
				String[] line_ws = null;
				StringTokenizer tokenizer = new StringTokenizer(inline, "(,) ",
						true);
				line_ws = new String[tokenizer.countTokens()];
				while (tokenizer.hasMoreTokens())
					line_ws[i++] = tokenizer.nextToken();
				int ws = 0;
				for (i = 0; i < line_ws.length; i++)
					if (isWS(line_ws[i]) == false)
						ws++;
				String[] line = new String[ws];
				ws = 0;
				for (i = 0; i < line_ws.length; i++)
					if (isWS(line_ws[i]) == false)
						line[ws++] = line_ws[i];
				// ---- END don't mess with -----
				if (line.length > 0) {

					// req'd by specs page 8
					// BUT is this OK for invalid commands too?

					System.out.println("*****");
					System.out.print("==> ");
					for (int theIter = 0; theIter < line.length; theIter++) {
						System.out.print( 
								//theIter + "=[" + 
								line[theIter] 
								//	 + "]"
							    );
					}
					System.out.println();
					vet(line);
				}
			}
		} catch (Exception e) {
		}
		/*
		System.out.println("DEBUG Running time (ms): "
				+ (System.currentTimeMillis() - start));
        */
	}

	/**
	 * @param theLine
	 *            receives line from main and dispatches it to the handler for
	 *            that cmd, if any
	 */
	private static void vet(String[] theLine) {
		if (theLine[0].equals("COLOR_DOT"))
			vetCmdColorDot(theLine);
		else if (theLine[0].equals("CREATE_DOT"))
			vetCmdCreateDot(theLine);
		else if (theLine[0].equals("CREATE_PATH"))
			vetCmdCreatePath(theLine);
		else if (theLine[0].equals("DELETE_DOT"))
			vetCmdDeleteDot(theLine);
		else if (theLine[0].equals("DELETE_PATH"))
			vetCmdDeletePath(theLine);
		else if (theLine[0].equals("EXIT"))
			vetCmdExit(theLine);
		else if (theLine[0].equals("LIST_DOTS"))
			vetCmdListDots(theLine);
		else if (theLine[0].equals("SHORTEST_PATH"))
			vetCmdShortestPath(theLine);
		else
			complain(theLine);
	}

	/*
	 * The following methods are invoked once we know that element 0 (the first
	 * element) is a valid command name, but before we know anything else about
	 * theCmdLine. This is the earliest point at which we can handle different
	 * commands differently. There is a 1-to-1 correspondence between these
	 * methods and the command implementors in DotWorld. They make the Parser
	 * class like an interpreter for a language, where (1) standard in is like the
	 * source code (the script), (2) DotWorld is like a native implementation of an
	 * API, and (3) the Parser class is the interpreter that maps the
	 * script to that API.
	 */

	/**
	 * @param theCmdLine
	 *            Element 0 is correct. Are the rest? If so, call the
	 *            corresponding method
	 */
	private static void vetCmdColorDot(String[] theCmdLine) {
		regurgitateCmd(theCmdLine); // for debugging
		if ((hasValidDelimiterPattern(theCmdLine, 2)) // 2 args
				&& isValidName(theCmdLine[2]) && isValidColor(theCmdLine[4]))
			DotWorld.colorDot(theCmdLine[2], theCmdLine[4]);
		else
			complain(theCmdLine);
	}

	/**
	 * @param theCmdLine
	 */
	private static void vetCmdCreateDot(String[] theCmdLine) {
		regurgitateCmd(theCmdLine);
		if (hasValidDelimiterPattern(theCmdLine, 5) // 5 args
				&& isValidName(theCmdLine[2])
				&& willBeValidCoord(theCmdLine[4])
				&& willBeValidCoord(theCmdLine[6])
				&& willBeValidRadius(theCmdLine[8])
				&& isValidColor(theCmdLine[10]))
			DotWorld.createDot(theCmdLine[2], 
					makeCoord( theCmdLine[4] ), 
					makeCoord( theCmdLine[6] ),
					makeRadius( theCmdLine[8] ), 
					theCmdLine[10]);
		else
			complain(theCmdLine);

	}

	/**
	 * @param theCmdLine
	 */
	private static void vetCmdCreatePath(String[] theCmdLine) {
		regurgitateCmd(theCmdLine);
		if (hasValidDelimiterPattern(theCmdLine, 2) // 2 args
				&& isValidName(theCmdLine[2]) && isValidName(theCmdLine[4]))
			DotWorld.createPath(theCmdLine[2], theCmdLine[4]);
		else
			complain(theCmdLine);

	}
	
	/**
	 * @param theCmdLine
	 */
	private static void vetCmdDeleteDot(String[] theCmdLine) {
		regurgitateCmd(theCmdLine);
		if (hasValidDelimiterPattern(theCmdLine, 1) // 1 args
				&& isValidName(theCmdLine[2]))
			DotWorld.deleteDot(theCmdLine[2]);
		else
			complain(theCmdLine);

	}
	
	/**
	 * @param theCmdLine
	 */
	private static void vetCmdDeletePath(String[] theCmdLine) {
		regurgitateCmd(theCmdLine);
		if (hasValidDelimiterPattern(theCmdLine, 2) // 2 args
				&& isValidName(theCmdLine[2]) 
				&& isValidName(theCmdLine[4])) {
			// 3rd arg is true so method will report on stdout
			DotWorld.deletePath(theCmdLine[2], theCmdLine[4], true);
		} else
			complain(theCmdLine);

	}
	
	/**
	 * @param theCmdLine
	 */
	private static void vetCmdExit(String[] theCmdLine) {
		regurgitateCmd(theCmdLine);
		if (hasValidDelimiterPattern(theCmdLine, 0)) // 0 args
			DotWorld.exit();
		else
			complain(theCmdLine);

	}
	
	/**
	 * @param theCmdLine
	 */
	private static void vetCmdListDots(String[] theCmdLine) {
		regurgitateCmd(theCmdLine);
		if (hasValidDelimiterPattern(theCmdLine, 0)) // 0 args
			DotWorld.listDots();
		else
			complain(theCmdLine);

	}
	
	/**
	 * @param theCmdLine
	 */
	private static void vetCmdShortestPath(String[] theCmdLine) {
		regurgitateCmd(theCmdLine);
		if (hasValidDelimiterPattern(theCmdLine, 2) // 2 args
				&& isValidName(theCmdLine[2]) 
				&& isValidName(theCmdLine[4]))
			DotWorld.shortestPath(theCmdLine[2], theCmdLine[4]);

		else
			complain(theCmdLine);

	}

	/**
	 * @param theCmdLine
	 * @param expectedNumOfArgs
	 * @return 
	 * 
	 * Asserts nothing about the args themselves, except that there is
	 *         the correct number of elements in place to match them, and that
	 *         all elements that can not be args are themselves correct.
	 * 
	 * If true, then all that's left is to examine the elements that are in the
	 * locations where the args are expected - are they really args?
	 */
	private static boolean hasValidDelimiterPattern(String[] theCmdLine,
			int expectedNumOfArgs) {
		boolean p = true;
		if (!(theCmdLine[1].equals( "(" ))) {
			p = false;
			//System.out.print( "~" );
		}
		else if ((0 < expectedNumOfArgs)
				&& (theCmdLine.length != (2 + (2 * expectedNumOfArgs)))) {
			p = false;
			//System.out.print( "!" );
		}
		// length is correct. Last comma (if any), therefore, should be at
		// theCmdLine[ expectedNumOfArgs * 2 - 1 ]
		else
			for (int i = 3; i <= (expectedNumOfArgs * 2 - 1); i += 2) {
				if (!(theCmdLine[i].equals( "," ))) {
					p = false;
					//System.out.print( "@" );
				}
			}
		// no else here! That wasn't an if, back there :)
		if (!(theCmdLine[theCmdLine.length - 1].equals( ")" ))) {
			p = false;
			//System.out.print( "#" );
		}
		return p;
	}

	private static boolean isValidName(String s) {
		// Seems it can be any String that survived the tokenizer :)
		return true;
	}

	private static boolean isValidColor(String s) {
		boolean p = false;
		if (s.equals( "RED" ) 
				|| s.equals("GREEN" ) 
				|| s.equals("BLUE" ) 
				|| s.equals("BLACK" )
				|| s.equals("WHITE" ))
			p = true;
		//System.out.print( "[c?" + p + "]" );
		return p;
	}

	private static boolean willBeValidRadius(String s) {
		boolean p = false;
		int n = -99; // should never see this in output
		try {
			n = Integer.parseInt(s);
			p = true;
		} catch (NumberFormatException e) {
			//System.out.println("   XEP in Parser.willBeValidRadius");
		}
		//System.out.print( "[r?" + p + "]" );
		return p;
	}

	private static int makeRadius(String s) {
		int n = -98; // should never see this in output
		try {
			n = Integer.parseInt(s);
		} catch (NumberFormatException e) {
		}
		return n; }
		
	private static boolean willBeValidCoord(String s) {
		boolean p = false;
		int n = -97; // should never see this in output
		try {
			n = Integer.parseInt(s);
			p = true;
		} catch (NumberFormatException e) {
			//System.out.println("   XEP in Parser.willBeValidCoord");
		}
		//System.out.print( "[xy?" + p + "]" );
		return p;
	}

	private static int makeCoord(String s) {
		int n = -96; // should never see this in output
		try {
			n = Integer.parseInt(s);
		} catch (NumberFormatException e) {
		}
		return n; }
	
	/**
	 * Element 0 is not recognized, OR delimiters screwy, OR name (or color) is
	 * invalid, OR coord (or radius) is invalid.
	 */
	private static void complain(String[] theCmdLine) {
		//		 req'd by specs page 2
		System.out.println("*****");
		System.out.println("Error: Invalid Command.");
	}
	
/*
	private static void complain(String[] theLine, int i) {
		// DEBUG only!
		System.out.println("**" + i + "**");
		System.out.println("Error: Invalid Command.");
	}
*/


	/**
	 * @param theCmdLine
	 *            for debugging only, so far
	 */
	private static void regurgitateCmd(String[] theCmdLine) {
		//System.out.println( "<" + theCmdLine[0] + ">");
	}

	// Evan's semicolon :)
};
