-- DESENV-21020
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16938', '6', null, '/v3/simularConsignacao', 'acao', 'validaOtpServidorSimularConsignacao', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16939', '6', null, '/v3/simularConsignacao', 'acao', 'erroValidaOtpServidorSimularConsignacao', 1, 'S', 'S', NULL, 'S', '2');

