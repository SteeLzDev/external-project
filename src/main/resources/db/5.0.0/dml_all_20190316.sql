-- DESENV-5666

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('632', 'Quantidade de dias para notificação de bloqueio por inatividade do usuário', 'INT', '0', 'N', 'N', 'N', 'N', '3');

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('632', '1', '0');

INSERT INTO tb_agendamento (AGD_CODIGO, TAG_CODIGO, SAG_CODIGO, USU_CODIGO, AGD_DESCRICAO, AGD_JAVA_CLASS_NAME, AGD_DATA_CADASTRO, AGD_DATA_PREVISTA, REL_CODIGO)
VALUES ('35', '1', '1', '1', 'Envia Notificação Bloqueio Automático de Usuários Inativos', 'com.zetra.timer.econsig.job.EnviaNotificacaoBloqueioUsuarioPorInatividadeJob', now(), now(), NULL);

INSERT INTO tb_tipo_notificacao (TNO_CODIGO, TNO_DESCRICAO, TNO_ENVIO)
VALUES ('8', 'E-mail notificação bloqueio de usuário inativos', 'D');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO)
VALUES ('enviarNotifUsuBloqInatividade', 'Sr.(a) <@usu_nome>,', '<p>Caso você não acesse o sistema até a data <@dataLimiteAcesso>, você será bloqueado(a) por inatividade.<br><br>Qualquer dúvida entre em contato com o suporte.</P>');
