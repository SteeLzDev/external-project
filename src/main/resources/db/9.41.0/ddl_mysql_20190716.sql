/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     16/07/2019 13:25:10                          */
/*==============================================================*/

alter table tb_beneficio
   modify BEN_CODIGO_PLANO     varchar(40),
   modify BEN_CODIGO_REGISTRO  varchar(40),
   modify BEN_CODIGO_CONTRATO  varchar(40),
   add    MBE_CODIGO           varchar(32)
;

/*==============================================================*/
/* Table: tb_endereco_consignataria                             */
/*==============================================================*/
create table tb_endereco_consignataria
(
   ENC_CODIGO           varchar(32) not null,
   TIE_CODIGO           varchar(32) not null,
   CSA_CODIGO           varchar(32) not null,
   ENC_LOGRADOURO       varchar(100) not null,
   ENC_NUMERO           varchar(15) not null,
   ENC_COMPLEMENTO      varchar(40) not null,
   ENC_BAIRRO           varchar(40) not null,
   ENC_MUNICIPIO        varchar(40) not null,
   ENC_UF               char(2) not null,
   ENC_CEP              varchar(10) not null,
   primary key (ENC_CODIGO)
) ENGINE=InnoDB;

/*==============================================================*/
/* Table: tb_modalidade_beneficio                               */
/*==============================================================*/
create table tb_modalidade_beneficio
(
   MBE_CODIGO           varchar(32) not null,
   MBE_DESCRICAO        varchar(100) not null,
   primary key (MBE_CODIGO)
) ENGINE=InnoDB;

alter table tb_natureza_servico
   add NSE_ORDEM_BENEFICIO smallint not null default 0;

alter table tb_natureza_servico
   add NSE_IMAGEM longblob;

/*==============================================================*/
/* Table: tb_palavra_chave                                      */
/*==============================================================*/
create table tb_palavra_chave
(
   PCH_CODIGO           int not null auto_increment,
   PCH_PALAVRA          varchar(100) not null,
   primary key (PCH_CODIGO)
) ENGINE=InnoDB;

/*==============================================================*/
/* Table: tb_palavra_chave_beneficio                            */
/*==============================================================*/
create table tb_palavra_chave_beneficio
(
   BEN_CODIGO           varchar(32) not null,
   PCH_CODIGO           int not null,
   primary key (BEN_CODIGO, PCH_CODIGO)
) ENGINE=InnoDB;

/*==============================================================*/
/* Table: tb_provedor_beneficio                                 */
/*==============================================================*/
create table tb_provedor_beneficio
(
   PRO_CODIGO           varchar(32) not null,
   CSA_CODIGO           varchar(32) not null,
   NSE_CODIGO           varchar(32) not null,
   PRO_TITULO_DETALHE_TOPO varchar(255) not null,
   PRO_TEXTO_DETALHE_TOPO text not null,
   PRO_TITULO_DETALHE_RODAPE varchar(255) not null,
   PRO_TEXTO_DETALHE_RODAPE text not null,
   PRO_TITULO_LISTA_BENEFICIO varchar(255) not null,
   PRO_TEXTO_CARD_BENEFICIO text not null,
   PRO_LINK_BENEFICIO   varchar(255),
   PRO_IMAGEM_BENEFICIO longblob,
   primary key (PRO_CODIGO)
) ENGINE=InnoDB;

alter table tb_beneficio add constraint FK_R_759 foreign key (MBE_CODIGO)
      references tb_modalidade_beneficio (MBE_CODIGO) on delete restrict on update restrict;

alter table tb_endereco_consignataria add constraint FK_R_760 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;

alter table tb_endereco_consignataria add constraint FK_R_761 foreign key (TIE_CODIGO)
      references tb_tipo_endereco (TIE_CODIGO) on delete restrict on update restrict;

alter table tb_palavra_chave_beneficio add constraint FK_R_762 foreign key (BEN_CODIGO)
      references tb_beneficio (BEN_CODIGO) on delete restrict on update restrict;

alter table tb_palavra_chave_beneficio add constraint FK_R_763 foreign key (PCH_CODIGO)
      references tb_palavra_chave (PCH_CODIGO) on delete restrict on update restrict;

alter table tb_provedor_beneficio add constraint FK_R_764 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;

alter table tb_provedor_beneficio add constraint FK_R_765 foreign key (NSE_CODIGO)
      references tb_natureza_servico (NSE_CODIGO) on delete restrict on update restrict;

