/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     09/09/2019 14:59:48                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_decisao_judicial                                   */
/*==============================================================*/
create table tb_decisao_judicial
(
   DJU_CODIGO           varchar(32) not null,
   OCA_CODIGO           varchar(32) not null,
   TJU_CODIGO           varchar(32) not null,
   CID_CODIGO           varchar(32),
   DJU_NUM_PROCESSO     varchar(50) not null,
   DJU_DATA             date not null,
   DJU_TEXTO            text not null,
   primary key (DJU_CODIGO)
) ENGINE=InnoDB;

/*==============================================================*/
/* Table: tb_ht_decisao_judicial                                */
/*==============================================================*/
create table ht_decisao_judicial
(
   DJU_CODIGO           varchar(32) not null,
   OCA_CODIGO           varchar(32) not null,
   TJU_CODIGO           varchar(32) not null,
   CID_CODIGO           varchar(32),
   DJU_NUM_PROCESSO     varchar(50) not null,
   DJU_DATA             date not null,
   DJU_TEXTO            text not null,
   primary key (DJU_CODIGO)
) ENGINE=InnoDB;

/*==============================================================*/
/* Table: tb_tipo_justica                                       */
/*==============================================================*/
create table tb_tipo_justica
(
   TJU_CODIGO           varchar(32) not null,
   TJU_DESCRICAO        varchar(40) not null,
   primary key (TJU_CODIGO)
) ENGINE=InnoDB;

alter table tb_decisao_judicial add constraint FK_R_772 foreign key (TJU_CODIGO)
      references tb_tipo_justica (TJU_CODIGO) on delete restrict on update restrict;

alter table tb_decisao_judicial add constraint FK_R_773 foreign key (CID_CODIGO)
      references tb_cidade (CID_CODIGO) on delete restrict on update restrict;

alter table tb_decisao_judicial add constraint FK_R_774 foreign key (OCA_CODIGO)
      references tb_ocorrencia_autorizacao (OCA_CODIGO) on delete restrict on update restrict;

alter table ht_decisao_judicial add constraint FK_R_775 foreign key (TJU_CODIGO)
      references tb_tipo_justica (TJU_CODIGO) on delete restrict on update restrict;

alter table ht_decisao_judicial add constraint FK_R_776 foreign key (CID_CODIGO)
      references tb_cidade (CID_CODIGO) on delete restrict on update restrict;

alter table ht_decisao_judicial add constraint FK_R_777 foreign key (OCA_CODIGO)
      references ht_ocorrencia_autorizacao (OCA_CODIGO) on delete restrict on update restrict;
