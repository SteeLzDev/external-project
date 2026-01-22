-- DESENV-11184
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/conciliarArquivo', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_CODIGO = '11797';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/conciliarArquivo', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_CODIGO = '11803';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/conciliarArquivo', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_CODIGO = '12540';

UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/conciliarArquivo', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'selecionar' WHERE ACR_CODIGO = '11796';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/conciliarArquivo', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'selecionar' WHERE ACR_CODIGO = '11802';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/conciliarArquivo', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'selecionar' WHERE ACR_CODIGO = '12539';

UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/conciliarArquivoMultiplo', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_CODIGO IN ('11804', '12541','11798');
UPDATE tb_acesso_recurso SET ACR_OPERACAO = 'listar' WHERE ACR_CODIGO IN ('14166', '14167');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15767', '1', '226', '/v3/conciliarArquivo', 'acao', 'processa', '1', 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15768', '2', '226', '/v3/conciliarArquivo', 'acao', 'processa', '1', 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15769', '7', '226', '/v3/conciliarArquivo', 'acao', 'processa', '1', 'S', 'S', null, 'N', '2');

UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/downloadArquivo', ACR_PARAMETRO = 'tipo', ACR_OPERACAO = 'conciliacao' WHERE ACR_CODIGO IN ('11795', '11801', '12538');
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/excluirArquivo', ACR_PARAMETRO = 'tipo', ACR_OPERACAO = 'conciliacao' WHERE ACR_CODIGO IN ('11794', '11800', '12537');
