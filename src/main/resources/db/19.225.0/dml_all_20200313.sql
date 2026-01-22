-- DESENV-13320
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_VLR_DEFAULT, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('717', 'SalaryPay pode sobrepor senha do servidor.', 'SN', 'N', 'N', 'N', 'N','N', '3');

-- Rodar nos sistemas em que *não* será permitido que o servidor cadastre sua senha via SalaryPay
INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR) 
VALUES ('717', '1', 'N');
