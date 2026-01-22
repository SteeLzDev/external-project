-- DESENV-9305
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterServico', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'consultarCampo' WHERE ACR_CODIGO IN ('10974', '10975', '12253');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterServico', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'consultarServico' WHERE ACR_CODIGO IN ('10983', '10984', '12258');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterServico', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'consultarServicoOrg' WHERE ACR_CODIGO IN ('10987', '10988', '12260');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterServico', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_CODIGO IN ('10997', '10998', '12267');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterServico', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'listarPrioridadeServicos' WHERE ACR_CODIGO IN ('10978', '10979', '12255');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterServico', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'listarServicosCsa' WHERE ACR_CODIGO IN ('10200', '10201', '12015');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterServico', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'editarCampo' WHERE ACR_CODIGO IN ('10976', '10977', '12254');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterServico', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'editarServico' WHERE ACR_CODIGO IN ('10985', '10986', '12259');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterServico', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'editarServicoOrg' WHERE ACR_CODIGO IN ('10989', '10990', '12261');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterServico', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'efetivarAcaoServico' WHERE ACR_CODIGO IN ('14807', '14808', '14809');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterServico', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'inserirServico' WHERE ACR_CODIGO IN ('10993', '10994', '12264');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterServico', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'recarregarNseCodigo' WHERE ACR_CODIGO IN ('14800', '14801', '14802');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterServico', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'salvarServico' WHERE ACR_CODIGO IN ('11001', '11002', '12269');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterServico', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'excluirServico' WHERE ACR_CODIGO IN ('11003', '11004', '12270');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15135', '1', '23', '/v3/manterServico', 'acao', 'incluirServico', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15136', '3', '23', '/v3/manterServico', 'acao', 'incluirServico', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15137', '7', '23', '/v3/manterServico', 'acao', 'incluirServico', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15138', '1', '23', '/v3/manterServico', 'acao', 'bloquearServico', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15139', '3', '23', '/v3/manterServico', 'acao', 'bloquearServico', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15140', '7', '23', '/v3/manterServico', 'acao', 'bloquearServico', 1, 'S', 'S', NULL, 'S', '2');
