-- DESENV-20466
-- MYSQL
SET @total_chaves = (SELECT COUNT(TEX_CHAVE) FROM tb_texto_sistema WHERE TEX_CHAVE IN ('mobile.mensagem.codigo.unico.enviado.por.email', 'mensagem.mobile.codigo.unico.enviado.por.email'));

DELETE FROM tb_texto_sistema
WHERE TEX_CHAVE IN ('mobile.mensagem.erro.envio.otp.sms.email', 'mobile.mensagem.codigo.unico.enviado.por.email', 'mobile.mensagem.codigo.unico.enviado.por.email.e.tela', 'mobile.mensagem.codigo.unico.enviado.por.sms', 'mobile.mensagem.codigo.unico.enviado.por.email.sms', 'mobile.mensagem.erro.envio.codigo.unico', 'mobile.mensagem.codigo.unico.enviado.por.email.ou.tela') AND @total_chaves = 2;

UPDATE tb_texto_sistema
SET TEX_CHAVE = REPLACE(TEX_CHAVE, 'mensagem.mobile', 'mobile.mensagem')
WHERE TEX_CHAVE IN ('mensagem.mobile.erro.envio.otp.sms.email', 'mensagem.mobile.codigo.unico.enviado.por.email', 'mensagem.mobile.codigo.unico.enviado.por.email.e.tela', 'mensagem.mobile.codigo.unico.enviado.por.sms', 'mensagem.mobile.codigo.unico.enviado.por.email.sms', 'mensagem.mobile.erro.envio.codigo.unico', 'mensagem.mobile.codigo.unico.enviado.por.email.ou.tela');

