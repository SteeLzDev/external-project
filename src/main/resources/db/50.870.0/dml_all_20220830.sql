-- DESENV-18608
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16651', '1', '528', '/v3/visualizarDashboardCredenciamento', 'acao', 'detalharCredenciamento', 1, 'S', 'S', NULL, 'N', '2'); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16652', '7', '528', '/v3/visualizarDashboardCredenciamento', 'acao', 'uploadArquivoCredenciamento', 1, 'S', 'S', NULL, 'N', '2');