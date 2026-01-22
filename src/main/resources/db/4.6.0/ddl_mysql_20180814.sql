/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     14/08/2018 15:47:30                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_notificacao_usuario                                */
/*==============================================================*/
create table tb_notificacao_usuario
(
   TNO_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null,
   NUS_ATIVO            smallint not null default 1,
   primary key (TNO_CODIGO, USU_CODIGO)
) ENGINE=InnoDB;

alter table tb_notificacao_usuario add constraint FK_R_724 foreign key (TNO_CODIGO)
      references tb_tipo_notificacao (TNO_CODIGO) on delete restrict on update restrict;

alter table tb_notificacao_usuario add constraint FK_R_725 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

