-- DESENV-12372
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('672', 'Habilita cadastro de decisão judicial na alteração e suspensão avançada de consignação', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR) 
VALUES ('672', '1', 'N'); 

INSERT INTO tb_tipo_justica (TJU_CODIGO, TJU_DESCRICAO) VALUES ('1', 'Federal');
INSERT INTO tb_tipo_justica (TJU_CODIGO, TJU_DESCRICAO) VALUES ('2', 'Estadual');

UPDATE tb_acesso_recurso SET ACR_PARAMETRO = 'acao' WHERE ACR_CODIGO IN ('14323', '14324');
UPDATE tb_acesso_recurso SET ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'acompanhar_leilao' WHERE ACR_CODIGO IN ('14326', '14327', '14328', '14329', '14330');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15231', '1', '228', '/cidade/selecionaCidade.jsp', 'acao', 'alterar', 1, 'S', 'S', NULL, 'N', '2'); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15232', '7', '228', '/cidade/selecionaCidade.jsp', 'acao', 'alterar', 1, 'S', 'S', NULL, 'N', '2'); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15233', '1', '382', '/cidade/selecionaCidade.jsp', 'acao', 'suspender', 1, 'S', 'S', NULL, 'N', '2'); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15234', '7', '382', '/cidade/selecionaCidade.jsp', 'acao', 'suspender', 1, 'S', 'S', NULL, 'N', '2'); 
