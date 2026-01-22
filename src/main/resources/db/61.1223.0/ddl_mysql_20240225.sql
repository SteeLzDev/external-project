/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     16/02/2024 09:36:45                          */
/*==============================================================*/


alter table tb_perfil
   add PER_AUTO_DESBLOQUEIO char(1) not null default 'N';

