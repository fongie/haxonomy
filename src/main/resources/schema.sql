CREATE TABLE IF NOT EXISTS `user` (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS role (
    name VARCHAR(255) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id INTEGER,
    role_name VARCHAR(255),

    PRIMARY KEY(user_id, role_name),
    FOREIGN KEY (user_id) REFERENCES `user`(id),
    FOREIGN KEY (role_name) REFERENCES role(name)
);

CREATE TABLE IF NOT EXISTS term (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    broader_term_id INTEGER,

    FOREIGN KEY(broader_term_id) REFERENCES term(id)
);

CREATE TABLE IF NOT EXISTS report (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    url VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS report_terms (
    reports_id INTEGER,
    terms_id INTEGER,

    PRIMARY KEY(reports_id, terms_id),
    FOREIGN KEY (reports_id) REFERENCES report(id),
    FOREIGN KEY (terms_id) REFERENCES term(id)
);
