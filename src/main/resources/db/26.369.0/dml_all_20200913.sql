-- DESENV-14336
INSERT INTO tb_tipo_param_svc (TPS_CODIGO, TPS_DESCRICAO, TPS_CSE_ALTERA, TPS_SUP_ALTERA, TPS_CSA_ALTERA, TPS_PODE_SOBREPOR_RSE)
VALUES ('288', 'Mensagem exibida para o servidor na solicitação de contratos de outro serviço de mesma natureza', 'N', 'N', 'N', NULL);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15989', '6', '63', '/v3/simularConsignacao', 'acao', 'escolherOutroSvc', 1, 'S', 'S', NULL, 'N', '2');
