/**
 * Java program to export Event Scripts to .js files for the repository
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
 * @author Jeremiah Johnson Updated by Shane Gilbert
 */
public class ExportEventScript implements Exporter {

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
		String fileString = "";

		try {
			String fileQuery = "select distinct es.SCRIPT_CODE ,es.SERV_PROV_CODE"
					+ "	        	from REVT_AGENCY_SCRIPT es"
					+ "             where es.SERV_PROV_CODE = '" + agency + "'"
					+ "             order by es.SERV_PROV_CODE, es.SCRIPT_CODE;";

			fileStmt = con.createStatement();
			rsFile = fileStmt.executeQuery(fileQuery);

			while (rsFile.next()) {
				fileString = "";
				fileName = rsFile.getString(1);
				agency = rsFile.getString(2);

				String codeQuery = "select es.SCRIPT_TEXT" + "				from REVT_AGENCY_SCRIPT es"
						+ "             where es.SERV_PROV_CODE = '" + agency + "'"
						+ "                 and es.SCRIPT_CODE = '" + fileName + "';";

				if (fileName != null) {
					fileName = AccelaExportUtils.fixStringForFileName(fileName, true);
				} else {
					fileName = "";
				}
				branch = AccelaExportUtils.fixStringForDirectoryName(branch);
				agency = AccelaExportUtils.fixStringForDirectoryName(agency);
				reposAddition = reposAddition + agency + branch + "Scripts/";
				
				codeStmt = con.createStatement();
				rsCode = codeStmt.executeQuery(codeQuery);

				while (rsCode.next()) {
					fileString = rsCode.getString(1);
				}

				dir = new File(reposlocation + reposAddition);
				file = new File(reposlocation + reposAddition + fileName + ".js");

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