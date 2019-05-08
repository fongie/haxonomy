INSERT IGNORE INTO term(id, name) VALUES (1,'attack surfaces')
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
INSERT INTO `time`(`time`)
VALUES
('5'),
('60'),
('360');

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
INSERT INTO term(name, broader_term_id)
VALUES
('programming language', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'language')),
('markup language', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'language')),
('frameworks & libraries', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'language'));

-- Source code
INSERT INTO term(name, broader_term_id)
VALUES
('source code resources', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code')),
('source code vulnerabilities', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code'));

-- Network & communication
INSERT INTO term(name, broader_term_id)
VALUES
('request', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'network & communication')),
('protocol', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'network & communication')),
('redirect', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'network & communication')),
('dns', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'network & communication'));

-- User interface
INSERT INTO term(name, broader_term_id)
VALUES
('website elements', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'user interface')),
('website functionality', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'user interface'));

-- Server
INSERT INTO term(name, broader_term_id)
VALUES
('cloud services', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server')),
('operating system', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server')),
('server software', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server')),
('database', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server')),
('server techniques', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server')),
('port', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server')),
('host', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server'));

-- Account security
INSERT INTO term(name, broader_term_id)
VALUES
('authorization', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'account security')),
('authentication', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'account security'));

-- Encryption
INSERT INTO term(name, broader_term_id)
VALUES
('base64', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'encryption')),
('public key', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'encryption')),
('secret key', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'encryption')),
('sigchain', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'encryption'));

-- Programming language
INSERT INTO term(name, broader_term_id)
VALUES
('python', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'programming language')),
('javascript', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'programming language')),
('coffeescript', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'programming language')),
('css', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'programming language')),
('php', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'programming language')),
('java', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'programming language')),
('asp', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'programming language'));

-- Markup language
INSERT INTO term(name, broader_term_id)
VALUES
('html', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'markup language')),
('svg', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'markup language')),
('xml', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'markup language')),
('json', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'markup language'));

-- Frameworks & libraries
INSERT INTO term(name, broader_term_id)
VALUES
('angularjs', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'frameworks & libraries')),
('jquery', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'frameworks & libraries')),
('django', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'frameworks & libraries')),
('ajax', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'frameworks & libraries')),
('bootstrap', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'frameworks & libraries'));

-- Source code resources
INSERT INTO term(name, broader_term_id)
VALUES
('github', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code resources')),
('wayback machine', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code resources')),
('crawler', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code resources'));

-- Source code vulnerabilities
INSERT INTO term(name, broader_term_id)
VALUES
('json stringify', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code vulnerabilities')),
('broken html', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code vulnerabilities')),
('canary protection', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code vulnerabilities')),
('concatenation', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code vulnerabilities')),
('dom', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code vulnerabilities')),
('location property', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code vulnerabilities')),
('unsanitized characters', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code vulnerabilities')),
('validation', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code vulnerabilities')),
('templating', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code vulnerabilities')),
('configuration', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'source code vulnerabilities'));

-- Website elements
INSERT INTO term(name, broader_term_id)
VALUES
('upload', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'website elements')),
('text input', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'website elements'));

-- Upload
INSERT INTO term(name, broader_term_id)
VALUES
('file upload', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'upload')),
('image upload', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'upload'));

-- Text input
INSERT INTO term(name, broader_term_id)
VALUES
('login', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'text input')),
('form', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'text input')),
('search field', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'text input')),
('editor', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'text input'));

-- Website functionality
INSERT INTO term(name, broader_term_id)
VALUES
('account', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'website functionality')),
('payment', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'website functionality')),
('profile', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'website functionality')),
('membership', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'website functionality')),
('subscription', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'website functionality'));

-- Request
INSERT INTO term(name, broader_term_id)
VALUES
('url', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'request')),
('parameter', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'request')),
('header', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'request')),
('post', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'request')),
('get', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'request')),
('put', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'request')),
('delete', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'request')),
('status codes', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'request'));

-- URL
INSERT INTO term(name, broader_term_id)
VALUES
('path', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'url')),
('deeplink', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'url'));

-- parameter
INSERT INTO term(name, broader_term_id)
VALUES
('http parameter', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'parameter')),
('sid parameter', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'parameter')),
('single-character parameter', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'parameter')),
('ssrf protection', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'parameter')),
('insecure direct object reference', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'parameter')),
('csrf protection', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'parameter'));

-- header
INSERT INTO term(name, broader_term_id)
VALUES
('user-agent header', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'header')),
('same-origin', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'header')),
('security header', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'header')),
('cookie', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'header')),
('content security policy', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'header'));

-- Protocol
INSERT INTO term(name, broader_term_id)
VALUES
('http', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'protocol')),
('https', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'protocol'));

-- Redirect
INSERT INTO term(name, broader_term_id)
VALUES
('server redirect', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'redirect')),
('client redirect', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'redirect'));

-- DNS
INSERT INTO term(name, broader_term_id)
VALUES
('cname', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'dns')),
('fqdn', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'dns')),
('subdomain', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'dns')),
('web cache', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'dns'));

-- Cloud services
INSERT INTO term(name, broader_term_id)
VALUES
('azure', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'cloud services')),
('amazon', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'cloud services')),
('google cloud', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'cloud services')),
('heroku', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'cloud services'));

-- Operation system
INSERT INTO term(name, broader_term_id)
VALUES
('linux', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'operating system')),
('windows nt', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'operating system')),
('windows', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'operating system')),
('osx', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'operating system'));

-- Server software
INSERT INTO term(name, broader_term_id)
VALUES
('apache http server', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server software')),
('apache tomcat', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server software')),
('jetty', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server software')),
('nginx', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server software')),
('cms', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server software'));

-- CMS
INSERT INTO term(name, broader_term_id)
VALUES
('wordpress', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'cms')),
('buddypress', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'cms'));

-- Database
INSERT INTO term(name, broader_term_id)
VALUES
('mysql', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'database')),
('postgres', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'database'));

-- Server techniques
INSERT INTO term(name, broader_term_id)
VALUES
('sandboxing', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server techniques')),
('virtual machine', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'server techniques'));

-- Authorization
INSERT INTO term(name, broader_term_id)
VALUES
('privilege', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'authorization')),
('write-access', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'authorization')),
('administrator', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'authorization')),
('read-access', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'authorization'));

-- Authentication
INSERT INTO term(name, broader_term_id)
VALUES
('token', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'authentication')),
('session', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'authentication')),
('credentials', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'authentication')),
('oauth', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'authentication'));

-- Token
INSERT INTO term(name, broader_term_id)
VALUES
('csrf token', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'token')),
('access token', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'token')),
('oauth token', (SELECT id FROM (SELECT id, name FROM term) AS a WHERE name = 'token'));
