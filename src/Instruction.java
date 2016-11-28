
public class Instruction {

	public long PC;
	public int opType;
	public int dest;
	public int src1;
	public int src2;
	public int s1OG;
	public int s2OG;
	public boolean src1rdy;
	public boolean src2rdy;
	public boolean src1Rob;
	public boolean src2Rob;
	public int stage;
	public int instNum;
	public int begin[] = new int[9];
	public int timer;
	
	public Instruction(long pc, int instnum, int cycle, int optype, int d, int s1, int s2, int stage) {
		this.PC = pc;
		this.instNum = instnum;
		this.opType = optype;
		this.dest = d;
		this.src1 = s1OG = s1;
		this.src2 = s2OG = s2;
		this.stage = stage;
		begin[stage] = cycle;
		src1rdy = false;
		src2rdy = false;
		src1Rob = false;
		src2Rob = false;
	}
	
	public Instruction ChangeStage(int destStage, int sequence){
		this.stage = destStage;
		begin[stage] = sequence;
		return this;
	}

	public Instruction ChangeStage(int destStage, int sequence, boolean s1r, boolean s2r){
		this.stage = destStage;
		begin[stage] = sequence;
		src1rdy = s1r;
		src2rdy = s2r;
		return this;
	}
	
	public Instruction ChangeStage(int destStage, int sequence, int time){
		this.stage = destStage;
		begin[stage] = sequence;
		timer = time;
		return this;
	}

}
