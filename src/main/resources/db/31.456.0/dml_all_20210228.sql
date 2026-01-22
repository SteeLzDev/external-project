-- DESENV-15344
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('809', 'Permite inclusão de anexo ao confirmar reserva', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('809', '1', 'N');

INSERT INTO tb_tipo_param_svc (TPS_CODIGO, TPS_DESCRICAO, TPS_CSE_ALTERA, TPS_CSA_ALTERA, TPS_SUP_ALTERA, TPS_PODE_SOBREPOR_RSE) 
VALUES ('295', 'Anexo na confirmação de reserva é obrigatório', 'N', 'N', 'N', NULL);
