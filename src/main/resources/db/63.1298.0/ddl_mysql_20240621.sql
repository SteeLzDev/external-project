/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     06/06/2024 09:08:22                          */
/*==============================================================*/


/*==============================================================*/
/* Table: ht_historico_ocorrencia_ade                           */
/*==============================================================*/
create table ht_historico_ocorrencia_ade
(
   HOA_CODIGO           varchar(32) not null,
   OCA_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null,
   HOA_DATA             datetime not null,
   HOA_IP_ACESSO        varchar(45) not null,
   HOA_OBS              text not null,
   primary key (HOA_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*==============================================================*/
/* Table: tb_historico_ocorrencia_ade                           */
/*==============================================================*/
create table tb_historico_ocorrencia_ade
(
   HOA_CODIGO           varchar(32) not null,
   OCA_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null,
   HOA_DATA             datetime not null,
   HOA_IP_ACESSO        varchar(45) not null,
   HOA_OBS              text not null,
   primary key (HOA_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table ht_historico_ocorrencia_ade add constraint FK_R_963 foreign key (OCA_CODIGO)
      references ht_ocorrencia_autorizacao (OCA_CODIGO) on delete restrict on update restrict;

alter table ht_historico_ocorrencia_ade add constraint FK_R_964 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

alter table tb_historico_ocorrencia_ade add constraint FK_R_961 foreign key (OCA_CODIGO)
      references tb_ocorrencia_autorizacao (OCA_CODIGO) on delete restrict on update restrict;

alter table tb_historico_ocorrencia_ade add constraint FK_R_962 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

