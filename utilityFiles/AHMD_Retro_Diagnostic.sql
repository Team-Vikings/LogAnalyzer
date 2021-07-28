/*
 Version     : 1.0
 Author      : Hiteshree Neve
 Email       : srikrish_org_ww@oracle.com
 Description : This is a modified version of Retro Script to get all the data related to retro in a go
 Usage       : Replace  with actual person number.
*/
PROMPT &&PersonNumber
alter session set nls_date_format='YYYY-MM-DD';
ALTER SESSION SET NLS_LANGUAGE    = 'AMERICAN';
set numformat 9999999999999999999;
set verify on
set markup html on

set pages 2000
spool on
spool retro_diagnostics.html

PROMPT "PERIOD OF SERVICE"

SELECT
    period_of_service_id,
    termination_accepted_person_id,
    person_id,
    date_start,
    accepted_termination_date,
    actual_termination_date,
    notified_termination_date,
    projected_termination_date,
    creation_date,
    created_by,
    last_update_date,
    last_updated_by,
    last_update_login,
    object_version_number,
    adjusted_svc_date,
    original_date_of_hire
FROM
    fusion.per_periods_of_service
WHERE
    person_id IN (
        SELECT
            person_id
        FROM
            fusion.per_all_people_f
        WHERE
            person_number = '&&PersonNumber'
    );


/*
SELECT
    lookup.meaning,
    ppa.payroll_action_id,
    ppa.action_status,
    pra.payroll_rel_action_id,
    pra.payroll_relationship_id,
    prr.run_result_id,
    prrv.Result_value,
    prr.ELEMENT_TYPE_ID as RR_Element_Type_id,
	pet.base_element_name as RR_Element_Name,
	prr.source_type as RR_Source_Type,
	prr.source_id as RR_Source_Id,
	prr.Element_entry_id, 
	prr.Start_Date,
	prr.End_Date,
    pra.retro_component_id,
    ppa.effective_date,
    ppa.date_earned,
    ppa.creation_date,
    ppa.created_by,
    ppa.last_update_date,
    ppa.last_update_login,
    ppa.last_updated_by
FROM
    fusion.pay_payroll_actions ppa,
    fusion.pay_payroll_rel_actions pra,
    fusion.pay_pay_relationships_dn ppr,
    fusion.per_all_people_f per,
    hcm_lookups lookup,
    pay_run_Results prr,
    pay_run_result_values prrv,
	pay_element_types_f pet
WHERE
        ppa.payroll_action_id = pra.payroll_action_id
    AND
        pra.payroll_relationship_id = ppr.payroll_relationship_id
    AND
        ppr.person_id = per.person_id
    AND
        per.person_number = '&&PersonNumber'
    AND
        lookup.lookup_code = ppa.action_type
    AND
        lookup_type = 'ACTION_TYPE'
    AND
        ppa.action_type <> 'G'
    AND 
        prr.payroll_rel_Action_id = pra.payroll_rel_action_id
    AND 
        prr.run_result_id = prrv.run_result_id
	AND
		prr.element_type_id = pet.element_type_id
	order by creation_Date, payroll_Action_id desc;
*/

PROMPT "ALL PAYROLL ACTIONS SEQUENCE"		
--- ALL PAYROLL ACTIONS SEQUENCE
SELECT distinct
    ppa.payroll_action_id,
    ppa.action_status,
    ppa.action_type,
    ppa.pay_request_id,
    ppa.start_date,
    ppa.end_date,
    ppa.effective_date,
    ppa.date_earned,
    ppa.creation_date,
    ppa.created_by,
    ppa.last_update_date,
    ppa.last_updated_by
FROM
    fusion.pay_payroll_actions ppa,
    fusion.pay_payroll_rel_actions pra,
    fusion.pay_pay_relationships_dn ppr,
    fusion.per_all_people_f per
