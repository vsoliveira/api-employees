WITH catalogs AS (
    SELECT
        ARRAY[
            'Ava', 'Noah', 'Mia', 'Liam', 'Sofia', 'Ethan', 'Isabella', 'Lucas',
            'Olivia', 'Mason', 'Emma', 'Benjamin', 'Charlotte', 'Elijah', 'Amelia',
            'James', 'Harper', 'Henry', 'Evelyn', 'Alexander', 'Grace', 'Daniel',
            'Scarlett', 'Michael', 'Aria', 'Samuel', 'Ella', 'Joseph', 'Chloe',
            'David', 'Layla', 'Matthew', 'Nora', 'Sebastian', 'Zoe', 'Jack',
            'Luna', 'Owen', 'Hannah', 'Levi'
        ]::text[] AS first_names,
        ARRAY[
            'Silva', 'Santos', 'Oliveira', 'Souza', 'Pereira', 'Costa', 'Rodrigues',
            'Almeida', 'Nascimento', 'Lima', 'Araujo', 'Fernandes', 'Carvalho',
            'Gomes', 'Martins', 'Rocha', 'Barbosa', 'Ribeiro', 'Alves', 'Cardoso',
            'Teixeira', 'Correia', 'Monteiro', 'Mendes', 'Freitas', 'Batista',
            'Campos', 'Castro', 'Dias', 'Moreira', 'Moura', 'Vieira', 'Farias',
            'Rezende', 'Peixoto', 'Machado', 'Ferreira', 'Queiroz', 'Tavares', 'Duarte'
        ]::text[] AS last_names,
        ARRAY[
            'Finance', 'Accounting', 'Treasury', 'Internal Audit', 'Legal', 'Compliance',
            'Risk Management', 'Human Resources', 'People Operations', 'Talent Acquisition',
            'Learning and Development', 'Engineering', 'Platform Engineering', 'QA Engineering',
            'Product Management', 'Product Design', 'Data Engineering', 'Data Science',
            'Business Intelligence', 'Information Security', 'IT Support', 'Cloud Operations',
            'Architecture', 'Sales', 'Sales Operations', 'Customer Success', 'Customer Support',
            'Marketing', 'Brand Strategy', 'Growth Marketing', 'Communications', 'Procurement',
            'Supply Chain', 'Logistics', 'Facilities', 'Workplace Experience', 'Administration',
            'Research', 'Innovation Lab', 'Partnerships', 'Strategy', 'Revenue Operations',
            'PMO', 'Operations', 'Field Services', 'Regional Management', 'Sustainability',
            'Investor Relations', 'Corporate Affairs', 'Executive Office'
        ]::text[] AS departments
),
seeded_employees AS (
    SELECT
        id,
        regexp_replace(email, '^employee\.([0-9]+)@example\.com$', '\1')::integer AS employee_number
    FROM employees
    WHERE email LIKE 'employee.%@example.com'
)
UPDATE employees AS employee
SET
    name = format(
        '%s %s',
        catalogs.first_names[((seeded_employees.employee_number - 1) % cardinality(catalogs.first_names)) + 1],
        catalogs.last_names[(((seeded_employees.employee_number - 1) / cardinality(catalogs.first_names)) % cardinality(catalogs.last_names)) + 1]
    ),
    department = catalogs.departments[((seeded_employees.employee_number - 1) % cardinality(catalogs.departments)) + 1]
FROM seeded_employees, catalogs
WHERE employee.id = seeded_employees.id;