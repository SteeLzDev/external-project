/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     02/01/2024 11:54:19                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_destinatario_email_cse                             */
/*==============================================================*/
create table tb_destinatario_email_cse
(
   FUN_CODIGO           varchar(32) not null,
   PAP_CODIGO           varchar(32) not null,
   CSE_CODIGO           varchar(32) not null,
   DEE_RECEBER          char(1) not null default 'S',
   DEE_EMAIL            varchar(100),
   primary key (FUN_CODIGO, PAP_CODIGO, CSE_CODIGO)
) ENGINE=InnoDB;

/*==============================================================*/
/* Table: tb_destinatario_email_ser                             */
/*==============================================================*/
create table tb_destinatario_email_ser
(
   FUN_CODIGO           varchar(32) not null,
   PAP_CODIGO           varchar(32) not null,
   SER_CODIGO           varchar(32) not null,
   DES_RECEBER          char(1) not null default 'S',
   primary key (FUN_CODIGO, PAP_CODIGO, SER_CODIGO)
) ENGINE=InnoDB;

alter table tb_destinatario_email_cse add constraint FK_R_946 foreign key (FUN_CODIGO)
      references tb_funcao (FUN_CODIGO) on delete restrict on update restrict;

alter table tb_destinatario_email_cse add constraint FK_R_947 foreign key (PAP_CODIGO)
      references tb_papel (PAP_CODIGO) on delete restrict on update restrict;

alter table tb_destinatario_email_cse add constraint FK_R_948 foreign key (CSE_CODIGO)
      references tb_consignante (CSE_CODIGO) on delete restrict on update restrict;

alter table tb_destinatario_email_ser add constraint FK_R_949 foreign key (FUN_CODIGO)
      references tb_funcao (FUN_CODIGO) on delete restrict on update restrict;

alter table tb_destinatario_email_ser add constraint FK_R_950 foreign key (PAP_CODIGO)
      references tb_papel (PAP_CODIGO) on delete restrict on update restrict;

alter table tb_destinatario_email_ser add constraint FK_R_951 foreign key (SER_CODIGO)
      references tb_servidor (SER_CODIGO) on delete restrict on update restrict;

