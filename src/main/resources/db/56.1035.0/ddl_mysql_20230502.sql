/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     13/04/2023 16:49:06                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_anexo_consignataria                                */
/*==============================================================*/
create table tb_anexo_consignataria
(
   AXC_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null,
   CSA_CODIGO           varchar(32) not null,
   TAR_CODIGO           varchar(32) not null,
   AXC_NOME             varchar(255) not null,
   AXC_ATIVO            smallint not null default 1,
   AXC_DATA             datetime not null,
   AXC_IP_ACESSO        varchar(45),
   primary key (AXC_CODIGO)
) ENGINE=InnoDB;

alter table tb_consignataria
   add CSA_DATA_INI_CONTRATO date;

alter table tb_consignataria
   add CSA_DATA_RENOVACAO_CONTRATO date;

alter table tb_consignataria
   add CSA_NRO_PROCESSO varchar(40);

alter table tb_consignataria
   add CSA_OBS_CONTRATO text;

alter table tb_anexo_consignataria add constraint FK_R_905 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;

alter table tb_anexo_consignataria add constraint FK_R_906 foreign key (TAR_CODIGO)
      references tb_tipo_arquivo (TAR_CODIGO) on delete restrict on update restrict;

alter table tb_anexo_consignataria add constraint FK_R_907 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

