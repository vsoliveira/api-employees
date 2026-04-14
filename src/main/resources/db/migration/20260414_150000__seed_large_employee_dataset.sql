WITH generated_employees AS (
    SELECT
        gs AS employee_number,
        format(
            '%s-%s-%s-%s-%s',
            substr(md5(gs::text), 1, 8),
            substr(md5(gs::text), 9, 4),
            substr(md5(gs::text), 13, 4),
            substr(md5(gs::text), 17, 4),
            substr(md5(gs::text), 21, 12)
        )::uuid AS id,
        format('Employee %s', gs) AS name,
        format('employee.%s@example.com', gs) AS email,
        format('Department %s', ((gs - 1) % 50) + 1) AS department
    FROM generate_series(1, 100500) AS gs
)
INSERT INTO employees (id, name, email, department)
SELECT id, name, email, department
FROM generated_employees;