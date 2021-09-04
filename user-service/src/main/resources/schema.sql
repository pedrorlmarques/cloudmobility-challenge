CREATE TABLE "user"
(
    id                    SERIAL PRIMARY KEY,
    first_name            varchar(50),
    last_name             varchar(50),
    identification_number varchar(50),
    role                  varchar(50),
    email                 varchar(200)
);

