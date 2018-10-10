/**
 * Java program to export Stand Choice System Switch to .json files for the
 * repository Copyright � 2015 Jeremiah Johnson Montana State Department of
 * Labor and Industry License GNU GPLv3
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
 * @author Jeremiah Johnson
 *
 */
public class ExportStandardChoiceSystemSwitch implements Exporter {

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
		String active;
		String fileString = "";
		String lineNum;
		String code;

		try {
			String fileQuery = "select distinct d.BIZDOMAIN" + "               ,d.SERV_PROV_CODE"
					+ "	        	from RBIZDOMAIN_VALUE d" + "					join RBIZDOMAIN n"
					+ "						on d.BIZDOMAIN= n.BIZDOMAIN"
					+ "				where n.STD_CHOICE_TYPE = 'SystemSwitch'"
					+ "                     and d.SERV_PROV_CODE = '" + agency + "'"
					+ "             order by d.SERV_PROV_CODE;";

			fileStmt = con.createStatement();
			rsFile = fileStmt.executeQuery(fileQuery);

			while (rsFile.next()) {
				fileString = "";
				fileName = rsFile.getString(1);
				path = rsFile.getString(1);
				agency = rsFile.getString(2);

				String codeQuery = "select distinct d.BIZDOMAIN_VALUE"
						+ "	                       ,d.VALUE_DESC" + "	                       ,d.REC_STATUS"
						+ "	        	from RBIZDOMAIN_VALUE d" + "					join RBIZDOMAIN n"
						+ "						on d.BIZDOMAIN = n.BIZDOMAIN"
						+ "                     and d.SERV_PROV_CODE = n.SERV_PROV_CODE"
						+ "	            where n.STD_CHOICE_TYPE = 'SystemSwitch'"
						+ "					and d.BIZDOMAIN = '" + fileName + "'"
						+ "                  and d.SERV_PROV_CODE = '" + agency + "'"
						+ "				order by d.BIZDOMAIN_VALUE;";

				if (fileName != null) {
					fileName = AccelaExportUtils.fixStringForFileName(fileName, true);
				} else {
					fileName = "";
				}

				if (path != null && path.indexOf(":") > -1) {
					path = AccelaExportUtils.fixStringForDirectoryName(path);
					// NOTE: check this
				} else if (path.indexOf(" ") > -1) {
					path = AccelaExportUtils.fixStringForDirectoryName(path);
					// NOTE: check this
				} else if (path.indexOf("_") > -1) {
					path = AccelaExportUtils.fixStringForDirectoryName(path);
					// NOTE: check this
				} else {
					path = "";
				}
				branch = AccelaExportUtils.fixStringForDirectoryName(branch);
				agency = AccelaExportUtils.fixStringForDirectoryName(agency);
				reposAddition = reposAddition + agency + branch + "StandardChoices/SystemSwitch/";
				
				codeStmt = con.createStatement();
				rsCode = codeStmt.executeQuery(codeQuery);

				fileString = "{\"" + fileName + "\":\n\t{\n";

				while (rsCode.next()) {
					lineNum = rsCode.getString(1);
					active = rsCode.getString(3);
					code = rsCode.getString(2);

					if (active.equals("A")) {
						fileString = fileString + "\t\t\"" + lineNum + "\":" + "\"" + code + "\"" + "\n";
					} else {
						fileString = fileString + "\t\t\"" + lineNum + "\":" + "\"" + code + "\" //inactive"
								+ "\n";
					}
				}

				fileString = fileString + "\n\t}\n}";

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