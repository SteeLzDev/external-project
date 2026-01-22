/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     29/04/2022 16:41:15                          */
/*==============================================================*/


alter table tb_tipo_motivo_operacao add tmo_decisao_judicial char(1) default 'N' not null;

