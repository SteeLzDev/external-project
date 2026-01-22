/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     05/11/2021 14:22:30                          */
/*==============================================================*/


alter table tb_beneficiario
   add BFC_IDENTIFICADOR varchar(40);

alter table tb_beneficiario
   modify column BFC_NOME varchar(255);

alter table tb_beneficiario
   modify column BFC_CPF varchar(19);

