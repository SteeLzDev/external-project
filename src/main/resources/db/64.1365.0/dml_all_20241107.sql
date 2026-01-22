-- DESENV-22331
UPDATE tb_texto_sistema
SET TEX_TEXTO = 'Selecione abaixo as consignatárias que você autoriza consultar, reservar margem, realizar portabilidade ou realizar renegociação:'
WHERE TEX_CHAVE = 'mensagem.informacao.autorizar.margem.consignataria';

UPDATE tb_texto_sistema
SET TEX_TEXTO = 'Autorizo as consignatárias selecionadas a consultar, reservar margem, realizar portabilidade e/ou realizar renegociação de contrato.'
WHERE TEX_CHAVE = 'mensagem.autorizar.margem.consignataria.modal';

