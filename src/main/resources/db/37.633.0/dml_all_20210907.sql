-- DESENV-16081
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('859', 'Registrar histórico de margem na operação de recálculo mesmo que a margem antes e depois sejam iguais', 'SN', 'N', 'N', 'N', 'N', 'N', '2');

INSERT tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('859', '1', 'N');
