-- DESENV-22774
-- @@delimiter = !
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('977', 'Habilita envio de OTP por E-mail/SMS para usuário servidor ao efetuar bloqueios de verbas', 'ESCOLHA[0=Desabilitado;1=SMS;2=Email;3=SMS/Email]', '0', 'N', 'N', 'N', 'N', NULL)
!

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('977', '1', '0')
!

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('17055', '6', '149', '/v3/listarConvenioServidor', 'acao', 'enviarOtp', 1, 'S', 'S', NULL, 'N', '2')
!