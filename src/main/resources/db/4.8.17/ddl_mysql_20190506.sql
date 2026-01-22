/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     22/11/2018 17:45:43                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_arquivo_previa_operadora                           */
/*==============================================================*/
create table tb_arquivo_previa_operadora
(
   APO_CODIGO           int not null auto_increment,
   CSA_CODIGO           varchar(32) not null,
   APO_NOME_ARQUIVO     varchar(100) not null,
   APO_OPERACAO         varchar(1) not null,
   APO_PERIODO_FATURAMENTO date not null,
   APO_DATA_INCLUSAO    date,
   APO_DATA_EXCLUSAO    date,
   CBE_NUMERO           varchar(40) not null,
   BEN_CODIGO_REGISTRO  varchar(40) not null,
   RSE_MATRICULA        varchar(20) not null,
   BEN_CODIGO_CONTRATO  varchar(40) not null,
   APO_VALOR_DEBITO     decimal(13,2) not null,
   APO_TIPO_LANCAMENTO  varchar(32) not null,
   APO_REAJUSTE_FAIXA_ETARIA varchar(1),
   APO_REAJUSTE_ANUAL   varchar(1),
   APO_NUMERO_LOTE      varchar(40),
   APO_ITEM_LOTE        varchar(40),
   APO_VALOR_SUBSIDIO   decimal(13,2),
   APO_VALOR_REALIZADO  decimal(13,2),
   APO_VALOR_NAO_REALIZADO decimal(13,2),
   APO_VALOR_TOTAL      decimal(13,2),
   APO_PERIODO_COBRANCA date,
   primary key (APO_CODIGO)
) ENGINE = InnoDB;

alter table tb_arquivo_previa_operadora add constraint FK_R_733 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;

