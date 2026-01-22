-- DESENV-9313
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterCoeficienteCorrecao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE FUN_CODIGO = '154' AND ACR_RECURSO = '/correcao/lst_coeficiente_correcao.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterCoeficienteCorrecao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'editar'  WHERE FUN_CODIGO = '154' AND ACR_RECURSO = '/correcao/edt_coeficiente_correcao.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterCoeficienteCorrecao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'salvar'  WHERE FUN_CODIGO = '147' AND ACR_RECURSO = '/correcao/edt_coeficiente_correcao.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterCoeficienteCorrecao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'excluir' WHERE FUN_CODIGO = '147' AND ACR_RECURSO = '/correcao/remove_coeficiente_correcao.jsp';
