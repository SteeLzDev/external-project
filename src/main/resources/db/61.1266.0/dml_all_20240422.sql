-- DESENV-21343
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('954', 'Envia email para servidor na consulta de margem', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('954', '1', 'N');

INSERT INTO tb_tipo_notificacao VALUES ('38', 'E-mail de Notificação de Consulta de Margem do Servidor', 'I');

INSERT INTO tb_modelo_email VALUES ('enviarEmailConsultaMargemSer', '<@nome_sistema> - Nova consulta de margem realizada: <@cse_nome>', 'Prezado (a) <@ser_nome>,<br>Informamos que sua margem foi consultada pela empresa <@csa_nome>.<br>Atenciosamente,<br><img src="<@logoSistema>" alt="logo-sistema" >');

