/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     03/10/2023 15:03:19                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_dados_consignante                                  */
/*==============================================================*/
create table tb_dados_consignante
(
   CSE_CODIGO           varchar(32) not null,
   TDA_CODIGO           varchar(32) not null,
   DAC_VALOR            varchar(255) not null,
   primary key (CSE_CODIGO, TDA_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*==============================================================*/
/* Table: tb_dados_consignataria                                */
/*==============================================================*/
create table tb_dados_consignataria
(
   CSA_CODIGO           varchar(32) not null,
   TDA_CODIGO           varchar(32) not null,
   DAA_VALOR            varchar(255) not null,
   primary key (CSA_CODIGO, TDA_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*==============================================================*/
/* Table: tb_dados_correspondente                               */
/*==============================================================*/
create table tb_dados_correspondente
(
   COR_CODIGO           varchar(32) not null,
   TDA_CODIGO           varchar(32) not null,
   DAR_VALOR            varchar(255) not null,
   primary key (COR_CODIGO, TDA_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*==============================================================*/
/* Table: tb_dados_estabelecimento                              */
/*==============================================================*/
create table tb_dados_estabelecimento
(
   EST_CODIGO           varchar(32) not null,
   TDA_CODIGO           varchar(32) not null,
   DAE_VALOR            varchar(255) not null,
   primary key (EST_CODIGO, TDA_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*==============================================================*/
/* Table: tb_dados_orgao                                        */
/*==============================================================*/
create table tb_dados_orgao
(
   ORG_CODIGO           varchar(32) not null,
   TDA_CODIGO           varchar(32) not null,
   DAO_VALOR            varchar(255) not null,
   primary key (ORG_CODIGO, TDA_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_dados_consignante add constraint FK_R_933 foreign key (TDA_CODIGO)
      references tb_tipo_dado_adicional (TDA_CODIGO) on delete restrict on update restrict;

alter table tb_dados_consignante add constraint FK_R_938 foreign key (CSE_CODIGO)
      references tb_consignante (CSE_CODIGO) on delete restrict on update restrict;

alter table tb_dados_consignataria add constraint FK_R_936 foreign key (TDA_CODIGO)
      references tb_tipo_dado_adicional (TDA_CODIGO) on delete restrict on update restrict;

alter table tb_dados_consignataria add constraint FK_R_941 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;

alter table tb_dados_correspondente add constraint FK_R_937 foreign key (TDA_CODIGO)
      references tb_tipo_dado_adicional (TDA_CODIGO) on delete restrict on update restrict;

alter table tb_dados_correspondente add constraint FK_R_942 foreign key (COR_CODIGO)
      references tb_correspondente (COR_CODIGO) on delete restrict on update restrict;

alter table tb_dados_estabelecimento add constraint FK_R_934 foreign key (TDA_CODIGO)
      references tb_tipo_dado_adicional (TDA_CODIGO) on delete restrict on update restrict;

alter table tb_dados_estabelecimento add constraint FK_R_939 foreign key (EST_CODIGO)
      references tb_estabelecimento (EST_CODIGO) on delete restrict on update restrict;

alter table tb_dados_orgao add constraint FK_R_935 foreign key (TDA_CODIGO)
      references tb_tipo_dado_adicional (TDA_CODIGO) on delete restrict on update restrict;

alter table tb_dados_orgao add constraint FK_R_940 foreign key (ORG_CODIGO)
      references tb_orgao (ORG_CODIGO) on delete restrict on update restrict;