WHERE
        ppa.payroll_action_id = pra.payroll_action_id
    AND
        pra.payroll_relationship_id = ppr.payroll_relationship_id
    AND
        ppr.person_id = per.person_id
    AND
        per.person_number = '&&PersonNumber'
    AND
        ppa.action_type in('Q','R','L','U','P')
ORDER BY ppa.creation_date DESC;		

PROMPT "LAST RETRO REL ACTIONS"
-- LAST RETRO REL ACTIONS

SELECT
    ppa.payroll_action_id,
    ppa.action_status,
    ppa.action_type,
    pra.payroll_Rel_action_id as "REL ACTION ID",
    pra.action_status as "REL ACTION STATUS",
    ppa.pay_request_id,
    ppa.start_date,
    ppa.end_date,
    ppa.effective_date,
    ppa.date_earned,
    ppa.creation_date,
    ppa.created_by,
    ppa.last_update_date,
    ppa.last_updated_by
FROM
    fusion.pay_payroll_actions ppa,
    fusion.pay_payroll_rel_actions pra,
    fusion.pay_pay_relationships_dn ppr,
    fusion.per_all_people_f per,
	fusion.pay_requests pr,
	fusion.pay_flow_instances pfi
WHERE
        ppa.payroll_action_id = pra.payroll_action_id
    AND
        pra.payroll_relationship_id = ppr.payroll_relationship_id
    AND
        ppr.person_id = per.person_id
    AND
        per.person_number = '&&PersonNumber'
    AND
        ppa.action_type =  'L' 
	AND 
		pr.flow_instance_id = pfi.flow_instance_id
	AND
		pr.pay_request_id  = ppa.pay_request_id
	AND 
		pfi.instance_name = '&&Flowname'
ORDER BY ppa.creation_date ;		

PROMPT "Retro flow Parameters"
-- Get flow parameters
SELECT  fti.flow_task_instance_id
      ,fti.base_task_action_id
      ,tp.base_task_action_id
      ,ta.base_task_action_name
      ,ft.base_flow_task_name
      ,tp.base_task_parameter_name
      ,ftp.base_parameter_name
      ,ftpv.flow_task_param_value
FROM    fusion.pay_flow_instances fi
      ,fusion.pay_flow_task_instances fti
      ,fusion.pay_flow_tasks ft
      ,fusion.pay_task_parameters tp
      ,fusion.pay_flow_task_parameters ftp
      ,fusion.pay_flow_task_param_vals ftpv
      ,fusion.pay_task_actions ta
WHERE   fi.instance_name = '&&Flowname'
AND     fi.flow_instance_id = fti.flow_instance_id
AND     ftpv.flow_task_instance_id = fti.flow_task_instance_id
AND     ft.base_flow_task_id = fti.base_flow_task_id
AND     ftp.base_flow_task_id = ft.base_flow_task_id
AND     tp.base_task_parameter_id = ftp.base_task_parameter_id
AND     tp.base_task_action_id = ta.base_task_action_id
AND     ft.base_flow_id = fi.base_flow_id
AND     ft.base_task_id = ft.base_task_id
AND     (
               ft.base_flow_task_name NOT LIKE 'START%%'
       OR      ft.base_flow_task_name NOT LIKE 'END%%'
       )
AND     ftp.base_flow_task_param_id = ftpv.base_flow_task_param_id
AND     ta.base_task_action_id = ftpv.action
AND     ta.base_task_id = ft.base_task_id
ORDER BY tp.BASE_TASK_ACTION_ID;



PROMPT "All Retro NOtifications"
--- All Retro NOtification

Select * from fusion.pay_retro_relationships where payroll_relationship_id in(Select payroll_relationship_id from fusion.pay_pay_relationships_dn where person_id in (Select person_id from fusion.per_all_people_F where person_number = '&&PersonNumber')) order by last_update_date desc;

PROMPT "All Retro Entries"
--- All Retro Entries 
		
