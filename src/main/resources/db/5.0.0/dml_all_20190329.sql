-- DESENV-11093

delete from tb_texto_sistema where tex_chave in ('tb_tipo_dados_autorizacao.tda_codigo', 'tb_tipo_dados_autorizacao.tda_descricao');

UPDATE tb_texto_sistema SET TEX_TEXTO = 'Clique aqui para listar os arquivos de eventos disponíveis para download.' WHERE TEX_CHAVE = 'rotulo.integracao.orientada.dashboard.download.movimento.ajuda' AND TEX_TEXTO = 'Clique aqui para listar os arqiuvos de eventos disponíveis para download.';
UPDATE tb_texto_sistema SET TEX_TEXTO = 'Upload de lote "{0}" em processamento.' WHERE TEX_CHAVE = 'mensagem.informacao.arquivo.lote.soap.processando' AND TEX_TEXTO = 'Lote "{0}" em processamento.';
