-- DESENV-11176
update tb_acesso_recurso SET ACR_RECURSO = '/v3/consultarPlanoDesconto', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'bloquear' WHERE ACR_CODIGO = '12933';
update tb_acesso_recurso SET ACR_RECURSO = '/v3/consultarPlanoDesconto', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'excluir'  WHERE ACR_CODIGO = '12934';
