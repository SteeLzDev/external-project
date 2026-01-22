/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     29/07/2025 19:00:00                          */
/*==============================================================*/

-- DESENV-23796
ALTER TABLE tb_controle_documento_margem
   CHANGE COLUMN CDM_BASE64 CDM_LOCAL_ARQUIVO VARCHAR(244) NOT NULL;

