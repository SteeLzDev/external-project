/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     21/06/2023 16:31:53                          */
/*==============================================================*/

alter table tb_calendario_folha_cse
   drop column CFC_NUM_SEMANAS;

alter table tb_calendario_folha_cse
   drop column CFC_DIVISAO;

alter table tb_calendario_folha_est
   drop column CFE_NUM_SEMANAS;

alter table tb_calendario_folha_est
   drop column CFE_DIVISAO;

alter table tb_calendario_folha_org
   drop column CFO_NUM_SEMANAS;

alter table tb_calendario_folha_org
   drop column CFO_DIVISAO;

alter table tb_calendario_folha_cse
   add CFC_NUM_PERIODO smallint not null default 0;

alter table tb_calendario_folha_est
   add CFE_NUM_PERIODO smallint not null default 0;

alter table tb_calendario_folha_org
   add CFO_NUM_PERIODO smallint not null default 0;

-- Atualiza o número do período de acordo com o período
-- Mensal
update tb_calendario_folha_cse set CFC_NUM_PERIODO = month(CFC_PERIODO) where (select psi_vlr from tb_param_sist_consignante where tpc_codigo = '465') = 'M';
update tb_calendario_folha_est set CFE_NUM_PERIODO = month(CFE_PERIODO) where (select psi_vlr from tb_param_sist_consignante where tpc_codigo = '465') = 'M';
update tb_calendario_folha_org set CFO_NUM_PERIODO = month(CFO_PERIODO) where (select psi_vlr from tb_param_sist_consignante where tpc_codigo = '465') = 'M';
-- Quinzenal
update tb_calendario_folha_cse set CFC_NUM_PERIODO = month(CFC_PERIODO) * 2 + day(CFC_PERIODO) - 2 where (select psi_vlr from tb_param_sist_consignante where tpc_codigo = '465') = 'Q';
update tb_calendario_folha_est set CFE_NUM_PERIODO = month(CFE_PERIODO) * 2 + day(CFE_PERIODO) - 2 where (select psi_vlr from tb_param_sist_consignante where tpc_codigo = '465') = 'Q';
update tb_calendario_folha_org set CFO_NUM_PERIODO = month(CFO_PERIODO) * 2 + day(CFO_PERIODO) - 2 where (select psi_vlr from tb_param_sist_consignante where tpc_codigo = '465') = 'Q';
