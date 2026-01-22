-- DESENV-9307
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/consultarConveniosBloqueados', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE FUN_CODIGO = '194';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/consultarConveniosBloqueados', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE FUN_CODIGO = '76' AND ACR_RECURSO = '/margem/convenios_bloqueados.jsp';
