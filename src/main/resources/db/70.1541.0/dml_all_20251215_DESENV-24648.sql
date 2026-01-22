INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('1010', 'Quantidade mensal de envios de mensagens para servidor via push notification ', 'INT', '0', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('1010', '1', '0');

INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO)
VALUES ('260', 'Envio de mensagem para servidor via push notification');

