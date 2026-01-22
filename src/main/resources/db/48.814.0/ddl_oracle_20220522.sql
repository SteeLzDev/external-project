-- @@delimiter=!

/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     19/05/2022 16:58:21                          */
/*==============================================================*/


drop index r_852_fk
!

drop index r_853_fk
!

drop index r_851_fk
!

drop index r_376_fk
!

drop index r_375_fk
!

drop index r_374_fk
!

drop index r_293_fk
!

drop index r_289_fk
!

drop index r_290_fk
!

drop index r_291_fk
!

drop index r_292_fk
!

drop index r_287_fk
!

drop index r_286_fk
!

drop index r_285_fk
!

drop index r_288_fk
!

drop index r_850_fk
!

drop index r_849_fk
!

drop index r_847_fk
!

drop index r_848_fk
!

drop index r_846_fk
!

drop index r_379_fk
!

drop index r_378_fk
!

drop index r_377_fk
!

drop index r_302_fk
!

drop index r_301_fk
!

drop index r_300_fk
!

drop index r_299_fk
!

drop index r_298_fk
!

drop index r_297_fk
!

drop index r_296_fk
!

drop index r_295_fk
!

drop index r_294_fk
!


CALL dropTableIfExists('tmp_tb_fato_contrato')
!
rename tb_fato_contrato to tmp_tb_fato_contrato
!

CALL dropTableIfExists('tmp_tb_fato_parcela')
!
rename tb_fato_parcela to tmp_tb_fato_parcela
!

create sequence s_dimensao_verba_convenio
!


/*==============================================================*/
/* Table: tb_dimensao_verba_convenio                            */
/*==============================================================*/
create table tb_dimensao_verba_convenio  (
   dve_codigo           integer                         not null,
   dve_descricao        varchar2(40)                    not null,
   constraint pk_tb_dimensao_verba_convenio primary key (dve_codigo)
)
!

insert into tb_dimensao_verba_convenio (DVE_CODIGO, DVE_DESCRICAO) values (0, 'N/D')
!

/*==============================================================*/
/* Table: tb_fato_contrato                                      */
/*==============================================================*/
create table tb_fato_contrato  (
   dss_codigo           smallint                        not null,
   dsx_codigo           smallint                        not null,
   dvc_codigo           smallint                        not null,
   dcr_codigo           smallint                        not null,
   dpr_codigo           smallint                        not null,
   dis_codigo           smallint                        not null,
   dte_codigo           integer                         not null,
   dca_codigo           smallint                        not null,
   dse_codigo           smallint                        not null,
   dve_codigo           integer                         not null,
   dlo_codigo           smallint                        not null,
   dsc_codigo           smallint                        not null,
   dpc_codigo           smallint                        not null,
   doc_codigo           smallint                        not null,
   dtc_codigo           smallint                        not null,
   dcs_codigo           smallint                        not null,
   fac_qtd              integer                         not null,
   fac_vlr_mes          number(13,2)                    not null,
   fac_vlr_devido       number(13,2)                    not null,
   fac_vlr_total        number(13,2)                    not null,
   fac_vlr_liberado     number(13,2)                    not null,
   fac_taxa_juros       number(13,8)                    not null
)
!

insert into tb_fato_contrato (dss_codigo, dsx_codigo, dvc_codigo, dcr_codigo, dpr_codigo, dis_codigo, dte_codigo, dca_codigo, dse_codigo, dve_codigo, dlo_codigo, dsc_codigo, dpc_codigo, doc_codigo, dtc_codigo, dcs_codigo, fac_qtd, fac_vlr_mes, fac_vlr_devido, fac_vlr_total, fac_vlr_liberado, fac_taxa_juros)
select dss_codigo, dsx_codigo, dvc_codigo, dcr_codigo, dpr_codigo, dis_codigo, dte_codigo, dca_codigo, dse_codigo, 0, dlo_codigo, dsc_codigo, dpc_codigo, doc_codigo, dtc_codigo, dcs_codigo, fac_qtd, fac_vlr_mes, fac_vlr_devido, fac_vlr_total, fac_vlr_liberado, fac_taxa_juros
from tmp_tb_fato_contrato
!

drop table tmp_tb_fato_contrato cascade constraints
!

/*==============================================================*/
/* Index: r_288_fk                                              */
/*==============================================================*/
create index r_288_fk on tb_fato_contrato (
   dte_codigo asc
)
!

