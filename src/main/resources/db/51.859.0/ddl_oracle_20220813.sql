/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     03/08/2022 08:48:01                          */
/*==============================================================*/


drop index idx_cod_verba_folha;

alter table tb_convenio 
   drop column cnv_cod_verba_folha;