SELECT
    prr.retro_relationship_id,
    prr.approval_status,
    prr.reprocess_date,
    pre.element_entry_id,
    pre.retro_component_id,
    pre.effective_date,
    pet.base_element_name,
    prr.retro_rel_action_id,
    pre.creation_date,
    pre.created_by,
    pre.last_update_date,
    pre.last_updated_by
FROM
    fusion.pay_retro_relationships prr,
    fusion.pay_retro_entries pre,
    fusion.pay_pay_relationships_dn rel,
    fusion.per_all_people_f per,
    fusion.pay_element_types_f pet,
	fusion.pay_requests pr,
	fusion.pay_flow_instances pfi,
	fusion.pay_payroll_actions ppa,
    fusion.pay_payroll_rel_actions pra
WHERE
        prr.retro_relationship_id = pre.retro_relationship_id
    AND
        pre.element_type_id = pet.element_type_id
    AND
        prr.payroll_relationship_id = rel.payroll_relationship_id
    AND
        rel.person_id = per.person_id
    AND
        per.person_number = '&&PersonNumber'
	AND 
		pr.flow_instance_id = pfi.flow_instance_id
	AND
		pr.pay_request_id  = ppa.pay_request_id
	AND 
		ppa.payroll_action_id = pra.payroll_action_id
	AND 
		pfi.instance_name = '&&Flowname'
	AND
		prr.retro_rel_action_id = pra.payroll_Rel_action_id
	order by creation_Date, retro_relationship_id desc;		

		--- Element Entries  created by Retro Run
	
	
PROMPT "Element Entries created by Retro Run"

Select 
		pet.base_element_name,
		pee.element_entry_id,
		pee.effective_start_date,
		pee.effective_end_date,
		pee.entry_type,
		pee.creator_type,
		pee.creator_id,
		pee.created_by,
		pee.creation_Date,
		pee.last_update_date,
		pee.last_updated_by
FROM 
		fusion.pay_element_entries_f pee,
		fusion.pay_element_types_f pet,
		fusion.pay_retro_relationships prr,
		fusion.pay_payroll_Actions ppa,
		fusion.pay_payroll_rel_actions pra,
		fusion.pay_Requests pr,
		fusion.pay_flow_instances pfi,
		fusion.pay_pay_relationships_dn rel,
		fusion.per_all_people_f per
WHERE 
		pfi.instance_name = '&&Flowname'
	AND 
		pr.flow_instance_id = pfi.flow_instance_id
	AND
		pr.pay_request_id  = ppa.pay_request_id
	AND 
		ppa.payroll_action_id = pra.payroll_action_id
	AND
		prr.retro_rel_action_id = pra.payroll_Rel_action_id
	AND
        prr.payroll_relationship_id = rel.payroll_relationship_id
    AND
        rel.person_id = per.person_id
    AND
        per.person_number = '&&PersonNumber'
	AND 
		pra.payroll_Rel_action_id = pee.creator_id
	AND
		pee.element_type_id = pet.element_type_id
order by element_entry_id;
	
	--- Element Entries Values created by Retro Run
	
PROMPT "Element Entries Values created by Retro Run"	


Select 
		pet.base_element_name,
		piv.base_name,
		pev.screen_entry_Value,
		pee.element_entry_id,
		pee.effective_start_date,
		pee.effective_end_date,
		pee.entry_type,
		pee.creator_type,
		pee.creator_id,
		pee.created_by,
		pee.creation_Date,
		pee.last_update_date,
		pee.last_updated_by
FROM 
		fusion.pay_element_entries_f pee,
		fusion.pay_element_types_f pet,
		fusion.pay_element_entry_values_f pev,
		fusion.pay_input_values_f piv,
		fusion.pay_retro_relationships prr,
		fusion.pay_payroll_Actions ppa,
		fusion.pay_payroll_rel_actions pra,
		fusion.pay_Requests pr,
		fusion.pay_flow_instances pfi,
		fusion.pay_pay_relationships_dn rel,
		fusion.per_all_people_f per
