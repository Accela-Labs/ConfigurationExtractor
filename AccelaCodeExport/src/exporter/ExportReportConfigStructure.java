/**
 * Java program to export report configuration information to .js files for the
 * repository Copyright � 2015 Montana State Department of Labor and Industry
 * License GNU GPLv3
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
public class ExportReportConfigStructure implements Exporter {

	private String p_repID;
	private String p_repParamID;

	/**
	 * @param args
	 * @throws IOException
	 * @throws SQLException
	 */
	public void export(String reposlocation, Connection con, Enviroments e, String agency, String repoadd) throws IOException, SQLException {
		String reposAddition = repoadd + "/";
		String branch = e.name();
		Statement catStmt = null;
		Statement datStmt = null;
		ResultSet rsCat = null;
		ResultSet rsDat = null;
		Statement fileStmt = null;
		Statement codeStmt = null;
		Statement ParamStmt = null;
		ResultSet rsParam = null;
		ResultSet rsFile = null;
		Statement PermStmt = null;
		ResultSet rsPerm = null;
		Statement PortletStmt = null;
		ResultSet rsPortlet = null;
		Statement wflowStmt = null;
		ResultSet rswflow = null;
		Statement pcriteriaStmt = null;
		ResultSet rspcriteria = null;
		File file = null;
		File dir = null;
		// report file variables
		String fileName = "";
		String CatName;
		String CatId;
		String path;
		String active;
		String fileString = "";
		String code;
		String RepName;
		String RepDesc;
		String RepId;
		String RepType;
		String RepLink;
		String RepCatID;
		String RepPortShow;
		String RepDefFormat;
		String RepEDMSMode;
		String RepEDMSObject;
		String RepEDMSLocation;
		String RepPrinterName;
		String RepPrintOnly;
		String RepPrintCopys;
		String RepResId;
		String RepAttachContact;
		String RepAuthType;
		String RepAuthName;
		String RepAuthPW;
		String RepDocGroup;
		String RepDocCat;
		String RepStatus;
		// parameter variables
		String p_params = "";
		String p_paramName;
		String p_paramType;
		String p_paramSource;
		String p_paramMask;
		String p_paramRequired;
		String p_paramVisible;
		String p_defaultValue;
		String p_paramOrder;
		String p_recDate;
		String p_recFullName;
		String p_recStatus;
		String p_paramRowNum;
		String p_resID;
		String p_viewID;
		String p_allowMultVal;
		// Permissions Variables
		String pe_perms = "";
		String pe_repID;
		String pe_recipLevel;
		String pe_recipModule;
		String pe_recipUserGroup;
		String pe_recipUserName;
		String pe_recDate;
		String pe_recFullName;
		String pe_recStatus;
		// Portlet Variables
		String po_portlets = "";
		String po_portletName;
		String po_portletID;
		String po_viewID;
		String po_reportID;
		String po_portletDesc;
		String po_portletModule;
		String po_singleWindow;
		String po_recDate;
		String po_recFullName;
		String po_recStatus;
		// Portlet Menu Criteria Variables
		String cr_criteria = "";
		String cr_prefix;
		String cr_fieldName;
		String cr_operator;
		String cr_value;
		String cr_suffix;
		String cr_booleanOptr;
		String cr_recDate;
		String cr_recFullName;
		String cr_recStatus;
		// Workflow variables
		String wf_workflow = "";
		String wf_processCode;
		String wf_proDes;
		String wf_appDes;
		String wf_recDate;
		String wf_recFullName;
		String wf_recStatus;

		try {
			String catQuery = "select distinct d.CATEGORY_NAME" 
					+ "               ,d.RPT_CATEGORY_ID"
					+ "               ,d.SERV_PROV_CODE" 
					+ "	        	from RPT_CATEGORY d"
					+ "             where a.SERV_PROF_CODE = '" + agency + "'"
					+ "             order by d.SERV_PROV_CODE;";

			catStmt = con.createStatement();
			rsCat = catStmt.executeQuery(catQuery);

			while (rsCat.next()) {
				fileString = "";
				CatName = rsCat.getString(1);
				CatId = rsCat.getString(2);
				path = rsCat.getString(1);
				agency = rsCat.getString(3);

				String fileQuery = "select distinct d.REPORT_NAME"
						+ "	                       ,d.REPORT_DESCRIPTION"
						+ "	                       ,d.REC_STATUS" + "	                       ,d.REPORT_ID"
						+ "	        	from RPT_DETAIL d" + "	            where  d.CATEGORY_ID = '" + CatId
						+ "'" + "                  and d.SERV_PROV_CODE = '" + agency + "'"
						+ "				order by d.REPORT_NAME;";

				if (path != null) {
					path = AccelaExportUtils.fixStringForDirectoryName(path);
				} else {
					path = "";
				}

				fileStmt = con.createStatement();
				rsFile = fileStmt.executeQuery(fileQuery);

				while (rsFile.next()) {
					RepId = rsFile.getString(4);

					String datQuery = "select distinct d.REPORT_NAME"
							+ "	                       ,d.REPORT_DESCRIPTION"
							+ "	                       ,d.REC_STATUS"
							+ "	                       ,d.REPORT_ID"
							+ "	                       ,d.REPORT_TYPE"
							+ "	                       ,d.REPORT_LINK"
							+ "	                       ,d.CATEGORY_ID"
							+ "	                       ,d.REPORT_SHOW"
							+ "	                       ,d.REPORT_FORMAT"
							+ "	                       ,d.EDMS_MODE"
							+ "	                       ,d.EDMS_OBJECT"
							+ "	                       ,d.EDMS_LOCATION"
							+ "	                       ,d.PRINTER_NAME"
							+ "	                       ,d.PRINT_ONLY"
							+ "	                       ,d.PRINT_COPYS" + "	                       ,d.RES_ID"
							+ "	                       ,d.ATTACH_CONTACT"
							+ "	                       ,d.AUTH_TYPE"
							+ "	                       ,d.AUTH_NAME"
							+ "	                       ,d.AUTH_PASSWORD"
							+ "	                       ,d.DOC_GROUP"
							+ "	                       ,d.DOC_CATEGORY" + "	        	from RPT_DETAIL d"
							+ "	            where  d.CATEGORY_ID = '" + CatId + "'"
							+ "                  and d.SERV_PROV_CODE = '" + agency + "'"
							+ "                  and d.REPORT_ID = '" + RepId + "'"
							+ "				order by d.REPORT_NAME;";

					datStmt = con.createStatement();
					rsDat = datStmt.executeQuery(datQuery);

					while (rsDat.next()) {

						fileName = rsDat.getString(1);
						active = rsDat.getString(3);
						RepName = rsDat.getString(1);
						RepDesc = rsDat.getString(2);
						RepId = rsDat.getString(4);
						RepType = rsDat.getString(5);
						RepLink = rsDat.getString(6);
						RepCatID = rsDat.getString(7);
						RepPortShow = rsDat.getString(8);
						RepDefFormat = rsDat.getString(9);
						RepEDMSMode = rsDat.getString(10);
						RepEDMSObject = rsDat.getString(11);
						RepEDMSLocation = rsDat.getString(12);
						RepPrinterName = rsDat.getString(13);
						RepPrintOnly = rsDat.getString(14);
						RepPrintCopys = rsDat.getString(15);
						RepResId = rsDat.getString(16);
						RepAttachContact = rsDat.getString(17);
						RepAuthType = rsDat.getString(18);
						RepAuthName = rsDat.getString(19);
						RepAuthPW = rsDat.getString(20);
						RepDocGroup = rsDat.getString(21);
						RepDocCat = rsDat.getString(22);

						String paramQuery = "select distinct e.SERV_PROV_CODE"
								+ "	                       ,e.REPORT_ID"
								+ "	                       ,e.RPT_PARAMETER_ID"
								+ "	                       ,e.PARAMETER_NAME"
								+ "	                       ,e.PARAMETER_TYPE"
								+ "	                       ,e.PARAMETER_SOURCE"
								+ "	                       ,e.PARAMETER_MASK"
								+ "	                       ,e.PARAMETER_REQUIRED"
								+ "	                       ,e.PARAMETER_VISIBLE"
								+ "	                       ,e.DEFAULT_VALUE"
								+ "	                       ,e.PARAMETER_ORDER"
								+ "	                       ,e.REC_DATE"
								+ "	                       ,e.REC_FUL_NAM"
								+ "	                       ,e.REC_STATUS"
								+ "	                       ,e.PARAMETER_ROWNUM"
								+ "	                       ,e.RES_ID" + "	                       ,e.VIEW_ID"
								+ "	                       ,e.ALLOW_MULTIPLE_VALUE"
								+ "	        	 from RPT_PARAMETER e"
								+ "	            where e.SERV_PROV_CODE = '" + agency + "'"
								+ "         and e.REPORT_ID = '" + RepId + "'"
								+ "				order by e.REPORT_ID;";

						ParamStmt = con.createStatement();
						rsParam = ParamStmt.executeQuery(paramQuery);

						while (rsParam.next()) {
							p_repID = rsParam.getString(2);
							p_repParamID = rsParam.getString(3);
							p_paramName = rsParam.getString(4);
							p_paramType = rsParam.getString(5);
							p_paramSource = rsParam.getString(6);
							p_paramMask = rsParam.getString(7);
							p_paramRequired = rsParam.getString(8);
							p_paramVisible = rsParam.getString(9);
							p_defaultValue = rsParam.getString(10);
							p_paramOrder = rsParam.getString(11);
							p_recDate = rsParam.getString(12);
							p_recFullName = rsParam.getString(13);
							p_recStatus = rsParam.getString(14);
							p_paramRowNum = rsParam.getString(15);
							p_resID = rsParam.getString(16);
							p_viewID = rsParam.getString(17);
							p_allowMultVal = rsParam.getString(18);

							p_params = p_params + "Name: " + p_paramName + "\n" + "Type: " + p_paramType
									+ "\n" + "Report Parameter Name: " + p_paramSource + "\n" + "Mask: "
									+ p_paramMask + "\n" + "Required: " + p_paramRequired + "\n" + "Visible: "
									+ p_paramVisible + "\n" + "Default Value: " + p_defaultValue + "\n"
									+ "Display Order: " + p_paramOrder + "\n"
									+ "----other db fields not on Accela Report Parameters Tab---- \n"
									+ "Parameter Date: " + p_recDate + "\n" + "Created By: " + p_recFullName
									+ "\n" + "Parameter Status: " + p_recStatus + "\n" + "Parameter RowNum: "
									+ p_paramRowNum + "\n" + "Parameter Res ID: " + p_resID + "\n"
									+ "Parameter View ID: " + p_viewID + "\n"
									+ "Parameter Allow Multiple Value: " + p_allowMultVal + "\n\n";
						}

						String permQuery = "select distinct f.SERV_PROV_CODE"
								+ "	                       ,f.REPORT_ID"
								+ "	                       ,f.RECIPIENT_LEVEL"
								+ "	                       ,f.RECIPIENT_MODULE"
								+ "	                       ,g.DISP_TEXT"
								+ "	                       ,f.RECIPIENT_USER_NAME"
								+ "	                       ,f.REC_DATE"
								+ "	                       ,f.REC_FUL_NAM"
								+ "	                       ,f.REC_STATUS"
								+ "	        	 from XRPT_RECIPIENT f"
								+ "	        	 left outer join PPROV_GROUP as g"
								+ "	        	 on g.GROUP_SEQ_NBR = f.RECIPIENT_USER_GROUP"
								+ "	            where f.SERV_PROV_CODE = '" + agency + "'"
								+ "         and f.REPORT_ID = '" + RepId + "'"
								+ "				order by f.REPORT_ID;";

						PermStmt = con.createStatement();
						rsPerm = PermStmt.executeQuery(permQuery);
						while (rsPerm.next()) {

							pe_repID = rsPerm.getString(2);
							pe_recipLevel = rsPerm.getString(3);
							pe_recipModule = rsPerm.getString(4);
							pe_recipUserGroup = rsPerm.getString(5);
							pe_recipUserName = rsPerm.getString(6);
							pe_recDate = rsPerm.getString(7);
							pe_recFullName = rsPerm.getString(8);
							pe_recStatus = rsPerm.getString(9);

							pe_perms = pe_perms + "Type: " + pe_recipLevel + "\n" + "Module Name: "
									+ pe_recipModule + "\n" + "OR Group ID: " + pe_recipUserGroup + "\n"
									+ "OR User Name: " + pe_recipUserName + "\n"
									+ "----other db fields not on Accela Report Parameters Tab---- \n"
									+ "Permission Date: " + pe_recDate + "\n" + "Created By: "
									+ pe_recFullName + "\n" + "Permission Status: " + pe_recStatus + "\n\n";
						}

						String portletQuery = "select distinct j.SERV_PROV_CODE"
								+ "	                       ,k.VIEW_NAME"
								+ "	                       ,j.RPT_PORTLET_ID"
								+ "	                       ,j.VIEW_ID"
								+ "	                       ,j.REPORT_ID"
								+ "	                       ,j.PORTLET_DESCRIPTION"
								+ "	                       ,j.PORTLET_MODULE"
								+ "	                       ,j.SINGLE_WINDOW"
								+ "	                       ,j.REC_DATE"
								+ "	                       ,j.REC_FUL_NAM"
								+ "	                       ,j.REC_STATUS"
								+ "	        	 from XRPT_PORTLET j"
								+ "	        	 left outer join GVIEW as k"
								+ "	        	 on j.VIEW_ID = k.VIEW_ID"
								+ "	            where j.SERV_PROV_CODE = '" + agency + "'"
								+ "         and j.REPORT_ID = '" + RepId + "'"
								+ "				order by j.REPORT_ID;";

						PortletStmt = con.createStatement();
						rsPortlet = PortletStmt.executeQuery(portletQuery);

						while (rsPortlet.next()) {

							po_portletName = rsPortlet.getString(2);
							po_portletID = rsPortlet.getString(3);
							po_viewID = rsPortlet.getString(4);
							po_reportID = rsPortlet.getString(5);
							po_portletDesc = rsPortlet.getString(6);
							po_portletModule = rsPortlet.getString(7);
							po_singleWindow = rsPortlet.getString(8);
							po_recDate = rsPortlet.getString(9);
							po_recFullName = rsPortlet.getString(10);
							po_recStatus = rsPortlet.getString(11);

							String pcriteriaQuery = "select distinct "
									+ "	                        v.RPT_MENU_CRITERIA_ID"
									+ "	                       ,v.REPORT_ID"
									+ "	                       ,v.PREFIX"
									+ "	                       ,v.FIELD_NAME"
									+ "	                       ,v.OPERATOR"
									+ "	                       ,v.VALUE"
									+ "	                       ,v.SUFFIX"
									+ "	                       ,v.BOOLEAN_OPTR"
									+ "	                       ,v.REC_DATE"
									+ "	                       ,v.REC_FUL_NAM"
									+ "	                       ,v.REC_STATUS"
									+ "	        	 from XRPT_MENU_CRITERIA v"
									+ "	            where v.SERV_PROV_CODE = '" + agency + "'"
									+ "         and v.REPORT_ID = '" + RepId + "'"
									+ "         and v.RPT_PORTLET_ID = '" + po_portletID + "'"
									+ "				order by v.REPORT_ID,v.RPT_MENU_CRITERIA_ID;";

							pcriteriaStmt = con.createStatement();
							rspcriteria = pcriteriaStmt.executeQuery(pcriteriaQuery);

							while (rspcriteria.next()) {

								cr_prefix = rspcriteria.getString(3);
								cr_fieldName = rspcriteria.getString(4);
								cr_operator = rspcriteria.getString(5);
								cr_value = rspcriteria.getString(6);
								cr_suffix = rspcriteria.getString(7);
								cr_booleanOptr = rspcriteria.getString(8);
								cr_recDate = rspcriteria.getString(9);
								cr_recFullName = rspcriteria.getString(10);
								cr_recStatus = rspcriteria.getString(11);

								cr_criteria = cr_criteria + "          Prefix: " + cr_prefix + "\n"
										+ "          Field Name: " + cr_fieldName + "\n"
										+ "          Operator: " + cr_operator + "\n" + "          Value: "
										+ cr_value + "\n" + "          Suffix: " + cr_suffix + "\n"
										+ "          Boolean OPTR: " + cr_booleanOptr + "\n"
										+ "          ----other db fields not on Accela Report Menu Criteria ---- \n"
										+ "          Criteria Date: " + cr_recDate + "\n"
										+ "          Created By: " + cr_recFullName + "\n"
										+ "          Criteria Status: " + cr_recStatus + "\n\n";

							}

							po_portlets = po_portlets + "Portlet Name: " + po_portletName + "\n" + "Module: "
									+ po_portletModule + "\n" + "Single Window: " + po_singleWindow + "\n"
									+ "Menu Criteria: \n" + cr_criteria + "\n" + "Description: "
									+ po_portletDesc + "\n"
									+ "----other db fields not on Accela Report Portlets Tab---- \n"
									+ "Portlet Date: " + po_recDate + "\n" + "Created By: " + po_recFullName
									+ "\n" + "Portlet Status: " + po_recStatus + "\n\n";
						}

						String workflowQuery = "select distinct l.SERV_PROV_CODE"
								+ "	                       ,l.RPT_WORKFLOW_ID"
								+ "	                       ,l.REPORT_ID"
								+ "	                       ,l.R1_PROCESS_CODE"
								+ "	                       ,l.SD_PRO_DES"
								+ "	                       ,l.SD_APP_DES"
								+ "	                       ,l.REC_DATE"
								+ "	                       ,l.REC_FUL_NAM"
								+ "	                       ,l.REC_STATUS"
								+ "	        	 from XRPT_WORKFLOW l"
								+ "	            where l.SERV_PROV_CODE = '" + agency + "'"
								+ "         and l.REPORT_ID = '" + RepId + "'"
								+ "				order by l.REPORT_ID;";

						wflowStmt = con.createStatement();
						rswflow = wflowStmt.executeQuery(workflowQuery);

						while (rswflow.next()) {

							wf_processCode = rswflow.getString(2);
							wf_proDes = rswflow.getString(2);
							wf_appDes = rswflow.getString(2);
							wf_recDate = rswflow.getString(2);
							wf_recFullName = rswflow.getString(2);
							wf_recStatus = rswflow.getString(2);

							wf_workflow = wf_workflow + "Process: " + wf_processCode + "\n" + "Task: "
									+ wf_proDes + "\n" + "Status: " + wf_appDes + "\n"
									+ "----other db fields not on Accela Report Portlets Tab---- \n"
									+ "Workflow Date: " + wf_recDate + "\n" + "Created By: " + wf_recFullName
									+ "\n" + "Workflow Status: " + wf_recStatus + "\n\n";
						}

						if (active.equals("A")) {
							RepStatus = "Active";
						} else {
							RepStatus = "Inactive";;
						}

						code = "Report ID: " + RepId + "\n" + "Report Name: " + RepName + "\n"
								+ "Report Status: " + RepStatus + "\n" + "Category ID: " + RepCatID
								+ " Category Name: " + CatName + "\n" + "Output Format: " + RepDefFormat
								+ "\n" + "Printer Name: " + RepPrinterName + "\n" + "Print Copies: "
								+ RepPrintCopys + "\n" + "Print Only Indicator: " + RepPrintOnly + "\n"
								+ "Save to EDMS: " + RepEDMSMode + "\n" + "EDMS Object: " + RepEDMSObject
								+ "\n" + "EDMS: " + RepEDMSLocation + "\n" + "Include on Report Portlet(s): "
								+ RepPortShow + "\n" + "Process Service ID#: " + RepType + "\n"
								+ "Report Link: " + RepLink + "\n" + "Report Description: " + RepDesc + "\n"
								+ "(Send in email) Attach Contact: " + RepAttachContact + "\n" + "Doc Group: "
								+ RepDocGroup + "\n" + "Doc Category: " + RepDocCat + "\n" + "Res Id: "
								+ RepResId + "\n"
								+ "----other db fields not on Accela Report Detail Tab---- \n"
								+ "Authorization Type: " + RepAuthType + "\n" + "Authorization Name: "
								+ RepAuthName + "\n" + "Authorization Pwd: " + RepAuthPW + "\n" + "\n"
								+ "// *** Paramaters Tab *** \n" + p_params + "// *** Permissions Tab *** \n"
								+ pe_perms + "// *** Portlets Tab *** \n" + po_portlets
								+ "// *** Workflow Tab *** \n" + wf_workflow;

						if (fileName != null) {
							fileName = AccelaExportUtils.fixStringForFileName(fileName, true);
						} else {
							fileName = "";
						}
						branch = AccelaExportUtils.fixStringForDirectoryName(branch);
						agency = AccelaExportUtils.fixStringForDirectoryName(agency);
						reposAddition = reposAddition + agency + branch + "ReportConfig/";
						
						if (active.equals("A")) {
							fileString = fileString + "// *** Report Detail Tab *** " + "\n" + code + "\n"
									+ "\n";
						} else {
							fileString = fileString + "//  Report Detail Tab " + "\n" + code + "\n" + "\n";
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
						p_params = "";
						pe_perms = "";
						po_portlets = "";
						wf_workflow = "";
						po_portletID = "";
						cr_criteria = "";
						fileName = "";
						path = "";
						reposAddition = repoadd + "/";

					}
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