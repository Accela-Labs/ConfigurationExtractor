package exporter;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Util.AccelaExportUtils;
import Util.Enviroments;

public class ExportScript implements Exporter {

	@Override
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
			String fileQuery = "select s.SERV_PROV_CODE ,s.SCRIPT_CODE ,s.SCRIPT_TEXT"
					+ " from REVT_AGENCY_SCRIPT s"
					+ " where s.SERV_PROV_CODE = '" + agency + "'"
					+ " substring(s.SCRIPT_CODE, 0, CHARINDEX(':', s.SCRIPT_TITLE))"
					+ " in (select r.VALUE_DESC from RBIZDOMAIN_VALUE r"
					+ " where r.BIZDOMAIN = 'EMSE_VARIABLE_BRANCH_PREFIX')"
					+ " or CHARINDEX(':', s.SCRIPT_TITLE) > 0;";

			fileStmt = con.createStatement();
			rsFile = fileStmt.executeQuery(fileQuery);

			while (rsFile.next()) {
				fileString = rsFile.getString(3);
				fileName = rsFile.getString(2);
				agency = rsFile.getString(1);

				if (fileName != null) {
					fileName = AccelaExportUtils.fixStringForFileName(fileName, false);
				} else {
					fileName = "";
				}
				agency = AccelaExportUtils.fixStringForDirectoryName(agency);
				//TODO: update this line with your emes tool
				reposlocation = reposlocation + agency + "Scripts/Event/";


				dir = new File(reposlocation);
				file = new File(reposlocation + fileName + ".js");

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
