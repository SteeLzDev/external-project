-- DESENV-13591
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15686', '6', '57', '/v3/executarKYC', 'acao', 'salvarPanNumber', 1, 'S', 'S', NULL, 'N', '2'); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15687', '6', '57', '/v3/executarKYC', 'acao', 'finalizar', 1, 'S', 'S', NULL, 'N', '2'); 

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) 
VALUES ('notificaProblemasProcessoKYC', '<@nome_sistema>: Problemas no processo KYC', '<br>Erro no processo KYC: <br><br>${rotulo.kyc.output.status}: <@kyc_output_status><br><br>${rotulo.kyc.pan.number}: <@kyc_pan_number><br><br>${rotulo.usuario.singular}: <@responsavel><br><br>${rotulo.orgao.singular}: <@org_nome><br><br>${rotulo.consignante.singular}: <@nome_sistema><br><br>Data da tentativa: <@agora><br><br>');

INSERT INTO tb_tipo_dado_adicional (TDA_CODIGO, TEN_CODIGO, TDA_DESCRICAO, TDA_DOMINIO, TDA_EXPORTA, TDA_SUP_CONSULTA, TDA_CSE_CONSULTA, TDA_CSA_CONSULTA, TDA_SER_CONSULTA, TDA_SUP_ALTERA, TDA_CSE_ALTERA, TDA_CSA_ALTERA, TDA_SER_ALTERA)
VALUES ('45', '6', 'Data/hora validação KYC', 'DATETIME', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N');

INSERT INTO tb_tipo_notificacao (TNO_CODIGO, TNO_DESCRICAO, TNO_ENVIO) 
VALUES ('17', 'Notificação de problemas no processo KYC.', 'I');

INSERT INTO tb_tipo_param_consignataria (TPA_CODIGO, TPA_DESCRICAO, TPA_DOMINIO, TPA_CSE_ALTERA, TPA_SUP_ALTERA, TPA_CSA_ALTERA)
VALUES ('56', 'Email para recebimento de notificação de falhas no processo KYC.', 'ALFA', 'N', 'N', 'N');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('724', 'KYC - URL de jornada de validação', 'ALFA', NULL, 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('725', 'KYC - API URL do GetTaxStatus', 'ALFA', NULL, 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('726', 'KYC - API Consumer Key', 'ALFA', NULL, 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('727', 'KYC - API Consumer Secret', 'ALFA', NULL, 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('728', 'KYC - API URL do CheckKYC', 'ALFA', NULL, 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('729', 'KYC - Origem do PAN Number', 'ALFA', NULL, 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('732', 'KYC - RSA Public Key - Modulus', 'ALFA', NULL, 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('733', 'KYC - RSA Public Key - Exponent', 'ALFA', NULL, 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('734', 'KYC - GetStatus - Nome do campo a ser usado para encontrar o registro correto.', 'ALFA', NULL, 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('735', 'KYC - GetStatus - Valor a ser comparado para encontrar o registro correto.Origem do PAN Number', 'ALFA', NULL, 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('736', 'KYC - GetStatus - Nome do campo que contém do código a ser retornado e usado no CheckKYC.', 'ALFA', NULL, 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('737', 'KYC - CheckKYC - Valor a ser usado no campo isNewVersion.', 'ALFA', NULL, 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('738', 'KYC - CheckKYC - Nome do campo que contém o código do status do KYC do PAN Number informado.', 'ALFA', NULL, 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_tipo_param_svc (TPS_CODIGO, TPS_DESCRICAO, TPS_SUP_ALTERA, TPS_CSE_ALTERA, TPS_CSA_ALTERA) 
VALUES ('285', 'Só permite solicitação pelo servidor/funcionário se este for "KYC Compliant".', 'N', 'N', 'N');
