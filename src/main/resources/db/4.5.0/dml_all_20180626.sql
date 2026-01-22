-- DESENV-8916

update tb_acesso_recurso set ACR_RECURSO = '/v3/consultarDetalhesCsa', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'visualizar' where ACR_CODIGO = '13049';
update tb_acesso_recurso set ACR_RECURSO = '/v3/consultarDetalhesCsa', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'listar' where ACR_CODIGO = '13050';
