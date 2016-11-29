
public class AROB {

	int value;
	int dst;
	int rdy;
	int exc;
	int mis;
	int instrNum;
	
	public AROB() {
		value = -1;
		dst = -1;
		rdy = -1;
		exc = -1;
		mis = -1;
		instrNum  = -1;
	}
	
	public void setValue(int val){
		this.value = val;
	}

}
