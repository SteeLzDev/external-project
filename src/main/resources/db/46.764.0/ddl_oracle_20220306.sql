-- @@delimiter=/

create table tb_historico_login  (
   hlo_codigo           integer                         not null,
   usu_codigo           varchar2(32)                    not null,
   hlo_data             date                            not null,
   hlo_canal            char(1)                         not null,
   constraint pk_tb_historico_login primary key (hlo_codigo)
)
/

create index r_859_fk on tb_historico_login (
   usu_codigo asc
)
/

alter table tb_historico_login
   add constraint fk_tb_histo_r_859_tb_usuar foreign key (usu_codigo)
      references tb_usuario (usu_codigo)
/

create sequence s_historico_login
/

create trigger tib_tb_historico_login before insert
on tb_historico_login for each row
when (new.hlo_codigo is null)
declare
    integrity_error  exception;
    errno            integer;
    errmsg           char(200);
    dummy            integer;
    found            boolean;

begin
    --  column "hlo_codigo" uses sequence s_historico_login
    select s_historico_login.nextval into :new.hlo_codigo from dual;

--  errors handling
exception
    when integrity_error then
       raise_application_error(errno, errmsg);
end;
/
