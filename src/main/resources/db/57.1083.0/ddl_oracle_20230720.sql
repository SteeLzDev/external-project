/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     20/07/2023 11:42:13                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_regra_limite_operacao                              */
/*==============================================================*/
create table tb_regra_limite_operacao  (
   rlo_codigo           varchar2(32)                    not null,
   est_codigo           varchar2(32),
   org_codigo           varchar2(32),
   sbo_codigo           varchar2(32),
   uni_codigo           varchar2(32),
   svc_codigo           varchar2(32),
   nse_codigo           varchar2(32),
   nca_codigo           varchar2(32),
   csa_codigo           varchar2(32),
   cor_codigo           varchar2(32),
   crs_codigo           varchar2(32),
   cap_codigo           varchar2(32),
   prs_codigo           varchar2(32),
   pos_codigo           varchar2(32),
   srs_codigo           varchar2(32),
   trs_codigo           varchar2(32),
   vrs_codigo           varchar2(32),
   fun_codigo           varchar2(32),
   rlo_data_cadastro     date                            not null,
   rlo_data_vigencia_ini date                            not null,
   rlo_data_vigencia_fim date,
   rlo_faixa_etaria_ini smallint,
   rlo_faixa_etaria_fim smallint,
   rlo_faixa_tempo_servico_ini smallint,
   rlo_faixa_tempo_servico_fim smallint,
   rlo_faixa_salario_ini number(13,2),
   rlo_faixa_salario_fim number(13,2),
   rlo_faixa_margem_folha_ini number(13,2),
   rlo_faixa_margem_folha_fim number(13,2),
   rlo_padrao_matricula clob,
   rlo_padrao_categoria clob,
   rlo_padrao_verba     clob,
   rlo_padrao_verba_ref clob,
   rlo_mensagem_erro    clob,
   rlo_limite_quantidade smallint,
   rlo_limite_data_fim_ade date,
   rlo_limite_prazo     smallint,
   rlo_limite_valor_parcela number(13,2),
   rlo_limite_valor_liberado number(13,2),
   rlo_limite_capital_devido number(13,2),
   constraint pk_tb_regra_limite_operacao primary key (rlo_codigo)
);

/*==============================================================*/
/* Index: r_912_fk                                              */
/*==============================================================*/
create index r_912_fk on tb_regra_limite_operacao (
   est_codigo asc
);

/*==============================================================*/
/* Index: r_913_fk                                              */
/*==============================================================*/
create index r_913_fk on tb_regra_limite_operacao (
   org_codigo asc
);

/*==============================================================*/
/* Index: r_914_fk                                              */
/*==============================================================*/
create index r_914_fk on tb_regra_limite_operacao (
   sbo_codigo asc
);

/*==============================================================*/
/* Index: r_915_fk                                              */
/*==============================================================*/
create index r_915_fk on tb_regra_limite_operacao (
   uni_codigo asc
);

/*==============================================================*/
/* Index: r_916_fk                                              */
/*==============================================================*/
create index r_916_fk on tb_regra_limite_operacao (
   svc_codigo asc
);

/*==============================================================*/
/* Index: r_917_fk                                              */
/*==============================================================*/
create index r_917_fk on tb_regra_limite_operacao (
   nse_codigo asc
);

/*==============================================================*/
/* Index: r_918_fk                                              */
/*==============================================================*/
create index r_918_fk on tb_regra_limite_operacao (
   nca_codigo asc
);

/*==============================================================*/
/* Index: r_919_fk                                              */
/*==============================================================*/
create index r_919_fk on tb_regra_limite_operacao (
   csa_codigo asc
);

/*==============================================================*/
/* Index: r_920_fk                                              */
/*==============================================================*/
create index r_920_fk on tb_regra_limite_operacao (
   cor_codigo asc
);

/*==============================================================*/
/* Index: r_921_fk                                              */
/*==============================================================*/
create index r_921_fk on tb_regra_limite_operacao (
   crs_codigo asc
);

/*==============================================================*/
/* Index: r_922_fk                                              */
/*==============================================================*/
create index r_922_fk on tb_regra_limite_operacao (
   cap_codigo asc
);

