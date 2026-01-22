/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     12/08/2022 13:30:55                          */
/*==============================================================*/


alter table tb_tipo_arquivo
   add TAR_UPLOAD_SER char(1) not null default 'N';

