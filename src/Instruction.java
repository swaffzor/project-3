
public class Instruction {

	public long PC;
	public int opType;
	public int dest;
	public int src1;
	public int src2;
	public boolean src1rdy;
	public boolean src2rdy;
	public int stage;
	public int instNum;
	public int begin[] = new int[9];
	
	public Instruction(long pc, int instnum, int cycle, int optype, int d, int s1, int s2, int stage) {
		this.PC = pc;
		this.instNum = instnum;
		this.opType = optype;
		this.dest = d;
		this.src1 = s1;
		this.src2 = s2;
		this.stage = stage;
		begin[stage] = cycle;
		src1rdy = false;
		src2rdy = false;
	}
	
	public Instruction ChangeStage(int destStage, int sequence){
		begin[stage] = sequence;
		this.stage = destStage;
		return this;
	}

	public Instruction ChangeStage(int destStage, int sequence, boolean s1r, boolean s2r){
		begin[stage] = sequence;
		this.stage = destStage;
		src1rdy = s1r;
		src2rdy = s2r;
		return this;
	}

}
