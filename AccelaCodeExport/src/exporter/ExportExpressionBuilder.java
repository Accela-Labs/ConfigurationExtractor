/**
 * Java program to export Expression Builder to .js files for the repository
 * Copyright � 2015 Jeremiah Johnson Montana State Department of Labor and
 * Industry License GNU GPLv3
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
 * @author Jeremiah Johnson Updated By Shane Gilbert
 */
public class ExportExpressionBuilder implements Exporter {

	/**
	 * @param args
	 * @throws IOException
	 * @throws SQLException
	 */
	public void export(String reposlocation, Connection con, Enviroments e, String agency, String repoadd) throws IOException, SQLException {
		String reposAddition = repoadd + "/";
		String branch = e.name();
		Statement fileStmt = null;
		Statement codeStmt = null;
		ResultSet rsCode = null;
		ResultSet rsFile = null;
		File file = null;
		File dir = null;
		String fileName;
		String path;
		String obj;
		String fileString = "";

		try {
			// NOTE JJ: verified. see doc/queryValidateionsforextractor.sql --#4
			String fileQuery = "select distinct eb.EXPRESSION_NAME"
					+ "                        ,eb.SERV_PROV_CODE"
					+ "                        ,eb.R1_CHCKBOX_CODE" 
					+ "	        	from REXPRESSION eb"
					+ "             where eb.SERV_PROV_CODE = '" + agency +"'"
					+ "             order by eb.SERV_PROV_CODE, eb.EXPRESSION_NAME";

			fileStmt = con.createStatement();
			rsFile = fileStmt.executeQuery(fileQuery);

			while (rsFile.next()) {
				fileString = "";
				fileName = rsFile.getString(1);
				path = rsFile.getString(1);
				agency = rsFile.getString(2);
				obj = rsFile.getString(3);

				if (obj == null || obj.equalsIgnoreCase("null")) {
					obj = "";
				} else {
					obj = obj.toUpperCase() + "/";
				}

				// NOTE JJ: verified. see doc/queryValidateionsforextractor.sql --#5
				String codeQuery = "select eb.SCRIPT_TEXT"
						+ "				from REXPRESSION eb"
						+ "             where eb.SERV_PROV_CODE = '" + agency + "'"
						+ "                 and eb.EXPRESSION_NAME = '" + fileName + "'";

				fileName = AccelaExportUtils.fixStringForFileName(fileName, true);

				if (path == null || path.equalsIgnoreCase("null")) {
					path = "";
				} else if (path.indexOf(" ") > -1) {
					path = path.substring(0, path.indexOf(" "));
					path = AccelaExportUtils.fixStringForDirectoryName(path);
					path = path.toUpperCase();
				} else if (path.indexOf("_") > -1) {
					path = path.substring(0, path.indexOf("_"));
					path = AccelaExportUtils.fixStringForDirectoryName(path);
					path = path.toUpperCase() + "/";
				} else {
					path = "";
				}
				branch = AccelaExportUtils.fixStringForDirectoryName(branch);
				agency = AccelaExportUtils.fixStringForDirectoryName(agency);
				reposAddition = reposAddition + agency + branch + "ExpressionBuilder/";
				
				codeStmt = con.createStatement();
				rsCode = codeStmt.executeQuery(codeQuery);

				while (rsCode.next()) {
					fileString = rsCode.getString(1);
				}

				dir = new File(reposlocation + reposAddition + path);
				file = new File(reposlocation + reposAddition + path + fileName + ".js");

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