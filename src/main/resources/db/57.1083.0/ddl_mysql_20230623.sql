/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     23/06/2023 10:58:55                          */
/*==============================================================*/


alter table tb_periodo_exportacao
   drop column PEX_NUM_SEMANAS;

alter table tb_periodo_exportacao
   add PEX_NUM_PERIODO smallint not null default 0;

