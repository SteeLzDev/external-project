/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     21/06/2023 16:47:16                          */
/*==============================================================*/

alter table tb_calendario_folha_cse 
   drop column cfc_num_semanas;

alter table tb_calendario_folha_cse 
   drop column cfc_divisao;

alter table tb_calendario_folha_est 
   drop column cfe_num_semanas;

alter table tb_calendario_folha_est 
   drop column cfe_divisao;

alter table tb_calendario_folha_org 
   drop column cfo_num_semanas;

alter table tb_calendario_folha_org 
   drop column cfo_divisao;

alter table tb_calendario_folha_cse 
   add cfc_num_periodo smallint default 0 not null;

alter table tb_calendario_folha_est 
   add cfe_num_periodo smallint default 0 not null;

alter table tb_calendario_folha_org 
   add cfo_num_periodo smallint default 0 not null;

-- Atualiza o número do período de acordo com o período
-- Mensal
update tb_calendario_folha_cse set cfc_num_periodo = extract(month from cfc_periodo) where (select psi_vlr from tb_param_sist_consignante where tpc_codigo = '465') = 'M';
update tb_calendario_folha_est set cfe_num_periodo = extract(month from cfe_periodo) where (select psi_vlr from tb_param_sist_consignante where tpc_codigo = '465') = 'M';
update tb_calendario_folha_org set cfo_num_periodo = extract(month from cfo_periodo) where (select psi_vlr from tb_param_sist_consignante where tpc_codigo = '465') = 'M';
-- Quinzenal
update tb_calendario_folha_cse set cfc_num_periodo = extract(month from cfc_periodo) * 2 + extract(day from cfc_periodo) - 2 where (select psi_vlr from tb_param_sist_consignante where tpc_codigo = '465') = 'Q';
update tb_calendario_folha_est set cfe_num_periodo = extract(month from cfe_periodo) * 2 + extract(day from cfe_periodo) - 2 where (select psi_vlr from tb_param_sist_consignante where tpc_codigo = '465') = 'Q';
update tb_calendario_folha_org set cfo_num_periodo = extract(month from cfo_periodo) * 2 + extract(day from cfo_periodo) - 2 where (select psi_vlr from tb_param_sist_consignante where tpc_codigo = '465') = 'Q';

