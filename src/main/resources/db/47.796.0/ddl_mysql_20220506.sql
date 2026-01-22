/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     29/04/2022 16:35:56                          */
/*==============================================================*/


alter table tb_tipo_motivo_operacao
   add TMO_DECISAO_JUDICIAL char(1) not null default 'N';

