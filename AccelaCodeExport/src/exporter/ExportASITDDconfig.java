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
 * @author Shane Gilbert
 *
 */
public class ExportASITDDconfig implements Exporter {

	/**
	 * @param args
	 * @throws IOException
	 * @throws SQLException
	 */
	public void export(String reposlocation, Connection con, Enviroments e, String agency, String repoadd) throws IOException, SQLException {
		//TODO: valid json conversion
		String branch = e.name();
		Statement fileStmt = null;
		Statement file2Stmt = null;
		Statement codeStmt = null;
		ResultSet rsCode = null;
		ResultSet rsFile = null;
		ResultSet rsFile2 = null;
		File file = null;
		File dir = null;
		String fileName = "";
		String path = "";
		String path2 = "";
		String active = "";
		String fileString = "";
		String asiName = "";
		String asiType = "";

		// ASI variables
		String a_asiGroupCode;
		String a_asiSubGroup;
		String a_asidesc;
		String a_value;
		String a_status;
		String recdate;
		String recfullnam;
		String recstatus;

		String code = "";

		String reposAddition = repoadd +  "/";

		try {
			String fileQuery = "select distinct a.SERV_PROV_CODE,a.R1_CHECKBOX_CODE"
					+ "	        	from R2CHCKBOX AS a"
					+ "             where a.SERV_PROV_CODE = '" + agency + "'"
					+ "             order by a.SERV_PROV_CODE,a.R1_CHECKBOX_CODE;";

			fileStmt = con.createStatement();
			rsFile = fileStmt.executeQuery(fileQuery);

			while (rsFile.next()) {
				fileString = "";
				// fileName = rsFile.getString(1);
				path = rsFile.getString(1);
				agency = rsFile.getString(1);
				asiName = rsFile.getString(2);

				String fileQuery2 = "select distinct b.SERV_PROV_CODE"
						+ "	        	            ,b.R1_CHECKBOX_CODE"
						+ "	        	            ,b.R1_CHECKBOX_TYPE"
						+ "	        	from R2CHCKBOX AS b" + "             WHERE b.SERV_PROV_CODE = '"
						+ agency + "'" + "               AND b.R1_CHECKBOX_CODE = '" + asiName + "'"
						+ "               AND b.R1_CHECKBOX_GROUP = 'FEEATTACHEDTABLE'"
						+ "             order by b.SERV_PROV_CODE,b.R1_CHECKBOX_CODE,b.R1_CHECKBOX_TYPE;";

				file2Stmt = con.createStatement();
				rsFile2 = file2Stmt.executeQuery(fileQuery2);

				while (rsFile2.next()) {
					path2 = rsFile2.getString(2);
					asiType = rsFile2.getString(3);
					fileName = rsFile2.getString(3);

					String codeQuery = "select distinct d.SERV_PROV_CODE" + "             ,d.R1_CHECKBOX_CODE"
							+ "             ,d.R1_CHECKBOX_TYPE" + "             ,d.R1_CHECKBOX_DESC"
							+ "             ,d.R1_CHECKBOX_VALUE" + "             ,d.REC_DATE"
							+ "             ,d.REC_FUL_NAM" + "             ,d.REC_STATUS"
							+ "             ,d.R1_CHECKBOX_GROUP" + "             ,d.RES_ID"
							+ "	        from R2CHCKBOX_VALUE AS d" + "             WHERE d.SERV_PROV_CODE = '"
							+ agency + "'" + "               AND d.R1_CHECKBOX_CODE = '" + asiName + "'"
							+ "               AND d.R1_CHECKBOX_TYPE = '" + asiType + "'"
							+ "               AND d.R1_CHECKBOX_GROUP = 'FEEATTACHEDTABLE'"
							+ "          order by d.SERV_PROV_CODE,d.R1_CHECKBOX_VALUE;";

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
					if (path2 != null) {
						path2 = AccelaExportUtils.fixStringForDirectoryName(path2);
					} else {
						path2 = "";
					}
					branch = AccelaExportUtils.fixStringForDirectoryName(branch);
					agency = AccelaExportUtils.fixStringForDirectoryName(agency);
					reposAddition = reposAddition + agency + branch + "ASIT/" + path2;
					
					codeStmt = con.createStatement();
					rsCode = codeStmt.executeQuery(codeQuery);

					while (rsCode.next()) {
						active = rsCode.getString(8);
						a_asiGroupCode = rsCode.getString(2);
						a_asiSubGroup = rsCode.getString(3);
						a_asidesc = rsCode.getString(4);
						a_value = rsCode.getString(5);

						recdate = rsCode.getString(6);
						recfullnam = rsCode.getString(7);
						recstatus = rsCode.getString(8);

						if (active.equals("A")) {
							a_status = "Enabled";
						} else {
							a_status = "Disabled";
						}
						code = code + "Application Spec Info Group Code:: " + a_asiGroupCode + "\n"
								+ "Application Spec Info Subgroup: " + a_asiSubGroup + "\n" + "\n"
								+ "----ASI Static Drop Down Detail info---- \n" + "For Field: " + a_asidesc
								+ "\n" + "Value: " + a_value + "\n" + "Status: " + a_status + "\n"
								+ "---- other db fields not on ASI Static Drop Down form ---- \n" + "Date: "
								+ recdate + "\n" + "Created by: " + recfullnam + "\n" + "Rec Status: "
								+ recstatus + "\n" + "\n";
					}
					fileString = fileString + "// *** SUBVERSIONED COPY *** " + "\n\n" + code + "\n" + "\n";

					dir = new File(reposlocation + reposAddition);
					file = new File(reposlocation + reposAddition + fileName + "_DD.json");

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
					path2 = "";
					reposAddition = repoadd + "/";
				}
				path = "";
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