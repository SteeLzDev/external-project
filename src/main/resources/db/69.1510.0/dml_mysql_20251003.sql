-- DESENV-23174
-- @@delimiter = !
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('993', 'Url base servico Auth SERASA', 'SN', 'N', 'N', 'N', 'N', 'N', NULL)
!

-- INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
-- VALUES ('993', '1', 'https://uat-api.serasaexperian.com.br/security/iam/v1/client-identities/login')
-- !

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('994', 'Url base servico Consentimento SERASA', 'SN', 'N', 'N', 'N', 'N', 'N', NULL)
!

-- INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
-- VALUES ('994', '1', 'https://experian-datahub-consent-management-api.uat-do.br.experian.eeca/v1/consents')
-- !

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('995', 'Client ID Consentimento Serasa', 'SN', 'N', 'N', 'N', 'N', 'N', NULL)
!

-- INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
-- VALUES ('995', '1',  '');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('996', 'Client SECRET Consentimento Serasa', 'SN', 'N', 'N', 'N', 'N', 'N', NULL)
!

-- INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
-- VALUES ('996', '1', '');
