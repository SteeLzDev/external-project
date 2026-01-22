/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     07/06/2022 09:20:44                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_usuario_unidade                                    */
/*==============================================================*/
create table tb_usuario_unidade  (
   usu_codigo           varchar2(32)                    not null,
   uni_codigo           varchar2(32)                    not null,
   constraint pk_tb_usuario_unidade primary key (usu_codigo, uni_codigo)
);

/*==============================================================*/
/* Index: r_874_fk                                              */
/*==============================================================*/
create index r_874_fk on tb_usuario_unidade (
   usu_codigo asc
);

/*==============================================================*/
/* Index: r_873_fk                                              */
/*==============================================================*/
create index r_873_fk on tb_usuario_unidade (
   uni_codigo asc
);

alter table tb_usuario_unidade
   add constraint fk_tb_usuar_r_873_tb_unida foreign key (uni_codigo)
      references tb_unidade (uni_codigo);

alter table tb_usuario_unidade
   add constraint fk_tb_usuar_r_874_tb_usuar foreign key (usu_codigo)
      references tb_usuario (usu_codigo);

