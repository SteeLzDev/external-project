/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     09/03/2023 09:24:28                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_destinatario_email_csa                             */
/*==============================================================*/
create table tb_destinatario_email_csa
(
   FUN_CODIGO           varchar(32) not null,
   PAP_CODIGO           varchar(32) not null,
   CSA_CODIGO           varchar(32) not null,
   DEM_RECEBER          char(1) not null default 'S',
   DEM_EMAIL            varchar(100),
   primary key (FUN_CODIGO, PAP_CODIGO, CSA_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_destinatario_email_csa add constraint FK_R_902 foreign key (FUN_CODIGO)
      references tb_funcao (FUN_CODIGO) on delete restrict on update restrict;

alter table tb_destinatario_email_csa add constraint FK_R_903 foreign key (PAP_CODIGO)
      references tb_papel (PAP_CODIGO) on delete restrict on update restrict;

alter table tb_destinatario_email_csa add constraint FK_R_904 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;

