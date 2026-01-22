/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     19/07/2022 14:22:41                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_bloqueio_posto_csa_svc                             */
/*==============================================================*/
create table tb_bloqueio_posto_csa_svc
(
   CSA_CODIGO           varchar(32) not null,
   SVC_CODIGO           varchar(32) not null,
   POS_CODIGO           varchar(32) not null,
   BPC_BLOQ_SOLICITACAO char(1) not null default 'N',
   BPC_BLOQ_RESERVA     char(1) not null default 'N',
   primary key (CSA_CODIGO, SVC_CODIGO, POS_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_bloqueio_posto_csa_svc add constraint FK_R_888 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;

alter table tb_bloqueio_posto_csa_svc add constraint FK_R_889 foreign key (SVC_CODIGO)
      references tb_servico (SVC_CODIGO) on delete restrict on update restrict;

alter table tb_bloqueio_posto_csa_svc add constraint FK_R_890 foreign key (POS_CODIGO)
      references tb_posto_registro_servidor (POS_CODIGO) on delete restrict on update restrict;

