/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     19/12/2024 19:22:19                          */
/*==============================================================*/


rename table tb_termo_adesao to tb_termo_adesao_servico;

/*==============================================================*/
/* Table: tb_leitura_termo_usuario                              */
/*==============================================================*/
create table tb_leitura_termo_usuario
(
   LTU_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null,
   TAD_CODIGO           varchar(32) not null,
   LTU_DATA             datetime not null,
   LTU_TERMO_ACEITO     char(1) not null default 'N',
   LTU_CANAL            char(1) not null default '1',
   LTU_IP_ACESSO        varchar(45) not null,
   LTU_PORTA            int not null,
   LTU_OBS              text not null,
   primary key (LTU_CODIGO)
) ENGINE = InnoDB;

/*==============================================================*/
/* Table: tb_termo_adesao                                       */
/*==============================================================*/
create table tb_termo_adesao
(
   TAD_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null,
   FUN_CODIGO           varchar(32),
   TAD_TITULO           varchar(100) not null,
   TAD_TEXTO            text not null,
   TAD_DATA             datetime not null,
   TAD_SEQUENCIA        int not null default 0,
   TAD_EXIBE_CSE        char(1) not null default 'N',
   TAD_EXIBE_ORG        char(1) not null default 'N',
   TAD_EXIBE_CSA        char(1) not null default 'N',
   TAD_EXIBE_COR        char(1) not null default 'N',
   TAD_EXIBE_SER        char(1) not null default 'N',
   TAD_EXIBE_SUP        char(1) not null default 'N',
   TAD_HTML             char(1) not null,
   TAD_PERMITE_RECUSAR  char(1) not null default 'S',
   TAD_PERMITE_LER_DEPOIS char(1) not null default 'N',
   primary key (TAD_CODIGO)
) ENGINE = InnoDB;

alter table tb_termo_adesao_servico
   change column TER_ADS_TEXTO TAS_TEXTO text not null;

alter table tb_leitura_termo_usuario add constraint FK_R_972 foreign key (TAD_CODIGO)
      references tb_termo_adesao (TAD_CODIGO) on delete restrict on update restrict;

alter table tb_leitura_termo_usuario add constraint FK_R_973 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

alter table tb_termo_adesao add constraint FK_R_970 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

alter table tb_termo_adesao add constraint FK_R_971 foreign key (FUN_CODIGO)
      references tb_funcao (FUN_CODIGO) on delete restrict on update restrict;

UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterTermoAdesaoServico' WHERE ACR_RECURSO = '/v3/manterTermoAdesao';

UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/visualizarTermoAdesaoServico' WHERE ACR_RECURSO = '/v3/visualizarTermoAdesao';

