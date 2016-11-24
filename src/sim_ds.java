import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class sim_ds {

	//input
	int rob_size, iq_size, theWidth, cache_size, theP;
	String tracefile;
	
	//simulator input
	Long thePC; 
	int opType, regDest, regSrc1, regSrc2;
	
//	output
//	1. Total number of instructions in the trace.
//	2. Total number of cycles to finish the program.
//	3. Average number of instructions retired per cycle (IPC).
//	4. Total number of instruction cache hits in the trace.
//	5. Total number of prefetch hits in the trace.
// 	TODO:	Note that you don't need to print out 4 and 5 by default when <CACHE_SIZE> and <P> are both 0.
	int instructionsCount = 0, cyclesCount = 0, retiredIPC = 0, cach_hits = 0, prefetch_hits = 0;
	
	int sequence = 0;
	FileReader fileReader;
	BufferedReader br;
	boolean traceDone = false;
	ReOrderBuffer robTable;
	RenameMapTab[] rmt;
	IssueQueue[] myIQ;
	
	//pipeline registers
	ArrayList<Instruction> decodeReg 	= new ArrayList<Instruction>();
	ArrayList<Instruction> renameReg 	= new ArrayList<Instruction>();
	ArrayList<Instruction> regReadReg 	= new ArrayList<Instruction>();
	ArrayList<Instruction> dispatchReg 	= new ArrayList<Instruction>();
	ArrayList<Instruction> issueReg 	= new ArrayList<Instruction>();
	ArrayList<Instruction> executeReg 	= new ArrayList<Instruction>();
	ArrayList<Instruction> writebackReg = new ArrayList<Instruction>();
	ArrayList<Instruction> retireReg 	= new ArrayList<Instruction>();
	
	
	int regCount = 67;
	
	public sim_ds(String[] args) throws IOException {
		boolean done = false;
		System.out.println("hello worlds");
		GetParameters(args);
		
		robTable = new ReOrderBuffer(rob_size);
		rmt = new RenameMapTab[regCount];
		for(int i=0; i<regCount; i++){
			rmt[i] = new RenameMapTab();
		}
		myIQ = new IssueQueue[iq_size];
		
		FileReader fileReader = new FileReader(tracefile);
		br = new BufferedReader(fileReader);
		while(true) {
            //Retire(myROB);
			Dispatch();
			RegRead();
			Rename();
            Decode();
            Fetch();
            if(done) break;
            sequence++;
		}
		
		br.close();
	}

	public static void main(String[] args) throws IOException {
		new sim_ds(args);
	}
	
	public void Retire(){
		for(int i=0; i<theWidth; i++){
			if(retireReg.size() < theWidth){
				if(robTable.rob[robTable.head].rdy == 1){
					int instNum = robTable.rob[robTable.head].instrNum;
					retireReg.remove(instNum);
					robTable.IncrementHead();
					//TODO: print instruction
					
				}
			}
		}
	}
	
	public void Fetch() throws IOException{
//		int loopsize = theWidth - decodeReg.size();
		for(int i=decodeReg.size(); i<theWidth; i++){
			if(decodeReg.size() < theWidth){
				String line = br.readLine();
				if(line != null){
					SetData(line);
					
					Instruction instr = new Instruction(thePC, instructionsCount, sequence, opType, regDest, regSrc1, regSrc2, Pipeline.FETCH);
					instructionsCount++;
					decodeReg.add(instr);
				}
				else traceDone = true;
			}
		}
	}
	
	public void Decode(){
		int loopsize = theWidth - renameReg.size();
		if(decodeReg.size() > 0){
			for(int i=0; i<loopsize; i++){
				if(renameReg.size() < theWidth){
					renameReg.add(decodeReg.get(0).ChangeStage(Pipeline.RENAME, sequence));
					decodeReg.remove(0);
				}
			}
		}
	}
	
	public void Rename(){
		int loopsize = theWidth - regReadReg.size();
		if(renameReg.size() > 0){
			for(int i=0; i<loopsize; i++){
				if(regReadReg.size() < theWidth){
					int robEntriesNeeded = CalcRenameRobSize(renameReg.get(0));
					if(robEntriesNeeded < robTable.spaceAvailable){
						//process
						RenameThisReg(false, true, false);	//rename src1
						RenameThisReg(false, false, true);	//rename src2
						RenameThisReg(true, false, false);	//rename dest
						
						//advance to RR
						regReadReg.add(renameReg.get(0).ChangeStage(Pipeline.REGREAD, sequence));
						renameReg.remove(0);
					}
				}
			}
		}
	}
	
	public void RegRead(){
		if(!regReadReg.isEmpty()){
			int loopsize = theWidth - dispatchReg.size();
			for(int i=0; i<loopsize; i++){
				if(dispatchReg.size() < theWidth){
					//process src1
					boolean src1ready = false;
					int rmtIdx = regReadReg.get(0).src1;
					int robIdx = rmt[rmtIdx].ROBtag;
					if(robTable.rob[robIdx].rdy == 1){
						src1ready = true;
					}
					//process src1
					boolean src2ready = false;
					rmtIdx = regReadReg.get(0).src2;
					robIdx = rmt[rmtIdx].ROBtag;
					if(robTable.rob[robIdx].rdy == 1){
						src2ready = true;
					}
					
					//advance
					dispatchReg.add(regReadReg.get(0).ChangeStage(Pipeline.DISPATCH, sequence, src1ready, src2ready));
					regReadReg.remove(0);
				}
			}
		}
	}
	
	public void Dispatch(){
		if(!dispatchReg.isEmpty()){
			int loopsize = iq_size - issueReg.size();
			for(int i=0; i<loopsize; i++){
				if((iq_size - issueReg.size()) >= dispatchReg.size()){
					AddToIssueQueue();
					issueReg.add(dispatchReg.get(0).ChangeStage(Pipeline.ISSUE, sequence));
					dispatchReg.remove(0);
				}
			}
		}
	}
	
	public void Issue(){
		if(!issueReg.isEmpty()){
			for(int i=0; i<theWidth; i++){
				if(executeReg.size() < theWidth*5){
					//check if instructions are ready
					int idx = CheckIQ();
					if(idx != -1){
						int latency = -1;
						if(issueReg.get(idx).opType == 0){
							latency = 1;
						}
						else if(issueReg.get(idx).opType == 1){
							latency = 2;
						}
						else if(issueReg.get(idx).opType == 2){
							latency = 5;
						}
						executeReg.add(issueReg.get(idx).ChangeStage(Pipeline.EXECUTE, sequence, latency));
						issueReg.remove(idx);
					}
				}
			}
		}
	}
	
	public void Execute(){
		for(int i=0; i<executeReg.size(); i++){
			if(executeReg.get(i).timer == 0){
				//finished
				//wakeup dependent instructions
				WakeUp(executeReg.get(i).dest);
				//advnace to wb
				writebackReg.add(executeReg.get(i).ChangeStage(Pipeline.WRITEBACK, sequence));
				//remove from executeReg
				executeReg.remove(i);
			}
			else{
				//decrement timer
				executeReg.get(i).timer--;
			}
		}
	}
	
	public void WriteBack(){
		for(int i=0; i<theWidth*5; i++){
			if(!writebackReg.isEmpty()){
				int robIdx = writebackReg.get(0).dest;
				robTable.rob[robIdx].rdy = 1;
				
				retireReg.add(writebackReg.get(0).ChangeStage(Pipeline.RETIRE, sequence));
				writebackReg.remove(0);
			}
		}
	}
	
	public void WakeUp(int robIdx){
		for(int i=0; i<iq_size; i++){
			if(myIQ[i].src1Tag == robIdx){
				myIQ[i].src1Ready = true;
			}
			if(myIQ[i].src2Tag == robIdx){
				myIQ[i].src2Ready = true;
			}
		}
		for(int i=0; i<issueReg.size(); i++){
			if(issueReg.get(i).src1 == robIdx){
				issueReg.get(i).src1rdy = true;
			}
			if(issueReg.get(i).src2 == robIdx){
				issueReg.get(i).src2rdy = true;
			}
		}
		for(int i=0; i<dispatchReg.size(); i++){
			if(dispatchReg.get(i).src1 == robIdx){
				dispatchReg.get(i).src1rdy = true;
			}
			if(dispatchReg.get(i).src2 == robIdx){
				dispatchReg.get(i).src2rdy = true;
			}
		}
		for(int i=0; i<regReadReg.size(); i++){
			if(regReadReg.get(i).src1 == robIdx){
				regReadReg.get(i).src1rdy = true;
			}
			if(regReadReg.get(i).src2 == robIdx){
				regReadReg.get(i).src2rdy = true;
			}
		}
	}
	
	public int CheckIQ(){
		int iqIdx = -1;
		int[] ready = new int[iq_size];
		int rdyIdx = 0;
		int lowest = 65535;
		
		//create array of ready instructions
		for(int i=0; i<iq_size; i++){
			if(myIQ[i].src1Ready && myIQ[i].src2Ready){
				ready[rdyIdx++] = myIQ[i].instNum;
			}
		}
		//find the oldest ready instruction
		for(int j=0; j<rdyIdx; j++){
			if(ready[j] < lowest){
				lowest = ready[j];
			}
		}
		//get the index of the lowest ready instruction from the register
		for(int i=0; i<issueReg.size(); i++){
			if(lowest == issueReg.get(i).instNum){
				iqIdx = i;
				break;
			}
		}
		
		return iqIdx;
	}
	
	public void AddToIssueQueue(){
		for(int i=0; i<iq_size; i++){
			if(myIQ[i].valid != 1){
				myIQ[i].dstTag = dispatchReg.get(0).dest;
				myIQ[i].src1Ready = dispatchReg.get(0).src1rdy;
				myIQ[i].src1Tag = dispatchReg.get(0).src1;
				myIQ[i].src2Ready = dispatchReg.get(0).src2rdy;
				myIQ[i].src2Tag = dispatchReg.get(0).src2;
				myIQ[i].instNum = dispatchReg.get(0).instNum;
				myIQ[i].valid = 1;
				break;
			}
		}
	}
	
	public int RenameThisReg(boolean dst, boolean s1, boolean s2){
		int robIndex = -1;
		int rmtIndex = -1;
		if(dst){
			rmtIndex = renameReg.get(0).dest;
			if(rmtIndex != -1){
				renameReg.get(0).dest = robTable.tail;
			}
		}
		else if(s1){
			rmtIndex = renameReg.get(0).src1;
			if(rmtIndex != -1){
				renameReg.get(0).src1 = robTable.tail;
			}
		}
		else if(s2){
			rmtIndex = renameReg.get(0).src2;
			if(rmtIndex != -1){
				renameReg.get(0).src2 = robTable.tail;
			}
		}
		if(rmtIndex != -1){
			robTable.rob[robTable.tail].instrNum = renameReg.get(0).instNum;
			robTable.rob[robTable.tail].dst = rmtIndex;
			rmt[rmtIndex].valid = 1;
			rmt[rmtIndex].ROBtag = robTable.tail;
			
			robTable.IncrementTail();
		}
		
		return robIndex;
	}
	
	public int CalcRenameRobSize(Instruction instr){
		int count = 0;
		int index = instr.dest; 
		if(index != -1){
			count++;
		}
		index = instr.src1;
		if(index != -1){
			if(rmt[index].valid == 1){
				count++;
			}
		}
		index = instr.src2;
		if(index != -1){
			if(rmt[index].valid == 1){
				count++;
			}
		}
		
		return count;
	}
	
	private void GetParameters(String[] args){
		if(args.length == 6){
			rob_size = Integer.parseInt(args[0]);
			iq_size = Integer.parseInt(args[1]);
			theWidth = Integer.parseInt(args[2]);
			cache_size = Integer.parseInt(args[3]);
			theP = Integer.parseInt(args[4]);
			tracefile = args[5];
		}
		else{
			System.out.println("incorrect number of parameters");
		}
	}
	
	private void SetData(String line){
		String[] temp = line.split(" ");
		
		thePC = Long.parseLong(temp[0], 16);
		opType = Integer.parseInt(temp[1]);
		regDest = Integer.parseInt(temp[2]);
		regSrc1 = Integer.parseInt(temp[3]);
		regSrc2 = Integer.parseInt(temp[4]);
		
		System.out.println("pc: " + thePC);
	}
}
