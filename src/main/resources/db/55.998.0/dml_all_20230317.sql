-- DESENV-19388
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('913', 'Importação de lote contém toda carteira de consignações ativas', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('913', '1', 'N');

INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO,TOC_DESCRICAO) VALUES ('217','Alteração via importação de lote com toda carteira ativa');

