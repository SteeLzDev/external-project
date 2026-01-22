-- DESENV-9302
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/editarLimiteTaxas', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_RECURSO = '/limite_taxa_juros/lst_limite_taxa_juros.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/editarLimiteTaxas', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'incluir' WHERE ACR_RECURSO = '/limite_taxa_juros/edt_limite_taxa_juros.jsp' AND ACR_OPERACAO = 'incluir';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/editarLimiteTaxas', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'salvar'  WHERE ACR_RECURSO = '/limite_taxa_juros/edt_limite_taxa_juros.jsp' AND ACR_OPERACAO = 'editar';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/editarLimiteTaxas', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'excluir' WHERE acr_codigo IN ('13123', '13127');
