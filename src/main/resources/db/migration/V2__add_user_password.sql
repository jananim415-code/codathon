-- Keeps installations created before JWT authentication upgradeable.
alter table users add column if not exists password_hash varchar(255);
