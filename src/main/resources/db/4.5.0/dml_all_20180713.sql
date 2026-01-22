-- DESENV-9108

update tb_acesso_recurso set ACR_RECURSO = '/v3/transferirConsignacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' where FUN_CODIGO = '205' and ACR_RECURSO = '/margem/transferir_consignacao.jsp';

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14865', '1', '205', '/v3/transferirConsignacao', 'acao', 'pesquisar', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14866', '7', '205', '/v3/transferirConsignacao', 'acao', 'pesquisar', 1, 'S', 'S', null, 'N', '2');

update tb_acesso_recurso set ACR_RECURSO = '/v3/transferirConsignacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'listarConsignacoes' where FUN_CODIGO = '205' and ACR_PARAMETRO = 'flow' and ACR_OPERACAO = 'start' and ACR_RECURSO = '/margem/transferir_consignacao2.jsp';

update tb_acesso_recurso set ACR_RECURSO = '/v3/transferirConsignacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'transferir' where FUN_CODIGO = '205' and ACR_PARAMETRO = 'flow' and ACR_OPERACAO = 'endpoint' and ACR_RECURSO = '/margem/transferir_consignacao2.jsp';

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('14867', '1', '205', '/v3/transferirConsignacao', 'acao', 'efetivarAcao', 1, 'S', 'S', null, 'N', '2'); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('14868', '7', '205', '/v3/transferirConsignacao', 'acao', 'efetivarAcao', 1, 'S', 'S', null, 'N', '2');

delete from tb_ajuda where acr_codigo in (select acr_codigo from tb_acesso_recurso where FUN_CODIGO = '205' and ACR_PARAMETRO = 'tipo' and ACR_OPERACAO = 'transferir_consignacao');
delete from tb_acesso_recurso where FUN_CODIGO = '205' and ACR_PARAMETRO = 'tipo' and ACR_OPERACAO = 'transferir_consignacao';
