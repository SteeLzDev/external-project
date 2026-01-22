/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     20/05/2022 11:30:36                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_campo_usuario                                      */
/*==============================================================*/
create table tb_campo_usuario  (
   usu_codigo           varchar2(32)                    not null,
   cau_chave            varchar2(200)                   not null,
   cau_valor            char(1)                         not null,
   constraint pk_tb_campo_usuario primary key (usu_codigo, cau_chave)
);

/*==============================================================*/
/* Index: r_869_fk                                              */
/*==============================================================*/
create index r_869_fk on tb_campo_usuario (
   usu_codigo asc
);

alter table tb_campo_usuario
   add constraint fk_tb_campo_r_869_tb_usuar foreign key (usu_codigo)
      references tb_usuario (usu_codigo);

