import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class sim_ds {

	//input
	int rob_size, iq_size, theWidth, cache_size, theP;
	String tracefile;
	
	//simulator input
	int thePC; 
	int opType, regDest, regSrc1, regSrc2;
	
//	output
//	1. Total number of instructions in the trace.
//	2. Total number of cycles to finish the program.
//	3. Average number of instructions retired per cycle (IPC).
//	4. Total number of instruction cache hits in the trace.
//	5. Total number of prefetch hits in the trace.
// 	TODO:	Note that you don't need to print out 4 and 5 by default when <CACHE_SIZE> and <P> are both 0.
	int instructionsCount = 0, cyclesCount = 0, retiredIPC = 0, cach_hits = 0, prefetch_hits = 0;
	
	//pipeline registers
	HashMap<Integer, Instruction> fetchReg 		= new HashMap<Integer, Instruction>();
	HashMap<Integer, Instruction> decodeReg 	= new HashMap<Integer, Instruction>();
	HashMap<Integer, Instruction> renameReg 	= new HashMap<Integer, Instruction>();
	HashMap<Integer, Instruction> regReadReg 	= new HashMap<Integer, Instruction>();
	HashMap<Integer, Instruction> dispatchReg 	= new HashMap<Integer, Instruction>();
	HashMap<Integer, Instruction> issueReg 		= new HashMap<Integer, Instruction>();
	HashMap<Integer, Instruction> executeReg 	= new HashMap<Integer, Instruction>();
	HashMap<Integer, Instruction> writebackReg 	= new HashMap<Integer, Instruction>();
	HashMap<Integer, Instruction> retireReg 	= new HashMap<Integer, Instruction>();
	
	
	int regCount = 67;
	
	public sim_ds(String[] args) throws IOException {
		boolean done = false;
		System.out.println("hello worlds");
		GetParameters(args);
		
		ReOrderBuffer myROB = new ReOrderBuffer(rob_size);
		ArchRegFile[] myARF = new ArchRegFile[regCount];
		RenameMapTab[] myRMT = new RenameMapTab[regCount];
		IssueQueue[] myIQ = new IssueQueue[iq_size];
		
		FileReader fileReader = new FileReader(tracefile);
		BufferedReader br = new BufferedReader(fileReader);
		while(true) {
            Retire(myROB);
            
            Fetch(myROB, br);
            if(done) break;
		}
		
		br.close();
	}

	public static void main(String[] args) throws IOException {
		new sim_ds(args);
	}
	
	public void Retire(ReOrderBuffer robTable){
		for(int i=0; i<theWidth; i++){
			if(retireReg.size() < theWidth){
				if(robTable.rob[robTable.head].rdy == 1){
					retireReg.remove(robTable.rob[robTable.head].PC);
					robTable.head++;
				}
			}
		}
	}
	
	public void Fetch(ReOrderBuffer robTable, BufferedReader br) throws IOException{
		for(int i=decodeReg.size(); i<theWidth; i++){
			if(decodeReg.size() < theWidth){
				String line = br.readLine();
				if(line != null){
					SetData(line);
					
					Instruction instr = new Instruction(thePC, opType, regDest, regSrc1, regSrc2, Pipeline.FETCH);
					instructionsCount++;
					decodeReg.put(thePC, instr);
				}
			}
		}
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
		
		thePC = Integer.parseInt(temp[0]);
		opType = Integer.parseInt(temp[1]);
		regDest = Integer.parseInt(temp[2]);
		regSrc1 = Integer.parseInt(temp[3]);
		regSrc2 = Integer.parseInt(temp[4]);
	}
}
