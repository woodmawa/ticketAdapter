package com.softwood.incident.adapters.simulators.SNOW

/**
 * sample :
 * curl "https://instance.service-now.com/api/now/table/incident" \
 * --request POST \
 * --header "Accept:application/json"\
 * --header "Content-Type:application/json" \
 * --data "{'short_description':'Unable to connect to office wifi','assignment_group':'287ebd7da9fe198100f92cc8d1d2154e','urgency':'2','impact':'2'}" \
 * --user 'admin':'admin'
 *
 *
 *{*   "result": {*     "upon_approval": "proceed",
 *     "location": "",
 *     "expected_start": "",
 *     "reopen_count": "0",
 *     "close_notes": "",
 *     "additional_assignee_list": "",
 *     "impact": "2",
 *     "urgency": "2",
 *     "correlation_id": "",
 *     "sys_tags": "",
 *     "sys_domain": {*       "link": "https://instance.service-now.com/api/now/table/sys_user_group/global",
 *       "value": "global"
 *},
 *     "description": "",
 *     "group_list": "",
 *     "priority": "3",
 *     "delivery_plan": "",
 *     "sys_mod_count": "0",
 *     "work_notes_list": "",
 *     "business_service": "",
 *     "follow_up": "",
 *     "closed_at": "",
 *     "sla_due": "",
 *     "delivery_task": "",
 *     "sys_updated_on": "2016-01-22 14:28:24",
 *     "parent": "",
 *     "work_end": "",
 *     "number": "INC0010002",
 *     "closed_by": "",
 *     "work_start": "",
 *     "calendar_stc": "",
 *     "category": "inquiry",
 *     "business_duration": "",
 *     "incident_state": "1",
 *     "activity_due": "",
 *     "correlation_display": "",
 *     "company": "",
 *     "active": "true",
 *     "due_date": "",
 *     "assignment_gro": {*       "link": "https://instance.service-now.com/api/now/table/sys_user_group/287ebd7da9fe198100f92cc8d1d2154e",
 *       "value": "287ebd7da9fe198100f92cc8d1d2154e"*     },
 *     "caller_id": "",
 *     "knowledge": "false",
 *     "made_sla": "true",
 *     "comments_and_work_notes": "",
 *     "parent_incident": "",
 *     "state": "1",
 *     "user_input": "",
 *     "sys_created_on": "2016-01-22 14:28:24",
 *     "approval_set": "",
 *     "reassignment_count": "0",
 *     "rfc": "",
 *     "child_incidents": "0",
 *     "opened_at": "2016-01-22 14:28:24",
 *     "short_description": "Unable to connect to office wifi",
 *     "order": "",
 *     "sys_updated_by": "admin",
 *     "resolved_by": "",
 *     "notify": "1",
 *     "upon_reject": "cancel",
 *     "approval_history": "",
 *     "problem_id": "",
 *     "work_notes": "",
 *     "calendar_duration": "",
 *     "close_code": "",
 *     "sys_id": "c537bae64f411200adf9f8e18110c76e",
 *     "approval": "not requested",
 *     "caused_by": "",
 *     "severity": "3",
 *     "sys_created_by": "admin",
 *     "resolved_at": "",
 *     "assigned_to": "",
 *     "business_stc": "",
 *     "wf_activity": "",
 *     "sys_domain_path": "/",
 *     "cmdb_ci": "",
 *     "open_by": {*       "link": "https://instance.service-now.com/api/now/table/sys_user/6816f79cc0a8016401c5a33be04be441",
 *       "value": "6816f79cc0a8016401c5a33be04"
 *},
 *     "subcategory": "",
 *     "rejection_goto": "",
 *     "sys_class_name": "incident",
 *     "watch_list": "",
 *     "time_worked": "",
 *     "contact_type": "phone",
 *     "escalation": "0",
 *     "comment ""*   }*}*
 */

//not using this right now - using IncidentTicket as wrapper and generate post body
class SnowIncidentCreatDAO {
    String short_description
    String Comments
    String category
    String assignment_group
    String urgency
    String impact
}
