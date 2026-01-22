-- DESENV-22933
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('982', 'Enviar e-mail para os responsáveis quando houver bloqueio de usuário por ultrapassar o limite de tentativa de login sem sucesso', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('982', '1', 'N');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO)
VALUES ('enviarEmailCseBloqueioUsuario', '<@nome_consignante>: Bloqueio automático de usuário', 'Prezado (a) <@nome_consignante>, <br> Informamos que o usuário abaixo foi bloqueado após atingir o limite máximo de tentativas de login sem sucesso. <br> <@dados_usuario> <br> Atenciosamente,<br> <@logoSistema>');

INSERT INTO tb_tipo_notificacao (TNO_CODIGO, TNO_DESCRICAO, TNO_ENVIO)
VALUES ('48', 'E-mail de notificação a CSE sobre bloqueio de usuários', 'I');

