-- DESENV-13702
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('742', 'Comandos aceitos pela folha na exportação de movimento semanal', 'ALFA', NULL, 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('743', 'Campos considerados no comando de alteração na exportação de movimento semanal', CONCAT('ESCOLHA[1=Valor', 0x3b, '2=Prazo', 0x3b, '3=Valor ou Prazo]'), '1', 'N', 'N', 'N', 'N', NULL);
