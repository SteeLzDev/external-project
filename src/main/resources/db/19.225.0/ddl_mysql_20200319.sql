/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     19/03/2020 11:21:58                          */
/*==============================================================*/


alter table tb_status_autorizacao_desconto
   add SAD_EXIBE char(1) not null default 'S';

