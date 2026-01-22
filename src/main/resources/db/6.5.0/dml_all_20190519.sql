-- DESENV-11158
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, GPS_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_VLR_DEFAULT, TPC_SUP_ALTERA, TPC_SUP_CONSULTA) 
VALUES ('642', NULL, 'Habilita opção para pré-visualizar o conteúdo dos relatórios na própria interface do sistema', 'SN', 'N', 'N', 'N', 'N', 'N');

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('642', '1', 'N');
