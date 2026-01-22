-- DESENV-9274
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/duplicarParcela', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_CODIGO = '10483';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/duplicarParcela', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_CODIGO = '10484';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/duplicarParcela', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_CODIGO = '10485';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/duplicarParcela', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_CODIGO = '12100';

UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/downloadArquivo' WHERE ACR_RECURSO = '/arquivos/download.jsp' AND ACR_OPERACAO = 'duplicacao';

UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/duplicarParcela', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'excluirArquivo' WHERE ACR_CODIGO = '12984';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/duplicarParcela', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'excluirArquivo' WHERE ACR_CODIGO = '12985';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/duplicarParcela', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'excluirArquivo' WHERE ACR_CODIGO = '12986';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/duplicarParcela', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'excluirArquivo' WHERE ACR_CODIGO = '12987';


INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15807', '1', '114', '/v3/duplicarParcela', 'acao', 'validarArquivo', 1, 'S', 'S', null, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15808', '2', '114', '/v3/duplicarParcela', 'acao', 'validarArquivo', 1, 'S', 'S', null, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15809', '3', '114', '/v3/duplicarParcela', 'acao', 'validarArquivo', 1, 'S', 'S', null, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15810', '7', '114', '/v3/duplicarParcela', 'acao', 'validarArquivo', 1, 'S', 'S', null, 'S', '2');


INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15811', '1', '114', '/v3/duplicarParcela', 'acao', 'processarArquivo', 1, 'S', 'S', null, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15812', '2', '114', '/v3/duplicarParcela', 'acao', 'processarArquivo', 1, 'S', 'S', null, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15813', '3', '114', '/v3/duplicarParcela', 'acao', 'processarArquivo', 1, 'S', 'S', null, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15814', '7', '114', '/v3/duplicarParcela', 'acao', 'processarArquivo', 1, 'S', 'S', null, 'S', '2');
