/**
 * Java program to export fee schedule/item configuration information to .js
 * files for the repository Copyright � 2015 Montana State Department of Labor
 * and Industry License GNU GPLv3
 */
package exporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Util.AccelaExportUtils;
import Util.Enviroments;

/**
 * @author Shane Gilbert
 * @note doesn't work???!!!
 */
public class ExportDBStoredProcedures implements Exporter {

	/**
	 * @param args
	 * @throws IOException
	 * @throws SQLException
	 */
	public void export(String reposlocation, Connection con, Enviroments e, String agency, String repoadd) throws IOException, SQLException {
		String reposAddition = repoadd + "Interfaces/StoredProcedures/" + e.name() + "/";
		Statement fileStmt = null;
		Statement codeStmt = null;
		ResultSet rsCode = null;
		ResultSet rsFile = null;
		File file = null;
		File dir = null;
		String fileName = "";
		String path = "";
		String fileString = "";
		String v_obj_id = "";

		String code = "";

		// NOTE: THIS PROCEDURE DOES NOT WORK DUE TO 1. DB PERMISSIONS
		// 2. Definition may be encrypted
		try {

			String fileQuery = "select distinct o.name, o.object_id"
					+ "	        	from sys.sql_modules AS m"
					+ "	        	     INNER JOIN sys.objects AS o"
					+ "	        	             ON m.[object_id] = o.[OBJECT_ID]"
					+ "	        	     INNER JOIN sys.schemas AS s"
					+ "	        	              ON s.[schema_id] = o.[schema_id]"
					+ "             WHERE o.type_desc ='SQL_STORED_PROCEDURE'"
					+ "             order by o.name, o.object_id;";

			fileStmt = con.createStatement();
			rsFile = fileStmt.executeQuery(fileQuery);

			while (rsFile.next()) {
				fileString = "";
				fileName = rsFile.getString(1);
				path = rsFile.getString(1);
				v_obj_id = rsFile.getString(2);

				String codeQuery = "select m.definition" + "	        	from sys.sql_modules AS m"
						+ "             WHERE m.object_id = '" + v_obj_id + "'"
						+ "             order by m.definition;";

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
				agency = AccelaExportUtils.fixStringForDirectoryName(agency);
				reposAddition = reposAddition + agency;

				codeStmt = con.createStatement();
				rsCode = codeStmt.executeQuery(codeQuery);

				while (rsCode.next()) {
					code = rsCode.getString(1);

					if (fileName != null) {
						fileName = AccelaExportUtils.fixStringForFileName(fileName, true);
					} else {
						fileName = "";
					}
				}
				// System.out.println(fileString);
				fileString = fileString + "// *** SUBVERSIONED COPY *** " + "\n\n " + code + "\n" + "\n";

				dir = new File(reposlocation + reposAddition + path);
				file = new File(reposlocation + reposAddition + path + fileName + ".sql");

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
				reposAddition = repoadd + "Interfaces/StoredProcedures/" + e.name() + "/";
				 
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
