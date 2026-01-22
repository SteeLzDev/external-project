/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     26/12/2024 15:10:00                          */
/*==============================================================*/


alter table tb_usuario add usu_operacoes_validacao_totp char(1) default '1' not null;

