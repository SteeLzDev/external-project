-- DESENV-9312
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/editarCalendario', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE FUN_CODIGO = '180' AND ACR_RECURSO = '/calendario/lst_calendario.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/editarCalendario', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'editar'  WHERE FUN_CODIGO = '179' AND ACR_RECURSO = '/calendario/edt_calendario.jsp';

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15273', '1', '179', '/v3/editarCalendario', 'acao', 'salvar', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15274', '7', '179', '/v3/editarCalendario', 'acao', 'salvar', 1, 'S', 'S', NULL, 'S', '2');
