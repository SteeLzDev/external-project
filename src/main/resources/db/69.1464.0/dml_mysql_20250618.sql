-- DESENV-23119
INSERT INTO tb_tipo_param_consignataria (TPA_CODIGO, TPA_DESCRICAO, TPA_DOMINIO, TPA_CSE_ALTERA, TPA_SUP_ALTERA, TPA_CSA_ALTERA)
VALUES ('103', 'Envio de notificação para CSA quando houver alteração nas regras do convênio', 'SN', 'N', 'N', 'N');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) VALUES ('enviarEmailCsaNotificacaoRegras', 'Regra de convênio alterada para <@nome_consignante>', 'Prezado (a) <@csa_nome>,<br>Informamos que as seguintes regras de convênio foram alteradas para <@nome_consignante>: <br>[Lista das regras de alteradas] <br> <@dados_regras> <br> Atenciosamente,<br> <@logoSistema>');

INSERT INTO tb_tipo_notificacao (TNO_CODIGO, TNO_DESCRICAO, TNO_ENVIO) VALUES ('50', 'E-mail de notificação a CSA sobre alteração nas regras do convênio', 'I');

-- MYSQL
INSERT INTO tb_agendamento (AGD_CODIGO, TAG_CODIGO, SAG_CODIGO, USU_CODIGO, AGD_DESCRICAO, AGD_JAVA_CLASS_NAME, AGD_DATA_CADASTRO, AGD_DATA_PREVISTA, REL_CODIGO)
VALUES ('59', '1', '1', '1', 'Envia notificação para CSAs sobre alteração das regras de convênio', 'com.zetra.econsig.job.jobs.EnviaNotificacaoCsaAlteracaoRegrasConvenioJob', CURDATE(), CURDATE(), NULL);

