/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     17/07/2023 16:34:22                          */
/*==============================================================*/


alter table tb_consignataria
   add CSA_PERMITE_API char(1) not null default 'N';

