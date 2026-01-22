-- DESENV-20243
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarConsignataria_whatsapp', 'S');

INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarConsignataria_email_contato', 'S');

-- MYSQL
INSERT INTO tb_tipo_param_consignataria (TPA_CODIGO, TPA_DESCRICAO, TPA_DOMINIO, TPA_CSE_ALTERA, TPA_SUP_ALTERA, TPA_CSA_ALTERA)
VALUES ('82', 'Permite ser contactada por Whatsapp, e-mail e/ou chamada', CONCAT('ESCOLHA[0=Não', 0x3b, '1=E-mail', 0x3b, '2=Chamada', 0x3b, '3=Whatsapp', 0x3b, '4=E-mail/Chamada/Whatsapp', 0x3b, '5=E-mail/Chamada', 0x3b, '6=E-mail/Whatsapp', 0x3b, '7=Chamada/Whatsapp]'), 'N', 'N', 'N');

