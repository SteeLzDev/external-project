-- DESENV-22333
UPDATE tb_texto_sistema 
SET TEX_TEXTO = 'Selecione abaixo as consignatárias que você autoriza consultar, reservar margem, realizar portabilidade ou realizar renegociação:' 
WHERE TEX_CHAVE = 'mobile.message.selecione.consignataria.sem.senha';

