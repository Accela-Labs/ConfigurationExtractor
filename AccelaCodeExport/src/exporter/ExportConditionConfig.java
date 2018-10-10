/**
 * Java program to export fee schedule/item configuration information to .js
 * files for the repository Copyright � 2015 Montana State Department of Labor
 * and Industry License GNU GPLv3
 */
package exporter;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

import Util.AccelaExportUtils;
import Util.Enviroments;
import main.Main;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Shane Gilbert Updated by Shane Gilbert
 */
public class ExportConditionConfig implements Exporter {

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
		String conditionDesc;
		String conditionStatus;
		String recdate;
		String recfullname;
		String recstatus;

		try {
			String fileQuery = "select distinct d.BIZDOMAIN_VALUE" + "               ,d.SERV_PROV_CODE"
					+ "	        	from RBIZDOMAIN_VALUE d"
					+ "				where d.BIZDOMAIN = 'CONDITION TYPE'"
					+ "                     and d.SERV_PROV_CODE = '" + agency + "'"
					+ "             order by d.SERV_PROV_CODE;";

			fileStmt = con.createStatement();
			rsFile = fileStmt.executeQuery(fileQuery);

			while (rsFile.next()) {
				fileString = "";
				fileName = rsFile.getString(1);
				path = rsFile.getString(1);
				agency = rsFile.getString(2);

				String codeQuery = "select distinct e.BIZDOMAIN_VALUE"
						+ "	                       ,e.VALUE_DESC" + "	                       ,e.REC_DATE"
						+ "	                       ,e.REC_FUL_NAM" + "	                       ,e.REC_STATUS"
						+ "	        	from RBIZDOMAIN_VALUE e"
						+ "	            where e.BIZDOMAIN = 'CONDITION TYPE'"
						+ "					and e.BIZDOMAIN_VALUE = '" + fileName + "'"
						+ "                  and e.SERV_PROV_CODE = '" + agency + "'"
						+ "				order by e.BIZDOMAIN_VALUE;";

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
					active = rsCode.getString(5);
					conditionType = rsCode.getString(1);
					conditionDesc = rsCode.getString(2);
					conditionStatus = rsCode.getString(5);
					recdate = rsCode.getString(3);
					recfullname = rsCode.getString(4);
					recstatus = rsCode.getString(5);

					if (active.equals("A")) {
						conditionStatus = "Enabled";
					} else {
						conditionStatus = "Disabled";
					}

					code = "Condition Type: " + conditionType + "\n" + "Description: " + conditionDesc + "\n"
							+ "Status: " + conditionStatus + "\n"
							+ "---- other db fields not on Accela Condition Type form ---- \n"
							+ "Condition Type Date: " + recdate + "\n" + "Created by: " + recfullname + "\n"
							+ "Rec Status: " + recstatus + "\n" + "\n";

					if (fileName != null) {
						fileName = AccelaExportUtils.fixStringForFileName(fileName, true);
					} else {
						fileName = "";
					}
				}
				branch = AccelaExportUtils.fixStringForDirectoryName(branch);
				agency = AccelaExportUtils.fixStringForDirectoryName(agency);
				reposAddition = reposAddition + agency + branch + "ConditionConfig/Condition_Types/";

				if (active.equals("A")) {
					fileString = fileString + "// *** Condition Type Configuration *** " + "\n" + code + "\n"
							+ "\n";
				} else {
					fileString = fileString + "// *** Condition Type Configuration ***" + "\n" + code + "\n"
							+ "\n";
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