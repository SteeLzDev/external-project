/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     08/07/2022 11:37:12                          */
/*==============================================================*/


alter table ht_anexo_autorizacao_desconto
   modify column AAD_NOME varchar(255) not null;


alter table ht_solicitacao_autorizacao
   add column SOA_DATA_RESPOSTA datetime;

alter table ht_solicitacao_autorizacao
   add column SOA_OBS text;

alter table ht_solicitacao_autorizacao
   add column SOA_PERIODO date;

alter table ht_solicitacao_autorizacao
   add column OSO_CODIGO varchar(32);

alter table ht_solicitacao_autorizacao add constraint FK_R_887 foreign key (OSO_CODIGO)
      references tb_origem_solicitacao (OSO_CODIGO) on delete restrict on update restrict;

