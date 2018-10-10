/**
 * Java program to export Ad-Hoc Reports to xml, json, and html one of each for
 * every report Copyright � 2015 Jeremiah Johnson Montana State Department of
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
import java.util.ArrayList;
import java.util.Arrays;

import Util.AccelaExportUtils;
import Util.Enviroments;
import main.Main;
import sun.management.resources.agent;

/**
 * @author Jeremiah Johnson Updated by Shane Gilbert
 */
public class ExportAdHocReport implements Exporter {

	/**
	 * @param con2
	 * @param reposlocation
	 * @param e
	 * @param args
	 * @throws IOException
	 * @throws SQLException
	 */
	public void export(String reposlocation, Connection con, Enviroments e, String agency, String reopadd) throws IOException, SQLException {
		String reposAddition = reopadd + "/Reports/AdHoc/";
		Statement fileStmt = null;
		ResultSet rsFile = null;
		File fileXml = null;
		File fileHtml = null;
		File fileJson = null;
		File dir = null;
		String fileName;
		String path;
		String xml;
		String html;
		String json;

		try {
			String reportQuery = "select rr.NAME ,rr.XML ,rr.TENANTID ,rr.FORM"
					+ " ,rc.REPORT_CUSTOM_DATA from RADHOC_REPORTS rr join RADHOC_CUSTOM_DATA rc"
					+ " on rc.REPORT_NAME = rr.NAME and rc.SERV_PROV_CODE = rr.TENANTID"
					+ " where rr.TENANTID = '"+agency+"' and rc.REC_STATUS = 'A';";

			fileStmt = con.createStatement();
			rsFile = fileStmt.executeQuery(reportQuery);

			while (rsFile.next()) {
				path = reposAddition + e.name() + "/" + rsFile.getString(3) + "/" + rsFile.getString(1);
				fileName = path.substring(path.lastIndexOf('\\') + 1);
				path = AccelaExportUtils.fixStringForDirectoryName(path);
				path = reposlocation + path;
				fileName = AccelaExportUtils.fixStringForFileName(fileName, true);

				xml = rsFile.getString(2);
				html = rsFile.getString(4);
				json = rsFile.getString(5);

				dir = new File(path);
				fileXml = new File(path + fileName + ".xml");
				fileHtml = new File(path + fileName + ".html");
				fileJson = new File(path + fileName + ".json");

				if (!dir.exists()) {
					try {
						dir.mkdirs();
					}
					catch (SecurityException se) {
						se.printStackTrace();
					}
				}

				ArrayList<File> fileSet = new ArrayList<File>();
				fileSet.add(fileXml);
				fileSet.add(fileHtml);
				fileSet.add(fileJson);

				for (File f : fileSet) {
					String fileString;
					if (f.getName().substring(f.getName().indexOf(".")).equals(".xml")) {
						fileString = xml;
					} else if (f.getName().substring(f.getName().indexOf(".")).equals(".html")) {
						fileString = html;
					} else if (f.getName().substring(f.getName().indexOf(".")).equals(".json")) {
						fileString = json;
					} else {
						break;
					}

					if (f.exists()) {
						AccelaExportUtils.compareFiles(fileString, f);
					} else {
						AccelaExportUtils.writeFile(fileString, f);
					}
					fileString = "";
					path = "";
					fileName = "";
					reposAddition = reopadd + "/Reports/AdHoc/";
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {			
			if (fileStmt != null) {
				fileStmt.close();
			}
		}
	}
}
