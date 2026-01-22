-- DESENV-11189
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/importarHistorico', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'excluirArquivoHistorico' WHERE ACR_CODIGO = '14045';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/importarHistorico', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_CODIGO = '14046';

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15649', '7', '384', '/arquivos/download.jsp', 'tipo', 'historico', '1', 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15650', '7', '384', '/v3/importarHistorico', 'acao', 'importar', '1', 'S', 'S', null, 'N', '2');
