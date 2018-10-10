/**
 * Java program to export fee schedule/item configuration information to .js
 * files for the repository Copyright � 2015 Montana State Department of Labor
 * and Industry License GNU GPLv3
 */
package exporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import Util.AccelaExportUtils;
import Util.Enviroments;
import main.Main;

/**
 * @author Shane Gilbert Updated by Shane Gilbert
 */
public class ExportConditionConfig2 implements Exporter {

	/**
	 * @param args
	 * @throws IOException
	 * @throws SQLException
	 */
	public void export(String reposlocation, Connection con, Enviroments e, String agency, String repoadd) throws IOException, SQLException {
		//TODO: valid json conversion
		String reposAddition = repoadd + "/";
		String branch = e.name();
		Statement fileStmt = null;
		Statement codeStmt = null;
		ResultSet rsCode = null;
		ResultSet rsFile = null;
		File file = null;
		File dir = null;
		String fileName = "";
		String path = "";
		String active = "";
		String fileString = "";
		String code = "";
		String conditionType;
		String conditionName;
		String conditionSeverity;
		String conditionComment;
		String conditionStatus;
		String recdate;
		String recfullname;
		String recstatus;

		try {
			String fileQuery = "select distinct d.R3_CON_DES" + "               ,d.SERV_PROV_CODE"
					+ "	        	from R3CLEART d"
					+ "             where d.SERV_PROV_CODE = '" + agency + "'"
					+ "             order by d.SERV_PROV_CODE,d.R3_CON_DES;";

			fileStmt = con.createStatement();
			rsFile = fileStmt.executeQuery(fileQuery);

			while (rsFile.next()) {
				fileString = "";
				fileName = rsFile.getString(1);
				path = rsFile.getString(1);
				agency = rsFile.getString(2);

				String codeQuery = "select distinct b.SERV_PROV_CODE" + "             ,b.R3_CON_TYPE"
						+ "             ,b.R3_CON_DES" + "             ,b.R3_CON_IMPACT_CODE"
						+ "             ,b.R3_CON_COMMENT" + "             ,b.REC_DATE"
						+ "             ,b.REC_FUL_NAM" + "             ,b.REC_STATUS"
						+ "	        from R3CLEART b" + "	       where b.R3_CON_DES = '" + fileName + "'"
						+ "          and b.SERV_PROV_CODE = '" + agency + "'"
						+ "  order by b.SERV_PROV_CODE,b.R3_CON_DES;";

				if (fileName != null) {
					fileName = AccelaExportUtils.fixStringForFileName(fileName, true);
				} else {
					fileName = "";
				}

				if (path != null) {
					path = AccelaExportUtils.fixStringForDirectoryName(path);
				} else {
					path = "";
				}
				
				codeStmt = con.createStatement();
				rsCode = codeStmt.executeQuery(codeQuery);
				fileString = "";

				while (rsCode.next()) {
					active = rsCode.getString(8);
					conditionType = rsCode.getString(2);
					conditionName = rsCode.getString(3);
					conditionSeverity = rsCode.getString(4);
					conditionComment = rsCode.getString(5);

					recdate = rsCode.getString(6);
					recfullname = rsCode.getString(7);
					recstatus = rsCode.getString(8);

					if (active.equals("A")) {
						conditionStatus = "Enabled";
					} else {
						conditionStatus = "Disabled";
					}

					code = "Condition Type: " + conditionType + "\n" + "Condition Name: " + conditionName
							+ "\n" + "Severity: " + conditionSeverity + "\n" + "Default Comment: "
							+ conditionComment + "\n" + "Status: " + conditionStatus + "\n"
							+ "---- other db fields not on Accela Standard Condition form ---- \n"
							+ "Standard Condition Date: " + recdate + "\n" + "Created by: " + recfullname
							+ "\n" + "Rec Status: " + recstatus + "\n" + "\n";

					if (fileName != null) {
						fileName = AccelaExportUtils.fixStringForFileName(fileName, true);
					} else {
						fileName = "";
					}
					branch = AccelaExportUtils.fixStringForDirectoryName(branch);
					agency = AccelaExportUtils.fixStringForDirectoryName(agency);
					reposAddition = reposAddition + agency + branch + "ConditionConfig/Standard_Conditions/";
				}

				if (active.equals("A")) {
					fileString = fileString + "// *** Standard Condition Configuration *** " + "\n" + code
							+ "\n" + "\n";
				} else {
					fileString = fileString + "// *** Standard Condition Configuration ***" + "\n" + code
							+ "\n" + "\n";
				}

				dir = new File(reposlocation + reposAddition + path);
				file = new File(reposlocation + reposAddition + path + fileName + ".json");

				if (!dir.exists()) {
					try {
						dir.mkdirs();
					}
					catch (SecurityException se) {
						se.printStackTrace();
					}
				}

				if (file.exists()) {
					AccelaExportUtils.compareFiles(fileString, file);
				} else {
					AccelaExportUtils.writeFile(fileString, file);
				}

				fileString = "";
				code = "";
				fileName = "";
				path = "";
				reposAddition = repoadd + "/";

			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {			
			if (fileStmt != null) {
				fileStmt.close();
			}
			if (codeStmt != null) {
				codeStmt.close();
			}
		}
	}
}