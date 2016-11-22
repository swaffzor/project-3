
public class Instruction {

	public int PC;
	public int opType;
	public int dest;
	public int src1;
	public int src2;
	public int stage;
	
	public Instruction(int pc, int optype, int d, int s1, int s2, int stage) {
		this.PC = pc;
		this.opType = optype;
		this.dest = d;
		this.src1 = s1;
		this.src2 = s2;
		this.stage = stage;
	}

}
