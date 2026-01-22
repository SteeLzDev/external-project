-- DESENV-11186
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/importarAdequacaoMargem', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar'   WHERE ACR_RECURSO = '/adequacao/adequacao.jsp' AND ACR_OPERACAO = 'iniciar';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/importarAdequacaoMargem', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'processar' WHERE ACR_RECURSO = '/adequacao/adequacao.jsp' AND ACR_OPERACAO = 'processar';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/importarAdequacaoMargem', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'validar'   WHERE ACR_RECURSO = '/adequacao/adequacao.jsp' AND ACR_OPERACAO = 'validar';
UPDATE tb_acesso_recurso set ACR_RECURSO = '/v3/excluirArquivo',  ACR_OPERACAO = 'adequacao' WHERE FUN_CODIGO = '376' AND ACR_RECURSO = '/arquivos/delete.jsp';
UPDATE tb_acesso_recurso set ACR_RECURSO = '/v3/downloadArquivo', ACR_OPERACAO = 'adequacao' WHERE FUN_CODIGO = '376' AND ACR_RECURSO = '/arquivos/download.jsp';
