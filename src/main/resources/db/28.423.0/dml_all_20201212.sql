-- DESENV-9273
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/descancelarConsignacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'efetivarAcao' WHERE ACR_CODIGO IN ('13116', '13117');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/descancelarConsignacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'descancelar' WHERE ACR_CODIGO IN ('13118', '13119');
