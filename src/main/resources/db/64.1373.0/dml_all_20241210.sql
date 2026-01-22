-- DESENV-22658
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('972', 'Enviar notificação aos gestores sobre bloqueio de consignatárias e seus motivos', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('972', '1', 'N');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) VALUES ('enviarEmailCseBloqCsa', '<@nome_sistema> - Consignatária <@csa_nome>: Bloqueada', 'A consignatária <@csa_nome> foi bloqueada no sistema: <p> <b>Data bloqueio: </b> <@data_bloqueio_noescape><p> <b>Usuário que efetivou o bloqueio:</b> <@usuario_bloqueiou><p> <b>Motivo do bloqueio:</b> <@motivo_bloqueio>'); 

INSERT INTO tb_tipo_notificacao (TNO_CODIGO, TNO_DESCRICAO, TNO_ENVIO) VALUES ('43', 'E-mail de notificação de bloqueio de consignatária para o cse', 'I');

