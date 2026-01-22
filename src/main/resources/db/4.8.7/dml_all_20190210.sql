-- DESENV-10466

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('628', 'Envia e-mail de notificação de previsão de retorno e lembrete de envio de arquivos folha para CSA', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR) 
VALUES ('628', '1', 'N');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) 
VALUES ('emailAlertEnvArqFolha_antes_csa', '<@nome_sistema> - <@nome_consignante>: Alerta upload de arquivos de retorno', 'Por favor, recorde-se de fazer o upload do arquivo de retorno no Sistema <@nome_sistema> na data <@data_prevista_retorno>.');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) 
VALUES ('emailAlertEnvArqFolha_depois_csa', '<@nome_sistema> - <@nome_consignante>: Alerta de upload de arquivo de retorno não realizado', 'Esteja ciente de que o arquivo de retorno era esperado para a data <@data_prevista_retorno>.<br>Favor, faça o upload agora no Sistema <@nome_sistema> para que este possa continuar seu processamento normal.<br><br>');
