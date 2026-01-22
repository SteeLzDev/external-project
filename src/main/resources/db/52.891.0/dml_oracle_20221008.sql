-- DESENV-18230
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('899', 'Bloquear consignatária por CET / Taxa de Juros com data de vigência expirada', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('899', '1', 'N');

INSERT INTO tb_tipo_motivo_bloqueio (TMB_CODIGO, TMB_DESCRICAO)
VALUES ('10', 'CET / Taxa de Juros com data de vigência expirada');

INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO)
VALUES ('215', 'Bloqueio de consignatária por CET / Taxa de Juros com data de vigência expirada');

-- ORACLE
INSERT INTO tb_agendamento (AGD_CODIGO, TAG_CODIGO, SAG_CODIGO, USU_CODIGO, AGD_DESCRICAO, AGD_JAVA_CLASS_NAME, AGD_DATA_CADASTRO, AGD_DATA_PREVISTA, REL_CODIGO)
VALUES ('50', '1', '1', '1', 'Bloqueio de Consignatária por CET / Taxa de Juros com data de vigência expirada', 'com.zetra.econsig.job.jobs.BloqueiaCsaPorCetExpiradoJob', SYSDATE, SYSDATE, NULL);