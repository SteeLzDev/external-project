-- DESENV-9278
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/listarBloqueiosConsignataria', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_RECURSO = '/consignataria/lst_bloqueios_csa.jsp';
