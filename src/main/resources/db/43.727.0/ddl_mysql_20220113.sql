/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     13/01/2022 11:58:39                          */
/*==============================================================*/


drop table if exists tmp_tb_fato_contrato;

rename table tb_fato_contrato to tmp_tb_fato_contrato;

drop table if exists tmp_tb_fato_margem;

rename table tb_fato_margem to tmp_tb_fato_margem;

drop table if exists tmp_tb_fato_parcela;

rename table tb_fato_parcela to tmp_tb_fato_parcela;

/*==============================================================*/
/* Table: tb_dimensao_cargo_servidor                            */
/*==============================================================*/
create table tb_dimensao_cargo_servidor
(
   DCR_CODIGO           smallint not null auto_increment,
   DCR_IDENTIFICADOR    varchar(40) not null,
   DCR_DESCRICAO        varchar(100) not null,
   primary key (DCR_CODIGO)
);

/*==============================================================*/
/* Table: tb_dimensao_posto_servidor                            */
/*==============================================================*/
create table tb_dimensao_posto_servidor
(
   DPR_CODIGO           smallint not null auto_increment,
   DPR_IDENTIFICADOR    varchar(40) not null,
   DPR_DESCRICAO        varchar(100) not null,
   primary key (DPR_CODIGO)
);

/*==============================================================*/
/* Table: tb_dimensao_valor_contrato                            */
/*==============================================================*/
create table tb_dimensao_valor_contrato
(
   DVC_CODIGO           smallint not null,
   DVC_FAIXA            varchar(20) not null,
   DVC_VLR_INI          decimal(13,2) not null,
   DVC_VLR_FIM          decimal(13,2) not null,
   primary key (DVC_CODIGO)
);

/*==============================================================*/
/* Table: tb_dimensao_valor_parcela                             */
/*==============================================================*/
create table tb_dimensao_valor_parcela
(
   DVP_CODIGO           smallint not null,
   DVP_FAIXA            varchar(20) not null,
   DVP_VLR_INI          decimal(13,2) not null,
   DVP_VLR_FIM          decimal(13,2) not null,
   primary key (DVP_CODIGO)
);

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
);

/*==============================================================*/
/* Table: tb_fato_margem                                        */
/*==============================================================*/
create table tb_fato_margem
(
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
   FAM_VLR_UTILIZADO    decimal(13,2) not null
);

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
);

insert into tb_fato_contrato (DSS_CODIGO, DSX_CODIGO, DVC_CODIGO, DCR_CODIGO, DPR_CODIGO, DIS_CODIGO, DTE_CODIGO, DCA_CODIGO, DSE_CODIGO, DLO_CODIGO, DSC_CODIGO, DPC_CODIGO, DOC_CODIGO, DTC_CODIGO, DCS_CODIGO, FAC_QTD, FAC_VLR_MES, FAC_VLR_DEVIDO, FAC_VLR_TOTAL, FAC_VLR_LIBERADO, FAC_TAXA_JUROS)
select DSS_CODIGO, DSX_CODIGO, 0, 0, 0, DIS_CODIGO, DTE_CODIGO, DCA_CODIGO, DSE_CODIGO, DLO_CODIGO, DSC_CODIGO, DPC_CODIGO, DOC_CODIGO, DTC_CODIGO, DCS_CODIGO, FAC_QTD, FAC_VLR_MES, FAC_VLR_DEVIDO, FAC_VLR_TOTAL, FAC_VLR_LIBERADO, FAC_TAXA_JUROS
from tmp_tb_fato_contrato;

insert into tb_fato_margem (DSX_CODIGO, DIS_CODIGO, DCR_CODIGO, DPR_CODIGO, DTE_CODIGO, DCO_CODIGO, DLO_CODIGO, DCS_CODIGO, DTM_CODIGO, FAM_QTD, FAM_VLR_TOTAL, FAM_VLR_UTILIZADO)
select DSX_CODIGO, DIS_CODIGO, 0, 0, DTE_CODIGO, DCO_CODIGO, DLO_CODIGO, DCS_CODIGO, DTM_CODIGO, FAM_QTD, FAM_VLR_TOTAL, FAM_VLR_UTILIZADO
from tmp_tb_fato_margem;

