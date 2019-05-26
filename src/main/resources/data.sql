INSERT IGNORE INTO term(id, name) VALUES (1,'attack surfaces')
ON DUPLICATE KEY UPDATE id=id;
INSERT IGNORE INTO term(id, name) VALUES (2,'Vulnerabilities')
ON DUPLICATE KEY UPDATE id=id;

-- Replies
INSERT INTO reply(name)
VALUES
       ('UNKNOWN'),
       ('YES'),
       ('NO');

-- Roles
INSERT INTO role(name)
VALUES
       ('taxonomist');

-- Times
INSERT INTO `time`(`time`, id)
VALUES
       ('5', 1),
       ('60', 2),
       ('360', 3);

-- Vulnerabilities
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('SQL Injection', 2, 2),
       ('Cross-Site Scripting (XSS)', 2, 2),
       ('Insecure Direct Object Reference (IDOR)', 2, 2),
       ('Open Redirect', 2, 2);

-- Top terms
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('language', 1, 1),
       ('source code', 1, 2),
       ('user interface', 1, 1),
       ('network & communication', 1, 1);

-- Language
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('programming language', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'language'), 2),
       ('markup language', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'language'), 2);


-- Reports
INSERT INTO report(url, title, bounty)
VALUES
       ('www.report1.com', 'SQL Injection', 100),
       ('www.report5.com', 'Cross-Site Scripting (XSS)', 100),
       ('www.report6.com', 'Insecure Direct Object Reference (IDOR)', 100);

-- ReportTerms
INSERT INTO report_terms(reports_id, terms_id)
VALUES
       (1,9),
       (1,3),
       (2,4),
       (2,9),
       (3,5),
       (3,10);


