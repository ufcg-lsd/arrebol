CREATE TABLE unfinished_commands AS (SELECT * FROM command WHERE state <> 'FINISHED');
CREATE TABLE unfinished_jobs AS  (SELECT * FROM job WHERE job_state <> 'FINISHED');
CREATE TABLE unfinished_tasks_spec AS (SELECT * FROM task_spec AS t WHERE SPLIT_PART(t.label, '#', 1) IN (SELECT label FROM unfinished_jobs));
CREATE TABLE unfinished_tasks AS (SELECT * FROM task WHERE state <> 'FINISHED' and state <> 'FAILED');

TRUNCATE task_spec CASCADE;
TRUNCATE task;
TRUNCATE command CASCADE;

INSERT INTO task_spec SELECT * FROM unfinished_tasks_spec;
INSERT INTO command SELECT * FROM unfinished_commands;
INSERT INTO task SELECT * FROM unfinished_tasks;

DROP TABLE unfinished_jobs;
DROP TABLE unfinished_tasks;
DROP TABLE unfinished_tasks_spec;
DROP TABLE unfinished_commands;


VACUUM command;
VACUUM task_spec_requirements;
VACUUM task_spec_commands;
VACUUM task_spec;

\q
