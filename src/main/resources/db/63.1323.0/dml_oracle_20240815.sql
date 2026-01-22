-- DESENV-21384
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('960', 'Prender a margem ao realizar a renegociação quando o valor dos contratos antigos forem maior que o novo', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('960', '1', 'N');

INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO) VALUES ('237','Retenção de margem dentro do prazo de renegociação');

INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO) VALUES ('238','Liberação de margem após expiração de prazo de retenção de margem');

INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO) VALUES ('239','Liberação de margem dentro do prazo de renegocição, por encerramento do contrato');

-- ORACLE
INSERT INTO tb_agendamento (AGD_CODIGO, TAG_CODIGO, SAG_CODIGO, USU_CODIGO, AGD_DESCRICAO, AGD_JAVA_CLASS_NAME, AGD_DATA_CADASTRO, AGD_DATA_PREVISTA, REL_CODIGO)
VALUES ('56', '1', '1', '1', 'Libera margem presa fruto de renegociação para menor', 'com.zetra.econsig.job.jobs.AtualizaMargemRenegociacaoPrazoExpiradoJob', SYSDATE, SYSDATE, NULL);

