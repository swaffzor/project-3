
public class ReOrderBuffer {

	public AROB[] rob;
	public int head, tail;
	public int size;
	public int spaceAvailable;
	
	public ReOrderBuffer(int rob_size) {
		AROB[] temprob = new AROB[rob_size];
		for(int i=0; i<rob_size; i++){
			temprob[i] = new AROB();
		}
		rob = temprob;
		head = 0;
		tail = 0;
		size = rob_size;
		spaceAvailable = rob_size;
	}
	
//	public void AddRob(int regDest, int addr){
//		if(tail < rob.length){
//			rob[tail].dst = regDest;
//			rob[tail].instrNum = addr;
//			tail++;
//		}
//	}

	public AROB Retire(){
		AROB doneRob = null;
		if(rob[head].rdy == 1){
			doneRob = rob[head++];
		}
		return doneRob;
	}
	
	public void IncrementHead(){
		if(head+1 < size){
			head++;
		}
		else{
			head = 0;
		}
		spaceAvailable++;
	}
	
	public void IncrementTail(){
		if(spaceAvailable > 0){
			if(tail+1 < size){
				tail++;
			}
			else{
				tail = 0;
			}
			spaceAvailable--;
		}
	}
}
