-- DESENV-17376

INSERT INTO tb_tipo_notificacao (TNO_CODIGO,TNO_DESCRICAO,TNO_ENVIO) VALUES ('26','E-mail de notificação ao servidor que existem contratos suspensos pendentes de reativação','I');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) VALUES ('enviarEmailSerAdeSuspPendReat', '<@nome_sistema>: Contratos suspensos pendentes de reativação', '<br> Prezado <@nome_servidor>,<br>Existem contratos suspensos pendentes de reativação, gentileza verificar.');

INSERT INTO tb_agendamento (AGD_CODIGO, TAG_CODIGO, SAG_CODIGO, USU_CODIGO, AGD_DESCRICAO, AGD_JAVA_CLASS_NAME, AGD_DATA_CADASTRO, AGD_DATA_PREVISTA, REL_CODIGO) 
VALUES ('49', '1', '1', '1', 'Enviar e-mail para os servidores que têm contratos suspensos pendentes de reativação', 'com.zetra.econsig.job.jobs.EnviarEmailServidorContratosSuspensosPendentesReativacaoJob', CURRENT_DATE, CURRENT_DATE, NULL);