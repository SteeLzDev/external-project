/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     09/12/2020 14:49:57                          */
/*==============================================================*/


drop table if exists tb_registro_servidor_validacao;

drop table if exists tb_servidor_validacao;

/*==============================================================*/
/* Table: ht_registro_servidor                                  */
/*==============================================================*/
create table ht_registro_servidor
(
   RSE_CODIGO           varchar(32) not null,
   MAR_CODIGO           smallint,
   SER_CODIGO           varchar(32) not null,
   SRS_CODIGO           varchar(32) not null default '1',
   ORG_CODIGO           varchar(32) not null,
   SBO_CODIGO           varchar(32),
   UNI_CODIGO           varchar(32),
   POS_CODIGO           varchar(32),
   TRS_CODIGO           varchar(32),
   CAP_CODIGO           varchar(32),
   PRS_CODIGO           varchar(32),
   VRS_CODIGO           varchar(32),
   CRS_CODIGO           varchar(32),
   USU_CODIGO           varchar(32),
   BCO_CODIGO           smallint,
   RSE_MATRICULA        varchar(20) not null,
   RSE_MARGEM           decimal(13,2) not null,
   RSE_MARGEM_REST      decimal(13,2) not null,
   RSE_MARGEM_USADA     decimal(13,2) default 0,
   RSE_TIPO             varchar(255),
   RSE_PRAZO            int,
   RSE_DATA_ADMISSAO    datetime,
   RSE_AGENCIA_SAL      varchar(30),
   RSE_AGENCIA_DV_SAL   char(1),
   RSE_CONTA_SAL        varchar(40),
   RSE_CONTA_DV_SAL     char(2),
   RSE_MARGEM_2         decimal(13,2),
   RSE_MARGEM_REST_2    decimal(13,2),
   RSE_MARGEM_USADA_2   decimal(13,2),
   RSE_MARGEM_3         decimal(13,2),
   RSE_MARGEM_REST_3    decimal(13,2),
   RSE_MARGEM_USADA_3   decimal(13,2),
   RSE_SALARIO          decimal(13,2),
   RSE_PROVENTOS        decimal(13,2),
   RSE_DESCONTOS_COMP   decimal(13,2),
   RSE_DESCONTOS_FACU   decimal(13,2),
   RSE_OUTROS_DESCONTOS decimal(13,2),
   RSE_ASSOCIADO        char(1),
   RSE_DATA_CARGA       datetime,
   RSE_DATA_CTC         date,
   RSE_MATRICULA_INST   varchar(20),
   RSE_CLT              char(1),
   RSE_BANCO_SAL        char(3),
   RSE_OBS              text,
   RSE_PARAM_QTD_ADE_DEFAULT smallint,
   RSE_ESTABILIZADO     char(1),
   RSE_DATA_FIM_ENGAJAMENTO date,
   RSE_DATA_LIMITE_PERMANENCIA date,
   RSE_AGENCIA_SAL_2    varchar(30),
   RSE_AGENCIA_DV_SAL_2 char(1),
   RSE_CONTA_SAL_2      varchar(40),
   RSE_CONTA_DV_SAL_2   char(2),
   RSE_BANCO_SAL_2      char(3),
   RSE_DATA_ALTERACAO   datetime,
   RSE_BASE_CALCULO     decimal(13,2),
   RSE_AUDITORIA_TOTAL  char(1) not null default 'N',
   RSE_MUNICIPIO_LOTACAO varchar(40),
   RSE_BENEFICIARIO_FINAN_DV_CART char(1),
   RSE_PRACA            text,
   RSE_PEDIDO_DEMISSAO  char(1),
   RSE_DATA_SAIDA       date,
   RSE_DATA_ULT_SALARIO date,
   RSE_DATA_RETORNO     date,
   RSE_PONTUACAO        int,
   primary key (RSE_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*==============================================================*/
/* Index: TB_REGISTRO_SERVIDOR_IDX1                             */
/*==============================================================*/
create unique index TB_REGISTRO_SERVIDOR_IDX1 on ht_registro_servidor
(
   ORG_CODIGO,
   RSE_MATRICULA
);

/*==============================================================*/
/* Index: RSE_MATRICULA_IDX                                     */
/*==============================================================*/
create index RSE_MATRICULA_IDX on ht_registro_servidor
(
   RSE_MATRICULA
);

/*==============================================================*/
/* Table: ht_servidor                                           */
/*==============================================================*/
create table ht_servidor
(
   SER_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32),
   SER_CPF              char(19) not null,
   SER_DATA_NASC        date,
   SER_NOME_MAE         varchar(100),
   SER_NOME_PAI         varchar(100),
   SER_NOME             varchar(255) not null,
   SER_SEXO             char(1),
   SER_EST_CIVIL        char(1),
   SER_NACIONALIDADE    varchar(40),
   SER_NRO_IDT          varchar(40),
   SER_CART_PROF        varchar(40),
   SER_PIS              varchar(40),
   SER_END              varchar(100),
   SER_BAIRRO           varchar(40),
   SER_CIDADE           varchar(40),
   SER_COMPL            varchar(40),
   SER_NRO              varchar(15),
   SER_CEP              char(10),
   SER_UF               varchar(5),
   SER_TEL              varchar(100),
   SER_EMISSOR_IDT      varchar(40),
   SER_UF_IDT           varchar(5),
   SER_EMAIL            varchar(100),
   SER_DATA_IDT         date,
   SER_CID_NASC         varchar(40),
   SER_UF_NASC          varchar(5),
   SER_NOME_CONJUGE     varchar(100),
   SER_DATA_ALTERACAO   datetime,
   SER_CELULAR          varchar(100),
   SER_DEFICIENTE_VISUAL char(1),
   SER_ACESSA_HOST_A_HOST char(1),
   SER_NOME_MEIO        varchar(100),
   SER_ULTIMO_NOME      varchar(100),
   SER_TITULACAO        varchar(10),
   SER_PRIMEIRO_NOME    varchar(40),
   SER_QTD_FILHOS       smallint,
   SER_DISPENSA_DIGITAL char(1),
   SSE_CODIGO           varchar(32),
   THA_CODIGO           varchar(32),
   NES_CODIGO           varchar(32),
   SER_DATA_IDENTIFICACAO_PESSOAL datetime,
   SER_DATA_VALIDACAO_EMAIL datetime,
   SER_PERMITE_ALTERAR_EMAIL char(1) not null default 'S',
   primary key (SER_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*==============================================================*/
/* Index: TB_SERVIDOR_VALIDACAO_AK                              */
/*==============================================================*/
create unique index TB_SERVIDOR_VALIDACAO_AK on ht_servidor
(
   SER_CPF,
   SER_NOME
);

/*==============================================================*/
/* Index: SER_CPF_IDX                                           */
/*==============================================================*/
create index SER_CPF_IDX on ht_servidor
(
   SER_CPF
);

alter table ht_registro_servidor add constraint FK_R_434 foreign key (SER_CODIGO)
      references ht_servidor (SER_CODIGO) on delete restrict on update restrict;

alter table ht_registro_servidor add constraint FK_R_435 foreign key (ORG_CODIGO)
      references tb_orgao (ORG_CODIGO) on delete restrict on update restrict;

alter table ht_registro_servidor add constraint FK_R_436 foreign key (SBO_CODIGO)
      references tb_sub_orgao (SBO_CODIGO) on delete restrict on update restrict;

alter table ht_registro_servidor add constraint FK_R_437 foreign key (UNI_CODIGO)
      references tb_unidade (UNI_CODIGO) on delete restrict on update restrict;

alter table ht_registro_servidor add constraint FK_R_438 foreign key (BCO_CODIGO)
      references tb_banco (BCO_CODIGO) on delete restrict on update restrict;

alter table ht_registro_servidor add constraint FK_R_439 foreign key (SRS_CODIGO)
      references tb_status_registro_servidor (SRS_CODIGO) on delete restrict on update restrict;

alter table ht_registro_servidor add constraint FK_R_440 foreign key (VRS_CODIGO)
      references tb_vinculo_registro_servidor (VRS_CODIGO) on delete restrict on update restrict;

alter table ht_registro_servidor add constraint FK_R_441 foreign key (CRS_CODIGO)
      references tb_cargo_registro_servidor (CRS_CODIGO) on delete restrict on update restrict;

alter table ht_registro_servidor add constraint FK_R_442 foreign key (PRS_CODIGO)
      references tb_padrao_registro_servidor (PRS_CODIGO) on delete restrict on update restrict;

alter table ht_registro_servidor add constraint FK_R_443 foreign key (POS_CODIGO)
      references tb_posto_registro_servidor (POS_CODIGO) on delete restrict on update restrict;

alter table ht_registro_servidor add constraint FK_R_444 foreign key (TRS_CODIGO)
      references tb_tipo_registro_servidor (TRS_CODIGO) on delete restrict on update restrict;

alter table ht_registro_servidor add constraint FK_R_445 foreign key (CAP_CODIGO)
      references tb_capacidade_registro_ser (CAP_CODIGO) on delete restrict on update restrict;

alter table ht_registro_servidor add constraint FK_R_446 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

alter table ht_registro_servidor add constraint FK_R_826 foreign key (MAR_CODIGO)
      references tb_margem (MAR_CODIGO) on delete restrict on update restrict;

alter table ht_servidor add constraint FK_R_433 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

alter table ht_servidor add constraint FK_R_654 foreign key (SSE_CODIGO)
      references tb_status_servidor (SSE_CODIGO) on delete restrict on update restrict;

alter table ht_servidor add constraint FK_R_670 foreign key (NES_CODIGO)
      references tb_nivel_escolaridade (NES_CODIGO) on delete restrict on update restrict;

alter table ht_servidor add constraint FK_R_671 foreign key (THA_CODIGO)
      references tb_tipo_habitacao (THA_CODIGO) on delete restrict on update restrict;
