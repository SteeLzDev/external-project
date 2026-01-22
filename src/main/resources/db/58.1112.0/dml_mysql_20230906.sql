-- DESENV-20304
INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO)
VALUES ('enviarEmailCsaNovoVinculo', '<@nome_sistema> - Novo vínculo criado', 'Prezada <@csa_nome>,<br> foi criado o vínculo <@vinculo_identificador>-<@vinculo_descricao> no dia <@dia_criacao> , gentileza acessar o sistema se deseja <@situacao_vinculo> o vínculo criado');

INSERT INTO tb_tipo_notificacao (TNO_CODIGO, TNO_DESCRICAO, TNO_ENVIO) VALUES ('37', 'E-mail notificação novo vínculo', 'I');

-- MYSQL
INSERT INTO tb_agendamento (AGD_CODIGO, TAG_CODIGO, SAG_CODIGO, USU_CODIGO, AGD_DESCRICAO, AGD_JAVA_CLASS_NAME, AGD_DATA_CADASTRO, AGD_DATA_PREVISTA, REL_CODIGO)
VALUES ('53', '1', '1', '1', 'Envia notificação para consignatárias caso um vinculo tenha sido criado', 'com.zetra.econsig.job.jobs.EnviaNotificaoCsaNovosVinculosJob', CURDATE(), CURDATE(), NULL);

