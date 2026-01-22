-- DESENV-11982
UPDATE tb_texto_sistema SET TEX_TEXTO = 'Confirmar nova senha app' WHERE TEX_CHAVE = 'rotulo.usuario.confirma.nova.senha.app';
UPDATE tb_texto_sistema SET TEX_TEXTO = 'O seu código de autorização para a solicitação de consignação é:' WHERE TEX_CHAVE = 'mensagem.sms.servidor.codigo.unico';
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE = 'rotulo.informe.codigo.unico';
