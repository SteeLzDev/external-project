-- DESENV-9304
UPDATE tb_acesso_recurso SET ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar', ACR_RECURSO = '/v3/listarTaxaJuros' WHERE ACR_RECURSO = '/juros/ranking.jsp';