WHERE 
		pfi.instance_name = '&&Flowname'
	AND 
		pr.flow_instance_id = pfi.flow_instance_id
	AND
		pr.pay_request_id  = ppa.pay_request_id
	AND 
		ppa.payroll_action_id = pra.payroll_action_id
	AND
		prr.retro_rel_action_id = pra.payroll_Rel_action_id
	AND
        prr.payroll_relationship_id = rel.payroll_relationship_id
    AND
        rel.person_id = per.person_id
    AND
        per.person_number = '&&PersonNumber'
	AND 
		pra.payroll_Rel_action_id = pee.creator_id
	AND
		pee.element_type_id = pet.element_type_id
	AND
		pee.element_entry_id = pev.element_entry_id
	AND
		pev.input_Value_id = piv.input_Value_id
order by element_entry_id;

		
	
PROMPT "Retro comp usages"	
	
	-- Retro comp usages

SELECT distinct
    bpet.base_element_name AS base_element_name,
    rpet.base_element_name AS retro_element_name,
    pesu.adjustment_type,
    prcu.retro_component_id,
    prcu.creator_type
FROM
    fusion.pay_element_span_usages pesu,
    fusion.pay_retro_comp_usages prcu,
    fusion.pay_element_types_f bpet,
    fusion.pay_element_types_f rpet,
    fusion.pay_element_entries_f pee,
    fusion.per_all_people_f per
WHERE
        per.person_id = pee.person_id
    AND
        pee.element_type_id = bpet.element_type_id
    AND
        per.person_number = '&&PersonNumber'
    AND
        bpet.element_type_id = prcu.creator_id
    AND
        pesu.retro_component_usage_id = prcu.retro_component_usage_id
    AND
        pesu.retro_element_type_id = rpet.element_type_id;

PROMPT "Entry Proc Details"			
---Entry Proc Details:
		
Select 
pet.base_element_name, pepd.*
from 
fusion.pay_element_entries_f pee,
fusion.pay_element_types_f pet,
fusion.pay_entry_proc_details pepd,
fusion.pay_payroll_Actions ppa,
		fusion.pay_payroll_rel_actions pra,
		fusion.pay_Requests pr,
		fusion.pay_flow_instances pfi,
		fusion.pay_pay_relationships_dn rel,
		fusion.per_all_people_f per
where 
pepd.source_element_type_id = pet.element_type_id
AND
        pepd.element_entry_id = pee.element_entry_id
    And
       pee.EFFECTIVE_START_DATE between pet.effective_start_date and pet.effective_end_date 
    and pee.creator_id = pra.payroll_rel_Action_id 
AND 
	pfi.instance_name = '&&Flowname'
	AND 
		pr.flow_instance_id = pfi.flow_instance_id
	AND
		pr.pay_request_id  = ppa.pay_request_id
	AND 
		ppa.payroll_action_id = pra.payroll_action_id
AND rel.payroll_relationship_id = pra.payroll_relationship_id
    AND
        rel.person_id = per.person_id
    AND
        per.person_number = '&&PersonNumber';  
		
-- Last Retro Original RR values
		
 PROMPT "Retro Original RR values"	       
        
Select 
pepd.element_entry_id,
    spet.base_element_name AS "Source_Element_Name",
    pepd.source_rel_action_id,
    pepd.source_entry_id,
    pepd.source_start_date,
    pepd.source_end_date,
    piv.BASE_NAME,
    pepd.source_run_result_id,
    pepd.run_result_id,
    srrv.result_value as "Original RR VAL",
    rrv.result_value as "New RR VAL"
