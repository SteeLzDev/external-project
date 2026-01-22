-- DESENV-11162
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/consultarArquivoXml', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_CODIGO = '13078';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/consultarArquivoXml', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'downloadArquivo' WHERE ACR_CODIGO = '13079'; 