insert into tb_fato_parcela (DPR_CODIGO, DVP_CODIGO, DTE_CODIGO_PARCELA, DTE_CODIGO_CONTRATO, DIS_CODIGO, DCR_CODIGO, DSX_CODIGO, DCA_CODIGO, DSC_CODIGO, DSS_CODIGO, DSE_CODIGO, DLO_CODIGO, DPC_CODIGO, DOC_CODIGO, DTC_CODIGO, DCS_CODIGO, DVC_CODIGO, FAP_QTD, FAP_QTD_PAGO, FAP_QTD_REJEITADO, FAP_VLR_PREVISTO, FAP_VLR_REALIZADO)
select 0, 0, DTE_CODIGO_PARCELA, DTE_CODIGO_CONTRATO, DIS_CODIGO, 0, DSX_CODIGO, DCA_CODIGO, 0, DSS_CODIGO, DSE_CODIGO, DLO_CODIGO, DPC_CODIGO, DOC_CODIGO, DTC_CODIGO, DCS_CODIGO, 0, FAP_QTD, FAP_QTD_PAGO, FAP_QTD_REJEITADO, FAP_VLR_PREVISTO, FAP_VLR_REALIZADO
from tmp_tb_fato_parcela;

drop table if exists tmp_tb_fato_contrato;
drop table if exists tmp_tb_fato_margem;
drop table if exists tmp_tb_fato_parcela;

/*==============================================================*/
/* Index: R_303_FK                                              */
/*==============================================================*/
create index R_303_FK on tb_fato_margem (
   dlo_codigo asc
);

/*==============================================================*/
/* Index: R_304_FK                                              */
/*==============================================================*/
create index R_304_FK on tb_fato_margem (
   dcs_codigo asc
);

/*==============================================================*/
/* Index: R_305_FK                                              */
/*==============================================================*/
create index R_305_FK on tb_fato_margem (
   dte_codigo asc
);

/*==============================================================*/
/* Index: R_306_FK                                              */
/*==============================================================*/
create index R_306_FK on tb_fato_margem (
   dtm_codigo asc
);

/*==============================================================*/
/* Index: R_307_FK                                              */
/*==============================================================*/
create index R_307_FK on tb_fato_margem (
   dco_codigo asc
);

/*==============================================================*/
/* Index: R_380_FK                                              */
/*==============================================================*/
create index R_380_FK on tb_fato_margem (
   dsx_codigo asc
);

/*==============================================================*/
/* Index: R_381_FK                                              */
/*==============================================================*/
create index R_381_FK on tb_fato_margem (
   dis_codigo asc
);

/*==============================================================*/
/* Index: R_854_FK                                              */
/*==============================================================*/
create index R_854_FK on tb_fato_margem (
   dcr_codigo asc
);

/*==============================================================*/
/* Index: R_855_FK                                              */
/*==============================================================*/
create index R_855_FK on tb_fato_margem (
   dpr_codigo asc
);

/*==============================================================*/
/* Index: R_288_FK                                              */
/*==============================================================*/
create index R_288_FK on tb_fato_contrato (
   dte_codigo asc
);

/*==============================================================*/
/* Index: R_285_FK                                              */
/*==============================================================*/
create index R_285_FK on tb_fato_contrato (
   dca_codigo asc
);

/*==============================================================*/
/* Index: R_286_FK                                              */
/*==============================================================*/
create index R_286_FK on tb_fato_contrato (
   dse_codigo asc
);

/*==============================================================*/
/* Index: R_287_FK                                              */
/*==============================================================*/
create index R_287_FK on tb_fato_contrato (
   dlo_codigo asc
);

/*==============================================================*/
/* Index: R_292_FK                                              */
/*==============================================================*/
create index R_292_FK on tb_fato_contrato (
   dsc_codigo asc
);

/*==============================================================*/
/* Index: R_291_FK                                              */
/*==============================================================*/
create index R_291_FK on tb_fato_contrato (
   dpc_codigo asc
);

/*==============================================================*/
/* Index: R_290_FK                                              */
/*==============================================================*/
create index R_290_FK on tb_fato_contrato (
   dtc_codigo asc
);

