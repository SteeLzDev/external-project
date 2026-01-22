-- DESENV-8927

update tb_acesso_recurso set ACR_RECURSO = '/v3/manterEstabelecimento', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' where ACR_RECURSO = '/estabelecimento/lst_estabelecimento.jsp';

update tb_acesso_recurso set ACR_RECURSO = '/v3/manterEstabelecimento', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'consultar', ACR_FIM_FLUXO = 'N' where ACR_RECURSO = '/estabelecimento/edt2_estabelecimento.jsp' and ACR_PARAMETRO = 'tipo' and ACR_OPERACAO = 'consultar';

update tb_acesso_recurso set ACR_RECURSO = '/v3/manterEstabelecimento', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'salvar' where ACR_RECURSO = '/estabelecimento/edt2_estabelecimento.jsp' and ACR_PARAMETRO = 'tipo' and ACR_OPERACAO = 'editar';

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES('14861', '1', '74', '/v3/manterEstabelecimento', 'acao', 'deletar', 1, 'S', 'S', null, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES('14862', '7', '74', '/v3/manterEstabelecimento', 'acao', 'deletar', 1, 'S', 'S', null, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES('14863', '1', '16', '/v3/manterEstabelecimento', 'acao', 'ativarDesativar', 1, 'S', 'S', null, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES('14864', '7', '16', '/v3/manterEstabelecimento', 'acao', 'ativarDesativar', 1, 'S', 'S', null, 'S', '2');

delete from tb_ajuda where acr_codigo in (select acr_codigo from tb_acesso_recurso where ACR_RECURSO = '/estabelecimento/modifica_estabelecimento.jsp');
delete from tb_acesso_recurso where ACR_RECURSO = '/estabelecimento/modifica_estabelecimento.jsp';
