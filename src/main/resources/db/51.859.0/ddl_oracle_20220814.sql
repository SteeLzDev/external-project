/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     10/08/2022 12:03:36                          */
/*==============================================================*/


alter table tb_banner_publicidade 
   add bpu_exibe_mobile char(1) default 'N' not null;

alter table tb_banner_publicidade 
   add bpu_data date;

alter table tb_banner_publicidade
   modify bpu_url_saida null;

