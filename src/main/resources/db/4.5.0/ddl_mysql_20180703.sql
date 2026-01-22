/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     03/07/2018 10:21:55                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_destinatario_email                                 */
/*==============================================================*/
create table tb_destinatario_email
(
   FUN_CODIGO              varchar(32) not null,
   PAP_CODIGO_OPERADOR     varchar(32) not null,
   PAP_CODIGO_DESTINATARIO varchar(32) not null,
   primary key (PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO, FUN_CODIGO)
) ENGINE=InnoDB;

alter table tb_destinatario_email add constraint FK_R_711 foreign key (FUN_CODIGO)
      references tb_funcao (FUN_CODIGO) on delete restrict on update restrict;

alter table tb_destinatario_email add constraint FK_R_712 foreign key (PAP_CODIGO_OPERADOR)
      references tb_papel (PAP_CODIGO) on delete restrict on update restrict;

alter table tb_destinatario_email add constraint FK_R_713 foreign key (PAP_CODIGO_DESTINATARIO)
      references tb_papel (PAP_CODIGO) on delete restrict on update restrict;

