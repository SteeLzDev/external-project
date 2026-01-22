-- DESENV-13319
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('710', 'Habilita uso de senha exclusiva para o App eConsig Mobile', 'SN', 'N', 'N', 'N', 'N','N', NULL);

-- habilita o parâmetro onde existe a função 410 - Definir Senha APP habilitada
INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('710', '1', CASE WHEN EXISTS (SELECT 1 FROM tb_funcao_perfil_ser WHERE fun_codigo = '410') THEN 'S' ELSE 'N' END);
