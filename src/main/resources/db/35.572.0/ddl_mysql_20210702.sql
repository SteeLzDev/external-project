/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     02/07/2021 13:51:41                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_operacao_libera_margem                             */
/*==============================================================*/
create table tb_operacao_libera_margem
(
   OLM_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null,
   RSE_CODIGO           varchar(32) not null,
   CSA_CODIGO           varchar(32),
   OLM_DATA             datetime not null,
   OLM_IP_ACESSO        varchar(45) not null,
   OLM_BLOQUEIO         char(1) not null default 'N',
   primary key (OLM_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_operacao_libera_margem add constraint FK_R_841 foreign key (RSE_CODIGO)
      references tb_registro_servidor (RSE_CODIGO) on delete restrict on update restrict;

alter table tb_operacao_libera_margem add constraint FK_R_842 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

alter table tb_operacao_libera_margem add constraint FK_R_843 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;

