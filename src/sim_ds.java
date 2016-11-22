import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
		
		ReOrderBuffer myROB = new ReOrderBuffer(rob_size);
		ArchRegFile[] myARF = new ArchRegFile[regCount];
		RenameMapTab[] myRMT = new RenameMapTab[regCount];
		IssueQueue[] myIQ = new IssueQueue[iq_size];
		
		FileReader fileReader = new FileReader(tracefile);
		BufferedReader br = new BufferedReader(fileReader);
		while(true) {
            //Retire(myROB);
            Decode(myROB);
            Fetch(myROB, br);
            if(done) break;
            sequence++;
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
					robTable.IncrementHead();
				}
			}
		}
	}
	
	public void Fetch(ReOrderBuffer robTable, BufferedReader br) throws IOException{
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
			}
		}
	}
	
	public void Decode(ReOrderBuffer robTable){
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
	
	public void Rename(ReOrderBuffer robTable){
		int loopsize = theWidth - regReadReg.size();
		if(renameReg.size() > 0){
			for(int i=0; i<loopsize; i++){
				if(regReadReg.size() < theWidth){
					
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
		
		thePC = Long.parseLong(temp[0], 16);
		opType = Integer.parseInt(temp[1]);
		regDest = Integer.parseInt(temp[2]);
		regSrc1 = Integer.parseInt(temp[3]);
		regSrc2 = Integer.parseInt(temp[4]);
		
		System.out.println("pc: " + thePC);
	}
}