/*==============================================================*/
/* Index: R_289_FK                                              */
/*==============================================================*/
create index R_289_FK on tb_fato_contrato (
   doc_codigo asc
);

/*==============================================================*/
/* Index: R_293_FK                                              */
/*==============================================================*/
create index R_293_FK on tb_fato_contrato (
   dcs_codigo asc
);

/*==============================================================*/
/* Index: R_374_FK                                              */
/*==============================================================*/
create index R_374_FK on tb_fato_contrato (
   dss_codigo asc
);

/*==============================================================*/
/* Index: R_375_FK                                              */
/*==============================================================*/
create index R_375_FK on tb_fato_contrato (
   dsx_codigo asc
);

/*==============================================================*/
/* Index: R_376_FK                                              */
/*==============================================================*/
create index R_376_FK on tb_fato_contrato (
   dis_codigo asc
);

/*==============================================================*/
/* Index: R_851_FK                                              */
/*==============================================================*/
create index R_851_FK on tb_fato_contrato (
   dvc_codigo asc
);

/*==============================================================*/
/* Index: R_853_FK                                              */
/*==============================================================*/
create index R_853_FK on tb_fato_contrato (
   dcr_codigo asc
);

/*==============================================================*/
/* Index: R_852_FK                                              */
/*==============================================================*/
create index R_852_FK on tb_fato_contrato (
   dpr_codigo asc
);

/*==============================================================*/
/* Index: R_294_FK                                              */
/*==============================================================*/
create index R_294_FK on tb_fato_parcela (
   dca_codigo
);

/*==============================================================*/
/* Index: R_295_FK                                              */
/*==============================================================*/
create index R_295_FK on tb_fato_parcela (
   dse_codigo
);

/*==============================================================*/
/* Index: R_296_FK                                              */
/*==============================================================*/
create index R_296_FK on tb_fato_parcela (
   dlo_codigo
);

/*==============================================================*/
/* Index: R_297_FK                                              */
/*==============================================================*/
create index R_297_FK on tb_fato_parcela (
   dte_codigo_parcela
);

/*==============================================================*/
/* Index: R_298_FK                                              */
/*==============================================================*/
create index R_298_FK on tb_fato_parcela (
   doc_codigo
);

/*==============================================================*/
/* Index: R_299_FK                                              */
/*==============================================================*/
create index R_299_FK on tb_fato_parcela (
   dtc_codigo
);

/*==============================================================*/
/* Index: R_300_FK                                              */
/*==============================================================*/
create index R_300_FK on tb_fato_parcela (
   dpc_codigo
);

/*==============================================================*/
/* Index: R_301_FK                                              */
/*==============================================================*/
create index R_301_FK on tb_fato_parcela (
   dcs_codigo
);

/*==============================================================*/
/* Index: R_302_FK                                              */
/*==============================================================*/
create index R_302_FK on tb_fato_parcela (
   dte_codigo_contrato
);

/*==============================================================*/
/* Index: R_377_FK                                              */
/*==============================================================*/
create index R_377_FK on tb_fato_parcela (
   dss_codigo
);

/*==============================================================*/
/* Index: R_378_FK                                              */
/*==============================================================*/
create index R_378_FK on tb_fato_parcela (
   dis_codigo
);

/*==============================================================*/
/* Index: R_379_FK                                              */
/*==============================================================*/
create index R_379_FK on tb_fato_parcela (
   dsx_codigo
);

/*==============================================================*/
/* Index: R_846_FK                                              */
/*==============================================================*/
create index R_846_FK on tb_fato_parcela (
   dsc_codigo
);

/*==============================================================*/
/* Index: R_848_FK                                              */
/*==============================================================*/
create index R_848_FK on tb_fato_parcela (
   dvp_codigo
);

/*==============================================================*/
/* Index: R_847_FK                                              */
/*==============================================================*/
create index R_847_FK on tb_fato_parcela (
   dvc_codigo
);

/*==============================================================*/
/* Index: R_849_FK                                              */
/*==============================================================*/
create index R_849_FK on tb_fato_parcela (
   dcr_codigo
);

/*==============================================================*/
/* Index: R_850_FK                                              */
/*==============================================================*/
create index R_850_FK on tb_fato_parcela (
   dpr_codigo
);