/*==============================================================*/
/* Index: r_285_fk                                              */
/*==============================================================*/
create index r_285_fk on tb_fato_contrato (
   dca_codigo asc
)
!

/*==============================================================*/
/* Index: r_286_fk                                              */
/*==============================================================*/
create index r_286_fk on tb_fato_contrato (
   dse_codigo asc
)
!

/*==============================================================*/
/* Index: r_287_fk                                              */
/*==============================================================*/
create index r_287_fk on tb_fato_contrato (
   dlo_codigo asc
)
!

/*==============================================================*/
/* Index: r_292_fk                                              */
/*==============================================================*/
create index r_292_fk on tb_fato_contrato (
   dsc_codigo asc
)
!

/*==============================================================*/
/* Index: r_291_fk                                              */
/*==============================================================*/
create index r_291_fk on tb_fato_contrato (
   dpc_codigo asc
)
!

/*==============================================================*/
/* Index: r_290_fk                                              */
/*==============================================================*/
create index r_290_fk on tb_fato_contrato (
   dtc_codigo asc
)
!

/*==============================================================*/
/* Index: r_289_fk                                              */
/*==============================================================*/
create index r_289_fk on tb_fato_contrato (
   doc_codigo asc
)
!

/*==============================================================*/
/* Index: r_293_fk                                              */
/*==============================================================*/
create index r_293_fk on tb_fato_contrato (
   dcs_codigo asc
)
!

/*==============================================================*/
/* Index: r_374_fk                                              */
/*==============================================================*/
create index r_374_fk on tb_fato_contrato (
   dss_codigo asc
)
!

/*==============================================================*/
/* Index: r_375_fk                                              */
/*==============================================================*/
create index r_375_fk on tb_fato_contrato (
   dsx_codigo asc
)
!

/*==============================================================*/
/* Index: r_376_fk                                              */
/*==============================================================*/
create index r_376_fk on tb_fato_contrato (
   dis_codigo asc
)
!

/*==============================================================*/
/* Index: r_851_fk                                              */
/*==============================================================*/
create index r_851_fk on tb_fato_contrato (
   dvc_codigo asc
)
!

/*==============================================================*/
/* Index: r_853_fk                                              */
/*==============================================================*/
create index r_853_fk on tb_fato_contrato (
   dcr_codigo asc
)
!

/*==============================================================*/
/* Index: r_852_fk                                              */
/*==============================================================*/
create index r_852_fk on tb_fato_contrato (
   dpr_codigo asc
)
!

/*==============================================================*/
/* Index: r_867_fk                                              */
/*==============================================================*/
create index r_867_fk on tb_fato_contrato (
   dve_codigo asc
)
!

/*==============================================================*/
/* Table: tb_fato_parcela                                       */
/*==============================================================*/
create table tb_fato_parcela  (
   dpr_codigo           smallint                        not null,
   dvp_codigo           smallint                        not null,
   dte_codigo_parcela   integer                         not null,
   dte_codigo_contrato  integer                         not null,
   dis_codigo           smallint                        not null,
   dve_codigo           integer                         not null,
   dcr_codigo           smallint                        not null,
   dsx_codigo           smallint                        not null,
   dca_codigo           smallint                        not null,
   dsc_codigo           smallint                        not null,
   dss_codigo           smallint                        not null,
   dse_codigo           smallint                        not null,
   dlo_codigo           smallint                        not null,
   dpc_codigo           smallint                        not null,
   doc_codigo           smallint                        not null,
   dtc_codigo           smallint                        not null,
   dcs_codigo           smallint                        not null,
   dvc_codigo           smallint                        not null,
   fap_qtd              integer                         not null,
   fap_qtd_pago         integer                         not null,
   fap_qtd_rejeitado    integer                         not null,
   fap_vlr_previsto     number(13,2)                    not null,
   fap_vlr_realizado    number(13,2)                    not null
)
!

insert into tb_fato_parcela (dpr_codigo, dvp_codigo, dte_codigo_parcela, dte_codigo_contrato, dis_codigo, dve_codigo, dcr_codigo, dsx_codigo, dca_codigo, dsc_codigo, dss_codigo, dse_codigo, dlo_codigo, dpc_codigo, doc_codigo, dtc_codigo, dcs_codigo, dvc_codigo, fap_qtd, fap_qtd_pago, fap_qtd_rejeitado, fap_vlr_previsto, fap_vlr_realizado)
select dpr_codigo, dvp_codigo, dte_codigo_parcela, dte_codigo_contrato, dis_codigo, 0, dcr_codigo, dsx_codigo, dca_codigo, dsc_codigo, dss_codigo, dse_codigo, dlo_codigo, dpc_codigo, doc_codigo, dtc_codigo, dcs_codigo, dvc_codigo, fap_qtd, fap_qtd_pago, fap_qtd_rejeitado, fap_vlr_previsto, fap_vlr_realizado
from tmp_tb_fato_parcela
!

