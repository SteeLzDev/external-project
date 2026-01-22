/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     18/02/2022 15:28:22                          */
/*==============================================================*/


alter table tb_relatorio_estatistico
   add REE_QTD int;

alter table tb_relatorio_estatistico
   change column REL_ETT_NOME REE_NOME varchar(32) not null;

alter table tb_relatorio_estatistico
   change column REL_ETT_REFERENCIA REE_REFERENCIA char(10) not null;

alter table tb_relatorio_estatistico
   change column REL_ETT_ORDEM REE_ORDEM int not null;

alter table tb_relatorio_estatistico
   change column REL_ETT_VERBA REE_VERBA varchar(32) not null;

alter table tb_relatorio_estatistico
   change column REL_ETT_CSA REE_CSA varchar(32) not null;

alter table tb_relatorio_estatistico
   change column REL_ETT_VALOR REE_VALOR decimal(13,2);

