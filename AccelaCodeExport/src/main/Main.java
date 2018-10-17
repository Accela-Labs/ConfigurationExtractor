/**
 * Copyright � 2015 Jeremiah Johnson Montana State Department of Labor and
 * Industry License GNU GPLv3
 */
package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import Util.Enviroments;
import exporter.ExportASIDDconfig;
import exporter.ExportASITDDconfig;
import exporter.ExportASITconfig;
import exporter.ExportASIconfig;
import exporter.ExportAdHocReport;
import exporter.ExportConditionConfig;
import exporter.ExportConditionConfig2;
import exporter.ExportEventScript;
import exporter.ExportExpressionBuilder;
import exporter.ExportFeeScheduleConfig;
import exporter.ExportMasterScripts;
import exporter.ExportReportConfigStructure;
import exporter.ExportScript;
import exporter.ExportSharedDropDown;
import exporter.ExportStandardChoiceEMSE;
import exporter.ExportStandardChoiceSystemSwitch;
import exporter.Exporter;

/**
 * @author Jeremiah Johnson Updated by Shane Gilbert
 */
public class Main {  //TODO: update these three lines with your local info.
	public static final String reposLocation = "C:/localFolder";
	public static final String user = "user1";
	public static final String password = "user1";
	public static final String agency = "ARLINGTONCO";
	public static final String repoadd = "";

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
//		ExportStandardChoiceEMSE scemse = new ExportStandardChoiceEMSE(); // Not used anymore
		ExportExpressionBuilder eb = new ExportExpressionBuilder();
//		ExportEventScript es = new ExportEventScript();
		ExportMasterScripts ms = new ExportMasterScripts();
		ExportReportConfigStructure rc = new ExportReportConfigStructure();
		ExportFeeScheduleConfig fs = new ExportFeeScheduleConfig();
		ExportConditionConfig cc = new ExportConditionConfig();
		ExportConditionConfig2 sc = new ExportConditionConfig2();
		// ExportDBStoredProcedures db = new ExportDBStoredProcedures(); //Doesn't work
		ExportASIconfig as = new ExportASIconfig();
		ExportASIDDconfig asdd = new ExportASIDDconfig();
		ExportASITconfig at = new ExportASITconfig();
		ExportASITDDconfig atdd = new ExportASITDDconfig();
		ExportSharedDropDown ssd = new ExportSharedDropDown();
		ExportStandardChoiceSystemSwitch ss = new ExportStandardChoiceSystemSwitch();
		ExportAdHocReport ahr = new ExportAdHocReport();
		ExportScript agencyscripts = new ExportScript();

		//NOTE: ms, eb, & agencyscripts are the main 3 for emeetool setup
		ArrayList<Exporter> exporters = new ArrayList<>();
//		exporters.add(es);
		exporters.add(ms);
		exporters.add(eb);
//		exporters.add(rc);
//		exporters.add(fs);
//		exporters.add(cc);
//		exporters.add(sc);
//		exporters.add(as);
//		exporters.add(asdd);
//		exporters.add(at);
//		exporters.add(atdd);
//		exporters.add(ssd);
//		exporters.add(ss);
// 		exporters.add(ahr);
		exporters.add(agencyscripts);

		for (Enviroments e : Enviroments.values()) {
			try {
				Properties props = new Properties();
				props.put("user","user1");
				props.put("password","user1");
				//Connection con = DriverManager.getConnection(e.toString(),props);
				Connection con = DriverManager.getConnection(e.toString(), user, password);

				for (Exporter ex : exporters) {
					ex.export(reposLocation, con, e, agency, repoadd);
				}

				if (con != null) {
					con.close();
				}
			}
			catch (SQLException sqle) {
				sqle.printStackTrace();
				System.out.println("Database connection for " + e + " failed going to next.");
				continue;
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		System.out.println("All, Done");
	}
}
