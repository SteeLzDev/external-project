/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     23/05/2022 11:32:31                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_origem_solicitacao                               */
/*==============================================================*/
create table tb_origem_solicitacao  (
   oso_codigo           varchar2(32)                    not null,
   oso_descricao        varchar2(40)                    not null,
   constraint pk_tb_origem_solicitacao primary key (oso_codigo)
);

alter table tb_solicitacao_autorizacao add soa_data_resposta date;

alter table tb_solicitacao_autorizacao add soa_obs clob;

alter table tb_solicitacao_autorizacao add oso_codigo varchar2(32);

/*==============================================================*/
/* Index: r_870_fk                                              */
/*==============================================================*/
create index r_870_fk on tb_solicitacao_autorizacao (
   oso_codigo asc
);

alter table tb_solicitacao_autorizacao
   add constraint fk_tb_solic_r_870_tb_orige foreign key (oso_codigo)
      references tb_origem_solicitacao (oso_codigo);

