/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     20/04/2023 16:49:17                          */
/*==============================================================*/


drop table if exists tmp_tb_fato_contrato;

rename table tb_fato_contrato to tmp_tb_fato_contrato;

drop table if exists tmp_tb_fato_margem;

rename table tb_fato_margem to tmp_tb_fato_margem;

drop table if exists tmp_tb_fato_parcela;

rename table tb_fato_parcela to tmp_tb_fato_parcela;

/*==============================================================*/
/* Table: tb_dimensao_lotacao_servidor                          */
/*==============================================================*/
create table tb_dimensao_lotacao_servidor
(
   DLS_CODIGO           smallint not null auto_increment,
   DLS_DESCRICAO        varchar(100) not null,
   primary key (DLS_CODIGO)
) ENGINE=InnoDB;

/*==============================================================*/
/* Table: tb_fato_contrato                                      */
/*==============================================================*/
create table tb_fato_contrato
(
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
   FAC_TAXA_JUROS       decimal(13,8) not null
) ENGINE=InnoDB;

insert into tb_fato_contrato (DSS_CODIGO, DSX_CODIGO, DVC_CODIGO, DCR_CODIGO, DLS_CODIGO, DPR_CODIGO, DIS_CODIGO, DTE_CODIGO, DCA_CODIGO, DSE_CODIGO, DVE_CODIGO, DLO_CODIGO, DSC_CODIGO, DPC_CODIGO, DOC_CODIGO, DTC_CODIGO, DCS_CODIGO, FAC_QTD, FAC_VLR_MES, FAC_VLR_DEVIDO, FAC_VLR_TOTAL, FAC_VLR_LIBERADO, FAC_TAXA_JUROS)
select DSS_CODIGO, DSX_CODIGO, DVC_CODIGO, DCR_CODIGO, 0, DPR_CODIGO, DIS_CODIGO, DTE_CODIGO, DCA_CODIGO, DSE_CODIGO, DVE_CODIGO, DLO_CODIGO, DSC_CODIGO, DPC_CODIGO, DOC_CODIGO, DTC_CODIGO, DCS_CODIGO, FAC_QTD, FAC_VLR_MES, FAC_VLR_DEVIDO, FAC_VLR_TOTAL, FAC_VLR_LIBERADO, FAC_TAXA_JUROS
from tmp_tb_fato_contrato;

drop table if exists tmp_tb_fato_contrato;

/*==============================================================*/
/* Table: tb_fato_margem                                        */
/*==============================================================*/
create table tb_fato_margem
(
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
   FAM_VLR_UTILIZADO    decimal(13,2) not null
) ENGINE=InnoDB;

insert into tb_fato_margem (DLS_CODIGO, DSX_CODIGO, DIS_CODIGO, DCR_CODIGO, DPR_CODIGO, DTE_CODIGO, DCO_CODIGO, DLO_CODIGO, DCS_CODIGO, DTM_CODIGO, FAM_QTD, FAM_VLR_TOTAL, FAM_VLR_UTILIZADO)
select 0, DSX_CODIGO, DIS_CODIGO, DCR_CODIGO, DPR_CODIGO, DTE_CODIGO, DCO_CODIGO, DLO_CODIGO, DCS_CODIGO, DTM_CODIGO, FAM_QTD, FAM_VLR_TOTAL, FAM_VLR_UTILIZADO
from tmp_tb_fato_margem;

drop table if exists tmp_tb_fato_margem;

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
   DLS_CODIGO           smallint not null,
   FAP_QTD              int not null,
   FAP_QTD_PAGO         int not null,
   FAP_QTD_REJEITADO    int not null,
   FAP_VLR_PREVISTO     decimal(13,2) not null,
   FAP_VLR_REALIZADO    decimal(13,2) not null
) ENGINE=InnoDB;

