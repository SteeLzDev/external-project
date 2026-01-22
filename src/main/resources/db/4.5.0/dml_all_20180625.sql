-- DESENV-8914

update tb_acesso_recurso set ACR_RECURSO = '/v3/declararMargem', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' where ACR_RECURSO = '/margem/declaracao_margem.jsp';
