-- DESENV-17824
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('883', 'URL base para serviço CRM', 'ALFA', NULL, 'N', 'N', 'N', 'N', NULL);

-- INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR) VALUES ('883', '1', 'https://localhost:9443');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('884', 'Manutenção de consignatária utiliza serviço CRM', 'ALFA', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('884', '1', 'N');

-- UPDATE tb_campo_sistema SET CAS_VALOR = 'O' WHERE CAS_CHAVE = 'sup.editarConsignataria_idn_interno';
DELETE FROM tb_campo_sistema WHERE CAS_CHAVE = 'sup.editarConsignataria_idn_interno';
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('sup.editarConsignataria_idn_interno', 'O');