/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     08/07/2022 11:52:24                          */
/*==============================================================*/

alter table ht_anexo_autorizacao_desconto modify aad_nome varchar2(255);

alter table ht_solicitacao_autorizacao add soa_data_resposta date;

alter table ht_solicitacao_autorizacao add soa_obs clob;

alter table ht_solicitacao_autorizacao add soa_periodo date;

alter table ht_solicitacao_autorizacao add oso_codigo varchar2(32);

/*==============================================================*/
/* Index: r_887_fk                                              */
/*==============================================================*/
create index r_887_fk on ht_solicitacao_autorizacao (
   oso_codigo asc
);

alter table ht_solicitacao_autorizacao
   add constraint fk_ht_solic_r_887_tb_orige foreign key (oso_codigo)
      references tb_origem_solicitacao (oso_codigo);