/*==============================================================*/
/* Index: e_923_fk                                              */
/*==============================================================*/
create index e_923_fk on tb_regra_limite_operacao (
   prs_codigo asc
);

/*==============================================================*/
/* Index: r_923_fk                                              */
/*==============================================================*/
create index r_923_fk on tb_regra_limite_operacao (
   pos_codigo asc
);

/*==============================================================*/
/* Index: r_924_fk                                              */
/*==============================================================*/
create index r_924_fk on tb_regra_limite_operacao (
   srs_codigo asc
);

/*==============================================================*/
/* Index: r_925_fk                                              */
/*==============================================================*/
create index r_925_fk on tb_regra_limite_operacao (
   trs_codigo asc
);

/*==============================================================*/
/* Index: r_926_fk                                              */
/*==============================================================*/
create index r_926_fk on tb_regra_limite_operacao (
   vrs_codigo asc
);

/*==============================================================*/
/* Index: r_927_fk                                              */
/*==============================================================*/
create index r_927_fk on tb_regra_limite_operacao (
   fun_codigo asc
);

alter table tb_regra_limite_operacao
   add constraint fk_tb_regra_e_923_tb_padra foreign key (prs_codigo)
      references tb_padrao_registro_servidor (prs_codigo);

alter table tb_regra_limite_operacao
   add constraint fk_tb_regra_r_912_tb_estab foreign key (est_codigo)
      references tb_estabelecimento (est_codigo);

alter table tb_regra_limite_operacao
   add constraint fk_tb_regra_r_913_tb_orgao foreign key (org_codigo)
      references tb_orgao (org_codigo);

alter table tb_regra_limite_operacao
   add constraint fk_tb_regra_r_914_tb_sub_o foreign key (sbo_codigo)
      references tb_sub_orgao (sbo_codigo);

alter table tb_regra_limite_operacao
   add constraint fk_tb_regra_r_915_tb_unida foreign key (uni_codigo)
      references tb_unidade (uni_codigo);

alter table tb_regra_limite_operacao
   add constraint fk_tb_regra_r_916_tb_servi foreign key (svc_codigo)
      references tb_servico (svc_codigo);

alter table tb_regra_limite_operacao
   add constraint fk_tb_regra_r_917_tb_natur foreign key (nse_codigo)
      references tb_natureza_servico (nse_codigo);

alter table tb_regra_limite_operacao
   add constraint fk_tb_regra_r_918_tb_natur foreign key (nca_codigo)
      references tb_natureza_consignataria (nca_codigo);

alter table tb_regra_limite_operacao
   add constraint fk_tb_regra_r_919_tb_consi foreign key (csa_codigo)
      references tb_consignataria (csa_codigo);

alter table tb_regra_limite_operacao
   add constraint fk_tb_regra_r_920_tb_corre foreign key (cor_codigo)
      references tb_correspondente (cor_codigo);

alter table tb_regra_limite_operacao
   add constraint fk_tb_regra_r_921_tb_cargo foreign key (crs_codigo)
      references tb_cargo_registro_servidor (crs_codigo);

alter table tb_regra_limite_operacao
   add constraint fk_tb_regra_r_922_tb_capac foreign key (cap_codigo)
      references tb_capacidade_registro_ser (cap_codigo);

alter table tb_regra_limite_operacao
   add constraint fk_tb_regra_r_923_tb_posto foreign key (pos_codigo)
      references tb_posto_registro_servidor (pos_codigo);

alter table tb_regra_limite_operacao
   add constraint fk_tb_regra_r_924_tb_statu foreign key (srs_codigo)
      references tb_status_registro_servidor (srs_codigo);

alter table tb_regra_limite_operacao
   add constraint fk_tb_regra_r_925_tb_tipo_ foreign key (trs_codigo)
      references tb_tipo_registro_servidor (trs_codigo);

alter table tb_regra_limite_operacao
   add constraint fk_tb_regra_r_926_tb_vincu foreign key (vrs_codigo)
      references tb_vinculo_registro_servidor (vrs_codigo);

alter table tb_regra_limite_operacao
   add constraint fk_tb_regra_r_927_tb_funca foreign key (fun_codigo)
      references tb_funcao (fun_codigo);

