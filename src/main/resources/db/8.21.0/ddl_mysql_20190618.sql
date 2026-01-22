/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     17/06/2019 12:13:49                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_boleto_servidor                                    */
/*==============================================================*/
create table tb_boleto_servidor
(
   BOS_CODIGO           varchar(32) not null,
   SER_CODIGO           varchar(32) not null,
   CSA_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null,
   ARQ_CODIGO           varchar(32),
   BOS_DATA_UPLOAD      datetime not null,
   BOS_DATA_DOWNLOAD    datetime,
   BOS_DATA_EXCLUSAO    datetime,
   primary key (BOS_CODIGO)
) ENGINE=InnoDB;

alter table tb_boleto_servidor add constraint FK_R_755 foreign key (SER_CODIGO)
      references tb_servidor (SER_CODIGO) on delete restrict on update restrict;

alter table tb_boleto_servidor add constraint FK_R_756 foreign key (ARQ_CODIGO)
      references tb_arquivo (ARQ_CODIGO) on delete restrict on update restrict;

alter table tb_boleto_servidor add constraint FK_R_757 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

alter table tb_boleto_servidor add constraint FK_R_758 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;

