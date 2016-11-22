
public interface Pipeline {
	public static final int FETCH  		= 0;
	public static final int DECODE  	= 1;
	public static final int RENAME  	= 2;
	public static final int REGREAD  	= 3;
	public static final int DISPATCH 	= 4;
	public static final int ISSUE 	 	= 5;
	public static final int EXECUTE  	= 6;
	public static final int WRITEBACK 	= 7;
	public static final int RETIRE		= 8;
}
