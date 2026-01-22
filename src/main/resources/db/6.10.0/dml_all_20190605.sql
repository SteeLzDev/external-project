-- DESENV-9216
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterConsignataria', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'editarCnvVincServidor' WHERE ACR_CODIGO IN ('10173');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterConsignataria', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'editarServico' WHERE ACR_CODIGO IN ('10178', '10179', '12004', '10180');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterConsignataria', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'listarServicos' WHERE ACR_CODIGO IN ('10189');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterConsignataria', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'inserirObsBloqueio' WHERE ACR_CODIGO IN ('11292', '11293', '12350');

UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterTermoAdesao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'editarTermoAdesao' WHERE ACR_CODIGO IN ('11277', '12346', '11279');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterTermoAdesao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'excluirTermoAdesao' WHERE ACR_CODIGO IN ('11280', '12347', '11282');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterTermoAdesao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'listarServicos' WHERE ACR_CODIGO IN ('11276', '12345', '11278');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15141', '1', '21', '/v3/manterConsignataria', 'acao', 'salvarServico', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15142', '3', '21', '/v3/manterConsignataria', 'acao', 'salvarServico', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15143', '7', '21', '/v3/manterConsignataria', 'acao', 'salvarServico', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15144', '2', '85', '/v3/manterConsignataria', 'acao', 'salvarServico', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15145', '2', '85', '/v3/manterConsignataria', 'acao', 'salvarCnvVincServidor', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15146', '1', '21', '/v3/manterTermoAdesao', 'acao', 'salvarTermoAdesao', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15147', '7', '21', '/v3/manterTermoAdesao', 'acao', 'salvarTermoAdesao', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15148', '2', '85', '/v3/manterTermoAdesao', 'acao', 'salvarTermoAdesao', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15149', '1', '21', '/v3/manterTermoAdesao', 'acao', 'revisarTermoAdesao', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15150', '7', '21', '/v3/manterTermoAdesao', 'acao', 'revisarTermoAdesao', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15151', '2', '85', '/v3/manterTermoAdesao', 'acao', 'revisarTermoAdesao', 1, 'S', 'S', NULL, 'N', '2');
