-- DESENV-12651
INSERT tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('691', 'Senha do servidor opcional na consulta de margem quando possui consignações com CSA/COR', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR) 
VALUES ('691', '1', 'N');
