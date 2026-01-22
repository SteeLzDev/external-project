-- DESENV-18139
INSERT INTO tb_status_credenciamento (SCR_CODIGO,SCR_DESCRICAO) VALUES ('1','Aguardando envio da documentação pela CSA');
INSERT INTO tb_status_credenciamento (SCR_CODIGO,SCR_DESCRICAO) VALUES ('2','Aguardando validação da documentação pelo CSE');
INSERT INTO tb_status_credenciamento (SCR_CODIGO,SCR_DESCRICAO) VALUES ('3','Aguardando preenchimento do termo aditivo pelo CSE');
INSERT INTO tb_status_credenciamento (SCR_CODIGO,SCR_DESCRICAO) VALUES ('4','Aguardando assinatura do termo aditivo pela CSA');
INSERT INTO tb_status_credenciamento (SCR_CODIGO,SCR_DESCRICAO) VALUES ('5','Aguardando assinatura do termo aditivo pelo CSE');
INSERT INTO tb_status_credenciamento (SCR_CODIGO,SCR_DESCRICAO) VALUES ('6','Aguardando aprovação do termo aditivo assinado');
INSERT INTO tb_status_credenciamento (SCR_CODIGO,SCR_DESCRICAO) VALUES ('7','Finalizado');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('881', 'Habilita módulo credenciamento saúde', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR) VALUES ('881', '1', 'N');

INSERT INTO tb_modelo_email (MEM_CODIGO,MEM_TITULO,MEM_TEXTO) VALUES ('enviarEmailCsaCredenciamento','<@nome_sistema> - <@nome_consignante>: Credenciamento','Gentileza acessar o sistema para realizar o credenciamento.<br> Os documentos necessário encontram-se em anexo.');

INSERT INTO tb_tipo_notificacao (TNO_CODIGO,TNO_DESCRICAO,TNO_ENVIO) VALUES ('29','E-mail de notificação de credenciamento para a consignatária','I');