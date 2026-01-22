-- DESENV-13634
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('721', 'Bloquear Consignatária ausência de mínimo de anexos', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('721', '1', 'N');

INSERT INTO tb_tipo_param_svc (TPS_CODIGO, TPS_DESCRICAO, TPS_CSE_ALTERA, TPS_CSA_ALTERA, TPS_SUP_ALTERA, TPS_PODE_SOBREPOR_RSE) 
VALUES ('284', 'Número mínimo de anexos em consignações realizadas por usuário CSA/COR', 'N', 'N', 'N', NULL);

INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO) 
VALUES ('176', 'Consignatária possui consignação sem mínimo de anexos exigidos');

INSERT INTO tb_agendamento (AGD_CODIGO, TAG_CODIGO, SAG_CODIGO, USU_CODIGO, AGD_DESCRICAO, AGD_JAVA_CLASS_NAME, AGD_DATA_CADASTRO, AGD_DATA_PREVISTA, REL_CODIGO) 
VALUES ('40', '1', '1', '1', 'Bloquear Consignatária com Consignações Sem Número Mínimo de Anexos', 'com.zetra.timer.econsig.job.BloqueiaCsaAdeSemMinAnexosJob', NOW(), CURDATE(), NULL);
