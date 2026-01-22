-- DESENV-9269
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/listarCompulsorio', ACR_PARAMETRO = 'acao' WHERE ACR_RECURSO = '/compulsorios/lst_consignacao.jsp';
