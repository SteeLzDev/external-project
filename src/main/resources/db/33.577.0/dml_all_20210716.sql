-- DESENV-16188
UPDATE tb_texto_sistema SET TEX_TEXTO = 'A nova margem {0} não pode ser maior do que a margem {0} atual.' WHERE TEX_CHAVE = 'mensagem.erro.nova.margem.arg0.nao.pode.ser.maior.arg0.atual';
UPDATE tb_texto_sistema SET TEX_TEXTO = 'A nova margem {0} não pode ser menor do que a margem {0} usada.' WHERE TEX_CHAVE = 'mensagem.erro.nova.margem.arg0.nao.pode.ser.menor.arg0.usada';