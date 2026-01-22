/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     23/06/2023 11:12:23                          */
/*==============================================================*/


alter table tb_periodo_exportacao 
   drop column pex_num_semanas;

alter table tb_periodo_exportacao 
   add pex_num_periodo smallint default 0 not null;

