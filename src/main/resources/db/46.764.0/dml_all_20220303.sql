-- DESENV-16848
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('874', 'Percentual de variação da margem folha do servidor na importação de margem', 'FLOAT', '0.00', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('874', '1', '0.00');

INSERT INTO tb_status_registro_servidor (SRS_CODIGO, SRS_DESCRICAO) 
VALUES ('8', 'Bloqueado Automaticamente por Variação de Margem');
