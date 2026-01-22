-- DESENV-11194
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/autenticar', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciarAlteracaoLoginSer' WHERE ACR_RECURSO = '/login/alterar_login_servidor.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/autenticar', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'selecionarServidor' WHERE ACR_RECURSO = '/login/selecionar_servidor.jsp';

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15478', '6', null, '/v3/autenticar', 'acao', 'alterarLoginSer', 1, 'S', 'S', null, 'N', '2');
