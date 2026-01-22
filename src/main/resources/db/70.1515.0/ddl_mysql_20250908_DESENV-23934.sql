create table tb_form_pesquisa
(
   FPE_CODIGO varchar(32) not null,
   FPE_NOME varchar(100) DEFAULT NULL,
   FPE_BLOQUEIA_SISTEMA tinyint(1) NOT NULL DEFAULT 1,
   FPE_DT_CRIACAO DATETIME NOT NULL,
   FPE_DT_FIM date DEFAULT NULL,
   FPE_PUBLICADO tinyint(1) NOT NULL DEFAULT 0,
   FPE_JSON json NOT NULL,
   primary key (FPE_CODIGO)
) ENGINE = InnoDB;

create table tb_form_pesquisa_resposta
(
   FPR_CODIGO varchar(32) not null,
   FPE_CODIGO varchar(32) not null,
   USU_CODIGO varchar(32) not null,
   FPR_DT_CRIACAO datetime not null,
   FPR_JSON json NOT NULL,
   primary key (FPR_CODIGO)
) ENGINE = InnoDB;

alter table tb_form_pesquisa_resposta add constraint FK_FPR_FPE_1 foreign key (FPE_CODIGO)
   references tb_form_pesquisa (FPE_CODIGO) on delete restrict on update restrict;

alter table tb_form_pesquisa_resposta add constraint FK_FPR_USU_1 foreign key (USU_CODIGO)
   references tb_usuario (USU_CODIGO) on delete restrict on update restrict;
