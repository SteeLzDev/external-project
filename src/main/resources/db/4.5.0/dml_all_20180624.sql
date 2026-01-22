-- DESENV-8912

UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/consultarComposicaoMargem', ACR_PARAMETRO = 'acao' WHERE ACR_RECURSO = '/margem/composicao_margem.jsp';

UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/consultarComposicaoMargem', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE FUN_CODIGO = '76' AND ACR_OPERACAO = 'comp_margem' AND ACR_RECURSO = '/margem/pesquisa.jsp';

UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/consultarComposicaoMargem', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'pesquisarServidor' WHERE FUN_CODIGO = '76' AND ACR_OPERACAO = 'comp_margem' AND ACR_RECURSO = '/margem/seleciona_servidor.jsp';

UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/consultarComposicaoMargem', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'listar' WHERE FUN_CODIGO = '76' AND ACR_OPERACAO = 'comp_margem' AND ACR_RECURSO = '/margem/consignacao.jsp';
