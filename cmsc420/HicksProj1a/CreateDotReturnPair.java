public class CreateDotReturnPair {

    private boolean successful;
    private String failureCode;

	public CreateDotReturnPair(boolean successful, String failureCode) {
		this.successful = successful;
		this.failureCode = failureCode;
	}
	public String getFailureCode() {
		return failureCode;
	}
	public void setFailureCode(String failureCode) {
		this.failureCode = failureCode;
	}
	public boolean isSuccessful() {
		return successful;
	}
	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}
}
