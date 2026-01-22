-- DESENV-16728
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('863', 'Encerrar consignações de servidores enviados como excluídos na carga de margem', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('863', '1', 'N');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('864', 'Reabrir consignações encerradas na carga de margem de servidores readmitidos', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('864', '1', 'N');

INSERT INTO tb_status_autorizacao_desconto (SAD_CODIGO, SAD_DESCRICAO) 
VALUES ('18', 'Encerrado por Exclusão');

