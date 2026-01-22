-- DESENV-17862
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('877', 'Habilitar mensagem de margem adequada por decisão judicial na consulta de margem', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('877', '1', 'N');

INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO,TOC_DESCRICAO) 
VALUES ('203','Alteração de múltiplos contratos utilizando margem limite por decisão judicial');

INSERT INTO tb_tipo_dado_adicional (TDA_CODIGO, TEN_CODIGO, TDA_DESCRICAO, TDA_DOMINIO, TDA_EXPORTA, TDA_SUP_CONSULTA, TDA_CSE_CONSULTA, TDA_CSA_CONSULTA, TDA_SER_CONSULTA, TDA_SUP_ALTERA, TDA_CSE_ALTERA, TDA_CSA_ALTERA, TDA_SER_ALTERA)
VALUES ('87', '19', 'Margem Limite utilizada na alteração multiplica por decisão judicial', 'INT', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N');
