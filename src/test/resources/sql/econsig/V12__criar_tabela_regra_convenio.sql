alter table tb_consignataria
   add CSA_EMAIL_NOTIFICACAO_RCO varchar(100);

create table tb_regra_convenio
(
   RCO_CODIGO           varchar(32) not null,
   CSA_CODIGO           varchar(32) not null,
   RCO_CAMPO_CODIGO     int not null,
   RCO_CAMPO_NOME       varchar(100) not null,
   RCO_CAMPO_VALOR      varchar(100) not null,
   primary key (RCO_CODIGO)
) ENGINE = InnoDB;

alter table tb_regra_convenio add constraint FK_R_985 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;


ALTER TABLE tb_regra_convenio
ADD COLUMN svc_codigo VARCHAR(32) NULL,
ADD COLUMN org_codigo VARCHAR(32) NULL,
ADD COLUMN mar_codigo SMALLINT NULL;


ALTER TABLE tb_regra_convenio
ADD CONSTRAINT fk_svc_codigo FOREIGN KEY (svc_codigo) REFERENCES tb_servico(svc_codigo);

ALTER TABLE tb_regra_convenio
ADD CONSTRAINT fk_org_codigo FOREIGN KEY (org_codigo) REFERENCES tb_orgao(org_codigo);

ALTER TABLE tb_regra_convenio
ADD CONSTRAINT fk_mar_codigo FOREIGN KEY (mar_codigo) REFERENCES tb_margem(mar_codigo);


ALTER TABLE tb_regra_convenio MODIFY COLUMN RCO_CAMPO_CODIGO VARCHAR(32);


ALTER TABLE tb_regra_convenio MODIFY RCO_CAMPO_NOME VARCHAR(255);     