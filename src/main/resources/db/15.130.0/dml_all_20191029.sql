-- DESENV-9215
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/simularRenegociacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_CODIGO IN ('13083'); 
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/simularRenegociacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'listarCsaRenegociacao' WHERE ACR_CODIGO IN ('13084'); 
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/simularRenegociacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'simular' WHERE ACR_CODIGO IN ('13087'); 
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/simularRenegociacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'confirmar' WHERE ACR_CODIGO IN ('13088'); 
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/simularRenegociacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'incluirReserva' WHERE ACR_CODIGO IN ('13089'); 
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/simularRenegociacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'emitirBoleto' WHERE ACR_CODIGO IN ('13090'); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15289', '6', '303', '/v3/simularRenegociacao', 'acao', 'visualizarRanking', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15290', '6', '303', '/v3/simularRenegociacao', 'acao', 'emitirBoletoExterno', 1, 'S', 'S', NULL, 'N', '2');
