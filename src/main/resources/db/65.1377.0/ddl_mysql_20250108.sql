/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     19/12/2024 20:40:48                          */
/*==============================================================*/


drop table if exists tmp_tb_fato_contrato;

rename table tb_fato_contrato to tmp_tb_fato_contrato;

drop table if exists tmp_tb_fato_margem;

rename table tb_fato_margem to tmp_tb_fato_margem;

drop table if exists tmp_tb_fato_parcela;

rename table tb_fato_parcela to tmp_tb_fato_parcela;

/*==============================================================*/
/* Table: tb_dados_fato_contrato                                */
/*==============================================================*/
create table tb_dados_fato_contrato
(
   FAC_CODIGO           int not null,
   DFC_NOME             varchar(255) not null,
   DFC_CPF              char(19) not null,
   DFC_MATRICULA        varchar(20) not null,
   DFC_NUMERO           bigint not null,
   DFC_IDENTIFICADOR    int not null,
   DFC_INDICE           int,
   DFC_VALOR            decimal(13,2) not null,
   DFC_VALOR_LIBERADO   decimal(13,2),
   DFC_PRAZO            int,
   DFC_PAGAS            int,
   DFC_TAXA             decimal(13,10),
   DFC_DATA             datetime not null,
   DFC_DATA_INI         date not null,
   DFC_DATA_FIM         date,
   DFC_STATUS           varchar(32) not null
) ENGINE = InnoDB;

/*==============================================================*/
/* Table: tb_dados_fato_margem                                  */
/*==============================================================*/
create table tb_dados_fato_margem
(
   FAM_CODIGO           int not null,
   DFM_NOME             varchar(255) not null,
   DFM_CPF              char(19) not null,
   DFM_MATRICULA        varchar(20) not null,
   DFM_MARGEM           decimal(13,2) not null,
   DFM_MARGEM_REST      decimal(13,2) not null,
   DFM_MARGEM_USADA     decimal(13,2) not null
) ENGINE = InnoDB;

/*==============================================================*/
/* Table: tb_dados_fato_parcela                                 */
/*==============================================================*/
create table tb_dados_fato_parcela
(
   FAP_CODIGO           int not null,
   DFP_NOME             varchar(255) not null,
   DFP_CPF              char(19) not null,
   DFP_MATRICULA        varchar(20) not null,
   DFP_NUMERO           bigint not null,
   DFP_IDENTIFICADOR    varchar(40) not null,
   DFP_INDICE           varchar(32),
   DFP_VALOR            decimal(13,2) not null,
   DFP_VALOR_LIBERADO   decimal(13,2),
   DFP_PRAZO            int,
   DFP_PAGAS            int,
   DFP_NUMERO_PARCELA   smallint,
   DFP_PERIODO          date not null,
   DFP_VALOR_PREVISTO   decimal(13,2) not null,
   DFP_VALOR_REALIZADO  decimal(13,2),
   DFP_DATA_REALIZADO   int,
   DFP_STATUS_PARCELA   varchar(32) not null
) ENGINE = InnoDB;

/*==============================================================*/
/* Table: tb_fato_contrato                                      */
/*==============================================================*/
create table tb_fato_contrato
(
   FAC_CODIGO           int not null auto_increment,
   DSS_CODIGO           smallint not null,
   DSX_CODIGO           smallint not null,
   DVC_CODIGO           smallint not null,
   DCR_CODIGO           smallint not null,
   DLS_CODIGO           smallint not null,
   DPR_CODIGO           smallint not null,
   DIS_CODIGO           smallint not null,
   DTE_CODIGO           int not null,
   DCA_CODIGO           smallint not null,
   DSE_CODIGO           smallint not null,
   DVE_CODIGO           int not null,
   DLO_CODIGO           smallint not null,
   DSC_CODIGO           smallint not null,
   DPC_CODIGO           smallint not null,
   DOC_CODIGO           smallint not null,
   DTC_CODIGO           smallint not null,
   DCS_CODIGO           smallint not null,
   FAC_QTD              int not null,
   FAC_VLR_MES          decimal(13,2) not null,
   FAC_VLR_DEVIDO       decimal(13,2) not null,
   FAC_VLR_TOTAL        decimal(13,2) not null,
   FAC_VLR_LIBERADO     decimal(13,2) not null,
   FAC_TAXA_JUROS       decimal(13,8) not null,
   primary key (FAC_CODIGO)
) ENGINE = InnoDB;

insert into tb_fato_contrato (DSS_CODIGO, DSX_CODIGO, DVC_CODIGO, DCR_CODIGO, DLS_CODIGO, DPR_CODIGO, DIS_CODIGO, DTE_CODIGO, DCA_CODIGO, DSE_CODIGO, DVE_CODIGO, DLO_CODIGO, DSC_CODIGO, DPC_CODIGO, DOC_CODIGO, DTC_CODIGO, DCS_CODIGO, FAC_QTD, FAC_VLR_MES, FAC_VLR_DEVIDO, FAC_VLR_TOTAL, FAC_VLR_LIBERADO, FAC_TAXA_JUROS)
select DSS_CODIGO, DSX_CODIGO, DVC_CODIGO, DCR_CODIGO, DLS_CODIGO, DPR_CODIGO, DIS_CODIGO, DTE_CODIGO, DCA_CODIGO, DSE_CODIGO, DVE_CODIGO, DLO_CODIGO, DSC_CODIGO, DPC_CODIGO, DOC_CODIGO, DTC_CODIGO, DCS_CODIGO, FAC_QTD, FAC_VLR_MES, FAC_VLR_DEVIDO, FAC_VLR_TOTAL, FAC_VLR_LIBERADO, FAC_TAXA_JUROS
from tmp_tb_fato_contrato;

