-- DESENV-11183
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/exportarMovimento', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_RECURSO = '/folha/lst_exp_mov_fin.jsp';

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15659', '1', '33', '/v3/exportarMovimento', 'acao', 'reexportar', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15660', '3', '33', '/v3/exportarMovimento', 'acao', 'reexportar', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15661', '7', '33', '/v3/exportarMovimento', 'acao', 'reexportar', 1, 'S', 'S', NULL, 'N', '2');


UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/processarMovimento', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_RECURSO = '/folha/prc_exp_mov_fin.jsp';

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15662', '1', '33', '/v3/processarMovimento', 'acao', 'processar', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15663', '3', '33', '/v3/processarMovimento', 'acao', 'processar', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15664', '7', '33', '/v3/processarMovimento', 'acao', 'processar', 1, 'S', 'S', NULL, 'N', '2');
