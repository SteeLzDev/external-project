/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     07/12/2022 16:19:04                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_historico_exportacao                             */
/*==============================================================*/
create table tb_historico_exportacao  (
   hie_codigo           varchar2(32)                    not null,
   org_codigo           varchar2(32)                    not null,
   usu_codigo           varchar2(32)                    not null,
   hie_periodo          date                            not null,
   hie_data_ini         date                            not null,
   hie_data_fim         date                            not null,
   hie_data             date                            not null,
   constraint pk_tb_historico_exportacao primary key (hie_codigo)
);

/*==============================================================*/
/* Index: r_899_fk                                              */
/*==============================================================*/
create index r_899_fk on tb_historico_exportacao (
   org_codigo asc
);

/*==============================================================*/
/* Index: r_900_fk                                              */
/*==============================================================*/
create index r_900_fk on tb_historico_exportacao (
   usu_codigo asc
);

alter table tb_historico_exportacao
   add constraint fk_tb_histo_r_899_tb_orgao foreign key (org_codigo)
      references tb_orgao (org_codigo);

alter table tb_historico_exportacao
   add constraint fk_tb_histo_r_900_tb_usuar foreign key (usu_codigo)
      references tb_usuario (usu_codigo);

insert into tb_historico_exportacao (HIE_CODIGO, ORG_CODIGO, USU_CODIGO, HIE_PERIODO, HIE_DATA_INI, HIE_DATA_FIM, HIE_DATA)
select HI_CODIGO, ORG_CODIGO, USU_CODIGO, HI_PERIODO, HI_DATA_INI, HI_DATA_FIM, HI_DATA
from tb_historico_integracao
;

alter table tb_historico_integracao
   drop constraint fk_tb_histo_r_85_tb_orgao;

alter table tb_historico_integracao
   drop constraint fk_tb_histo_r_87_tb_usuar;

CALL dropTableIfExists('tb_historico_integracao');

