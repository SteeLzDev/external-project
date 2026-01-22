-- DESENV-9310
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/editarCalendarioFolha', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE FUN_CODIGO = '300' AND ACR_RECURSO = '/calendario/edt_calendario_folha.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/editarCalendarioFolha', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'editar' WHERE FUN_CODIGO = '301' AND ACR_RECURSO = '/calendario/edt_calendario_folha.jsp';
