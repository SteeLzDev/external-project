-- DESENV-11197
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/efetivarPrimeiroAcesso', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_CODIGO = '14230';
