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
public class ExportFeeScheduleConfig implements Exporter {

	/**
	 * @param args
	 * @throws IOException
	 * @throws SQLException
	 */
	public void export(String reposlocation, Connection con, Enviroments e, String agency, String repoadd) throws IOException, SQLException {
		//TODO: valid json conversion
		String reposAddition = repoadd + "/";
		String branch = e.name();
		Statement fSchedStmt = null;
		ResultSet rsfSched = null;
		Statement fitemStmt = null;
		ResultSet rsfitem = null;
		Statement fileStmt = null;
		Statement codeStmt = null;
		ResultSet rsFile = null;
		File file = null;
		File dir = null;
		// report file variables
		String fileName = "";
		String version = "";
		String code;
		String f_schedName;
		String path;
		String fileString = "";
		String SchedStatus = "";
		String f_scheduleResID;
		String f_scheduleName;
		String f_scheduleVersion;
		String f_scheduleEffDate;
		String f_scheduleExpDate;
		String f_scheduleComment;
		String f_scheduleRecDate;
		String f_scheduleRecFullName;
		String f_scheduleStatus;
		// Fee Item Variables
		String fi_feeItem = "";
		String fi_R1_FEE_CODE;
		String fi_R1_GF_FEE_PERIOD;
		String fi_R1_GF_COD;
		String fi_R1_DISPLAY;
		String fi_R1_GF_DES;
		String fi_R1_GF_L1;
		String fi_R1_GF_L2;
		String fi_R1_GF_L3;
		String fi_R1_GF_MIN_FEE;
		String fi_R1_GF_MAX_FEE;
		String fi_R1_GF_CAL_PROC;
		String fi_R1_GF_FORMULA;
		String fi_R1_GF_UDES;
		String fi_R1_GF_DEFAULT_VALUE;
		String fi_R1_GF_REQUIRED_FLAG;
		String fi_REC_DATE;
		String fi_REC_FUL_NAM;
		String fi_REC_STATUS;
		String fi_R1_SUB_GROUP;
		String fi_R1_GF_AUTO_INVOICED_FLAG;
		String fi_R1_GF_AUTO_ASSESS_FLAG;
		String fi_COMMENTS;
		String fi_R1_GF_AUTO_ASSESS_QUANTITY;
		String fi_R1_GF_NET_FEE_FLAG;
		String fi_R1_GF_NEGATIVE_FEE_FLAG;
		String fi_R1_DISPLAY_ORDER;
		String fi_FEE_SCHEDULE_VERSION;
		String fi_GF_PRIORITY;
		String fi_ROUND_FEE_FLAG;
		String fi_ROUND_FEE_TYPE;
		String fi_R1_GF_ACA_REQUIRED_FLAG;
		String fi_R1_GF_QUANTITY_INDICATOR;
		String fi_Status;

		try {
			String fSchedQuery = "select distinct a.SERV_PROV_CODE"
					+ "                          ,a.FEE_SCHEDULE_NAME"
					+ "                          ,a.FEE_SCHEDULE_VERSION"
					+ "                          ,a.REC_STATUS" 
					+ "	            from RFEE_SCHEDULE a"
					+ "             where a.SERV_PROF_CODE = '" + agency + "'"
					+ "            order by a.SERV_PROV_CODE,a.FEE_SCHEDULE_NAME;";

			fSchedStmt = con.createStatement();
			rsfSched = fSchedStmt.executeQuery(fSchedQuery);

			while (rsfSched.next()) {
				fileString = "";
				version = rsfSched.getString(3);
				fileName = rsfSched.getString(2) + "_Version_" + version;
				f_schedName = rsfSched.getString(2);
				f_scheduleStatus = rsfSched.getString(4);
				path = rsfSched.getString(2);
				agency = rsfSched.getString(1);

				if (f_scheduleStatus.equals("A")) {
					SchedStatus = "Enabled";
				} else {
					SchedStatus = "Disabled";
				}

				String fSchedDetQuery = "select distinct b.SERV_PROV_CODE"
						+ "             ,b.FEE_SCHEDULE_NAME" + "             ,b.FEE_SCHEDULE_VERSION"
						+ "             ,b.EFF_DATE" + "             ,b.EXP_DATE"
						+ "             ,b.FEE_SCHEDULE_COMMENT" + "             ,b.REC_DATE"
						+ "             ,b.REC_FUL_NAM" + "             ,b.REC_STATUS"
						+ "             ,b.RES_ID" + "	        from RFEE_SCHEDULE b"
						+ "	       where  b.FEE_SCHEDULE_NAME = '" + f_schedName + "'"
						+ "	     and  b.FEE_SCHEDULE_VERSION = '" + version + "'"
						+ "          and  b.SERV_PROV_CODE = '" + agency + "'"
						+ "        order by b.SERV_PROV_CODE,b.FEE_SCHEDULE_NAME;";

				fileStmt = con.createStatement();
				rsFile = fileStmt.executeQuery(fSchedDetQuery);

				while (rsFile.next()) {

					f_scheduleName = rsFile.getString(2);
					f_scheduleVersion = rsFile.getString(3);
					f_scheduleEffDate = rsFile.getString(4);
					f_scheduleExpDate = rsFile.getString(5);
					f_scheduleComment = rsFile.getString(6);
					f_scheduleRecDate = rsFile.getString(7);
					f_scheduleRecFullName = rsFile.getString(8);
					f_scheduleStatus = rsFile.getString(9);
					f_scheduleResID = rsFile.getString(10);

					String fItemDetQuery = "select distinct C.SERV_PROV_CODE" + "             ,C.R1_FEE_CODE"
							+ "             ,C.R1_GF_FEE_PERIOD" + "             ,C.R1_GF_COD"
							+ "             ,C.R1_DISPLAY" + "             ,C.R1_GF_DES"
							+ "             ,C.R1_TAX_FLAG" + "             ,C.R1_GF_TAX_PERCENTAGE"
							+ "             ,C.R1_GF_L1" + "             ,C.R1_GF_L2"
							+ "             ,C.R1_GF_L3" + "             ,C.R1_GF_RANGE_LOW"
							+ "             ,C.R1_GF_RANGE_HIGH" + "             ,C.R1_GF_RANGE_INCREMENT"
							+ "             ,C.R1_GF_FAC" + "             ,C.R1_GF_FIX"
							+ "             ,C.R1_GF_MIN_FEE" + "             ,C.R1_GF_MAX_FEE"
							+ "             ,C.R1_GF_DEFAULT_FLAG" + "             ,C.R1_GF_APPEND_FLAG"
							+ "             ,C.R1_GF_PRE_PROC" + "             ,C.R1_GF_CAL_PROC"
							+ "             ,C.R1_GF_FORMULA" + "             ,C.R1_GF_UNIT"
							+ "             ,C.R1_GF_UDES" + "             ,C.R1_GF_CR_DR"
							+ "             ,C.R1_GF_DEFAULT_VALUE" + "             ,C.R1_GF_REQUIRED_FLAG"
							+ "             ,C.R1_FEE_CODE_STATUS" + "             ,C.RFEEITEM_UDF1"
							+ "             ,C.RFEEITEM_UDF2" + "             ,C.RFEEITEM_UDF3"
							+ "             ,C.RFEEITEM_UDF4" + "             ,C.REC_DATE"
							+ "             ,C.REC_FUL_NAM" + "             ,C.REC_STATUS"
							+ "             ,C.R1_SUB_GROUP" + "             ,C.R1_EFFECT_DATE"
							+ "             ,C.R1_EXPIRE_DATE" + "             ,C.R1_GF_AUTO_INVOICED_FLAG"
							+ "             ,C.FEE_NOTES" + "             ,C.R1_GF_CALCULATED_FLAG"
							+ "             ,C.R1_GF_AUTO_ASSESS_FLAG" + "             ,C.R1_GF_CALC_ID"
							+ "             ,C.COMMENTS" + "             ,C.R1_GF_AUTO_ASSESS_QUANTITY"
							+ "             ,C.R1_GF_NET_FEE_FLAG" + "             ,C.R1_GF_NEGATIVE_FEE_FLAG"
							+ "             ,C.R1_DISPLAY_ORDER" + "             ,C.FEE_SCHEDULE_VERSION"
							+ "             ,C.GF_PRIORITY" + "             ,C.ROUND_FEE_FLAG"
							+ "             ,C.ROUND_FEE_TYPE" + "             ,C.RES_ID"
							+ "             ,C.R1_GF_ACA_REQUIRED_FLAG"
							+ "             ,C.R1_GF_QUANTITY_INDICATOR" + "	        from RFEEITEM C"
							+ "	       where  C.R1_FEE_CODE = '" + f_schedName + "'"
							+ " 	     and  C.FEE_SCHEDULE_VERSION = '" + version + "'"
							+ "          and  C.SERV_PROV_CODE = '" + agency + "'"
							+ "        order by C.SERV_PROV_CODE,C.R1_FEE_CODE,C.FEE_SCHEDULE_VERSION;";

					fitemStmt = con.createStatement();
					rsfitem = fitemStmt.executeQuery(fItemDetQuery);

					while (rsfitem.next()) {

						fi_R1_FEE_CODE = rsfitem.getString(2);
						fi_R1_GF_FEE_PERIOD = rsfitem.getString(3);
						fi_R1_GF_COD = rsfitem.getString(4);
						fi_R1_DISPLAY = rsfitem.getString(5);
						fi_R1_GF_DES = rsfitem.getString(6);
						fi_R1_GF_L1 = rsfitem.getString(9);
						fi_R1_GF_L2 = rsfitem.getString(10);
						fi_R1_GF_L3 = rsfitem.getString(11);
						fi_R1_GF_MIN_FEE = rsfitem.getString(17);
						fi_R1_GF_MAX_FEE = rsfitem.getString(18);
						fi_R1_GF_CAL_PROC = rsfitem.getString(22);
						fi_R1_GF_FORMULA = rsfitem.getString(23);
						fi_R1_GF_UDES = rsfitem.getString(25);
						fi_R1_GF_DEFAULT_VALUE = rsfitem.getString(27);
						fi_R1_GF_REQUIRED_FLAG = rsfitem.getString(28);
						fi_REC_DATE = rsfitem.getString(34);
						fi_REC_FUL_NAM = rsfitem.getString(35);
						fi_REC_STATUS = rsfitem.getString(36);
						fi_R1_SUB_GROUP = rsfitem.getString(37);
						fi_R1_GF_AUTO_INVOICED_FLAG = rsfitem.getString(40);
						fi_R1_GF_AUTO_ASSESS_FLAG = rsfitem.getString(43);
						fi_COMMENTS = rsfitem.getString(45);
						fi_R1_GF_AUTO_ASSESS_QUANTITY = rsfitem.getString(46);
						fi_R1_GF_NET_FEE_FLAG = rsfitem.getString(47);
						fi_R1_GF_NEGATIVE_FEE_FLAG = rsfitem.getString(48);
						fi_R1_DISPLAY_ORDER = rsfitem.getString(49);
						fi_FEE_SCHEDULE_VERSION = rsfitem.getString(50);
						fi_GF_PRIORITY = rsfitem.getString(51);
						fi_ROUND_FEE_FLAG = rsfitem.getString(52);
						fi_ROUND_FEE_TYPE = rsfitem.getString(53);
						fi_R1_GF_ACA_REQUIRED_FLAG = rsfitem.getString(55);
						fi_R1_GF_QUANTITY_INDICATOR = rsfitem.getString(56);

						if (fi_REC_STATUS.equals("A")) {
							fi_Status = "Enabled";
						} else {
							fi_Status = "Disabled";
						}

						fi_feeItem = fi_feeItem + "          // *** FEE ITEM DETAIL *** \n"
								+ "          Fee Item Code: " + fi_R1_GF_COD + "\n"
								+ "          Fee Schedule: " + fi_R1_FEE_CODE + "\n" + "          Version: "
								+ fi_FEE_SCHEDULE_VERSION + "\n" + "          Fee Description: "
								+ fi_R1_GF_DES + "\n" + "          Comment: " + fi_COMMENTS + "\n"
								+ "          Unit: " + fi_R1_GF_UDES + "\n" + "          Calc Formula: "
								+ fi_R1_GF_CAL_PROC + "\n" + "          Calc Variable: " + fi_R1_GF_FORMULA
								+ "\n" + "          Default Value: " + fi_R1_GF_DEFAULT_VALUE + "\n"
								+ "          Fee Indicator (Qty.): " + fi_R1_GF_QUANTITY_INDICATOR + "\n"
								+ "          Round Fee Item: " + fi_ROUND_FEE_FLAG + "\n"
								+ "          Round Fee TYpe: " + fi_ROUND_FEE_TYPE + "\n"
								+ "          Required: " + fi_R1_GF_REQUIRED_FLAG + "\n"
								+ "          Auto Invoiced: " + fi_R1_GF_AUTO_INVOICED_FLAG + "\n"
								+ "          Auto Assess: " + fi_R1_GF_AUTO_ASSESS_FLAG + "\n"
								+ "          Quantity: " + fi_R1_GF_AUTO_ASSESS_QUANTITY + "\n"
								+ "          Priority: " + fi_GF_PRIORITY + "\n" + "          Minimum: "
								+ fi_R1_GF_MIN_FEE + "\n" + "          Maximum: " + fi_R1_GF_MAX_FEE + "\n"
								+ "          Seq for Calculation: " + fi_R1_DISPLAY + "\n"
								+ "          Display Order: " + fi_R1_DISPLAY_ORDER + "\n"
								+ "          Required in ACA: " + fi_R1_GF_ACA_REQUIRED_FLAG + "\n"
								+ "          Assess Adjustment on Recalculation: " + fi_R1_GF_NET_FEE_FLAG
								+ "\n" + "          Adjustment Credits Allowed: " + fi_R1_GF_NEGATIVE_FEE_FLAG
								+ "\n" + "          Status: " + fi_Status + "\n"
								+ "          Payment Period: " + fi_R1_GF_FEE_PERIOD + "\n"
								+ "          Sub Group: " + fi_R1_SUB_GROUP + "\n"
								+ "          Account Code 1: " + fi_R1_GF_L1 + "\n"
								+ "          Account Code 2: " + fi_R1_GF_L2 + "\n"
								+ "          Account Code 3: " + fi_R1_GF_L3 + "\n"
								+ "          ----other db fields not on Accela Fee Item Code ---- \n"
								+ "          Fee Item Date: " + fi_REC_DATE + "\n" + "          Created By: "
								+ fi_REC_FUL_NAM + "\n" + "          Fee Item Status: " + fi_REC_STATUS
								+ "\n\n";

					}

					if (f_scheduleStatus.equals("A")) {
						SchedStatus = "Enabled";
					} else {
						SchedStatus = "Disabled";
					}

					code = "Fee Schedule Name: " + f_scheduleName + "\n" + "Version: " + f_scheduleVersion
							+ "\n" + "Effective Date: " + f_scheduleEffDate + "\n" + "Disabled on Date: "
							+ f_scheduleExpDate + "\n" + "Comment: " + f_scheduleComment + "\n" + "Status: "
							+ SchedStatus + "\n"
							+ "----other db fields not on Accela Fee Schedule Detail Tab---- \n"
							+ "REC_DATE: " + f_scheduleRecDate + "\n" + "REC_FULL_NAME: "
							+ f_scheduleRecFullName + "\n" + "REC_STATUS: " + f_scheduleStatus + "\n"
							+ "RES_ID: " + f_scheduleResID + "\n" + "\n"
							+ "// *** Associated Fee Items Below *** \n \n" + fi_feeItem;

					if (fileName != null) {
						fileName = AccelaExportUtils.fixStringForFileName(fileName, true);
					} else {
						fileName = "";
					}

					if (path != null) {
						path = AccelaExportUtils.fixStringForFileName(path, true);
					} else {
						path = "";
					}
					branch = AccelaExportUtils.fixStringForDirectoryName(branch);
					agency = AccelaExportUtils.fixStringForDirectoryName(agency);
					reposAddition = reposAddition + agency + branch + "FeeScheduleConfig/";

					if (f_scheduleStatus.equals("A")) {
						fileString = fileString + "// *** Fee Schedule Info *** " + "\n"
								+ "// *** This Schedule is Enabled *** " + "\n" + code + "\n" + "\n";
					} else {
						fileString = fileString + "//*** Fee Schedule Info ***" + "\n"
								+ "// *** This Schedule is Disabled *** " + "\n" + code + "\n" + "\n";
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
					f_schedName = "";
					SchedStatus = "";
					version = "";
					f_scheduleName = "";
					f_scheduleVersion = "";
					f_scheduleEffDate = "";
					f_scheduleExpDate = "";
					f_scheduleComment = "";
					f_scheduleRecDate = "";
					f_scheduleRecFullName = "";
					f_scheduleStatus = "";
					f_scheduleResID = "";
					fi_feeItem = "";
					fileName = "";
					path = "";
					reposAddition = repoadd + "/";

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
			if (codeStmt != null) {
				codeStmt.close();
			}
		}
	}
}