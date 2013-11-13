public class CoordTuple implements Comparable {

	private int x;
	private int y;

	public CoordTuple(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}

	public int compareTo(Object o) throws ClassCastException {
		if (o instanceof CoordTuple) {
			int result;
			CoordTuple ct = (CoordTuple) o;

			/*
			 * DISCLAIMER: I got the idea for this ?: resolver from the book
			 * _Java Precisely_ by Sestoft, p. 77
			 */
			result = ((x != ct.getX()) ? (x - ct.getX()) : (y - ct.getY()));

			return result;
		} else {
            throw new ClassCastException();
		}
	}
    
    public boolean equals( Object o ) {
        boolean result = false;
        if (o instanceof CoordTuple) {
        	    // I don't think casting is necessary here,
            // but I do it for clarity:
        	    result = ( 0 == compareTo(( CoordTuple ) o ));
        }
        return result;
    }
    
    /* TODO must override hashCode() too before we start to use hash tables!
     */
}









