/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     18/10/2019 11:20:48                          */
/*==============================================================*/


drop table if exists tb_bloco_processamento;

/*==============================================================*/
/* Table: tb_bloco_processamento                                */
/*==============================================================*/
create table tb_bloco_processamento
(
   BPR_CODIGO           int not null auto_increment,
   TBP_CODIGO           varchar(32) not null,
   SBP_CODIGO           varchar(32) not null,
   CNV_CODIGO           varchar(32),
   EST_CODIGO           varchar(32),
   ORG_CODIGO           varchar(32),
   RSE_CODIGO           varchar(32),
   BPR_PERIODO          date not null,
   BPR_DATA_INCLUSAO    datetime not null,
   BPR_DATA_PROCESSAMENTO datetime,
   BPR_ORDEM_EXECUCAO   int not null,
   BPR_MENSAGEM         varchar(255),
   BPR_LINHA            text not null,
   BPR_CAMPOS           text not null,
   CNV_COD_VERBA        varchar(32),
   SVC_IDENTIFICADOR    varchar(40),
   CSA_IDENTIFICADOR    varchar(40),
   EST_IDENTIFICADOR    varchar(40),
   ORG_IDENTIFICADOR    varchar(40),
   RSE_MATRICULA        varchar(20),
   SER_CPF              varchar(19),
   ADE_NUMERO           bigint,
   ADE_INDICE           varchar(32),
   primary key (BPR_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_bloco_processamento add constraint FK_R_767 foreign key (TBP_CODIGO)
      references tb_tipo_bloco_processamento (TBP_CODIGO) on delete restrict on update restrict;

alter table tb_bloco_processamento add constraint FK_R_768 foreign key (SBP_CODIGO)
      references tb_status_bloco_processamento (SBP_CODIGO) on delete restrict on update restrict;

alter table tb_bloco_processamento add constraint FK_R_769 foreign key (RSE_CODIGO)
      references tb_registro_servidor (RSE_CODIGO) on delete restrict on update restrict;

alter table tb_bloco_processamento add constraint FK_R_770 foreign key (CNV_CODIGO)
      references tb_convenio (CNV_CODIGO) on delete restrict on update restrict;

alter table tb_bloco_processamento add constraint FK_R_780 foreign key (EST_CODIGO)
      references tb_estabelecimento (EST_CODIGO) on delete restrict on update restrict;

alter table tb_bloco_processamento add constraint FK_R_781 foreign key (ORG_CODIGO)
      references tb_orgao (ORG_CODIGO) on delete restrict on update restrict;

