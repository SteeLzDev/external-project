-- DESENV-9267
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/desliquidarConsignacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'desliquidar' WHERE ACR_RECURSO = '/margem/desliquidar_consignacao.jsp' AND FUN_CODIGO = '102';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/desliquidarConsignacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'confirmarDesliquidacao' WHERE ACR_RECURSO = '/margem/confirmar_desliquidacao.jsp' AND FUN_CODIGO = '264';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/desliquidarConsignacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'efetivarAcao' WHERE ACR_RECURSO = '/margem/efetiva_acao_consignacao.jsp' AND FUN_CODIGO = '102';
