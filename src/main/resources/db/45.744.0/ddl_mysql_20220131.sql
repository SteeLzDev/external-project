/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     31/01/2022 16:48:44                          */
/*==============================================================*/


alter table ht_aut_desconto
   add ADE_DATA_NOTIFICACAO_CSE datetime;

alter table ht_aut_desconto
   add ADE_DATA_LIBERACAO_VALOR datetime;

alter table tb_aut_desconto
   add ADE_DATA_NOTIFICACAO_CSE datetime;

alter table tb_aut_desconto
   add ADE_DATA_LIBERACAO_VALOR datetime;

