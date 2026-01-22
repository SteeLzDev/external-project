-- DESENV-9270
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/alterarMultiplasConsignacoes', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciarAlteracaoMultiplosAdes' WHERE ACR_RECURSO = '/margem/alterar_multiplos_ades.jsp' AND ACR_PARAMETRO = 'tipo' AND ACR_OPERACAO = 'consultar';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/alterarMultiplasConsignacoes', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'salvar' WHERE ACR_RECURSO = '/margem/alterar_multiplos_ades.jsp' AND ACR_PARAMETRO = 'tipo' AND ACR_OPERACAO = 'editar';
