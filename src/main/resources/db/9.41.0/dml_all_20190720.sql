-- DESENV-11699
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('651', 'Modo de notificação do servidor no envio de novo boleto pela consignatária', 'ALFA', NULL, 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_tipo_notificacao (TNO_CODIGO, TNO_DESCRICAO, TNO_ENVIO) 
VALUES ('13', 'Novo boleto para o servidor', 'I'); 

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) 
VALUES ('emailNovoBoletoServidor', '<@nome_sistema>: Novo boleto disponível', '<br>Prezado(a) <@ser_nome>,<br><br>um novo boleto para o CPF <b><@ser_cpf></b> foi enviado ao sistema <b>eConsig</b> por <@remetente>.<br><br>Para visualizá-lo, acesse o sistema através do endereço <a href="<@url_sistema>"><@url_sistema></a> e acesse a opção Consultar Boletos.');
