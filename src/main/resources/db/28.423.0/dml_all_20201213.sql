-- DESENV-9282
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/reimplantarConsignacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_CODIGO IN ('11644', '12493');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/reimplantarConsignacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'reimplantar' WHERE ACR_CODIGO IN ('11402', '12396');
