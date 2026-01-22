-- DESENV-12091
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA)
VALUES ('805', 'Dias para bloqueio de Consignatária por não confirmar liquidação', 'INT', NULL, 'N', 'N', 'N', 'N');

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR) 
VALUES ('805', '1', '0');

INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO) 
VALUES ('186', 'Bloqueio de consignatária por não confirmar liquidação');

INSERT INTO tb_agendamento (AGD_CODIGO, TAG_CODIGO, SAG_CODIGO, USU_CODIGO, AGD_DESCRICAO, AGD_JAVA_CLASS_NAME, AGD_DATA_CADASTRO, AGD_DATA_PREVISTA, REL_CODIGO) 
VALUES ('43', '1', '1', '1', 'Bloqueio Consignatária Não Confirmação da Solicitação de Liquidação', 'com.zetra.timer.econsig.job.BloqueioCsaNaoConfirmarLiquidacaoJob', current_timestamp(), current_date(), NULL);
