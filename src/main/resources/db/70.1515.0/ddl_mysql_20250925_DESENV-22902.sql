ALTER TABLE tb_destinatario_email_csa_svc
ADD COLUMN FUN_CODIGO VARCHAR(32) NOT NULL,
ADD COLUMN PAP_CODIGO VARCHAR(32) NOT NULL;

/*==============================================================*/
/* Index: IDX_FUN_DCS_3                                         */
/*==============================================================*/
create index IDX_FUN_DCS_3 on tb_destinatario_email_csa_svc (
   FUN_CODIGO asc
);

/*==============================================================*/
/* Index: IDX_PAP_DCS_4                                         */
/*==============================================================*/
create index IDX_PAP_DCS_4 on tb_destinatario_email_csa_svc (
   PAP_CODIGO asc
);

ALTER TABLE tb_destinatario_email_csa_svc
DROP PRIMARY KEY;

ALTER TABLE tb_destinatario_email_csa_svc
ADD CONSTRAINT PK_DCS PRIMARY KEY (CSA_CODIGO, SVC_CODIGO, FUN_CODIGO, PAP_CODIGO);

ALTER TABLE tb_destinatario_email_csa_svc ADD CONSTRAINT FK_FUN_DCS_3 FOREIGN KEY (FUN_CODIGO)
      REFERENCES tb_funcao (FUN_CODIGO) ON DELETE restrict ON UPDATE restrict;

ALTER TABLE tb_destinatario_email_csa_svc ADD CONSTRAINT FK_PAP_DCS_4 FOREIGN KEY (PAP_CODIGO)
      REFERENCES tb_papel (PAP_CODIGO) ON DELETE restrict ON UPDATE restrict;