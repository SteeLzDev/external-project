/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     12/09/2024 17:03:03                          */
/*==============================================================*/


drop table if exists tmp_tb_regra_limite_operacao;

rename table tb_regra_limite_operacao to tmp_tb_regra_limite_operacao;

/*==============================================================*/
/* Table: tb_regra_limite_operacao                              */
/*==============================================================*/
create table tb_regra_limite_operacao
(
   RLO_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null default '1',
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
) ENGINE = InnoDB;

insert into tb_regra_limite_operacao (RLO_CODIGO, EST_CODIGO, ORG_CODIGO, SBO_CODIGO, UNI_CODIGO, SVC_CODIGO, NSE_CODIGO, NCA_CODIGO, CSA_CODIGO, COR_CODIGO, CRS_CODIGO, CAP_CODIGO, PRS_CODIGO, POS_CODIGO, SRS_CODIGO, TRS_CODIGO, VRS_CODIGO, FUN_CODIGO, RLO_DATA_CADASTRO, RLO_DATA_VIGENCIA_INI, RLO_DATA_VIGENCIA_FIM, RLO_FAIXA_ETARIA_INI, RLO_FAIXA_ETARIA_FIM, RLO_FAIXA_TEMPO_SERVICO_INI, RLO_FAIXA_TEMPO_SERVICO_FIM, RLO_FAIXA_SALARIO_INI, RLO_FAIXA_SALARIO_FIM, RLO_FAIXA_MARGEM_FOLHA_INI, RLO_FAIXA_MARGEM_FOLHA_FIM, RLO_PADRAO_MATRICULA, RLO_PADRAO_CATEGORIA, RLO_PADRAO_VERBA, RLO_PADRAO_VERBA_REF, RLO_MENSAGEM_ERRO, RLO_LIMITE_QUANTIDADE, RLO_LIMITE_DATA_FIM_ADE, RLO_LIMITE_PRAZO, RLO_LIMITE_VALOR_PARCELA, RLO_LIMITE_VALOR_LIBERADO, RLO_LIMITE_CAPITAL_DEVIDO)
select RLO_CODIGO, EST_CODIGO, ORG_CODIGO, SBO_CODIGO, UNI_CODIGO, SVC_CODIGO, NSE_CODIGO, NCA_CODIGO, CSA_CODIGO, COR_CODIGO, CRS_CODIGO, CAP_CODIGO, PRS_CODIGO, POS_CODIGO, SRS_CODIGO, TRS_CODIGO, VRS_CODIGO, FUN_CODIGO, RLO_DATA_CADASTRO, RLO_DATA_VIGENCIA_INI, RLO_DATA_VIGENCIA_FIM, RLO_FAIXA_ETARIA_INI, RLO_FAIXA_ETARIA_FIM, RLO_FAIXA_TEMPO_SERVICO_INI, RLO_FAIXA_TEMPO_SERVICO_FIM, RLO_FAIXA_SALARIO_INI, RLO_FAIXA_SALARIO_FIM, RLO_FAIXA_MARGEM_FOLHA_INI, RLO_FAIXA_MARGEM_FOLHA_FIM, RLO_PADRAO_MATRICULA, RLO_PADRAO_CATEGORIA, RLO_PADRAO_VERBA, RLO_PADRAO_VERBA_REF, RLO_MENSAGEM_ERRO, RLO_LIMITE_QUANTIDADE, RLO_LIMITE_DATA_FIM_ADE, RLO_LIMITE_PRAZO, RLO_LIMITE_VALOR_PARCELA, RLO_LIMITE_VALOR_LIBERADO, RLO_LIMITE_CAPITAL_DEVIDO
from tmp_tb_regra_limite_operacao;

drop table if exists tmp_tb_regra_limite_operacao;

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

alter table tb_regra_limite_operacao add constraint FK_R_969 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

