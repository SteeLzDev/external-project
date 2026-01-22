/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     30/01/2019 15:10:46                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_ocorrencia_dados_servidor                          */
/*==============================================================*/
create table tb_ocorrencia_dados_servidor
(
   ODS_CODIGO           varchar(32) not null,
   TDA_CODIGO           varchar(32) not null,
   TOC_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null,
   SER_CODIGO           varchar(32) not null,
   ODS_DATA             datetime not null,
   ODS_OBS              text not null,
   ODS_VALOR_ANT        varchar(255),
   ODS_VALOR_NOVO       varchar(255),
   ODS_IP_ACESSO        varchar(45),
   primary key (ODS_CODIGO)
) ENGINE=InnoDB;

/*==============================================================*/
/* Table: tb_servico_permite_tda                                */
/*==============================================================*/
create table tb_servico_permite_tda
(
   TDA_CODIGO           varchar(32) not null,
   SVC_CODIGO           varchar(32) not null,
   SPT_EXIBE            char(1) not null default 'S',
   primary key (TDA_CODIGO, SVC_CODIGO)
) ENGINE=InnoDB;

alter table tb_ocorrencia_dados_servidor add constraint FK_R_740 foreign key (SER_CODIGO)
      references tb_servidor (SER_CODIGO) on delete restrict on update restrict;

alter table tb_ocorrencia_dados_servidor add constraint FK_R_741 foreign key (TDA_CODIGO)
      references tb_tipo_dado_adicional (TDA_CODIGO) on delete restrict on update restrict;

alter table tb_ocorrencia_dados_servidor add constraint FK_R_742 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

alter table tb_ocorrencia_dados_servidor add constraint FK_R_743 foreign key (TOC_CODIGO)
      references tb_tipo_ocorrencia (TOC_CODIGO) on delete restrict on update restrict;

alter table tb_servico_permite_tda add constraint FK_R_744 foreign key (SVC_CODIGO)
      references tb_servico (SVC_CODIGO) on delete restrict on update restrict;

alter table tb_servico_permite_tda add constraint FK_R_745 foreign key (TDA_CODIGO)
      references tb_tipo_dado_adicional (TDA_CODIGO) on delete restrict on update restrict;

