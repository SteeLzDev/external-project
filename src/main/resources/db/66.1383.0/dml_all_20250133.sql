-- DESENV-22748
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/manterTermoAdesaoServico' WHERE ACR_RECURSO = '/v3/manterTermoAdesao';

UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/visualizarTermoAdesaoServico' WHERE ACR_RECURSO = '/v3/visualizarTermoAdesao';


INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('17070', NULL, NULL, '/v3/informarTermoAdesao', 'acao', 'iniciar', 1, 'S', 'S', NULL, 'N', '0');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('17071', NULL, NULL, '/v3/informarTermoAdesao', 'acao', 'salvar', 1, 'S', 'S', NULL, 'N', '2');

