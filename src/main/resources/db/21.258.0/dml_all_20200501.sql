-- DESENV-9306
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/cadastrarAnaliseRiscoServidor', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar', ACR_FIM_FLUXO = 'N' WHERE ACR_CODIGO = '14388';

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15675', '2', '404', '/v3/cadastrarAnaliseRiscoServidor', 'acao', 'salvar', 1, 'S', 'S', 'S', '2');
