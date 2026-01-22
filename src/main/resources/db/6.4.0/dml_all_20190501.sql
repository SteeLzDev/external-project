-- DESENV-11524
UPDATE tb_texto_sistema SET TEX_TEXTO = 'ZZ12345' WHERE TEX_CHAVE = 'mensagem.placeholder.codigo.zetrasoft';
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE IN ('rotulo.email.titulo.convenios.anteriores', 'rotulo.email.titulo.convenios.atuais');
