--#1
select s.SERV_PROV_CODE ,s.SCRIPT_CODE ,s.SCRIPT_TEXT
					from REVT_AGENCY_SCRIPT s
					where s.SERV_PROV_CODE = 'agency' 
                    and substr(s.SCRIPT_CODE, 0, INSTR(':', s.SCRIPT_TITLE))
					in (select r.VALUE_DESC from RBIZDOMAIN_VALUE r
					where r.BIZDOMAIN = 'EMSE_VARIABLE_BRANCH_PREFIX')
					or INSTR(':', s.SCRIPT_TITLE) > 0;
                    
--#2
select distinct ms.MASTER_SCRIPT_NAME
					,ms.SERV_PROV_CODE
					from REVT_MASTER_SCRIPT ms
					where ms.SERV_PROV_CODE = 'TRAIN1'
					order by ms.SERV_PROV_CODE, ms.MASTER_SCRIPT_NAME;

--#3
select ms.MASTER_SCRIPT_TEXT
						from REVT_MASTER_SCRIPT ms
						where ms.SERV_PROV_CODE = 'TRAIN1'
						and ms.MASTER_SCRIPT_VERSION = (select max(s.MASTER_SCRIPT_VERSION)
						from REVT_MASTER_SCRIPT s
						where s.MASTER_SCRIPT_NAME = ms.MASTER_SCRIPT_NAME)
						and ms.MASTER_SCRIPT_NAME = 'INCLUDES_CUSTOM';

--#4
select distinct eb.EXPRESSION_NAME
					,eb.SERV_PROV_CODE
					,eb.R1_CHCKBOX_CODE 
					from REXPRESSION eb
					where eb.SERV_PROV_CODE = 'TRAIN1'
					order by eb.SERV_PROV_CODE, eb.EXPRESSION_NAME;

--#5
select eb.SCRIPT_TEXT from REXPRESSION eb
					         where eb.SERV_PROV_CODE = 'TRAIN1'
					         and eb.EXPRESSION_NAME = 'EH County';
    
--#6
select distinct es.SCRIPT_CODE ,es.SERV_PROV_CODE
					from REVT_AGENCY_SCRIPT es
					where es.SERV_PROV_CODE = 'TRAIN1'
					order by es.SERV_PROV_CODE, es.SCRIPT_CODE;
    
--#7
select es.SCRIPT_TEXT from REVT_AGENCY_SCRIPT es
						where es.SERV_PROV_CODE = 'TRAIN1'
						and es.SCRIPT_CODE = 'STANDARDFIELD';