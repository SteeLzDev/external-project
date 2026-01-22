-- DESENV-17160
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('904', 'Recuperar senha de usuário via OTP', 'ESCOLHA[0=Desabilitado' || chr(to_number('3B', 'XX')) || '1=SMS' || chr(to_number('3B', 'XX')) || '2=Email' || chr(to_number('3B', 'XX')) || '3=SMS/Email]', '0', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('904', '1', '0');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('905', 'Recuperar senha de servidor via OTP', 'ESCOLHA[0=Desabilitado' || chr(to_number('3B', 'XX')) || '1=SMS' || chr(to_number('3B', 'XX')) || '2=Email' || chr(to_number('3B', 'XX')) || '3=SMS/Email]', '0', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('905', '1', '0');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) VALUES ('enviarEmailOtpUsuario', '<@tituloEmail>', '<@tituloEmail><br/><br/><br/><b>Código de Verificação: <@otp></b><br/><br/>');

INSERT INTO tb_tipo_notificacao (TNO_CODIGO, TNO_DESCRICAO, TNO_ENVIO) 
VALUES ('35', 'E-mail de envio de otp para o usuário', 'I');

