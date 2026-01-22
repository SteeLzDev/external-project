-- DESENV-16132
INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO) 
VALUES ('191', 'Bloqueio automático de segurança');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('862', 'E-mail para envio de notificação de segurança do sistema', 'ALFA', NULL, 'N', 'N', 'N', 'N', NULL);
