-- DESENV-9280
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/reativarConsignacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'efetivarAcao'        WHERE FUN_CODIGO = '31' AND ACR_RECURSO = '/margem/efetiva_acao_consignacao.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/reativarConsignacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'confirmarReativacao' WHERE FUN_CODIGO = '31' AND ACR_RECURSO = '/margem/confirmar_reativacao.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/reativarConsignacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'reativarConsignacao' WHERE FUN_CODIGO = '31' AND ACR_RECURSO = '/margem/reativar_consignacao.jsp';
