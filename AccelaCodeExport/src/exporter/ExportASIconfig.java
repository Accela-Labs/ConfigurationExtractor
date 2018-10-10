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
public class ExportASIconfig implements Exporter {

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
		String a_locQuery;
		String a_fieldLabel;
		String a_fieldType;
		String a_displayOrder;
		String a_defaultValue;
		String a_unit;
		String a_feeIndicator;
		String a_requiredFlag;
		String a_reqForFeeCalc;
		String a_supEditOnly;
		String a_searchableFlag;
		String a_maxLen;
		String a_dispLen;
		String a_justification;
		String a_acaDisp;
		String a_acaSearch;
		String a_status;
		String a_altACAlabel;
		String recdate;
		String recfullnam;
		String recstatus;

		String code = "";

		String reposAddition = repoadd + "/";

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
						+ "               AND b.R1_CHECKBOX_GROUP <> 'FEEATTACHEDTABLE'"
						+ "             order by b.SERV_PROV_CODE,b.R1_CHECKBOX_CODE,b.R1_CHECKBOX_TYPE;";

				file2Stmt = con.createStatement();
				rsFile2 = file2Stmt.executeQuery(fileQuery2);

				while (rsFile2.next()) {
					path2 = rsFile2.getString(2);
					asiType = rsFile2.getString(3);
					fileName = rsFile2.getString(3);

					String codeQuery = "select distinct c.SERV_PROV_CODE" + "             ,c.R1_CHECKBOX_CODE"
							+ "             ,c.R1_CHECKBOX_TYPE" + "             ,c.R1_CHECKBOX_DESC"
							+ "             ,CASE WHEN c.R1_CHECKBOX_IND = '1' THEN 'TEXT'"
							+ "                   WHEN c.R1_CHECKBOX_IND = '2' THEN 'DATE'"
							+ "                   WHEN c.R1_CHECKBOX_IND = '3' THEN 'Y/N'"
							+ "                   WHEN c.R1_CHECKBOX_IND = '4' THEN 'NUMBER'"
							+ "                   WHEN c.R1_CHECKBOX_IND = '5' THEN 'DROP DOWN LIST'"
							+ "                   WHEN c.R1_CHECKBOX_IND = '6' THEN 'TEXT AREA'"
							+ "                   WHEN c.R1_CHECKBOX_IND = '7' THEN 'TIME'"
							+ "                   WHEN c.R1_CHECKBOX_IND = '8' THEN 'MONEY'"
							+ "                   WHEN c.R1_CHECKBOX_IND = '9' THEN 'CHECK BOX' ELSE c.R1_CHECKBOX_IND END"
							+ "             ,c.REC_DATE" + "             ,c.REC_FUL_NAM"
							+ "             ,c.REC_STATUS" + "             ,c.R1_DISPLAY_ORDER"
							+ "             ,c.R1_FEE_INDICATOR" + "             ,c.R1_ATTRIBUTE_VALUE"
							+ "             ,c.R1_ATTRIBUTE_UNIT_TYPE"
							+ "             ,c.R1_ATTRIBUTE_VALUE_REQ_FLAG"
							+ "             ,c.R1_VALIDATION_SCRIPT_NAME"
							+ "             ,c.R1_SEARCHABLE_FLAG" + "             ,c.R1_CHECKBOX_GROUP"
							+ "             ,c.R1_TASK_STATUS_REQ_FLAG" + "             ,c.MAX_LENGTH"
							+ "             ,c.DISPLAY_LENGTH" + "             ,c.R1_DEFAULT_SELECTED"
							+ "             ,c.R1_GROUP_DISPLAY_ORDER" + "             ,c.VCH_DISP_FLAG"
							+ "             ,c.R1_TABLE_GROUP_NAME" + "             ,c.LOCATION_QUERY_FLAG"
							+ "             ,c.DEFAULT_APO_GIS_LAYER" + "             ,c.R1_REQ_FEE_CALC"
							+ "             ,c.R1_SUPERVISOR_EDIT_ONLY_FLAG"
							+ "             ,c.R1_SHARED_DDLIST_ID" + "             ,c.R1_ALIGNMENT"
							+ "             ,c.R1_DISABLE_SORT_TABLE"
							+ "             ,c.R1_SEARCHABLE_FOR_ACA" + "             ,c.R1_CHECKBOX_DESC_ALT"
							+ "             ,c.R1_DISPLAY_LIC_VERIF_ACA" + "	        from R2CHCKBOX AS c"
							+ "             WHERE c.SERV_PROV_CODE = '" + agency + "'"
							+ "               AND c.R1_CHECKBOX_CODE = '" + asiName + "'"
							+ "               AND c.R1_CHECKBOX_TYPE = '" + asiType + "'"
							+ "               AND c.R1_CHECKBOX_GROUP <> 'FEEATTACHEDTABLE'"
							+ "          order by c.SERV_PROV_CODE,c.R1_DISPLAY_ORDER;";

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
					reposAddition = reposAddition + agency + branch + "ASI/" + path2;;
					
					codeStmt = con.createStatement();
					rsCode = codeStmt.executeQuery(codeQuery);

					while (rsCode.next()) {
						active = rsCode.getString(8);
						a_asiGroupCode = rsCode.getString(2);
						a_asiSubGroup = rsCode.getString(3);
						a_locQuery = rsCode.getString(24);
						a_fieldLabel = rsCode.getString(4);
						a_fieldType = rsCode.getString(5);
						a_displayOrder = rsCode.getString(9);
						a_defaultValue = rsCode.getString(11);
						a_unit = rsCode.getString(12);
						a_feeIndicator = rsCode.getString(10);
						a_requiredFlag = rsCode.getString(13);
						a_reqForFeeCalc = rsCode.getString(26);
						a_supEditOnly = rsCode.getString(27);
						a_searchableFlag = rsCode.getString(15);
						a_maxLen = rsCode.getString(18);
						a_dispLen = rsCode.getString(19);
						a_justification = "no column found for this";
						a_acaDisp = rsCode.getString(22);
						a_acaSearch = rsCode.getString(31);
						a_altACAlabel = rsCode.getString(32);

						recdate = rsCode.getString(6);
						recfullnam = rsCode.getString(7);
						recstatus = rsCode.getString(8);

						if (active.equals("A")) {
							a_status = "Enabled";
						} else {
							a_status = "Disabled";
						}

						code = code + "Application Spec Info Group Code:: " + a_asiGroupCode + "\n"
								+ "Application Spec Info Subgroup: " + a_asiSubGroup + "\n"
								+ "Location Query: " + a_locQuery + "\n" + "----ASI Detail info---- \n"
								+ "Field Label: " + a_fieldLabel + "\n" + "Type: " + a_fieldType + "\n"
								+ "Display Order: " + a_displayOrder + "\n" + "Fee Indicator: "
								+ a_feeIndicator + "\n" + "Unit: " + a_unit + "\n" + "Default Value: "
								+ a_defaultValue + "\n" + "Required: " + a_requiredFlag + "\n"
								+ "Req for Fee Calc: " + a_reqForFeeCalc + "\n" + "Supervisor Edit Only: "
								+ a_supEditOnly + "\n" + "Searchable: " + a_searchableFlag + "\n"
								+ "Max Len: " + a_maxLen + "\n" + "Display Len: " + a_dispLen + "\n"
								+ "ACA Displayable: " + a_acaDisp + "\n" + "ACA Searchable: " + a_acaSearch
								+ "\n" + "Justification: " + a_justification + "\n" + "Status: " + a_status
								+ "\n" + "ACA alt text: " + a_altACAlabel + "\n"
								+ "---- other db fields not on Accela ASI form ---- \n" + "Date: " + recdate
								+ "\n" + "Created by: " + recfullnam + "\n" + "Rec Status: " + recstatus
								+ "\n" + "\n";
					}

					fileString = fileString + "// *** SUBVERSIONED COPY *** " + "\n\n" + code + "\n" + "\n";

					dir = new File(reposlocation + reposAddition);
					file = new File(reposlocation + reposAddition + fileName + ".json");

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