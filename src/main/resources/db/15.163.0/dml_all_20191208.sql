-- DESENV-9286
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/acompanharLeilao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE FUN_CODIGO = '379' AND ACR_RECURSO = '/leilao/acompanhamento.jsp';

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15322', '1', '379', '/v3/acompanharLeilao', 'acao', 'pesquisar', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15323', '2', '379', '/v3/acompanharLeilao', 'acao', 'pesquisar', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15324', '3', '379', '/v3/acompanharLeilao', 'acao', 'pesquisar', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15325', '4', '379', '/v3/acompanharLeilao', 'acao', 'pesquisar', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15326', '7', '379', '/v3/acompanharLeilao', 'acao', 'pesquisar', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15327', '6', '379', '/v3/acompanharLeilao', 'acao', 'pesquisar', 1, 'S', 'S', NULL, 'N', '2');

UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/editarPropostaLeilao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_RECURSO = '/leilao/edt_proposta.jsp' AND ACR_OPERACAO = 'consultar';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/editarPropostaLeilao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'salvar' WHERE ACR_RECURSO = '/leilao/edt_proposta.jsp' AND ACR_OPERACAO = 'editar';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/editarPropostaLeilao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'calcularValorPrestacao' WHERE ACR_RECURSO = '/leilao/calcularValorPrestacao.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/acompanharLeilao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciarCriacaoFiltro' WHERE ACR_RECURSO = '/leilao/edt_filtro.jsp';

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15328', '2', '379', '/v3/acompanharLeilao', 'acao', 'salvarFiltro', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15329', '4', '379', '/v3/acompanharLeilao', 'acao', 'salvarFiltro', 1, 'S', 'S', null, 'N', '2');

UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/acompanharLeilao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'visualizarFiltro' WHERE ACR_RECURSO = '/leilao/acompanhamento_filtros.jsp';

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15330', '2', '379', '/v3/acompanharLeilao', 'acao', 'excluirFiltro', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15331', '4', '379', '/v3/acompanharLeilao', 'acao', 'excluirFiltro', 1, 'S', 'S', null, 'N', '2');

UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/editarPropostaLeilao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'aprovarProposta' WHERE ACR_RECURSO = '/leilao/aprovar_proposta.jsp';
