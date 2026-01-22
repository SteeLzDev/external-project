-- DESENV-8835

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, GPS_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA)
VALUES ('631', NULL, 'Habilita a utilização do período da ocorrência na seleção de contratos na rotina de exportação', 'SN', 'N', 'N', 'N', 'N', 'N'); 

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('631', '1', 'N');
