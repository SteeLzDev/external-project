-- DESENV-12886
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, GPS_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_SUP_CONSULTA, TPC_SUP_ALTERA, TPC_CSE_CONSULTA, TPC_CSE_ALTERA)
VALUES ('700', '3', 'Exige unicidade de e-mail entre usuários não servidores', 'SN', 'N', 'N', 'N', 'N', 'N');

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('700', '1', 'N');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, GPS_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_SUP_CONSULTA, TPC_SUP_ALTERA, TPC_CSE_CONSULTA, TPC_CSE_ALTERA)
VALUES ('701', '3', 'Exige unicidade de e-mail entre usuários e servidores', 'SN', 'N', 'N', 'N', 'N', 'N');

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('701', '1', 'N');
