INSERT INTO term(id, name) VALUES(1,'attack surfaces');

INSERT INTO term(name, broader_term_id)
VALUES
('language', 1),
('source code', 1),
('user interface', 1),
('network & communication', 1),
('server', 1),
('account security', 1),
('encryption', 1);

INSERT INTO term(name, broader_term_id)
VALUES
('programming language', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'language')),
('markup language', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'language')),
('frameworks & libraries', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'language'));

INSERT INTO term(name, broader_term_id)
VALUES
('source code resources', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code')),
('source code vulnerabilities', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code'));

INSERT INTO term(name, broader_term_id)
VALUES
('request', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'network & communication')),
('protocol', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'network & communication')),
('redirect', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'network & communication')),
('dns', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'network & communication'));

