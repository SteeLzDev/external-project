-- DESENV-12437
INSERT tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('690', 'Exibe somente EST e ORG ativos na autenticação de servidor/funcionário', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR) 
VALUES ('690', '1', 'N');
