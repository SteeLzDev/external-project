/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     05/11/2018 17:54:29                          */
/*==============================================================*/


alter table tb_solicitacao_suporte
   add SOS_PRIORIDADE varchar(40);

alter table tb_solicitacao_suporte
   add SOS_SLA varchar(40);

