/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     21/06/2019 17:57:12                          */
/*==============================================================*/


create index FK_R_727 
   on tb_beneficio_servico (BEN_CODIGO);

alter table tb_beneficio_servico
   drop primary key;

alter table tb_beneficio_servico
   add primary key (SVC_CODIGO, BEN_CODIGO, TIB_CODIGO);

