-- DESENV-11562
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, TPC_VLR_DEFAULT)
VALUES ('650', 'Dias após o corte para envio de notificação download não realizado do movimento para usuário CSE', 'INT', 'N', 'N', 'N', 'N', NULL); 

-- INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR) VALUES ('650', '1', '1,5,10');

-- Cria agendamento para bloqueio de consignatarias - MYSQL 
INSERT INTO tb_agendamento (AGD_CODIGO, TAG_CODIGO, SAG_CODIGO, USU_CODIGO, AGD_DESCRICAO, AGD_JAVA_CLASS_NAME, AGD_DATA_CADASTRO, AGD_DATA_PREVISTA, REL_CODIGO)
VALUES ('36', '1', '1', '1', 'Envia email de alerta de download não realizado de Movimento Financeiro para usuário CSE', 'com.zetra.timer.econsig.job.EnviarEmailDownloadNaoRealizadoMovFinJob', current_date(), current_date(), null);

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) 
VALUES ('emailDownloadNaoRealizadoMovFin', '<@nome_sistema> - <@nome_consignante>: Alerta para download de arquivo de movimento financeiro', 'Por favor, recorde-se de fazer o download do(s) arquivo(s) de movimento financeiro "<@arquivo>" no Sistema <@nome_sistema>.');

INSERT INTO tb_tipo_arquivo (TAR_CODIGO, TAR_DESCRICAO, TAR_QTD_DIAS_LIMPEZA)
VALUES ('45', 'Download de Arquivo de Movimento Financeiro', 0);
