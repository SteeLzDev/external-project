/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     08/05/2025 12:21:24                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_controle_documento_margem                          */
/*==============================================================*/
create table tb_controle_documento_margem  (
   cdm_codigo           varchar2(32)                    not null,
   rse_codigo           varchar2(32)                    not null,
   cdm_base64           varchar2(244)                   not null,
   cdm_codigo_auth      varchar2(32)                    not null,
   cdm_data             date                            not null,
   constraint pk_tb_controle_documento_marge primary key (cdm_codigo)
);

/*==============================================================*/
/* Index: r_986_fk                                              */
/*==============================================================*/
create index r_986_fk on tb_controle_documento_margem (
   rse_codigo asc
);

alter table tb_controle_documento_margem
   add constraint fk_tb_contr_r_986_tb_regis foreign key (rse_codigo)
      references tb_registro_servidor (rse_codigo);

