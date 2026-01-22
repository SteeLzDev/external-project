/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     13/08/2019 09:52:02                          */
/*==============================================================*/


alter table tb_consignataria
   add CSA_PERMITE_INCLUIR_ADE char(1) not null default 'S';