drop table tmp_tb_fato_parcela cascade constraints
!

/*==============================================================*/
/* Index: r_294_fk                                              */
/*==============================================================*/
create index r_294_fk on tb_fato_parcela (
   dca_codigo asc
)
!

/*==============================================================*/
/* Index: r_295_fk                                              */
/*==============================================================*/
create index r_295_fk on tb_fato_parcela (
   dse_codigo asc
)
!

/*==============================================================*/
/* Index: r_296_fk                                              */
/*==============================================================*/
create index r_296_fk on tb_fato_parcela (
   dlo_codigo asc
)
!

/*==============================================================*/
/* Index: r_297_fk                                              */
/*==============================================================*/
create index r_297_fk on tb_fato_parcela (
   dte_codigo_parcela asc
)
!

/*==============================================================*/
/* Index: r_298_fk                                              */
/*==============================================================*/
create index r_298_fk on tb_fato_parcela (
   doc_codigo asc
)
!

/*==============================================================*/
/* Index: r_299_fk                                              */
/*==============================================================*/
create index r_299_fk on tb_fato_parcela (
   dtc_codigo asc
)
!

/*==============================================================*/
/* Index: r_300_fk                                              */
/*==============================================================*/
create index r_300_fk on tb_fato_parcela (
   dpc_codigo asc
)
!

/*==============================================================*/
/* Index: r_301_fk                                              */
/*==============================================================*/
create index r_301_fk on tb_fato_parcela (
   dcs_codigo asc
)
!

/*==============================================================*/
/* Index: r_302_fk                                              */
/*==============================================================*/
create index r_302_fk on tb_fato_parcela (
   dte_codigo_contrato asc
)
!

/*==============================================================*/
/* Index: r_377_fk                                              */
/*==============================================================*/
create index r_377_fk on tb_fato_parcela (
   dss_codigo asc
)
!

/*==============================================================*/
/* Index: r_378_fk                                              */
/*==============================================================*/
create index r_378_fk on tb_fato_parcela (
   dis_codigo asc
)
!

/*==============================================================*/
/* Index: r_379_fk                                              */
/*==============================================================*/
create index r_379_fk on tb_fato_parcela (
   dsx_codigo asc
)
!

/*==============================================================*/
/* Index: r_846_fk                                              */
/*==============================================================*/
create index r_846_fk on tb_fato_parcela (
   dsc_codigo asc
)
!

/*==============================================================*/
/* Index: r_848_fk                                              */
/*==============================================================*/
create index r_848_fk on tb_fato_parcela (
   dvp_codigo asc
)
!

/*==============================================================*/
/* Index: r_847_fk                                              */
/*==============================================================*/
create index r_847_fk on tb_fato_parcela (
   dvc_codigo asc
)
!

/*==============================================================*/
/* Index: r_849_fk                                              */
/*==============================================================*/
create index r_849_fk on tb_fato_parcela (
   dcr_codigo asc
)
!

/*==============================================================*/
/* Index: r_850_fk                                              */
/*==============================================================*/
create index r_850_fk on tb_fato_parcela (
   dpr_codigo asc
)
!

/*==============================================================*/
/* Index: r_868_fk                                              */
/*==============================================================*/
create index r_868_fk on tb_fato_parcela (
   dve_codigo asc
)
!

-- TRIGGER PARA GERAR PREENCHER O DVE_CODIGO NA DIMENSAO DE VERBA
create trigger tib_tb_dimensao_verba_convenio before insert
on tb_dimensao_verba_convenio for each row
when (new.dve_codigo is null)
declare
    integrity_error  exception;
    errno            integer;
    errmsg           char(200);
    dummy            integer;
    found            boolean;

begin
    --  column dve_codigo uses sequence s_dimensao_verba_convenio
    select s_dimensao_verba_convenio.nextval into :new.dve_codigo from dual;

--  errors handling
exception
    when integrity_error then
       raise_application_error(errno, errmsg);
end;
!