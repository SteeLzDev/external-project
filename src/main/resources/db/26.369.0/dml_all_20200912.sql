-- DESENV-13921
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('775', 'Distância Máxima em Metros para Busca de Geolocalização de Estabelecimentos Parceiros', 'FLOAT', NULL, 'N', 'N', 'N', 'N', '3');

INSERT INTO tb_tipo_param_consignataria (TPA_CODIGO, TPA_DESCRICAO, TPA_DOMINIO, TPA_CSE_ALTERA, TPA_SUP_ALTERA, TPA_CSA_ALTERA) 
VALUES ('60', 'Distância Máxima em Metros para Busca de Geolocalização de Estabelecimentos Parceiros', 'FLOAT', 'N', 'N', 'N');
