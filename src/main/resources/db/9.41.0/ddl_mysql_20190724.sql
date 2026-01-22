/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     24/07/2019 16:08:14                          */
/*==============================================================*/


alter table ht_anexo_autorizacao_desconto
   add AAD_EXIBE_SUP char(1) not null default 'S';

alter table ht_anexo_autorizacao_desconto
   add AAD_EXIBE_CSE char(1) not null default 'S';

alter table ht_anexo_autorizacao_desconto
   add AAD_EXIBE_ORG char(1) not null default 'S';

alter table ht_anexo_autorizacao_desconto
   add AAD_EXIBE_CSA char(1) not null default 'S';

alter table ht_anexo_autorizacao_desconto
   add AAD_EXIBE_COR char(1) not null default 'S';

alter table ht_anexo_autorizacao_desconto
   add AAD_EXIBE_SER char(1) not null default 'S';

alter table tb_anexo_autorizacao_desconto
   add AAD_EXIBE_SUP char(1) not null default 'S';

alter table tb_anexo_autorizacao_desconto
   add AAD_EXIBE_CSE char(1) not null default 'S';

alter table tb_anexo_autorizacao_desconto
   add AAD_EXIBE_ORG char(1) not null default 'S';

alter table tb_anexo_autorizacao_desconto
   add AAD_EXIBE_CSA char(1) not null default 'S';

alter table tb_anexo_autorizacao_desconto
   add AAD_EXIBE_COR char(1) not null default 'S';

alter table tb_anexo_autorizacao_desconto
   add AAD_EXIBE_SER char(1) not null default 'S';

