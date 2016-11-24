
public class IssueQueue {

	int valid;
	int dstTag;
	boolean src1Ready;
	boolean src2Ready;
	int src1Tag;
	int src2Tag;
	int instNum;
	
	public IssueQueue() {
		valid = 0;
		dstTag = 0;
		src1Ready = false;
		src2Ready = false;
		src1Tag = 0;
		src2Tag = 0;
		instNum = 0;
	}
}
