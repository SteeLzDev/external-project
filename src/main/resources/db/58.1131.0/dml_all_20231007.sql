-- DESENV-20364
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16861', '6', '532', '/v3/renegociarConsignacao', 'acao', 'incluirReserva', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16862', '6', '532', '/v3/solicitarPortabilidade', 'acao', 'confirmarSimulacaoPortabilidade', 1, 'S', 'S', NULL, 'N', '2');

UPDATE tb_tipo_param_sist_consignante SET TPC_DESCRICAO = 'Habilitar ranking de consignatárias na solicitação de portabilidade por servidor' WHERE TPC_CODIGO = '937';

