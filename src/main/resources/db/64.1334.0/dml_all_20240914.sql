-- DESENV-21861
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('963', ' Reativação de contrato suspenso por rejeição na folha exige confirmação do gestor', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('963', '1', 'N');

UPDATE tb_acesso_recurso SET ACR_ATIVO = '0' WHERE FUN_CODIGO = '31' AND PAP_CODIGO = '6';

