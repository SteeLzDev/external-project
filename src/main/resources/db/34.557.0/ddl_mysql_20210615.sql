/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     15/06/2021 10:58:52                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_arquivo_rse                                        */
/*==============================================================*/
create table tb_arquivo_rse
(
   RSE_CODIGO           varchar(32) not null,
   ARQ_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null,
   ARS_DATA_CRIACAO     datetime not null,
   ARS_NOME             varchar(255) not null,
   ARS_IP_ACESSO        varchar(45) not null,
   primary key (RSE_CODIGO, ARQ_CODIGO)
) ENGINE=InnoDB;

alter table tb_arquivo_rse add constraint FK_R_838 foreign key (RSE_CODIGO)
      references tb_registro_servidor (RSE_CODIGO) on delete restrict on update restrict;

alter table tb_arquivo_rse add constraint FK_R_839 foreign key (ARQ_CODIGO)
      references tb_arquivo (ARQ_CODIGO) on delete restrict on update restrict;

alter table tb_arquivo_rse add constraint FK_R_840 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

