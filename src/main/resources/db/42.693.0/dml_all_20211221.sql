-- DESENV-16952
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_VLR_DEFAULT, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('871', 'Envia email de alerta ao treinamento a cada novo usuário de cse e org criado', 'SN', 'N', 'S', 'N', 'S', 'S', '3');

-- INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, psi_vlr) VALUES('871', '1', 'treinamento@zetrasoft.com.br');

INSERT INTO tb_tipo_notificacao (TNO_CODIGO, TNO_DESCRICAO, TNO_ENVIO) VALUES('20', 'E-mail notificação criação novo usuário CSE e ORG', 'I');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO)
VALUES ('enviarEmailAlertaCriaNovoUsuCse', '<@nome_sistema> - Novo usuário gestor: <@nome_consignante>', concat('<b>Classificação da Informação: CONFIDENCIAL<br>', rpad(' ', 70, cast(0x2d as char)), '</b><br><br>Treinamento,<br><br>Novo usuário de papel gestor criado em <@date> às <@hora>. Seguem os dados deste novo usuário:<br><br>Convênio: <@convenio><br>Usuário: <@usu_login><br>Nome: <@usu_nome><br>E-mail: <@usu_email><br>Telefone: <@usu_tel><br><br> Atenciosamente,<br>'));
