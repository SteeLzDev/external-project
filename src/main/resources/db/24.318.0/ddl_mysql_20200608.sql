/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     08/06/2020 15:42:18                          */
/*==============================================================*/


alter table tb_calendario_folha_cse
   add CFC_DATA_INI_FISCAL datetime;

alter table tb_calendario_folha_cse
   add CFC_DATA_FIM_FISCAL datetime;

alter table tb_calendario_folha_est
   add CFE_DATA_INI_FISCAL datetime;

alter table tb_calendario_folha_est
   add CFE_DATA_FIM_FISCAL datetime;

alter table tb_calendario_folha_org
   add CFO_DATA_INI_FISCAL datetime;

alter table tb_calendario_folha_org
   add CFO_DATA_FIM_FISCAL datetime;

