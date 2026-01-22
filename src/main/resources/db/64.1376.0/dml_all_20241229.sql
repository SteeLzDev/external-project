-- DESENV-22622
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('17038', '1', '473', '/v3/executarInclusaoJudicial', 'acao', 'reservarMargem', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('17039', '7', '473', '/v3/executarInclusaoJudicial', 'acao', 'reservarMargem', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('17040', '2', '473', '/v3/executarInclusaoJudicial', 'acao', 'reservarMargem', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('17041', '1', '473', '/v3/executarInclusaoJudicial', 'acao', 'incluirReserva', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('17042', '7', '473', '/v3/executarInclusaoJudicial', 'acao', 'incluirReserva', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('17043', '2', '473', '/v3/executarInclusaoJudicial', 'acao', 'incluirReserva', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('17044', '1', '473', '/v3/executarInclusaoJudicial', 'acao', 'autorizarReserva', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('17045', '7', '473', '/v3/executarInclusaoJudicial', 'acao', 'autorizarReserva', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('17046', '2', '473', '/v3/executarInclusaoJudicial', 'acao', 'autorizarReserva', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('17047', '1', '473', '/v3/executarDecisaoJudicial', 'acao', 'listarServicos', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('17048', '7', '473', '/v3/executarDecisaoJudicial', 'acao', 'listarServicos', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('17049', '2', '473', '/v3/executarDecisaoJudicial', 'acao', 'listarServicos', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('decisaoJudicial_incluirConsignacao_tipoJustica', 'S');

INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('decisaoJudicial_incluirConsignacao_comarcaJustica', 'S');

INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('decisaoJudicial_incluirConsignacao_numeroProcesso', 'S');

INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('decisaoJudicial_incluirConsignacao_dataDecisao', 'S');

INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('decisaoJudicial_incluirConsignacao_textoDecisao', 'S');

INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('decisaoJudicial_incluirConsignacao_anexo', 'S');

