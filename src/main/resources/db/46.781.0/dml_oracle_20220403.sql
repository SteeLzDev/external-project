-- DESENV-17484
-- @@delimiter=!

INSERT INTO tb_tipo_notificacao (TNO_CODIGO,TNO_DESCRICAO,TNO_ENVIO) VALUES (27,'E-mail de notificação de contrato sendo colocado em estoque','I')
!

DECLARE
vClob VARCHAR(8000);

BEGIN

vClob := '<br> Prezada consignatária <@nome_csa_abrev>,<br>O contrato abaixo foi colocado em estoque devido à inserção de um desconto compulsório.<br><b>Matrícula:</b><@matricula_servidor><br><b>CPF:</b><@cpf_servidor><br><b>Nome:</b><@nome_servidor><br><b>Valor Prestação:</b><@valor_contrato><br><b>Número de Prestações:</b><@prazo_contrato><br><b>Número ADE:</b><@numero_contrato>';

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) 
VALUES ('enviarEmailCsaAdeStatusEmEstoque', '<@nome_sistema>: Contrato colocado em estoque', vClob);

END;
!
