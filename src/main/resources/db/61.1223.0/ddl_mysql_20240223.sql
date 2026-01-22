/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     31/01/2024 10:00:00                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_vinculo_consignataria                              */
/*==============================================================*/
create table tb_vinculo_consignataria
(
   VCS_CODIGO           varchar(32) not null,
   CSA_CODIGO           varchar(32) not null,
   VCS_IDENTIFICADOR    varchar(40) not null,
   VCS_DESCRICAO        varchar(255) not null,
   VCS_ATIVO            smallint not null default 1,
   VCS_DATA_CRIACAO     datetime not null,
   primary key (VCS_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*==============================================================*/
/* Index: TB_VINCULO_CONSIGNATARIA_AK                           */
/*==============================================================*/
create unique index TB_VINCULO_CONSIGNATARIA_AK on tb_vinculo_consignataria
(
   CSA_CODIGO,
   VCS_IDENTIFICADOR
);

/*==============================================================*/
/* Table: tb_vinculo_csa_rse                                    */
/*==============================================================*/
create table tb_vinculo_csa_rse
(
   VRS_CODIGO           varchar(32) not null,
   VCS_CODIGO           varchar(32) not null,
   primary key (VRS_CODIGO, VCS_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_vinculo_consignataria add constraint FK_R_952 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;

alter table tb_vinculo_csa_rse add constraint FK_R_953 foreign key (VRS_CODIGO)
      references tb_vinculo_registro_servidor (VRS_CODIGO) on delete restrict on update restrict;

alter table tb_vinculo_csa_rse add constraint FK_R_954 foreign key (VCS_CODIGO)
      references tb_vinculo_consignataria (VCS_CODIGO) on delete restrict on update restrict;