drop table if exists tmp_tb_fato_contrato;

/*==============================================================*/
/* Table: tb_fato_margem                                        */
/*==============================================================*/
create table tb_fato_margem
(
   FAM_CODIGO           int not null auto_increment,
   DLS_CODIGO           smallint not null,
   DSX_CODIGO           smallint not null,
   DIS_CODIGO           smallint not null,
   DCR_CODIGO           smallint not null,
   DPR_CODIGO           smallint not null,
   DTE_CODIGO           int not null,
   DCO_CODIGO           smallint not null,
   DLO_CODIGO           smallint not null,
   DCS_CODIGO           smallint not null,
   DTM_CODIGO           smallint not null,
   FAM_QTD              int not null,
   FAM_VLR_TOTAL        decimal(13,2) not null,
   FAM_VLR_UTILIZADO    decimal(13,2) not null,
   primary key (FAM_CODIGO)
) ENGINE = InnoDB;

insert into tb_fato_margem (DLS_CODIGO, DSX_CODIGO, DIS_CODIGO, DCR_CODIGO, DPR_CODIGO, DTE_CODIGO, DCO_CODIGO, DLO_CODIGO, DCS_CODIGO, DTM_CODIGO, FAM_QTD, FAM_VLR_TOTAL, FAM_VLR_UTILIZADO)
select DLS_CODIGO, DSX_CODIGO, DIS_CODIGO, DCR_CODIGO, DPR_CODIGO, DTE_CODIGO, DCO_CODIGO, DLO_CODIGO, DCS_CODIGO, DTM_CODIGO, FAM_QTD, FAM_VLR_TOTAL, FAM_VLR_UTILIZADO
from tmp_tb_fato_margem;

drop table if exists tmp_tb_fato_margem;

/*==============================================================*/
/* Table: tb_fato_parcela                                       */
/*==============================================================*/
create table tb_fato_parcela
(
   FAP_CODIGO           int not null auto_increment,
   DPR_CODIGO           smallint not null,
   DVP_CODIGO           smallint not null,
   DTE_CODIGO_PARCELA   int not null,
   DTE_CODIGO_CONTRATO  int not null,
   DIS_CODIGO           smallint not null,
   DVE_CODIGO           int not null,
   DCR_CODIGO           smallint not null,
   DSX_CODIGO           smallint not null,
   DCA_CODIGO           smallint not null,
   DSC_CODIGO           smallint not null,
   DSS_CODIGO           smallint not null,
   DSE_CODIGO           smallint not null,
   DLO_CODIGO           smallint not null,
   DPC_CODIGO           smallint not null,
   DOC_CODIGO           smallint not null,
   DTC_CODIGO           smallint not null,
   DCS_CODIGO           smallint not null,
   DVC_CODIGO           smallint not null,
   DLS_CODIGO           smallint not null,
   FAP_QTD              int not null,
   FAP_QTD_PAGO         int not null,
   FAP_QTD_REJEITADO    int not null,
   FAP_VLR_PREVISTO     decimal(13,2) not null,
   FAP_VLR_REALIZADO    decimal(13,2) not null,
   primary key (FAP_CODIGO)
) ENGINE = InnoDB;

insert into tb_fato_parcela (DPR_CODIGO, DVP_CODIGO, DTE_CODIGO_PARCELA, DTE_CODIGO_CONTRATO, DIS_CODIGO, DVE_CODIGO, DCR_CODIGO, DSX_CODIGO, DCA_CODIGO, DSC_CODIGO, DSS_CODIGO, DSE_CODIGO, DLO_CODIGO, DPC_CODIGO, DOC_CODIGO, DTC_CODIGO, DCS_CODIGO, DVC_CODIGO, DLS_CODIGO, FAP_QTD, FAP_QTD_PAGO, FAP_QTD_REJEITADO, FAP_VLR_PREVISTO, FAP_VLR_REALIZADO)
select DPR_CODIGO, DVP_CODIGO, DTE_CODIGO_PARCELA, DTE_CODIGO_CONTRATO, DIS_CODIGO, DVE_CODIGO, DCR_CODIGO, DSX_CODIGO, DCA_CODIGO, DSC_CODIGO, DSS_CODIGO, DSE_CODIGO, DLO_CODIGO, DPC_CODIGO, DOC_CODIGO, DTC_CODIGO, DCS_CODIGO, DVC_CODIGO, DLS_CODIGO, FAP_QTD, FAP_QTD_PAGO, FAP_QTD_REJEITADO, FAP_VLR_PREVISTO, FAP_VLR_REALIZADO
from tmp_tb_fato_parcela;

drop table if exists tmp_tb_fato_parcela;

alter table tb_dados_fato_contrato add constraint FK_R_975 foreign key (FAC_CODIGO)
      references tb_fato_contrato (FAC_CODIGO) on delete restrict on update restrict;

alter table tb_dados_fato_margem add constraint FK_R_974 foreign key (FAM_CODIGO)
      references tb_fato_margem (FAM_CODIGO) on delete restrict on update restrict;

alter table tb_dados_fato_parcela add constraint FK_R_976 foreign key (FAP_CODIGO)
      references tb_fato_parcela (FAP_CODIGO) on delete restrict on update restrict;

