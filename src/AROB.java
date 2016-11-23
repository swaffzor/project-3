
public class AROB {

	String value;
	int dst;
	int rdy;
	int exc;
	int mis;
	long PC;
	
	public AROB() {
		value = "na";
		dst = 0;
		rdy = 0;
		exc = 0;
		mis = 0;
		PC  = 0;
	}
	
	public void setValue(String val){
		this.value = val;
	}

}
