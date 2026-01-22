/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     06/08/2024 11:09:03                          */
/*==============================================================*/


alter table tb_registro_servidor
   add RSE_MOTIVO_FALTA_MARGEM varchar(100);

alter table ht_registro_servidor
   add RSE_MOTIVO_FALTA_MARGEM varchar(100);

