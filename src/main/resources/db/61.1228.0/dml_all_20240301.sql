-- DESENV-21225
UPDATE tb_texto_sistema
SET TEX_TEXTO = 'Não é permitido reservar margem para o vínculo deste ${lower(rotulo.servidor.singular)}: {0}'
WHERE TEX_CHAVE = 'mensagem.vinculoNaoPermiteReserva';

UPDATE tb_texto_sistema
SET TEX_TEXTO = 'HC Não é permitido reservar margem para o vínculo deste ${lower(rotulo.servidor.singular)}: {0}.'
WHERE TEX_CHAVE = 'mensagem.vinculoNaoPermiteReserva.lote';

UPDATE tb_texto_sistema
SET TEX_TEXTO = 'HC Não é permitido reservar margem para o vínculo deste ${lower(rotulo.servidor.singular)}: {0}.'
WHERE TEX_CHAVE = 'mensagem.vinculoNaoPermiteReserva.febraban';

UPDATE tb_texto_sistema
SET TEX_TEXTO = 'Não é permitido consultar margem para o vínculo deste ${lower(rotulo.servidor.singular)}: {0}.'
WHERE TEX_CHAVE = 'mensagem.vinculoNaoPermiteConsultarMargem';

