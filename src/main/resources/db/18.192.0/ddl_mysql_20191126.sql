/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     26/11/2019 10:52:50                          */
/*==============================================================*/


alter table tb_bloco_processamento
   add BPR_NUM_LINHA int not null default 0;

/*==============================================================*/
/* Table: tb_historico_processamento                            */
/*==============================================================*/
create table tb_historico_processamento
(
   HPR_CODIGO           varchar(32) not null,
   ORG_CODIGO           varchar(32),
   EST_CODIGO           varchar(32),
   HPR_PERIODO          date not null,
   HPR_DATA_INI         datetime not null,
   HPR_DATA_FIM         datetime,
   HPR_ARQUIVO_MARGEM   varchar(255) not null,
   HPR_CONF_ENTRADA_MARGEM varchar(255) not null,
   HPR_CONF_TRADUTOR_MARGEM varchar(255) not null,
   HPR_LINHAS_ARQUIVO_MARGEM int not null default 0,
   HPR_ARQUIVO_RETORNO  varchar(255) not null,
   HPR_CONF_ENTRADA_RETORNO varchar(255) not null,
   HPR_CONF_TRADUTOR_RETORNO varchar(255) not null,
   HPR_LINHAS_ARQUIVO_RETORNO int not null default 0,
   HPR_CHAVE_IDENTIFICACAO text not null,
   HPR_ORDEM_EXC_CAMPOS_CHAVE text not null,
   primary key (HPR_CODIGO)
) ENGINE=InnoDB;

alter table tb_historico_processamento add constraint FK_R_787 foreign key (EST_CODIGO)
      references tb_estabelecimento (EST_CODIGO) on delete restrict on update restrict;

alter table tb_historico_processamento add constraint FK_R_788 foreign key (ORG_CODIGO)
      references tb_orgao (ORG_CODIGO) on delete restrict on update restrict;

