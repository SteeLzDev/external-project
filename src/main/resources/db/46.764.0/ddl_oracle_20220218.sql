/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     18/02/2022 10:52:45                          */
/*==============================================================*/


alter table tb_ordem_relatorio_estatistico add ore_sequencia smallint default 0 not null;

alter table tb_ordem_relatorio_estatistico add ore_ativo smallint default 1 not null;

alter table tb_ordem_relatorio_estatistico 
   rename column ord_ett_ordem to ore_codigo;

alter table tb_ordem_relatorio_estatistico 
   rename column ord_ett_descricao to ore_descricao;

