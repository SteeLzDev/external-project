-- DESENV-9271
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/anexarPagamentoConsignacao', ACR_PARAMETRO = 'acao' , ACR_OPERACAO = 'iniciar' WHERE ACR_CODIGO IN ('13901', '14228', '14229');
