--------- working qury -----------
select time,status,amount from loan_request where created_at in('2023-04-23','2023-04-22') and
loan_type_id in(1,5,3,8,10) and status in('pending','approved') order by "requested_date" asc limit 5 offset 0;
------------------ calling statement -----------
call get_list('rate','''2023-04-23''',1,'''pending''','requested_date','asc',5,0,null);

------------  Store Procedure ------------
CREATE OR REPLACE PROCEDURE get_list(
in columns_input text DEFAULT null,
in created_at text default NULL,
in loan_type_id text default NULL,
in status text default NULL,
in sort_by_column text default NULL,
in sort_by_order text default NULL,
in limit_val integer DEFAULT NULL,
in offset_val integer DEFAULT NULL,
inout query_str text DEFAULT NULL
)
LANGUAGE plpgsql
AS $$
DECLARE
  columns_str text;
BEGIN
  query_str := 'SELECT ' || columns_input || ' FROM loan_system u, loan_type d ';

IF created_at <> '''all''' THEN
    query_str := query_str || ' WHERE u.created_at in(' || created_at || ') ';
END IF;
  
IF loan_type_id <> 'all' THEN
   if created_at = '''all''' then
    query_str := query_str || ' WHERE u.loan_type_id in(' || loan_type_id || ') ';
   else	
    query_str := query_str || 'and u.loan_type_id in(' || loan_type_id || ') ';
   END IF;
END IF;
  
IF status != '''all''' THEN 
   if loan_type_id = 'all' then
    query_str := query_str || ' WHERE u.status in(' || status || ') ';
   else
    query_str := query_str || 'and u.status in(' || status || ') ';
   END IF;
END IF;
  
IF sort_by_column IS NOT NULL THEN
  IF status = '''all''' and created_at = '''all''' THEN 
    query_str := query_str || ' WHERE u.loan_type_id = d.loan_type_id ORDER BY  ' || sort_by_column;
  else
    query_str := query_str || ' and u.loan_type_id = d.loan_type_id ORDER BY  ' || sort_by_column;
  END IF;
END IF;

IF sort_by_order IS NOT NULL THEN
    query_str := query_str || ' ' || sort_by_order;
END IF;

IF limit_val IS NOT NULL THEN
    query_str := query_str || ' LIMIT ' || limit_val;
END IF;

IF offset_val IS NOT NULL THEN
    query_str := query_str || ' OFFSET ' || offset_val;
END IF;
   execute query_str;
END;
$$;


------------------ calling statement -----------
call get_list(status,'2023-04-23',3,'pending',"requested_date","asc",5,0);
call get_list('rate','''2023-04-23''',1,'''pending''','requested_date','asc',5,0);


--------------------
DO $$
DECLARE _query text; _cursor CONSTANT refcursor := '_cursor';
BEGIN _query := 'SELECT * FROM loan_request';
  OPEN _cursor FOR EXECUTE _query; END
$$; FETCH ALL FROM _cursor;

-------------------------------------------------------------------
----Customer by id
CREATE OR REPLACE PROCEDURE CxLoanReqsts(
in columns_input text,
in customer_id int default null,
in created_at text default NULL,
in loan_type_id text default NULL,
in status text default NULL,
in sort_by_column text default NULL,
in sort_by_order text default NULL,
in limit_val int DEFAULT NULL,
in offset_val int DEFAULT NULL	,
inout query_str text DEFAULT null
)
LANGUAGE plpgsql
AS $$
DECLARE
BEGIN
  query_str := 'SELECT ' || columns_input || ' FROM loan_request u';

  IF customer_id IS NOT null THEN
    query_str := query_str || ' and WHERE u.customer_id in(' || customer_id || ') ';
  END IF;
  
  IF created_at IS NOT NULL THEN
    query_str := query_str || ' WHERE u.created_at in(' || created_at || ') ';
  END IF;
  
  IF loan_type_id IS NOT NULL THEN
    query_str := query_str || 'and u.loan_type_id in(' || loan_type_id || ') ';
  END IF;
  
  IF status IS NOT NULL THEN
    query_str := query_str || 'and u.status in(' || status || ') ';
  END IF;

  IF sort_by_column IS NOT NULL THEN
    query_str := query_str || ' ORDER BY ' || sort_by_column;
  END IF;

  IF sort_by_order IS NOT NULL THEN
    query_str := query_str || ' ' || sort_by_order;
  END IF;

  IF limit_val IS NOT NULL THEN
    query_str := query_str || ' LIMIT ' || limit_val;
  END IF;

  IF offset_val IS NOT NULL THEN
    query_str := query_str || ' OFFSET ' || offset_val;
  END IF;
   execute query_str;
END;
$$;









