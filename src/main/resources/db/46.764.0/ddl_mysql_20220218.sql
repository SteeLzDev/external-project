/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     18/02/2022 10:46:33                          */
/*==============================================================*/


alter table tb_ordem_relatorio_estatistico
   add ORE_SEQUENCIA smallint not null default 0;

alter table tb_ordem_relatorio_estatistico
   add ORE_ATIVO smallint not null default 1;

alter table tb_ordem_relatorio_estatistico
   change column ORD_ETT_ORDEM ORE_CODIGO int not null;

alter table tb_ordem_relatorio_estatistico
   change column ORD_ETT_DESCRICAO ORE_DESCRICAO varchar(100) not null;

