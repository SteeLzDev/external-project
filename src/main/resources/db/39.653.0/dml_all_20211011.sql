-- DESENV-16725
INSERT INTO tb_funcao (FUN_CODIGO, GRF_CODIGO, FUN_DESCRICAO, FUN_PERMITE_BLOQUEIO, FUN_EXIGE_TMO, FUN_EXIGE_SEGUNDA_SENHA_CSE, FUN_AUDITAVEL, FUN_RESTRITA_NCA, FUN_EXIGE_SEGUNDA_SENHA_SUP, FUN_EXIGE_SEGUNDA_SENHA_ORG, FUN_EXIGE_SEGUNDA_SENHA_CSA, FUN_EXIGE_SEGUNDA_SENHA_COR)
VALUES ('520', '1', 'Ajustar Consignações à Margem', 'N', 'N', 'N', 'S', 'N', 'N', 'N', 'N', 'N');

-- INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('2', '520');

INSERT INTO tb_item_menu (ITM_CODIGO, MNU_CODIGO, ITM_CODIGO_PAI, ITM_DESCRICAO, ITM_ATIVO, ITM_SEQUENCIA, ITM_SEPARADOR)
VALUES ('265', '1', NULL, 'Ajustar Consignações à Margem', 1, 99, 'N');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16447', '2', '520', '/v3/ajustarConsignacoesMargem', 'acao', 'iniciar', '1', 'S', 'S', 265, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16448', '2', '520', '/v3/ajustarConsignacoesMargem', 'acao', 'pesquisarServidor', '1', 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16449', '2', '520', '/v3/ajustarConsignacoesMargem', 'acao', 'iniciarAjustarConsignacoesMargem', '1', 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16450', '2', '520', '/v3/ajustarConsignacoesMargem', 'acao', 'salvar', '1', 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16451', '2', '520', '/v3/ajustarConsignacoesMargem', 'acao', 'validar', '1', 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16452', '2', '520', '/v3/listarCidades', 'acao', 'ajustarConsignacoesMargem', '1', 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('ajustarConsignacoesMargem_tipoJustica', 'O');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('ajustarConsignacoesMargem_comarcaJustica', 'O');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('ajustarConsignacoesMargem_numeroProcesso', 'O');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('ajustarConsignacoesMargem_dataDecisao', 'O');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('ajustarConsignacoesMargem_textoDecisao', 'O');
