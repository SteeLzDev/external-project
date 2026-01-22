/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     26/12/2024 15:20:01                          */
/*==============================================================*/


alter table tb_funcao
   add FUN_EXIGE_SEGUNDA_SENHA_SER char(1) not null default 'N' after FUN_EXIGE_SEGUNDA_SENHA_COR;

