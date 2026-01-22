/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     17/07/2023 16:47:32                          */
/*==============================================================*/


alter table tb_consignataria 
   add csa_permite_api char(1) default 'N' not null;

