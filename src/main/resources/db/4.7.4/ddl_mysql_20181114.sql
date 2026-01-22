/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     19/11/2018 14:09:33                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_arquivo_faturamento_beneficio                      */
/*==============================================================*/
create table tb_arquivo_faturamento_beneficio
(
   AFB_CODIGO           int not null auto_increment,
   FAT_CODIGO           varchar(32) not null,
   ADE_CODIGO           varchar(32) not null,
   TLA_CODIGO           varchar(32) not null,
   RSE_MATRICULA        varchar(20),
   CBE_NUMERO           varchar(40),
   CBE_VALOR_TOTAL      decimal(13,2) default 0,
   CBE_DATA_INCLUSAO    datetime,
   BEN_CODIGO_REGISTRO  varchar(40),
   BEN_CODIGO_CONTRATO  varchar(40),
   BFC_CPF              char(19),
   BFC_CELULAR          varchar(40),
   BFC_ORDEM_DEPENDENCIA smallint,
   BFC_NOME             varchar(255),
   ENS_LOGRADOURO       varchar(100),
   ENS_NUMERO           varchar(15),
   ENS_COMPLEMENTO      varchar(40),
   ENS_BAIRRO           varchar(40),
   ENS_MUNICIPIO        varchar(40),
   ENS_UF               char(2),
   ENS_CEP              varchar(10),
   ENS_CODIGO_MUNICIPIO varchar(7),
   PRD_VLR_PREVISTO     decimal(13,2),
   ADE_ANO_MES_INI      date,
   CNV_COD_VERBA        varchar(32),
   RSE_MATRICULA_INST   varchar(20),
   AFB_NUMERO_LOTE      varchar(40),
   AFB_ITEM_LOTE        varchar(40),
   AFB_VALOR_SUBSIDIO   decimal(13,2),
   AFB_VALOR_REALIZADO  decimal(13,2),
   AFB_VALOR_NAO_REALIZADO decimal(13,2),
   AFB_VALOR_TOTAL      decimal(13,2),
   AFB_CODIGO_FUNDO_REPASSE varchar(2),
   AFB_DESCRICAO_FUNDO_REPASSE varchar(255),
   primary key (AFB_CODIGO)
) ENGINE=InnoDB;

alter table tb_arquivo_faturamento_beneficio add constraint FK_R_730 foreign key (FAT_CODIGO)
      references tb_faturamento_beneficio (FAT_CODIGO) on delete restrict on update restrict;

alter table tb_arquivo_faturamento_beneficio add constraint FK_R_731 foreign key (TLA_CODIGO)
      references tb_tipo_lancamento (TLA_CODIGO) on delete restrict on update restrict;

alter table tb_arquivo_faturamento_beneficio add constraint FK_R_732 foreign key (ADE_CODIGO)
      references tb_aut_desconto (ADE_CODIGO) on delete restrict on update restrict;

