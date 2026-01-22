-- DESENV-21572
-- MYSQL
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_VLR_DEFAULT, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES('961', 'Envia email de notificação do servidor sobre autorização prévia de operações sem senha', CONCAT('ESCOLHA[0=Não', 0x3b, '1=SMS', 0x3b, '2=Email', 0x3b, '3=SMS/Email', 0x3b, '4=Push notification', 0x3b, '5=SMS/Email/Push notification]'), 'N', 'N', '0', 'S', 'S', '1');

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('961', '1', '0');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO)
VALUES ('enviarEmailNotReservaMargem', 'Novo contrato efetuado com <@csa_nome>', 'Prezado (a) <@ser_nome>,<br>Informamos que foi efetuado um novo contrato com <@csa_nome>. Caso não reconheça essa operação, entre em contato com o banco.<br>Atenciosamente,<br><@logoSistema>');

INSERT INTO tb_tipo_notificacao (TNO_CODIGO, TNO_DESCRICAO, TNO_ENVIO) VALUES ('39', 'E-mail de Notificação após Reserva de Margem Sem Necessidade de Senha ou Código Único', 'I');

