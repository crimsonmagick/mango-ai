CREATE TABLE IF NOT EXISTS users (
    id IDENTITY PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS CONVERSATIONS (
    ID VARCHAR(255) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS EXPRESSIONS (
     ID INT GENERATED ALWAYS AS IDENTITY,
     CONTENT CLOB NOT NULL,
     ACTOR_TYPE VARCHAR(64) NOT NULL,
     SEQUENCE_NUMBER INT NOT NULL,
     CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     UPDATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     CONVERSATION_ID VARCHAR(255),
     FOREIGN KEY (CONVERSATION_ID) REFERENCES CONVERSATIONS(ID)
);
