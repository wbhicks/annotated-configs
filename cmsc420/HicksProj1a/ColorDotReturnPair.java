public class ColorDotReturnPair {

    private boolean successful;
    private String oldColor;
    
	public ColorDotReturnPair( boolean b, String c ) {
        // use of "this" here is optional.
        // could have used the setters, but didn't :)
		successful = b;
		oldColor = c;
	}
	public String getOldColor() {
		return oldColor;
	}
	public void setOldColor(String oldColor) {
		this.oldColor = oldColor;
	}
	public boolean isSuccessful() {
		return successful;
	}
	public void setSuccessful(boolean b) {
		successful = b;
	}
}
