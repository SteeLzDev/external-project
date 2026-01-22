-- DESENV-22123
INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO) VALUES ('242', 'Desbloqueio de verbas dos registros servidores');

INSERT INTO tb_tipo_notificacao (TNO_CODIGO, TNO_DESCRICAO, TNO_ENVIO)
VALUES ('42', 'E-mail de Notificação de Bloqueio de Verba do Registro Servidor', 'I');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO)
VALUES ('enviarEmailCsaDesbloqueioVerba', '<@nome_sistema> - <@nome_consignante>: Desbloqueios de todas as verbas de registros servidores', 'Prezada <@csa_nome>, Informamos que foi realizado o desbloqueio de todas as verbas de <@qtde_desbloqueios> registros servidores pelo usuário <@usuario_logado> em <@agora>.');

