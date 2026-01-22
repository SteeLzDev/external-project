-- DESENV-11417
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('702', 'Informa de quantos em quantos dias a CSE deve proceder sua atualização cadastral', 'INT', '0', 'N', 'N', 'N', 'N', NULL); 

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('703', 'Informa de quantos em quantos dias a CSA deve proceder sua atualização cadastral', 'INT', '0', 'N', 'N', 'N', 'N', NULL); 

-- INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR) values ('702', '1', '0');
-- INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR) values ('703', '1', '0');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15479', '1', '15', '/v3/atualizarCadastro', 'acao', 'editar', 1, 'S', 'S', NULL, 'N', '2'); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15480', '2', '85', '/v3/atualizarCadastro', 'acao', 'editar', 1, 'S', 'S', NULL, 'N', '2'); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15481', '1', '15', '/v3/atualizarCadastro', 'acao', 'iniciar', 1, 'S', 'S', NULL, 'N', '0'); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15482', '2', '85', '/v3/atualizarCadastro', 'acao', 'iniciar', 1, 'S', 'S', NULL, 'N', '0'); 

INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO) 
VALUES ('171', 'Alteração de dados cadastrais da entidade');
