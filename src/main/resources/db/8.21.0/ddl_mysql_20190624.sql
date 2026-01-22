/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     24/06/2019 15:31:03                          */
/*==============================================================*/


alter table tb_tipo_motivo_operacao
   add TMO_EXIGE_OBS char(1) not null default 'N';

