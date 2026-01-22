-- sincronizacao texto sistema
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE = 'mensagem.definicao.senha.email';
UPDATE tb_texto_sistema SET TEX_TEXTO = 'Deve-se realizar upload de um número mínimo de {0} arquivo(s) para contratos deste ${lower(rotulo.servico.singular)}. O prazo limite para o anexo dos arquivos para este contrato é até {1}. Falta(m) anexar {2} arquivo(s) neste contrato.' WHERE TEX_CHAVE = 'mensagem.alerta.reservar.margem.anexos.minimos';
