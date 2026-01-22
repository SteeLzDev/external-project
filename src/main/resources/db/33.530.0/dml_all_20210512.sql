-- DESENV-15606
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('819', 'Exibe data prevista de conclusão no detalhe de consignação', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

-- INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
-- VALUES ('819', 1, 'N');

INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('detalheConsignacao_dataPrevistaConclusao', 'S');

