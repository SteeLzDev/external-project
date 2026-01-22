/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     15/07/2020 16:03:52                          */
/*==============================================================*/


alter table tb_calendario_folha_cse
   add CFC_NUM_SEMANAS smallint;

alter table tb_calendario_folha_est
   add CFE_NUM_SEMANAS smallint;

alter table tb_calendario_folha_org
   add CFO_NUM_SEMANAS smallint;

