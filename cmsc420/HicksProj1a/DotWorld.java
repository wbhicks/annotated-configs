public class DotWorld {

	//  Commented out because:
	//  I'll use names as keys, instead of using the whole DotValue:
	//    private static java.util.TreeMap theGraphByName
	//        = new java.util.TreeMap( new NameComparator() );

	private static java.util.TreeMap theGraphByName = new java.util.TreeMap();

	//  Commented out because:
	//  I'll use CoordTuple as keys, instead of using
	//    the whole DotValue, and I've made CoordTuple Comparable:
	//    private static java.util.TreeMap theGraphByCoord
	//        = new java.util.TreeMap( new CoordComparator() );

	private static java.util.TreeMap theGraphByCoord = new java.util.TreeMap();

	private static String dneString = "Error: The specified dot does not exist.";

	public static ColorDotReturnPair colorDot(String theName, String theColor) {
		boolean dotExisted;
		String oldColor;

		Object o = theGraphByName.get(theName);
		if (o instanceof DotValue) {
			dotExisted = true;
			DotValue dv = (DotValue) o;
			oldColor = dv.getColor();
			dv.setColor(theColor);
			theGraphByName.put(theName, dv);
		} else {
			dotExisted = false;
			oldColor = "IMPOSSIBLECOLOR";
		}
		// handle stdout from here
		if (dotExisted) {
			System.out.println("Color of " + theName + " changed from "
					+ oldColor + " to " + theColor + ".");
		} else {
			System.out.println(dneString);
		}
		ColorDotReturnPair pair = new ColorDotReturnPair(dotExisted, oldColor);
		return pair;
	}

	public static CreateDotReturnPair createDot(String theName, int theX,
			int theY, int theRadius, String theColor) {
		boolean successful;
		String failureCode;
		String squatter = "";

		if (theGraphByName.containsKey(theName)) {
			successful = false;
			failureCode = "AE";
		} else {
			CoordTuple ct = new CoordTuple(theX, theY);
			if (theGraphByCoord.containsKey(ct)) {
				successful = false;
				failureCode = "DC";
				// need squatter's name for stdout
				squatter = ((DotValue) (theGraphByCoord.get(ct))).getName();
			} else {
				// we're good to go :)
				successful = true;
				failureCode = "YOUSHOULDNOTSEETHIS";
				DotValue dv = new DotValue(theName, ct, theRadius, theColor);
				theGraphByName.put(theName, dv);
				theGraphByCoord.put(ct, dv);
			}
		}
		// handle stdout from here
		if (successful) {
			System.out.println("Created dot "
					+ dotAsString(theName, theX, theY, theColor) + ".");
		} else { // failure
			if (failureCode.equals("AE")) {
				System.out
						.println("Error: Dot " + theName + " already exists.");
			} else { // must be DC
				System.out.println("Error: Dot " + squatter
						+ " already exists at the specified coordinates.");
			}
		}
		CreateDotReturnPair pair = new CreateDotReturnPair(successful,
				failureCode);
		return pair;

	}

	public static void createPath(String theFirstName, String theSecondName) {
		if (theGraphByName.containsKey(theFirstName)
				&& theGraphByName.containsKey(theSecondName)) {

			// doesn't matter which vertex we use to see if edge exists,
			// since all edges are bidirectional:
			DotValue dv1 = (DotValue) theGraphByName.get(theFirstName);
			if (dv1.getNeighbors().containsKey(theSecondName)) {
				System.out.println("Error: The specified path already exists.");
			} else { // add the edge:
				// now we need the other DotValue:
				DotValue dv2 = (DotValue) theGraphByName.get(theSecondName);
				// compute the new edge's weight:
				double weight = euclidDist(dv1.getCoords(), dv2.getCoords());
				// tell the first about the second:
				NeighborValue tempnv = new NeighborValue(dv2, weight);
				dv1.getNeighbors().put(theSecondName, tempnv);
				// now recycle tempnv, and tell the second about the first:
				tempnv = new NeighborValue(dv1, weight);
				dv2.getNeighbors().put(theFirstName, tempnv);
				System.out.println("Created path (" + theFirstName + ","
						+ theSecondName + ").");
			}
		} else { // dne
			System.out.println(dneString);
		}
	}

	public static void deleteDot(String theName) {
        if ( theGraphByName.containsKey( theName )) {
            DotValue dv = (DotValue) theGraphByName.get( theName );
            java.util.Iterator i = dv.getNeighbors().values().iterator();
            NeighborValue tempnv;
            DotValue someNeighborDv;
            String someNeighborName;
            while (i.hasNext()) {
                tempnv = (NeighborValue) i.next();
                someNeighborDv = tempnv.getDv();
                // finally we get ahold of a 2nd name:
                someNeighborName = someNeighborDv.getName();
                // delete the bidirectional edge btwn them:
                // (3rd arg must be false to follow BNF)
                deletePath( theName, someNeighborName, false );
            } 
            // dv now has no adjacent vertices. It's disconnected.
            // get its coords so it can be removed from theGraphByCoord:
            theGraphByCoord.remove( dv.getCoords() );
            theGraphByName.remove( theName );
            System.out.println( "Deleted dot " + theName + "." );
        }
        else { // dne
            System.out.println(dneString);
        }
	}

	public static void deletePath(String theFirstName, String theSecondName,
			boolean verbose) {
		if (theGraphByName.containsKey(theFirstName)
				&& theGraphByName.containsKey(theSecondName)) {
			// doesn't matter which vertex we use to see if edge exists,
			// since all edges are bidirectional:
			DotValue dv1 = (DotValue) theGraphByName.get(theFirstName);
			if (dv1.getNeighbors().containsKey(theSecondName)) {
				// For starters, delete the edge from 1st to 2nd:
				dv1.getNeighbors().remove(theSecondName);
				// Next, delete the edge from 2nd to 1st:
				// For that we need the other DotValue:
				DotValue dv2 = (DotValue) theGraphByName.get(theSecondName);
				dv2.getNeighbors().remove(theFirstName);
				if (verbose) {
					System.out.println("Deleted path (" + theFirstName + ","
							+ theSecondName + ").");
				}
			} else { // there's no edge to delete:
				if (verbose) {
					System.out.println( 
                            "Error: The specified path does not exist.");
				}
			}
		} else { // dne
			if (verbose) {
				System.out.println(dneString);
		}}}

	public static void exit() {
		System.out.println("Have a nice day!");
	}

	public static void listDots() {
		if (theGraphByName.isEmpty()) {
			System.out.println("Dictionary is empty.");
		} else {
			DotValue dv;
			String name;
			String color;
			CoordTuple ct;
			int x;
			int y;
			java.util.Iterator i = theGraphByName.values().iterator();
			while (i.hasNext()) {
				dv = (DotValue) i.next();
				name = dv.getName();
				color = dv.getColor();
				ct = dv.getCoords();
				x = ct.getX();
				y = ct.getY();
				System.out.println(dotAsString(name, x, y, color));
			}
		}
	}

	public static void shortestPath(String theFirstName, String theSecondName) {
	}

	private static String dotAsString(String name, int x, int y, String color) {
		String s = name + " at (" + x + "," + y + ") color:" + color;
		return s;
	}

	private static double euclidDist(CoordTuple a, CoordTuple b) {
		return Math.sqrt((a.getX() - b.getX()) * (a.getX() - b.getX())
				+ (a.getY() - b.getY()) * (a.getY() - b.getY()));
	}

}

