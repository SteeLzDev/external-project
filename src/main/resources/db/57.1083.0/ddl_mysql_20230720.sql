/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     20/07/2023 11:20:25                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_regra_limite_operacao                              */
/*==============================================================*/
create table tb_regra_limite_operacao
(
   RLO_CODIGO           varchar(32) not null,
   EST_CODIGO           varchar(32),
   ORG_CODIGO           varchar(32),
   SBO_CODIGO           varchar(32),
   UNI_CODIGO           varchar(32),
   SVC_CODIGO           varchar(32),
   NSE_CODIGO           varchar(32),
   NCA_CODIGO           varchar(32),
   CSA_CODIGO           varchar(32),
   COR_CODIGO           varchar(32),
   CRS_CODIGO           varchar(32),
   CAP_CODIGO           varchar(32),
   PRS_CODIGO           varchar(32),
   POS_CODIGO           varchar(32),
   SRS_CODIGO           varchar(32),
   TRS_CODIGO           varchar(32),
   VRS_CODIGO           varchar(32),
   FUN_CODIGO           varchar(32),
   RLO_DATA_CADASTRO    datetime not null,
   RLO_DATA_VIGENCIA_INI datetime not null,
   RLO_DATA_VIGENCIA_FIM datetime,
   RLO_FAIXA_ETARIA_INI smallint,
   RLO_FAIXA_ETARIA_FIM smallint,
   RLO_FAIXA_TEMPO_SERVICO_INI smallint,
   RLO_FAIXA_TEMPO_SERVICO_FIM smallint,
   RLO_FAIXA_SALARIO_INI decimal(13,2),
   RLO_FAIXA_SALARIO_FIM decimal(13,2),
   RLO_FAIXA_MARGEM_FOLHA_INI decimal(13,2),
   RLO_FAIXA_MARGEM_FOLHA_FIM decimal(13,2),
   RLO_PADRAO_MATRICULA text,
   RLO_PADRAO_CATEGORIA text,
   RLO_PADRAO_VERBA     text,
   RLO_PADRAO_VERBA_REF text,
   RLO_MENSAGEM_ERRO    text,
   RLO_LIMITE_QUANTIDADE smallint,
   RLO_LIMITE_DATA_FIM_ADE date,
   RLO_LIMITE_PRAZO     smallint,
   RLO_LIMITE_VALOR_PARCELA decimal(13,2),
   RLO_LIMITE_VALOR_LIBERADO decimal(13,2),
   RLO_LIMITE_CAPITAL_DEVIDO decimal(13,2),
   primary key (RLO_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_regra_limite_operacao add constraint FK_E_923 foreign key (PRS_CODIGO)
      references tb_padrao_registro_servidor (PRS_CODIGO) on delete restrict on update restrict;

alter table tb_regra_limite_operacao add constraint FK_R_912 foreign key (EST_CODIGO)
      references tb_estabelecimento (EST_CODIGO) on delete restrict on update restrict;

alter table tb_regra_limite_operacao add constraint FK_R_913 foreign key (ORG_CODIGO)
      references tb_orgao (ORG_CODIGO) on delete restrict on update restrict;

alter table tb_regra_limite_operacao add constraint FK_R_914 foreign key (SBO_CODIGO)
      references tb_sub_orgao (SBO_CODIGO) on delete restrict on update restrict;

alter table tb_regra_limite_operacao add constraint FK_R_915 foreign key (UNI_CODIGO)
      references tb_unidade (UNI_CODIGO) on delete restrict on update restrict;

alter table tb_regra_limite_operacao add constraint FK_R_916 foreign key (SVC_CODIGO)
      references tb_servico (SVC_CODIGO) on delete restrict on update restrict;

alter table tb_regra_limite_operacao add constraint FK_R_917 foreign key (NSE_CODIGO)
      references tb_natureza_servico (NSE_CODIGO) on delete restrict on update restrict;

alter table tb_regra_limite_operacao add constraint FK_R_918 foreign key (NCA_CODIGO)
      references tb_natureza_consignataria (NCA_CODIGO) on delete restrict on update restrict;

alter table tb_regra_limite_operacao add constraint FK_R_919 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;

alter table tb_regra_limite_operacao add constraint FK_R_920 foreign key (COR_CODIGO)
      references tb_correspondente (COR_CODIGO) on delete restrict on update restrict;

alter table tb_regra_limite_operacao add constraint FK_R_921 foreign key (CRS_CODIGO)
      references tb_cargo_registro_servidor (CRS_CODIGO) on delete restrict on update restrict;

alter table tb_regra_limite_operacao add constraint FK_R_922 foreign key (CAP_CODIGO)
      references tb_capacidade_registro_ser (CAP_CODIGO) on delete restrict on update restrict;

alter table tb_regra_limite_operacao add constraint FK_R_923 foreign key (POS_CODIGO)
      references tb_posto_registro_servidor (POS_CODIGO) on delete restrict on update restrict;

alter table tb_regra_limite_operacao add constraint FK_R_924 foreign key (SRS_CODIGO)
      references tb_status_registro_servidor (SRS_CODIGO) on delete restrict on update restrict;

alter table tb_regra_limite_operacao add constraint FK_R_925 foreign key (TRS_CODIGO)
      references tb_tipo_registro_servidor (TRS_CODIGO) on delete restrict on update restrict;

alter table tb_regra_limite_operacao add constraint FK_R_926 foreign key (VRS_CODIGO)
      references tb_vinculo_registro_servidor (VRS_CODIGO) on delete restrict on update restrict;

alter table tb_regra_limite_operacao add constraint FK_R_927 foreign key (FUN_CODIGO)
      references tb_funcao (FUN_CODIGO) on delete restrict on update restrict;

