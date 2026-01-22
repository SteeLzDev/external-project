-- DESENV-11191
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/processarLoteInconsistencia', ACR_PARAMETRO = 'acao' WHERE ACR_CODIGO IN ('14026', '14027');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/downloadArquivo' WHERE ACR_CODIGO = '14029';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/processarLoteInconsistencia', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'excluir' WHERE ACR_CODIGO = '14028';
