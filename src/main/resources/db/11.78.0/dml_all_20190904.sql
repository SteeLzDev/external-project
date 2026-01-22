-- DESENV-11163
UPDATE tb_acesso_recurso set ACR_RECURSO = '/v3/manterParamConsignante', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar', ACR_FIM_FLUXO = 'N' WHERE ACR_CODIGO IN ('12419', '11462');
UPDATE tb_acesso_recurso set ACR_RECURSO = '/v3/manterParamConsignante', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'salvar',  ACR_FIM_FLUXO = 'S' WHERE ACR_CODIGO IN ('10167', '11998');
UPDATE tb_acesso_recurso set ACR_RECURSO = '/v3/manterParamConsignante', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'listarHistoricoParametro' WHERE ACR_CODIGO = '14122';
