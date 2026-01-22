-- DESENV-9932
INSERT IGNORE INTO tb_tipo_param_sist_consignante (tpc_codigo, tpc_descricao, TPC_DOMINIO, tpc_cse_altera, TPC_CSE_CONSULTA, TPC_VLR_DEFAULT, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('9', 'Dia de Previsão de Retorno', 'INT', 'N', NULL, '1', 'S', 'S', NULL);

UPDATE tb_tipo_param_sist_consignante SET tpc_descricao = 'Dia de Previsão de Retorno', TPC_DOMINIO = 'INT', tpc_cse_altera = 'N', TPC_VLR_DEFAULT = '1', TPC_SUP_ALTERA = 'S', TPC_SUP_CONSULTA = 'S' WHERE tpc_codigo = '9';
