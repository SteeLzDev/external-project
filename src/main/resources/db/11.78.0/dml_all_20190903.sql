-- DESENV-9214
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/editarParamMargem', ACR_OPERACAO = 'iniciar', ACR_PARAMETRO = 'acao' WHERE ACR_CODIGO IN ('11463','12420');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/editarParamMargem', ACR_OPERACAO = 'salvar',  ACR_PARAMETRO = 'acao' WHERE ACR_CODIGO IN ('11297','12353');
