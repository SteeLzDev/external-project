UPDATE tb_texto_sistema
SET TEX_TEXTO='ATENÇÃO: Contrato não foi registrado no sistema.<br>Não existe ${rotulo.cet.abreviado} anunciado para o ${lower(rotulo.prazo.singular)} do contrato.', TEX_DATA_ALTERACAO=NULL
WHERE TEX_CHAVE='mensagem.aviso.sem.cet.prazo.csa';
