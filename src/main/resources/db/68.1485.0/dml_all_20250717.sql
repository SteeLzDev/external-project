-- DESENV-23816
DELETE FROM tb_ocorrencia_param_sist_cse WHERE TPC_CODIGO IN ('989', '990');

DELETE FROM tb_nivel_seguranca_param_sist WHERE TPC_CODIGO IN ('989', '990');

DELETE FROM tb_perfil_param_sist_cse WHERE TPC_CODIGO IN ('989', '990');

DELETE FROM tb_param_sist_consignante WHERE TPC_CODIGO IN ('989', '990');

DELETE FROM tb_tipo_param_sist_consignante WHERE TPC_CODIGO IN ('989', '990');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('989', 'URL Base Portal Emprega Brasil', 'ALFA', NULL, 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('990', 'Habilitar Controle de documento de margem', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('990', '1', 'N');

