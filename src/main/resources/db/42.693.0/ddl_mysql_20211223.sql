/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     09/11/2021 15:12:17                          */
/*==============================================================*/


alter table ta_beneficiario
   add BFC_IDENTIFICADOR varchar(40);

alter table ta_beneficiario
   modify column BFC_NOME varchar(255);

alter table ta_beneficiario
   modify column BFC_CPF varchar(19);
