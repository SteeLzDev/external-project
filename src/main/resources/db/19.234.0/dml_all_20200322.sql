-- DESENV-11167
UPDATE tb_acesso_recurso set ACR_RECURSO = '/v3/recalcularMargemGeral', ACR_PARAMETRO = 'acao' , ACR_OPERACAO = 'iniciar' WHERE ACR_CODIGO IN ('11929', '10003', '11285');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15628', '1', '202', '/v3/recalcularMargemGeral', 'acao', 'confirmar', '1', 'S', 'S', null, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15629', '3', '202', '/v3/recalcularMargemGeral', 'acao', 'confirmar', '1', 'S', 'S', null, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15630', '7', '202', '/v3/recalcularMargemGeral', 'acao', 'confirmar', '1', 'S', 'S', null, 'S', '2');
