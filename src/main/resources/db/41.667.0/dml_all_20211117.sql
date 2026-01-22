-- DESENV-16472
INSERT INTO tb_agendamento (AGD_CODIGO, TAG_CODIGO, SAG_CODIGO, USU_CODIGO, AGD_DESCRICAO, AGD_JAVA_CLASS_NAME, AGD_DATA_CADASTRO, AGD_DATA_PREVISTA, REL_CODIGO)
VALUES ('45', '1', '1', '1', 'Bloquear Perfils com data de expiração passada', 'com.zetra.timer.econsig.job.BloqueioPerfilDataExpiradaJob', CURDATE(), CURDATE(), NULL);
