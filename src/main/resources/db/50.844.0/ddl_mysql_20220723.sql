/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     17/06/2022 14:27:03                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_anexo_credenciamento                               */
/*==============================================================*/
create table tb_anexo_credenciamento
(
   ANC_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null,
   CRE_CODIGO           varchar(32) not null,
   TAR_CODIGO           varchar(32) not null,
   ANC_NOME             varchar(255) not null,
   ANC_ATIVO            smallint not null default 1,
   ANC_DATA             datetime not null,
   ANC_IP_ACESSO        varchar(45),
   primary key (ANC_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*==============================================================*/
/* Table: tb_ocorrencia_credenciamento                          */
/*==============================================================*/
create table tb_ocorrencia_credenciamento
(
   OCD_CODIGO           varchar(32) not null,
   CRE_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null,
   TOC_CODIGO           varchar(32) not null,
   TMO_CODIGO           varchar(32),
   OCD_DATA             datetime not null,
   OCD_OBS              text not null,
   OCD_IP_ACESSO        varchar(45),
   primary key (OCD_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_anexo_credenciamento add constraint FK_R_882 foreign key (CRE_CODIGO)
      references tb_credenciamento_csa (CRE_CODIGO) on delete restrict on update restrict;

alter table tb_anexo_credenciamento add constraint FK_R_883 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

alter table tb_anexo_credenciamento add constraint FK_R_884 foreign key (TAR_CODIGO)
      references tb_tipo_arquivo (TAR_CODIGO) on delete restrict on update restrict;

alter table tb_ocorrencia_credenciamento add constraint FK_R_878 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

alter table tb_ocorrencia_credenciamento add constraint FK_R_879 foreign key (TOC_CODIGO)
      references tb_tipo_ocorrencia (TOC_CODIGO) on delete restrict on update restrict;

alter table tb_ocorrencia_credenciamento add constraint FK_R_880 foreign key (CRE_CODIGO)
      references tb_credenciamento_csa (CRE_CODIGO) on delete restrict on update restrict;

alter table tb_ocorrencia_credenciamento add constraint FK_R_881 foreign key (TMO_CODIGO)
      references tb_tipo_motivo_operacao (TMO_CODIGO) on delete restrict on update restrict;

