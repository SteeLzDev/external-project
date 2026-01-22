-- DESENV-13210
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('707', 'Definir esforço para identificar o charset do arquivo no upload', 'INT', '16536', 'N', 'N', 'N', 'N', null);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR) 
VALUES ('707', '1', '16536');
