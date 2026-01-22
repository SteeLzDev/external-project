-- DESENV-22132
UPDATE tb_texto_sistema SET TEX_CHAVE = 'Não é possível inserir ou alterar esta reserva pois o ${lower(rotulo.servidor.singular)} está bloqueado para o ${lower(rotulo.convenio.singular)} escolhido. Motivo: {0}.' WHERE TEX_CHAVE LIKE 'mensagem.servidorBloqueadoConvenio.motivo';

