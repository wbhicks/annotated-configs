public class DblBug {
	public static void main(String[] args) {
		double x = 0;
		System.out.println("x = " + x);
		x = Double.valueOf("2.2250738585072012e-308");
		System.out.println("x = " + x);
		System.out.println("end");
	}
}
