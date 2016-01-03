CREATE TABLE user (
    id_user INTEGER PRIMARY KEY AUTOINCREMENT,
    account_number TEXT UNIQUE NOT NULL,
    pin INTEGER NOT NULL,
    balance REAL NOT NULL DEFAULT 0);

CREATE TABLE movement (
    id_movement INTEGER PRIMARY KEY AUTOINCREMENT,
    id_usr_to INTEGER NOT NULL,
    id_usr_from INTEGER,
    date TEXT NOT NULL,
    amount REAL NOT NULL,
    FOREIGN KEY(id_usr_to) REFERENCES user(id_user),
    FOREIGN KEY(id_usr_from) REFERENCES user(id_user));

CREATE TRIGGER compute_resulting_balance_to AFTER INSERT ON movement
    BEGIN
        UPDATE user SET balance = (balance + new.amount) WHERE id_user = new.id_usr_to;
    END;

CREATE TRIGGER compute_resulting_balance_from AFTER INSERT ON movement
    WHEN new.id_usr_from IS NOT NULL
        BEGIN
            UPDATE user SET balance = (balance - new.amount) WHERE id_user = new.id_usr_from;
        END;

CREATE TABLE atm (
    id_atm TEXT PRIMARY KEY,
    name TEXT,
    address TEXT
    lat REAL NOT NULL,
    lng REAL NOT NULL);
	