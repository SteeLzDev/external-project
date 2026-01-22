-- DESENV-12999
INSERT INTO tb_status_login (STU_CODIGO, STU_DESCRICAO) 
VALUES ('9', 'Aguardando aprovação de cadastro');

INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO) 
VALUES ('168', 'Aprovação do cadastro do usuário');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('693', 'URL do serviço FacesWeb para identificação facial', 'ALFA', NULL, 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('694', 'Chave de autenticação (ApiKey) para utilização do serviço FacesWeb', 'ALFA', NULL, 'N', 'N', 'N', 'N', NULL);

-- INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR) VALUES ('693', '1', 'http://localhost:8090/facesweb');
-- INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR) VALUES ('694', '1', '4b3c7387062ab0ec5ae5256a2b5bcc75');
