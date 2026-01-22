/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     26/12/2024 14:55:21                          */
/*==============================================================*/


alter table tb_usuario
   add USU_OPERACOES_VALIDACAO_TOTP char(1) not null default '1';

