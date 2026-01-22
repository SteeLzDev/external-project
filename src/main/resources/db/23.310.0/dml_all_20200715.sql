-- DESENV-11179
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/lancarDespesaComum', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_CODIGO = '12952';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/lancarDespesaComum', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'inserirDados' WHERE ACR_CODIGO = '12953';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/lancarDespesaComum', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'listarPermissionario' WHERE ACR_CODIGO = '12954';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/lancarDespesaComum', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'lancarDespesaComum' WHERE ACR_CODIGO = '12955';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/lancarDespesaComum', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'editarDespesaComum' WHERE ACR_CODIGO = '12956';
