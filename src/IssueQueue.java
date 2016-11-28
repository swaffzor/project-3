
public class IssueQueue {

	int valid;
	int dstTag;
	boolean src1Ready;
	boolean src2Ready;
	int src1Tag;
	int src2Tag;
	int instNum;
	
	public IssueQueue() {
		valid = -1;
		dstTag = -1;
		src1Ready = false;
		src2Ready = false;
		src1Tag = -1;
		src2Tag = -1;
		instNum = -1;
	}
}
