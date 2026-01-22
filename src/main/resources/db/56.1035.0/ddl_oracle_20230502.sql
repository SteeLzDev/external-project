/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     13/04/2023 17:02:46                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_anexo_consignataria                                */
/*==============================================================*/
create table tb_anexo_consignataria  (
   axc_codigo           varchar2(32)                    not null,
   usu_codigo           varchar2(32)                    not null,
   csa_codigo           varchar2(32)                    not null,
   tar_codigo           varchar2(32)                    not null,
   axc_nome             varchar2(255)                   not null,
   axc_ativo            smallint                       default 1 not null,
   axc_data             date                            not null,
   axc_ip_acesso        varchar2(45),
   constraint pk_tb_anexo_consignataria primary key (axc_codigo)
);

/*==============================================================*/
/* Index: r_905_fk                                              */
/*==============================================================*/
create index r_905_fk on tb_anexo_consignataria (
   csa_codigo asc
);

/*==============================================================*/
/* Index: r_906_fk                                              */
/*==============================================================*/
create index r_906_fk on tb_anexo_consignataria (
   tar_codigo asc
);

/*==============================================================*/
/* Index: r_907_fk                                              */
/*==============================================================*/
create index r_907_fk on tb_anexo_consignataria (
   usu_codigo asc
);

alter table tb_consignataria add csa_data_ini_contrato date;

alter table tb_consignataria add csa_data_renovacao_contrato date;

alter table tb_consignataria add csa_nro_processo varchar2(40);

alter table tb_consignataria add csa_obs_contrato clob;

alter table tb_anexo_consignataria
   add constraint fk_tb_anexo_r_905_tb_consi foreign key (csa_codigo)
      references tb_consignataria (csa_codigo);

alter table tb_anexo_consignataria
   add constraint fk_tb_anexo_r_906_tb_tipo_ foreign key (tar_codigo)
      references tb_tipo_arquivo (tar_codigo);

alter table tb_anexo_consignataria
   add constraint fk_tb_anexo_r_907_tb_usuar foreign key (usu_codigo)
      references tb_usuario (usu_codigo);

