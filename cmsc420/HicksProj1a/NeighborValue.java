public class NeighborValue {
    
    private DotValue dv;
    private double weight;

	public NeighborValue(DotValue dv, double weight) {
		this.dv = dv;
		this.weight = weight;
	}

	public DotValue getDv() {
		return dv;
	}
	public void setDv(DotValue dv) {
		this.dv = dv;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
}
