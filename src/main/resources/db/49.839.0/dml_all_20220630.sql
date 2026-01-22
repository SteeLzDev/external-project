-- DESENV-17957
INSERT INTO tb_modelo_email
(MEM_CODIGO, MEM_TITULO, MEM_TEXTO)
VALUES('enviarEmailNotDesbloqCSA', 'DESBLOQUEIO CSA <@csa_nome> - <@nome_sistema>', 'Informamos que foi realizado o desbloqueio da CSA <@csa_nome> no convênio <@nome_consignante>.');

INSERT INTO tb_tipo_notificacao (TNO_CODIGO,TNO_DESCRICAO,TNO_ENVIO)
VALUES ('31','E-mail de Notificação de Desbloqueio de CSA','I');