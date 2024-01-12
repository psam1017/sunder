create user `local`@`localhost` identified by 'local';
grant all on *.* to `local`@`localhost`;
flush privileges;

show grants for `local`@`localhost`;