-- DESENV-16272
/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     15/07/2021 16:14:17                          */
/*==============================================================*/


alter table tb_coeficiente
   add CFT_VLR_REF decimal(13,8);

alter table tb_coeficiente_ativo
   add CFT_VLR_REF decimal(13,8);
