-- DESENV-20243
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarConsignataria_whatsapp', 'S');

INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarConsignataria_email_contato', 'S');

-- ORACLE
INSERT INTO tb_tipo_param_consignataria (TPA_CODIGO, TPA_DESCRICAO, TPA_DOMINIO, TPA_CSE_ALTERA, TPA_SUP_ALTERA, TPA_CSA_ALTERA)
VALUES ('82', 'Permite ser contactada por Whatsapp, e-mail e/ou chamada', 'ESCOLHA[0=Não' || chr(to_number('3B', 'XX')) || '1=E-mail' || chr(to_number('3B', 'XX')) || '2=Chamada' || chr(to_number('3B', 'XX')) || '3=Whatsapp' || chr(to_number('3B', 'XX')) || '4=E-mail/Chamada/Whatsapp' || chr(to_number('3B', 'XX')) || '5=E-mail/Chamada' || chr(to_number('3B', 'XX')) || '6=E-mail/Whatsapp' || chr(to_number('3B', 'XX')) || '7=Chamada/Whatsapp]', 'N', 'N', 'N');

