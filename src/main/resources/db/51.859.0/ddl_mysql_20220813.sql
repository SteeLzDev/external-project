/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     03/08/2022 08:40:11                          */
/*==============================================================*/


drop index IDX_COD_VERBA_FOLHA on tb_convenio;

alter table tb_convenio
   drop column CNV_COD_VERBA_FOLHA;

