-- DESENV-5578

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, GPS_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_VLR_DEFAULT, TPC_SUP_ALTERA, TPC_SUP_CONSULTA)
VALUES ('609', NULL, 'Habilita o envio de e-mail para o servidor com contratos rejeitados pela folha', 'SN', 'N', 'N', 'N', 'N', 'N');

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('609', '1', 'N');

INSERT INTO tb_tipo_notificacao (TNO_CODIGO, TNO_DESCRICAO, TNO_ENVIO)
VALUES ('6', 'E-mail de notificação de contratos rejeitados pela folha', 'I');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO)
VALUES ('emailServidorContratosRejeitados', 'Rejeito de contrato folha', 'Os seguintes contratos foram rejeitados pela folha:');
