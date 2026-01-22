-- DESENV-13680
UPDATE tb_tipo_param_sist_consignante SET TPC_DESCRICAO = 'Dias para bloqueio de Consignatária por Comunicações pendentes enviadas por SER' WHERE TPC_CODIGO = '314';

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('723', 'Dias para bloqueio de Consignatária por Comunicações pendentes enviadas por CSE/ORG', 'INT', '0', 'N', 'N', 'N', 'N', '3');

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('723', '1', '0');
