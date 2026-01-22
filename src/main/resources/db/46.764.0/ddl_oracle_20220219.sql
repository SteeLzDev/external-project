/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     18/02/2022 15:33:35                          */
/*==============================================================*/


alter table tb_relatorio_estatistico add ree_qtd integer;

alter table tb_relatorio_estatistico 
   rename column rel_ett_nome to ree_nome;

alter table tb_relatorio_estatistico 
   rename column rel_ett_referencia to ree_referencia;

alter table tb_relatorio_estatistico 
   rename column rel_ett_ordem to ree_ordem;

alter table tb_relatorio_estatistico 
   rename column rel_ett_verba to ree_verba;

alter table tb_relatorio_estatistico 
   rename column rel_ett_csa to ree_csa;

alter table tb_relatorio_estatistico 
   rename column rel_ett_valor to ree_valor;

