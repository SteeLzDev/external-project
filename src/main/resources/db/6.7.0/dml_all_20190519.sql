-- DESENV-9210
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/acompanharPortabilidade', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'listarHistLiquidacoesAntecipadas' WHERE ACR_RECURSO = '/margem/hist_liq_antecipadas.jsp' AND FUN_CODIGO = '139';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/reservarMargem', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'listarHistLiquidacoesAntecipadas' WHERE ACR_RECURSO = '/margem/hist_liq_antecipadas.jsp' AND FUN_CODIGO = '57'; 
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/renegociarConsignacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'listarHistLiquidacoesAntecipadas' WHERE ACR_RECURSO = '/margem/hist_liq_antecipadas.jsp' AND FUN_CODIGO = '60'; 
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/comprarConsignacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'listarHistLiquidacoesAntecipadas' WHERE ACR_RECURSO = '/margem/hist_liq_antecipadas.jsp' AND FUN_CODIGO = '140'; 
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/consultarMargem', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'listarHistLiquidacoesAntecipadas' WHERE ACR_RECURSO = '/margem/hist_liq_antecipadas.jsp' AND FUN_CODIGO = '76';
