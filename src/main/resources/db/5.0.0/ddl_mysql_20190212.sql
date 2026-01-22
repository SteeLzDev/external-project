/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     12/02/2019 11:47:57                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_definicao_taxa_juros                               */
/*==============================================================*/
create table tb_definicao_taxa_juros
(
   DTJ_CODIGO           varchar(32) not null,
   CSA_CODIGO           varchar(32) not null,
   ORG_CODIGO           varchar(32),
   SVC_CODIGO           varchar(32) not null,
   DTJ_FAIXA_ETARIA_INI smallint,
   DTJ_FAIXA_ETARIA_FIM smallint,
   DTJ_FAIXA_TEMP_SERVICO_INI smallint,
   DTJ_FAIXA_TEMP_SERVICO_FIM smallint,
   DTJ_FAIXA_SALARIO_INI decimal(13,2),
   DTJ_FAIXA_SALARIO_FIM decimal(13,2),
   DTJ_FAIXA_MARGEM_INI decimal(13,2),
   DTJ_FAIXA_MARGEM_FIM decimal(13,2),
   DTJ_FAIXA_VALOR_TOTAL_INI decimal(13,2),
   DTJ_FAIXA_VALOR_TOTAL_FIM decimal(13,2),
   DTJ_FAIXA_VALOR_CONTRATO_INI decimal(13,2),
   DTJ_FAIXA_VALOR_CONTRATO_FIM decimal(13,2),
   DTJ_FAIXA_PRAZO_INI  smallint not null,
   DTJ_FAIXA_PRAZO_FIM  smallint not null,
   DTJ_TAXA_JUROS       decimal(13,8) not null,
   DTJ_DATA_VIGENCIA_INI datetime,
   DTJ_DATA_VIGENCIA_FIM datetime,
   DTJ_DATA_CADASTRO    datetime not null,
   primary key (DTJ_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_definicao_taxa_juros add constraint FK_R_749 foreign key (SVC_CODIGO)
      references tb_servico (SVC_CODIGO) on delete restrict on update restrict;

alter table tb_definicao_taxa_juros add constraint FK_R_750 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;

alter table tb_definicao_taxa_juros add constraint FK_R_751 foreign key (ORG_CODIGO)
      references tb_orgao (ORG_CODIGO) on delete restrict on update restrict;

