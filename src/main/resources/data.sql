drop table if exists student CASCADE;
create table student (id bigint not null, name varchar(255), no varchar(255), weight integer, primary key (id));
insert into student values (1, 'zhangsan', '200001', 56);
insert into student values (2, 'lisi', '200002', 60);
insert into student values (3, 'wangwu', null, 65);
insert into student values (4, 'zhaoliu', null, 60);