/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     11/02/2019 09:56:17                          */
/*==============================================================*/


alter table tb_tipo_arquivo
   add TAR_NOTIFICACAO_UPLOAD char(1) not null default 'N';

