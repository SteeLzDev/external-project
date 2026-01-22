-- DESENV-18063
INSERT INTO tb_tipo_motivo_bloqueio (TMB_CODIGO,TMB_DESCRICAO)
VALUES ('9','Pendente de aprovação');

INSERT INTO tb_tipo_ocorrencia (toc_codigo,TOC_DESCRICAO)
VALUES ('204','Desbloqueio da consignatária pendente ');

INSERT INTO tb_modelo_email
(MEM_CODIGO, MEM_TITULO, MEM_TEXTO)
VALUES('enviarEmailConfirmacaoDesbloqCSA', 'Solicitação de desbloqueio de consignatária <@csa_nome> - <@nome_sistema>', 'Usuário:<@nome> <br>\nSolicita o desbloqueio da consignatária: <@csa_nome>.');

INSERT INTO tb_tipo_notificacao (TNO_CODIGO,TNO_DESCRICAO,TNO_ENVIO)
VALUES ('30','E-mail de solicitação de Desbloqueio de CSA','I');