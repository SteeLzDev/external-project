/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     05/07/2021 13:58:59                          */
/*==============================================================*/


alter table tb_tipo_param_consignataria
   modify TPA_DESCRICAO varchar(200) not null
;

alter table tb_tipo_param_sist_consignante
   modify TPC_DESCRICAO varchar(200) not null
;

alter table tb_tipo_param_svc
   modify TPS_DESCRICAO varchar(200) not null
;
