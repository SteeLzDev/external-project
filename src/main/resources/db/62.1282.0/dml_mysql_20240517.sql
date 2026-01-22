-- DESENV-21581
INSERT IGNORE INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('958', ' Ocultar as informações de correspondente no Relatório Gerencial Geral', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT IGNORE INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('958', '1', 'N');

UPDATE tb_relatorio SET REL_TEMPLATE_SUBRELATORIO = CONCAT(REL_TEMPLATE_SUBRELATORIO, ', GerencialGeralCsaSituacao.jasper') WHERE REL_CODIGO = 'gerencial';

