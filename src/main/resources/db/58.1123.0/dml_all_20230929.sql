-- DESENV-20372
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16845', '1', '536', '/v3/listarRelatorioCustomizado', 'acao', 'iniciar', 1, 'S', 'S', '275', 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16846', '1', '536', '/v3/downloadArquivo', 'tipo', 'relatorioCustomizadoCse', 1, 'S', 'S', NULL, 'N', '2');
	
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16847', '7', '536', '/v3/downloadArquivo', 'tipo', 'relatorioCustomizadoCse', 1, 'S', 'S', NULL, 'N', '2');

UPDATE tb_acesso_recurso SET ACR_OPERACAO = 'relatorioCustomizadoCsa' WHERE ACR_OPERACAO = 'relatorioCustomizado' AND PAP_CODIGO = '7';

UPDATE tb_acesso_recurso SET ACR_OPERACAO = 'relatorioCustomizadoCsa' WHERE ACR_OPERACAO = 'relatorioCustomizado' AND PAP_CODIGO = '2';

