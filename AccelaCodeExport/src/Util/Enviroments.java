package Util;

public enum Enviroments {
	//TODO: update these with your enviroment info
//	Sandbox("jdbc:jtds:sqlserver://DatabaseComputer:1433/SchemeaName;instance=SQLEXPRESS"),
//	Development("jdbc:jtds:sqlserver://DatabaseComputer:1433/SchemeaName;instance=SQLEXPRESS"),
//	Test("jdbc:jtds:sqlserver://DatabaseComputer:1433/SchemeaName;instance=SQLEXPRESS"),
//	Production("jdbc:jtds:sqlserver://DatabaseComputer:1433/SchemeaName;instance=PRD3SQL2008");

	// Service Name
	// Sandbox("jdbc:oracle:thin:@//<ip>:<port>/<serviceName>");
	
	// SID
	// Sandbox("jdbc:oracle:thin:@//<ip>:<port>:<sid>");
	
	// Full Description (TNS) entry
	Sandbox("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=<ipNum>)(PORT=<port>)))(CONNECT_DATA=(SERVICE_NAME=<serviceName>)))");
	
//	,
//	Development("jdbc:jtds:sqlserver://DatabaseComputer:1433/SchemeaName;instance=SQLEXPRESS"),
//	Test("jdbc:jtds:sqlserver://DatabaseComputer:1433/SchemeaName;instance=SQLEXPRESS"),
//	Production("jdbc:jtds:sqlserver://DatabaseComputer:1433/SchemeaName;instance=PRD3SQL2008");
//	
	private String enviroment;

	private Enviroments(String envo){
		this.enviroment = envo;
	}

	@Override
	public String toString(){
		return enviroment;
	}
}
