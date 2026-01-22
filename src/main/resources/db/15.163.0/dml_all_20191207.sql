-- DESENV-9227
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/listarCidades', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'acompanharLeilao' WHERE ACR_RECURSO = '/cidade/selecionaCidade.jsp' AND FUN_CODIGO = '379';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/listarCidades', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'solicitarLeilaoReverso' WHERE ACR_RECURSO = '/cidade/selecionaCidade.jsp' AND FUN_CODIGO = '399';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/listarCidades', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'solicitarConsignacao' WHERE ACR_RECURSO = '/cidade/selecionaCidade.jsp' AND FUN_CODIGO = '79';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/listarCidades', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'alterarConsignacao' WHERE ACR_RECURSO = '/cidade/selecionaCidade.jsp' AND FUN_CODIGO = '228';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/listarCidades', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'reservarConsignacao' WHERE ACR_RECURSO = '/cidade/selecionaCidade.jsp' AND FUN_CODIGO = '263';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/listarCidades', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'suspenderConsignacao' WHERE ACR_RECURSO = '/cidade/selecionaCidade.jsp' AND FUN_CODIGO = '382';
