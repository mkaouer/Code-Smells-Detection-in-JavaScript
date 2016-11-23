USE quickfix;

DROP TABLE IF EXISTS outgoing_log;

CREATE TABLE outgoing_log (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  time DATETIME NOT NULL,
  beginstring CHAR(8) NOT NULL,
  sendercompid VARCHAR(64) NOT NULL,
  targetcompid VARCHAR(64) NOT NULL,
  text BLOB NOT NULL,
  PRIMARY KEY (id)
);