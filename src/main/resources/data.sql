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
       ('Open Redirect', 2, 2),
       ('XML External Entities (XXE)', 2, 2),
       ('Cross-Site Request Forgery (CSRF)', 2, 2),
       ('Server-Side Request Forgery (SSRF)', 2, 3),
       ('Path Traversal', 2, 2),
       ('Denial of Service (DoS)', 2, 2),
       ('Violation of Secure Design Principles', 2, 2),
       ('CRLF Injection', 2, 2),
       ('HTMLInjection', 2, 2),
       ('Code Injection', 2, 2),
       ('Sub Domain Takeover', 2, 2),
       ('Race Conditions', 2, 3);

-- Top terms
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('language', 1, 1),
       ('source code', 1, 2),
       ('user interface', 1, 1),
       ('network & communication', 1, 1),
       ('server', 1, 1),
       ('account security', 1, 1),
       ('encryption', 1, 3);

-- Made up reports
INSERT INTO report(title, url, bounty, vulnerability)
VALUES
('Made up title 1', 'www.maduptitle1.com', 100, 'sql_injection1'),
('Made up title 2', 'www.maduptitle2.com', 100, 'sql_injection2'),
('Made up title 3', 'www.maduptitle3.com', 100, 'sql_injection3'),
('Made up title 4', 'www.maduptitle4.com', 100, 'sql_injection4');

-- Made up connections between reports and terms
INSERT INTO report_terms(reports_id, terms_id)
VALUES
(1,1),
(2,2),
(3,3),
(4,4);

-- Language
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('programming language', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'language'), 2),
       ('markup language', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'language'), 2),
       ('frameworks & libraries', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'language'), 2);

-- Source code
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('source code resources', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code'), 2),
       ('source code vulnerabilities', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code'), 2);

-- Network & communication
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('request', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'network & communication'), 1),
       ('protocol', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'network & communication'), 2),
       ('redirect', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'network & communication'), 2),
       ('dns', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'network & communication'), 2);

-- User interface
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('website elements', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'user interface'), 1),
       ('website functionality', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'user interface'), 2);

-- Server
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('cloud services', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server'), 2),
       ('operating system', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server'), 2),
       ('server software', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server'), 2),
       ('database', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server'), 2),
       ('server techniques', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server'), 3),
       ('port', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server'), 2),
       ('host', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server'), 2);

-- Account security
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('authorization', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'account security'), 1),
       ('authentication', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'account security'), 1);

-- Encryption
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('base64', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'encryption'), 1),
       ('public key', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'encryption'), 1),
       ('secret key', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'encryption'), 3),
       ('sigchain', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'encryption'), 3);

-- Programming language
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('python', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'programming language'), 3),
       ('javascript', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'programming language'), 1),
       ('coffeescript', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'programming language'), 2),
       ('css', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'programming language'), 1),
       ('php', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'programming language'), 3),
       ('java', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'programming language'), 3),
       ('asp', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'programming language'), 3);

-- Markup language
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('html', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'markup language'), 1),
       ('svg', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'markup language'), 2),
       ('xml', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'markup language'), 2),
       ('json', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'markup language'), 2);

-- Frameworks & libraries
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('angularjs', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'frameworks & libraries'), 1),
       ('jquery', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'frameworks & libraries'), 1),
       ('django', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'frameworks & libraries'), 2),
       ('ajax', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'frameworks & libraries'), 2),
       ('bootstrap', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'frameworks & libraries'), 1);

-- Source code resources
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('github', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code resources'), 2),
       ('wayback machine', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code resources'), 2),
       ('crawler', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code resources'), 2);

-- Source code vulnerabilities
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('json stringify', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code vulnerabilities'), 2),
       ('broken html', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code vulnerabilities'), 2),
       ('canary protection', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code vulnerabilities'), 2),
       ('concatenation', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code vulnerabilities'), 2),
       ('dom', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code vulnerabilities'), 2),
       ('location property', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code vulnerabilities'), 2),
       ('unsanitized characters', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code vulnerabilities'), 2),
       ('validation', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code vulnerabilities'), 2),
       ('templating', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code vulnerabilities'), 2),
       ('configuration', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code vulnerabilities'), 3);

-- Website elements
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('upload', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'website elements'), 1),
       ('text input', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'website elements'), 1);

-- Upload
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('file upload', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'upload'), 1),
       ('image upload', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'upload'), 1);

-- Text input
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('login', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'text input'), 1),
       ('form', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'text input'), 1),
       ('search field', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'text input'), 1),
       ('editor', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'text input'), 1);

-- Website functionality
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('account', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'website functionality'), 1),
       ('payment', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'website functionality'), 1),
       ('profile', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'website functionality'), 1),
       ('membership', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'website functionality'), 1),
       ('subscription', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'website functionality'), 1);

-- Request
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('url', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'request'), 1),
       ('parameter', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'request'), 1),
       ('header', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'request'), 1),
       ('post', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'request'), 1),
       ('get', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'request'), 1),
       ('put', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'request'), 1),
       ('delete', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'request'), 1),
       ('status codes', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'request'), 2);

-- URL
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('path', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'url'), 2),
       ('deeplink', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'url'), 2);

-- parameter
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('http parameter', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'parameter'), 1),
       ('sid parameter', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'parameter'), 1),
       ('single-character parameter', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'parameter'), 1),
       ('ssrf protection', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'parameter'), 1),
       ('insecure direct object reference', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'parameter'), 3),
       ('csrf protection', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'parameter'), 1);

-- header
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('user-agent header', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'header'), 1),
       ('same-origin', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'header'), 1),
       ('security header', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'header'), 1),
       ('cookie', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'header'), 1),
       ('content security policy', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'header'), 1);

-- Protocol
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('http', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'protocol'), 1),
       ('https', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'protocol'), 1);

-- Redirect
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('server redirect', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'redirect'), 2),
       ('client redirect', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'redirect'), 2);

-- DNS
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('cname', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'dns'), 2),
       ('fqdn', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'dns'), 2),
       ('subdomain', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'dns'), 2),
       ('web cache', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'dns'), 2);

-- Cloud services
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('azure', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'cloud services'), 2),
       ('amazon', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'cloud services'), 2),
       ('google cloud', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'cloud services'), 2),
       ('heroku', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'cloud services'), 2);

-- Operation system
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('linux', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'operating system'), 3),
       ('windows nt', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'operating system'), 3),
       ('windows', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'operating system'), 3),
       ('osx', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'operating system'), 3);

-- Server software
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('apache http server', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server software'), 2),
       ('apache tomcat', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server software'), 2),
       ('jetty', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server software'), 2),
       ('nginx', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server software'), 2),
       ('cms', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server software'), 2);

-- CMS
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('wordpress', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'cms'), 1),
       ('buddypress', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'cms'), 1);

-- Database
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('mysql', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'database'), 3),
       ('postgres', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'database'), 3);

-- Server techniques
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('sandboxing', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server techniques'), 3),
       ('virtual machine', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server techniques'), 3);

-- Authorization
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('privilege', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'authorization'), 2),
       ('write-access', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'authorization'), 2),
       ('administrator', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'authorization'), 2),
       ('read-access', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'authorization'), 2);

-- Authentication
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('token', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'authentication'), 1),
       ('session', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'authentication'), 1),
       ('credentials', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'authentication'), 1),
       ('oauth', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'authentication'), 1);

-- Token
INSERT INTO term(name, broader_term_id, time_id)
VALUES
       ('csrf token', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'token'), 1),
       ('access token', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'token'), 1),
       ('oauth token', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'token'), 1);