insert into tb_fato_parcela (DPR_CODIGO, DVP_CODIGO, DTE_CODIGO_PARCELA, DTE_CODIGO_CONTRATO, DIS_CODIGO, DVE_CODIGO, DCR_CODIGO, DSX_CODIGO, DCA_CODIGO, DSC_CODIGO, DSS_CODIGO, DSE_CODIGO, DLO_CODIGO, DPC_CODIGO, DOC_CODIGO, DTC_CODIGO, DCS_CODIGO, DVC_CODIGO, DLS_CODIGO, FAP_QTD, FAP_QTD_PAGO, FAP_QTD_REJEITADO, FAP_VLR_PREVISTO, FAP_VLR_REALIZADO)
select DPR_CODIGO, DVP_CODIGO, DTE_CODIGO_PARCELA, DTE_CODIGO_CONTRATO, DIS_CODIGO, DVE_CODIGO, DCR_CODIGO, DSX_CODIGO, DCA_CODIGO, DSC_CODIGO, DSS_CODIGO, DSE_CODIGO, DLO_CODIGO, DPC_CODIGO, DOC_CODIGO, DTC_CODIGO, DCS_CODIGO, DVC_CODIGO, 0, FAP_QTD, FAP_QTD_PAGO, FAP_QTD_REJEITADO, FAP_VLR_PREVISTO, FAP_VLR_REALIZADO
from tmp_tb_fato_parcela;

drop table if exists tmp_tb_fato_parcela;


/*==============================================================*/
/* Index: r_288_fk                                              */
/*==============================================================*/
create index r_288_fk on tb_fato_contrato (
dte_codigo asc
)
;

/*==============================================================*/
/* Index: r_285_fk                                              */
/*==============================================================*/
create index r_285_fk on tb_fato_contrato (
dca_codigo asc
)
;

/*==============================================================*/
/* Index: r_286_fk                                              */
/*==============================================================*/
create index r_286_fk on tb_fato_contrato (
dse_codigo asc
)
;

/*==============================================================*/
/* Index: r_287_fk                                              */
/*==============================================================*/
create index r_287_fk on tb_fato_contrato (
dlo_codigo asc
)
;

/*==============================================================*/
/* Index: r_292_fk                                              */
/*==============================================================*/
create index r_292_fk on tb_fato_contrato (
dsc_codigo asc
)
;

/*==============================================================*/
/* Index: r_291_fk                                              */
/*==============================================================*/
create index r_291_fk on tb_fato_contrato (
dpc_codigo asc
)
;

/*==============================================================*/
/* Index: r_290_fk                                              */
/*==============================================================*/
create index r_290_fk on tb_fato_contrato (
dtc_codigo asc
)
;

/*==============================================================*/
/* Index: r_289_fk                                              */
/*==============================================================*/
create index r_289_fk on tb_fato_contrato (
doc_codigo asc
)
;

/*==============================================================*/
/* Index: r_293_fk                                              */
/*==============================================================*/
create index r_293_fk on tb_fato_contrato (
dcs_codigo asc
)
;

/*==============================================================*/
/* Index: r_374_fk                                              */
/*==============================================================*/
create index r_374_fk on tb_fato_contrato (
dss_codigo asc
)
;

/*==============================================================*/
/* Index: r_375_fk                                              */
/*==============================================================*/
create index r_375_fk on tb_fato_contrato (
dsx_codigo asc
)
;

/*==============================================================*/
/* Index: r_376_fk                                              */
/*==============================================================*/
create index r_376_fk on tb_fato_contrato (
dis_codigo asc
)
;

/*==============================================================*/
/* Index: r_851_fk                                              */
/*==============================================================*/
create index r_851_fk on tb_fato_contrato (
dvc_codigo asc
)
;

/*==============================================================*/
/* Index: r_853_fk                                              */
/*==============================================================*/
create index r_853_fk on tb_fato_contrato (
dcr_codigo asc
)
;

/*==============================================================*/
/* Index: r_852_fk                                              */
/*==============================================================*/
create index r_852_fk on tb_fato_contrato (
dpr_codigo asc
)
;

/*==============================================================*/
/* Index: r_867_fk                                              */
/*==============================================================*/
create index r_867_fk on tb_fato_contrato (
dve_codigo asc
)
;

/*==============================================================*/
/* Index: r_908_fk                                              */
/*==============================================================*/
create index r_908_fk on tb_fato_contrato (
dls_codigo asc
)
;

/*==============================================================*/
/* Index: r_303_fk                                              */
/*==============================================================*/
create index r_303_fk on tb_fato_margem (
dlo_codigo asc
)
;

/*==============================================================*/
/* Index: r_304_fk                                              */
/*==============================================================*/
create index r_304_fk on tb_fato_margem (
dcs_codigo asc
)
;

/*==============================================================*/
/* Index: r_305_fk                                              */
/*==============================================================*/
create index r_305_fk on tb_fato_margem (
dte_codigo asc
)
;

/*==============================================================*/
/* Index: r_306_fk                                              */
/*==============================================================*/
create index r_306_fk on tb_fato_margem (
dtm_codigo asc
)
;

