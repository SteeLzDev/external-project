ALTER TABLE tb_mensagem 
ADD MEN_PUSH_NOTIFICATION_SER CHAR(1) DEFAULT 'N' NOT NULL;

insert into tb_tipo_notificacao (tno_codigo, tno_descricao, tno_envio) 
values ('51', 'Push notification de mensagem para servidor', 'I');

INSERT INTO tb_funcao (FUN_CODIGO, GRF_CODIGO, FUN_DESCRICAO, FUN_PERMITE_BLOQUEIO, FUN_EXIGE_TMO, FUN_AUDITAVEL, FUN_RESTRITA_NCA, FUN_EXIGE_SEGUNDA_SENHA_CSE, FUN_EXIGE_SEGUNDA_SENHA_SUP, FUN_EXIGE_SEGUNDA_SENHA_ORG, FUN_EXIGE_SEGUNDA_SENHA_CSA, FUN_EXIGE_SEGUNDA_SENHA_COR)
VALUES ('570', '0', 'Enviar mensagem em massa via push notification', 'N', 'N', 'S', 'N', 'N', 'N', 'N', 'N', 'N');