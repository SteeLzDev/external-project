-- DESENV-11200
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/editarRelatorio', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_CODIGO = '12881';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/editarRelatorio', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciarEdicao' WHERE ACR_CODIGO = '12882';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/editarRelatorio', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'salvar' WHERE ACR_CODIGO = '12883';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/editarRelatorio', ACR_PARAMETRO = 'acao' WHERE ACR_CODIGO = '12884';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/editarRelatorio', ACR_PARAMETRO = 'acao' WHERE ACR_CODIGO = '12885';
