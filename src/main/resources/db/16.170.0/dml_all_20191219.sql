-- DESENV-12721
INSERT INTO tb_agendamento (AGD_CODIGO, TAG_CODIGO, SAG_CODIGO, USU_CODIGO, AGD_DESCRICAO, AGD_JAVA_CLASS_NAME, AGD_DATA_CADASTRO, AGD_DATA_PREVISTA, REL_CODIGO)
VALUES ('38', '1', '1', '1', 'Reativação Automática de Consignações', 'com.zetra.timer.econsig.job.ReativacaoAutomaticaAdeJob', current_date(), current_date(), null);
