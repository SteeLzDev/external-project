-- DESENV-22215
INSERT INTO tb_tipo_notificacao (TNO_CODIGO, TNO_DESCRICAO, TNO_ENVIO) VALUES ('44', 'E-mail de notificação para portabilidade de cartão', 'I');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO , MEM_TEXTO)
VALUES ('enviarEmailPortabilidadeCartao', '<@nome_sistema>: Portabilidade de cartão efetuada com sucesso.', 'Prezados (as), Há Reservas de Margem de Cartão portada para sua Instituição:<br>ADE: <@ade_numero>,<br>CPF: <@ser_cpf>,<br>Matrícula: <@ser_matricula>.<br>Acesse o eConsig e consulte.<br>');

