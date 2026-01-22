/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     28/06/2022 11:48:38                          */
/*==============================================================*/


alter table tb_mensagem
   add MEN_LIDA_INDIVIDUALMENTE char(1) not null default 'N';

