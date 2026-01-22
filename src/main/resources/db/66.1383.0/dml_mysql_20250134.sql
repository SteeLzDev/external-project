-- DESENV-22641
-- MYSQL
INSERT INTO tb_agendamento (AGD_CODIGO, TAG_CODIGO, SAG_CODIGO, USU_CODIGO, AGD_DESCRICAO, AGD_JAVA_CLASS_NAME, AGD_DATA_CADASTRO, AGD_DATA_PREVISTA, REL_CODIGO)
VALUES ('58', '1', '1', '1', 'Envia notificação de prazo de expiração de senha', 'com.zetra.econsig.job.jobs.EnviaNotificacaoPrazoExpiracaoSenhaJob', CURDATE(), CURDATE(), NULL);

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) VALUES ('enviarEmailPrazoExpiracaoSenha', '<@cse_nome>: Sua senha irá expirar', 'Prezado (a) <@usu_nome>,<br>Sua senha irá expirar em <@qtd_dias_expiracao_senha> dias. Considere alterá-la!<br>Atenciosamente,<br> <@logoSistema>');

INSERT INTO tb_tipo_notificacao (TNO_CODIGO, TNO_DESCRICAO, TNO_ENVIO) VALUES ('47', 'E-mail de notificação de prazo de expiração de senha', 'I');

