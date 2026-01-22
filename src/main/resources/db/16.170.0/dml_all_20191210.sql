-- DESENV-11195
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/cadastrarSenhaServidor', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_RECURSO = '/servidor/cad_servidor.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/cadastrarSenhaServidor', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'selecionarServidor' WHERE ACR_RECURSO = '/servidor/sel_servidor.jsp';

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15359', NULL, NULL, '/v3/cadastrarSenhaServidor', 'acao', 'salvar', 1, 'S', 'N', NULL, 'N', '0');
