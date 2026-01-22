-- DESENV-13274
UPDATE tb_acesso_recurso SET ACR_OPERACAO = 'iniciar' WHERE ACR_RECURSO = '/v3/listarContratosBeneficioPendentes';
