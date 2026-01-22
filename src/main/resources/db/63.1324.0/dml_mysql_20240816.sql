-- DESENV-21573
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('962', 'Dias para notificação ao servidor sobre o fim da sua autorização de margem para as Consignatárias', 'INT', '0', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('962', '1', '0');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO)
VALUES ('enviarEmailAutorizacaoIraVencer', 'Vencimento de autorização para consulta/reserva de margem sem senha/código único', 'Prezado (a) <@ser_nome>,<br>Informamos que a autorização para consulta/reserva de margem/portabilidade sem senha/código único dada para <@csa_nome> vence em <@data_vencimento_noescape>. Para continuar, realize nova autorização após o vencimento.<br>Atenciosamente,<br><@logoSistema>');

INSERT INTO tb_tipo_notificacao VALUES ('40', 'E-mail de Notificação que a Autorização Irá Vencer', 'I');

-- MYSQL
INSERT INTO tb_agendamento (AGD_CODIGO, TAG_CODIGO, SAG_CODIGO, USU_CODIGO, AGD_DESCRICAO, AGD_JAVA_CLASS_NAME, AGD_DATA_CADASTRO, AGD_DATA_PREVISTA, REL_CODIGO)
VALUES ('57', '1', '1', '1', 'Envia Notificação que a Autorização Irá Vencer', 'com.zetra.econsig.job.jobs.EnviaNotificacaoAutorizacaoIraVencerJob', CURDATE(), CURDATE(), NULL);

