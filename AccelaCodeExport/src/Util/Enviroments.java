package Util;

public enum Enviroments {
	//TODO: update these with your enviroment info
	Sandbox("jdbc:jtds:sqlserver://DatabaseComputer:1433/SchemeaName;instance=SQLEXPRESS"),
	Development("jdbc:jtds:sqlserver://DatabaseComputer:1433/SchemeaName;instance=SQLEXPRESS"),
	Test("jdbc:jtds:sqlserver://DatabaseComputer:1433/SchemeaName;instance=SQLEXPRESS"),
	Production("jdbc:jtds:sqlserver://DatabaseComputer:1433/SchemeaName;instance=PRD3SQL2008");

	private String enviroment;

	private Enviroments(String envo){
		this.enviroment = envo;
	}

	@Override
	public String toString(){
		return enviroment;
	}
}
