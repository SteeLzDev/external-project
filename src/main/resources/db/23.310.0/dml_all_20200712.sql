-- DESENV-11170
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/consultarDespesaComum', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_CODIGO = '12974';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/consultarDespesaComum', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'consultar' WHERE ACR_CODIGO = '12975';
