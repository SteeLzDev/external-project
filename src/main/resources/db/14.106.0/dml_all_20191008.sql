-- DESENV-12080
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('675', 'Data de habilitação do aviso de privacidade do sistema (CSE)', 'DATE', NULL, 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('676', 'Data de habilitação do aviso de privacidade do sistema (ORG)', 'DATE', NULL, 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('677', 'Data de habilitação do aviso de privacidade do sistema (SER)', 'DATE', NULL, 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('678', 'Data de habilitação do aviso de privacidade do sistema (CSA)', 'DATE', NULL, 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('679', 'Data de habilitação do aviso de privacidade do sistema (COR)', 'DATE', NULL, 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('680', 'Data de habilitação do aviso de privacidade do sistema (SUP)', 'DATE', NULL, 'N', 'N', 'N', 'N', NULL);


INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO)
VALUES ('163', 'Aceitação do Aviso de Privacidade do Sistema');

INSERT INTO tb_item_menu (ITM_CODIGO, MNU_CODIGO, TEX_CHAVE, ITM_CODIGO_PAI, ITM_DESCRICAO, ITM_ATIVO, ITM_SEQUENCIA, ITM_SEPARADOR, ITM_CENTRALIZADOR, ITM_IMAGEM) 
VALUES ('228', '4', NULL, NULL, 'Política de Privacidade', 0, 82, 'S', 'S', NULL);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15275', NULL, NULL, '/v3/visualizarPoliticaPrivacidade', 'acao', 'iniciar', 1, 'S', 'S', '228', 'N', '0');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15276', NULL, NULL, '/v3/aceitarPoliticaPrivacidade', 'acao', 'aceitar', 1, 'S', 'S', NULL, 'N', '2');
