-- DESENV-6365

INSERT INTO tb_tipo_param_svc (TPS_CODIGO, TPS_DESCRICAO, TPS_SUP_ALTERA, TPS_CSE_ALTERA, TPS_CSA_ALTERA) 
VALUES ('272', 'Mensagem a ser enviada ao servidor após o deferimento de uma solicitação', 'N', 'N', 'N');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('636', 'Habilita o envio de SMS/e-mail ao servidor quando sua consignação é deferida', 'SN', 'N', 'N', 'N', 'N', 'N', '3');

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR) 
VALUES ('636', '1', 'N');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) 
VALUES ('emailNotificacaoConsignacaoDef', '<@horario> - <@csa_nome> - <@solicitacao_aprovada>', 'Olá,<br> Sua solicitação foi aprovada.<br> ADE Número: <@ade_numero><br> Atenciosamente, <@csa_nome>');

INSERT INTO tb_tipo_notificacao (TNO_CODIGO, TNO_DESCRICAO, TNO_ENVIO) 
VALUES ('9', 'E-mail notificação consignação deferida', 'I');
