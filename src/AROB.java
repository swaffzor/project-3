
public class AROB {

	int value;
	int dst;
	int rdy;
	int exc;
	int mis;
	int instrNum;
	
	public AROB() {
		value = 0;
		dst = 0;
		rdy = 0;
		exc = 0;
		mis = 0;
		instrNum  = 0;
	}
	
	public void setValue(int val){
		this.value = val;
	}

}
