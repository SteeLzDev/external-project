-- DESENV-11172
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/editarEnderecoConjHab', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_CODIGO = '12935';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/editarEnderecoConjHab', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'excluir' WHERE ACR_CODIGO = '12936';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/editarEnderecoConjHab', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciarEdicao' WHERE ACR_CODIGO = '12937';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/editarEnderecoConjHab', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'salvarEdicao' WHERE ACR_CODIGO = '12938';
