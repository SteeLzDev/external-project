/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     21/11/2022 15:15:29                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_param_posto_csa_svc                                */
/*==============================================================*/
create table tb_param_posto_csa_svc
(
   TPS_CODIGO           varchar(32) not null,
   SVC_CODIGO           varchar(32) not null,
   CSA_CODIGO           varchar(32) not null,
   POS_CODIGO           varchar(32) not null,
   PPO_VLR              varchar(255) not null,
   primary key (TPS_CODIGO, SVC_CODIGO, CSA_CODIGO, POS_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_param_posto_csa_svc add constraint FK_R_895 foreign key (TPS_CODIGO)
      references tb_tipo_param_svc (TPS_CODIGO) on delete restrict on update restrict;

alter table tb_param_posto_csa_svc add constraint FK_R_896 foreign key (SVC_CODIGO)
      references tb_servico (SVC_CODIGO) on delete restrict on update restrict;

alter table tb_param_posto_csa_svc add constraint FK_R_897 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;

alter table tb_param_posto_csa_svc add constraint FK_R_898 foreign key (POS_CODIGO)
      references tb_posto_registro_servidor (POS_CODIGO) on delete restrict on update restrict;

