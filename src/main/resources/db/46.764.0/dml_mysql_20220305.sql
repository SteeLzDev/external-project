-- DESENV-17578
-- Alterar o status dos registros que geraram bloqueio para confirmado.
UPDATE tb_operacao_libera_margem SET OLM_CONFIRMADA = 'S' WHERE OLM_BLOQUEIO = 'S';

-- MYSQL
INSERT INTO tb_agendamento (AGD_CODIGO, TAG_CODIGO, SAG_CODIGO, USU_CODIGO, AGD_DESCRICAO, AGD_JAVA_CLASS_NAME, AGD_DATA_CADASTRO, AGD_DATA_PREVISTA, REL_CODIGO)
VALUES ('48', '5', '2', '1', 'Confirmação das operações que liberam margem para bloqueio automático de segurança', 'com.zetra.econsig.job.jobs.ConfirmarOperacoesLiberacaoMargemJob', CURDATE(), CURDATE(), NULL);
