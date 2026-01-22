-- DESENV-13254
UPDATE tb_tipo_param_sist_consignante SET TPC_DESCRICAO = 'Habilita o extrato de consignatária/correspondente' WHERE TPC_CODIGO = '656';

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15647', '4', null, '/v3/atualizarExtratoDiaAjax', null, null, '1', 'S', 'S', null, 'N', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15648', '4', null, '/v3/listarExtrato', null, null, '1', 'S', 'S', null, 'N', 2);
