-- DESENV-11174
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/consultarPlanoDesconto', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_CODIGO = '12930';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/consultarPlanoDesconto', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'consultar' WHERE ACR_CODIGO = '12931';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/consultarPlanoDesconto', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'editar', FUN_CODIGO = '266' WHERE ACR_CODIGO = '12932';
