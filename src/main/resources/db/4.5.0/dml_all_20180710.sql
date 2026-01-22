-- DESENV-9064

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, GPS_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_VLR_DEFAULT, TPC_SUP_ALTERA, TPC_SUP_CONSULTA) 
VALUES ('595', NULL, 'Permite inclusão de contrato de prazo 1 além do limite de consignatária para servidor', 'SN', 'N', 'N', 'N', 'N', 'N');

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('595', '1', 'N');
