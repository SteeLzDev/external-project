-- DESENV-8109

UPDATE tb_acesso_recurso SET FUN_CODIGO = null, ACR_OPERACAO = 'ajudar', ACR_PARAMETRO = 'acao', ACR_RECURSO = '/v3/exibirAjudaMarkdown' WHERE ACR_CODIGO IN ('13564', '13563', '13562', '13561', '13560', '13559');
