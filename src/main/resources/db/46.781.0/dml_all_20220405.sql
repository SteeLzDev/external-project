-- DESENV-17375
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('875', 'Suspender contrato com parcela rejeitada no retorno', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('875', '1', 'N');

INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO) VALUES ('197','Suspensão de contrato por parcela rejeitada em folha');

INSERT INTO tb_acao (ACA_CODIGO, ACA_DESCRICAO) VALUES ('11','Suspender contratos com parcela rejeitada em folha');