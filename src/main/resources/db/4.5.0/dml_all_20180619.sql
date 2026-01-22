-- DESENV-8798

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14859', '6', '79', '/v3/simularConsignacao', 'acao', 'iniciarLeilao', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14860', '6', '399', '/v3/solicitarLeilao', 'acao', 'iniciarLeilao', 1, 'S', 'S', NULL, 'N', '2');

-- REFACTORING BACK-END
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/solicitarLeilao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'listarServicos' WHERE ACR_CODIGO = '14313';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/solicitarLeilao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciarSimulacao' WHERE ACR_CODIGO = '14314';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/solicitarLeilao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'simular' WHERE ACR_CODIGO = '14315';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/solicitarLeilao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'confirmar' WHERE ACR_CODIGO = '14316';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/solicitarLeilao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'emitirBoleto' WHERE ACR_CODIGO = '14317';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/solicitarLeilao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'emitirBoletoExterno' WHERE ACR_CODIGO = '14318';
