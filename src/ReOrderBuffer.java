
public class ReOrderBuffer {

	public AROB[] rob;
	public int head, tail;
	public int size;
	
	public ReOrderBuffer(int rob_size) {
		AROB[] temprob = new AROB[rob_size];
		for(int i=0; i<rob_size; i++){
			temprob[i] = new AROB();
		}
		rob = temprob;
		head = 0;
		tail = 0;
		size = rob_size;
	}
	
	public void AddRob(int regDest, int addr){
		if(tail < rob.length){
			rob[tail].dst = regDest;
			rob[tail].PC = addr;
			tail++;
		}
	}

	public AROB Retire(){
		AROB doneRob = null;
		if(rob[head].rdy == 1){
			doneRob = rob[head++];
		}
		return doneRob;
	}
	
	public void IncrementHead(){
		if(head < size){
			head++;
		}
		else{
			head = 0;
		}
	}
	
	public void IncrementTail(){
		if(tail < size){
			size++;
		}
		else{
			tail = 0;
		}
	}
}