from 
fusion.pay_element_entries_f pee,
fusion.pay_element_types_f pet,
fusion.pay_element_types_f spet,
fusion.pay_entry_proc_details pepd,
fusion.pay_run_results srr,
fusion.pay_run_results rr,
fusion.pay_run_result_values srrv,
fusion.pay_run_result_values rrv,
fusion.pay_input_Values_f piv,
fusion.pay_payroll_Actions ppa,
		fusion.pay_payroll_rel_actions pra,
		fusion.pay_Requests pr,
		fusion.pay_flow_instances pfi,
		fusion.pay_pay_relationships_dn rel,
		fusion.per_all_people_f per
where 
        pepd.element_entry_id = pee.element_entry_id
AND
        pet.element_type_id = pee.element_type_id
AND
       pepd.source_element_type_id = spet.element_type_id
AND
     pepd.source_run_result_id(+) = srr.run_result_id 
AND
       srr.run_result_id = srrv.run_result_id 
AND
       pepd.run_result_id  = rr.run_result_id
AND
      rr.run_result_id  = rrv.run_result_id 
AND 
      rrv.input_value_id = srrv.input_Value_id 
and 
     piv.input_value_id = srrv.input_Value_id
And
       pee.EFFECTIVE_START_DATE between pet.effective_start_date and pet.effective_end_date 
and pee.creator_id = pra.payroll_rel_Action_id 
AND 
	pfi.instance_name = '&&Flowname'
	AND 
		pr.flow_instance_id = pfi.flow_instance_id
	AND
		pr.pay_request_id  = ppa.pay_request_id
	AND 
		ppa.payroll_action_id = pra.payroll_action_id
AND rel.payroll_relationship_id = pra.payroll_relationship_id
    AND
        rel.person_id = per.person_id
    AND
        per.person_number = '&&PersonNumber';          
       
		

 PROMPT "Retro Original RR values"	 
-- All retros		

SELECT
    pepd.element_entry_id,
    spet.base_element_name AS "Source_Element_Name",
    pepd.source_rel_action_id,
    pepd.source_entry_id,
    pepd.source_start_date,
    pepd.source_end_date,
    piv.BASE_NAME,
    pepd.source_run_result_id,
    pepd.run_result_id,
    srrv.result_value as "Original RR VAL",
    rrv.result_value as "New RR VAL"
FROM
    fusion.pay_entry_proc_details pepd,
    fusion.pay_element_entries_f pee,
    fusion.pay_element_types_f pet,
    fusion.pay_element_types_f spet,
    fusion.per_all_people_f per,
    fusion.pay_run_results srr,
    fusion.pay_run_results rr,
    fusion.pay_run_result_values srrv,
    fusion.pay_run_result_values rrv,
    fusion.pay_input_Values_f piv
WHERE
        pepd.element_entry_id = pee.element_entry_id
    AND
        pet.element_type_id = pee.element_type_id
    AND
        pee.person_id = per.person_id
    AND
        per.person_number = '&&PersonNumber'
    AND
        pepd.source_element_type_id = spet.element_type_id
    AND
        pepd.source_run_result_id = srr.run_result_id
    AND
        srr.run_result_id = srrv.run_result_id
    AND
        pepd.run_result_id = rr.run_result_id
    AND
        rr.run_result_id = rrv.run_result_id
    AND 
        rrv.input_value_id = srrv.input_Value_id
    and 
        piv.input_value_id = srrv.input_Value_id
    And
        pee.EFFECTIVE_START_DATE between pet.effective_start_date and pet.effective_end_date;

PROMPT "Pay Process Events"			
		-- Pay Process Events

SELECT
    ppe.*
FROM
    fusion.pay_process_events ppe,
    fusion.pay_entry_proc_details pepd,
    fusion.pay_element_entries_f pee,
    fusion.pay_element_types_f pet,
    fusion.per_all_people_f per
WHERE
        per.person_number = '&&PersonNumber'
    AND
        per.person_id = pee.person_id
    AND
        pee.element_type_id = pet.element_type_id
    AND
        pee.element_entry_id = pepd.element_entry_id
    AND
        pepd.source_entry_id = ppe.surrogate_key_id
	order by ppe.creation_date desc	;


		

spool off
set markup html off