/*==============================================================*/
/* Index: r_307_fk                                              */
/*==============================================================*/
create index r_307_fk on tb_fato_margem (
dco_codigo asc
)
;

/*==============================================================*/
/* Index: r_380_fk                                              */
/*==============================================================*/
create index r_380_fk on tb_fato_margem (
dsx_codigo asc
)
;

/*==============================================================*/
/* Index: r_381_fk                                              */
/*==============================================================*/
create index r_381_fk on tb_fato_margem (
dis_codigo asc
)
;

/*==============================================================*/
/* Index: r_854_fk                                              */
/*==============================================================*/
create index r_854_fk on tb_fato_margem (
dcr_codigo asc
)
;

/*==============================================================*/
/* Index: r_855_fk                                              */
/*==============================================================*/
create index r_855_fk on tb_fato_margem (
dpr_codigo asc
)
;

/*==============================================================*/
/* Index: r_909_fk                                              */
/*==============================================================*/
create index r_909_fk on tb_fato_margem (
dls_codigo asc
)
;

/*==============================================================*/
/* Index: r_294_fk                                              */
/*==============================================================*/
create index r_294_fk on tb_fato_parcela (
dca_codigo asc
)
;

/*==============================================================*/
/* Index: r_295_fk                                              */
/*==============================================================*/
create index r_295_fk on tb_fato_parcela (
dse_codigo asc
)
;

/*==============================================================*/
/* Index: r_296_fk                                              */
/*==============================================================*/
create index r_296_fk on tb_fato_parcela (
dlo_codigo asc
)
;

/*==============================================================*/
/* Index: r_297_fk                                              */
/*==============================================================*/
create index r_297_fk on tb_fato_parcela (
dte_codigo_parcela asc
)
;

/*==============================================================*/
/* Index: r_298_fk                                              */
/*==============================================================*/
create index r_298_fk on tb_fato_parcela (
doc_codigo asc
)
;

/*==============================================================*/
/* Index: r_299_fk                                              */
/*==============================================================*/
create index r_299_fk on tb_fato_parcela (
dtc_codigo asc
)
;

/*==============================================================*/
/* Index: r_300_fk                                              */
/*==============================================================*/
create index r_300_fk on tb_fato_parcela (
dpc_codigo asc
)
;

/*==============================================================*/
/* Index: r_301_fk                                              */
/*==============================================================*/
create index r_301_fk on tb_fato_parcela (
dcs_codigo asc
)
;

/*==============================================================*/
/* Index: r_302_fk                                              */
/*==============================================================*/
create index r_302_fk on tb_fato_parcela (
dte_codigo_contrato asc
)
;

/*==============================================================*/
/* Index: r_377_fk                                              */
/*==============================================================*/
create index r_377_fk on tb_fato_parcela (
dss_codigo asc
)
;

/*==============================================================*/
/* Index: r_378_fk                                              */
/*==============================================================*/
create index r_378_fk on tb_fato_parcela (
dis_codigo asc
)
;

/*==============================================================*/
/* Index: r_379_fk                                              */
/*==============================================================*/
create index r_379_fk on tb_fato_parcela (
dsx_codigo asc
)
;

/*==============================================================*/
/* Index: r_846_fk                                              */
/*==============================================================*/
create index r_846_fk on tb_fato_parcela (
dsc_codigo asc
)
;

/*==============================================================*/
/* Index: r_848_fk                                              */
/*==============================================================*/
create index r_848_fk on tb_fato_parcela (
dvp_codigo asc
)
;

/*==============================================================*/
/* Index: r_847_fk                                              */
/*==============================================================*/
create index r_847_fk on tb_fato_parcela (
dvc_codigo asc
)
;

/*==============================================================*/
/* Index: r_849_fk                                              */
/*==============================================================*/
create index r_849_fk on tb_fato_parcela (
dcr_codigo asc
)
;

/*==============================================================*/
/* Index: r_850_fk                                              */
/*==============================================================*/
create index r_850_fk on tb_fato_parcela (
dpr_codigo asc
)
;

/*==============================================================*/
/* Index: r_868_fk                                              */
/*==============================================================*/
create index r_868_fk on tb_fato_parcela (
dve_codigo asc
)
;

/*==============================================================*/
/* Index: r_910_fk                                              */
/*==============================================================*/
create index r_910_fk on tb_fato_parcela (
dls_codigo asc
)
;
