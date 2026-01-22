/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     12/12/2019 16:25:41                          */
/*==============================================================*/


alter table tb_usuario
   add USU_CHAVE_VALIDACAO_EMAIL varchar(32);

alter table tb_usuario
   add USU_DATA_VALIDACAO_EMAIL datetime;

