-- DESENV-21296
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('956', 'Quantidade de dias para se manter o histórico de ocorréncia de agendamento', 'INT', '180', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('956', '1', '180');

-- ORACLE
INSERT INTO tb_agendamento (AGD_CODIGO, TAG_CODIGO, SAG_CODIGO, USU_CODIGO, AGD_DESCRICAO, AGD_JAVA_CLASS_NAME, AGD_DATA_CADASTRO, AGD_DATA_PREVISTA, REL_CODIGO)
VALUES ('55', '1', '1', '1', 'Verifica se existem ocorrências do histórico de agendamento para exclusão', 'com.zetra.econsig.job.jobs.ExclusaoHistoricoOcorrenciaAgendamentoExternoJob', SYSDATE, SYSDATE, NULL);

