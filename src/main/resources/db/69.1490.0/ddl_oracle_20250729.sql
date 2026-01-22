/*==============================================================*/
/* DBMS name:      Oracle                                       */
/* Created on:     29/07/2025 19:00:00                          */
/*==============================================================*/

-- DESENV-23796
alter table tb_controle_documento_margem
   rename column cdm_base64 to cdm_local_arquivo;

