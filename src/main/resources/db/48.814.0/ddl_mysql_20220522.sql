/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     19/05/2022 16:51:22                          */
/*==============================================================*/


drop table if exists tmp_tb_fato_contrato;

rename table tb_fato_contrato to tmp_tb_fato_contrato;

drop table if exists tmp_tb_fato_parcela;

rename table tb_fato_parcela to tmp_tb_fato_parcela;

/*==============================================================*/
/* Table: tb_dimensao_verba_convenio                            */
/*==============================================================*/
create table tb_dimensao_verba_convenio
(
   DVE_CODIGO           int not null auto_increment,
   DVE_DESCRICAO        varchar(40) not null,
   primary key (DVE_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

insert into tb_dimensao_verba_convenio (DVE_CODIGO, DVE_DESCRICAO) values (0, 'N/D');

/*==============================================================*/
/* Table: tb_fato_contrato                                      */
/*==============================================================*/
create table tb_fato_contrato
(
   DSS_CODIGO           smallint not null,
   DSX_CODIGO           smallint not null,
   DVC_CODIGO           smallint not null,
   DCR_CODIGO           smallint not null,
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
   FAC_TAXA_JUROS       decimal(13,8) not null
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

insert into tb_fato_contrato (DSS_CODIGO, DSX_CODIGO, DVC_CODIGO, DCR_CODIGO, DPR_CODIGO, DIS_CODIGO, DTE_CODIGO, DCA_CODIGO, DSE_CODIGO, DVE_CODIGO, DLO_CODIGO, DSC_CODIGO, DPC_CODIGO, DOC_CODIGO, DTC_CODIGO, DCS_CODIGO, FAC_QTD, FAC_VLR_MES, FAC_VLR_DEVIDO, FAC_VLR_TOTAL, FAC_VLR_LIBERADO, FAC_TAXA_JUROS)
select DSS_CODIGO, DSX_CODIGO, DVC_CODIGO, DCR_CODIGO, DPR_CODIGO, DIS_CODIGO, DTE_CODIGO, DCA_CODIGO, DSE_CODIGO, 0, DLO_CODIGO, DSC_CODIGO, DPC_CODIGO, DOC_CODIGO, DTC_CODIGO, DCS_CODIGO, FAC_QTD, FAC_VLR_MES, FAC_VLR_DEVIDO, FAC_VLR_TOTAL, FAC_VLR_LIBERADO, FAC_TAXA_JUROS
from tmp_tb_fato_contrato;

drop table if exists tmp_tb_fato_contrato;

/*==============================================================*/
/* Table: tb_fato_parcela                                       */
/*==============================================================*/
create table tb_fato_parcela
(
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
   FAP_QTD              int not null,
   FAP_QTD_PAGO         int not null,
   FAP_QTD_REJEITADO    int not null,
   FAP_VLR_PREVISTO     decimal(13,2) not null,
   FAP_VLR_REALIZADO    decimal(13,2) not null
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

insert into tb_fato_parcela (DPR_CODIGO, DVP_CODIGO, DTE_CODIGO_PARCELA, DTE_CODIGO_CONTRATO, DIS_CODIGO, DVE_CODIGO, DCR_CODIGO, DSX_CODIGO, DCA_CODIGO, DSC_CODIGO, DSS_CODIGO, DSE_CODIGO, DLO_CODIGO, DPC_CODIGO, DOC_CODIGO, DTC_CODIGO, DCS_CODIGO, DVC_CODIGO, FAP_QTD, FAP_QTD_PAGO, FAP_QTD_REJEITADO, FAP_VLR_PREVISTO, FAP_VLR_REALIZADO)
select DPR_CODIGO, DVP_CODIGO, DTE_CODIGO_PARCELA, DTE_CODIGO_CONTRATO, DIS_CODIGO, 0, DCR_CODIGO, DSX_CODIGO, DCA_CODIGO, DSC_CODIGO, DSS_CODIGO, DSE_CODIGO, DLO_CODIGO, DPC_CODIGO, DOC_CODIGO, DTC_CODIGO, DCS_CODIGO, DVC_CODIGO, FAP_QTD, FAP_QTD_PAGO, FAP_QTD_REJEITADO, FAP_VLR_PREVISTO, FAP_VLR_REALIZADO
from tmp_tb_fato_parcela;

drop table if exists tmp_tb_fato_parcela;
