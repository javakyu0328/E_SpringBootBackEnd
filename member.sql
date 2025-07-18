/*데이터베이스 생성*/
create database db_member;

/*사용자 생성*/
create user Administrator@localhost identified by '1234';

/*권한부여*/
grant all privileges on db_member.* to Administrator@localhost;

commit;

/*테이블 확인*/
select *
from member_table;