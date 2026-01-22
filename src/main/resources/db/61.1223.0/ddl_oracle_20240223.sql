/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     31/01/2024 10:17:22                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_vinculo_consignataria                            */
/*==============================================================*/
create table tb_vinculo_consignataria  (
   vcs_codigo           varchar2(32)                    not null,
   csa_codigo           varchar2(32)                    not null,
   vcs_identificador    varchar2(40)                    not null,
   vcs_descricao        varchar2(255)                   not null,
   vcs_ativo            smallint                        default 1 not null,
   vcs_data_criacao     date                            not null,
   constraint pk_tb_vinculo_consignataria primary key (vcs_codigo)
);

/*==============================================================*/
/* Index: r_952_fk                                              */
/*==============================================================*/
create index r_952_fk on tb_vinculo_consignataria (
   csa_codigo asc
);

/*==============================================================*/
/* Index: tb_vinculo_consignataria_ak                           */
/*==============================================================*/
create unique index tb_vinculo_consignataria_ak on tb_vinculo_consignataria (
   csa_codigo asc,
   vcs_identificador asc
);

/*==============================================================*/
/* Table: tb_vinculo_csa_rse                                  */
/*==============================================================*/
create table tb_vinculo_csa_rse  (
   vrs_codigo           varchar2(32)                    not null,
   vcs_codigo           varchar2(32)                    not null,
   constraint pk_tb_vinculo_csa_rse primary key (vrs_codigo, vcs_codigo)
);

/*==============================================================*/
/* Index: r_953_fk                                              */
/*==============================================================*/
create index r_953_fk on tb_vinculo_csa_rse (
   vrs_codigo asc
);

/*==============================================================*/
/* Index: r_954_fk                                              */
/*==============================================================*/
create index r_954_fk on tb_vinculo_csa_rse (
   vcs_codigo asc
);

alter table tb_vinculo_consignataria
   add constraint fk_tb_vincu_r_952_tb_consi foreign key (csa_codigo)
      references tb_consignataria (csa_codigo);

alter table tb_vinculo_csa_rse
   add constraint fk_tb_vincu_r_953_tb_vincu foreign key (vrs_codigo)
      references tb_vinculo_registro_servidor (vrs_codigo);

alter table tb_vinculo_csa_rse
   add constraint fk_tb_vincu_r_954_tb_vincu foreign key (vcs_codigo)
      references tb_vinculo_consignataria (vcs_codigo);

