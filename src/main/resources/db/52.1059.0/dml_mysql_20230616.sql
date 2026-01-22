-- DESENV-16185
-- MYSQL
INSERT INTO tb_agendamento (AGD_CODIGO, TAG_CODIGO, SAG_CODIGO, USU_CODIGO, AGD_DESCRICAO, AGD_JAVA_CLASS_NAME, AGD_DATA_CADASTRO, AGD_DATA_PREVISTA, REL_CODIGO) 
VALUES ('52', '3', '6', '1', 'Carregar Automaticamente Calendario Folha', 'com.zetra.econsig.job.jobs.CarregarCalendarioFolhaAutomaticamenteJob', CURDATE(), CURDATE(), NULL);

