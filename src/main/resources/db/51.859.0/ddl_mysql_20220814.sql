/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     10/08/2022 11:57:27                          */
/*==============================================================*/


alter table tb_banner_publicidade
   add BPU_EXIBE_MOBILE char(1) not null default 'N';

alter table tb_banner_publicidade
   add BPU_DATA datetime;

alter table tb_banner_publicidade
   modify column BPU_URL_SAIDA varchar(255);

