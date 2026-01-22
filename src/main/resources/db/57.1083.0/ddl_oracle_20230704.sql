/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     04/07/2023 14:43:20                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_sub_relatorio                                    */
/*==============================================================*/
create table tb_sub_relatorio  (
   sre_codigo           varchar2(32)                    not null,
   rel_codigo           varchar2(32)                    not null,
   sre_template_jasper  varchar2(100)                   not null,
   sre_nome_parametro   varchar2(100)                   not null,
   sre_template_sql     clob                            not null,
   constraint pk_tb_sub_relatorio primary key (sre_codigo)
);

/*==============================================================*/
/* Index: r_911_fk                                              */
/*==============================================================*/
create index r_911_fk on tb_sub_relatorio (
   rel_codigo asc
);

alter table tb_sub_relatorio
   add constraint fk_tb_sub_r_r_911_tb_relat foreign key (rel_codigo)
      references tb_relatorio (rel_codigo);

