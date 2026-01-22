-- DESENV-13743
UPDATE tb_texto_sistema 
SET TEX_TEXTO = REPLACE(TEX_TEXTO, '${lower(rotulo.servidor.singular)}', 'diretório de arquivos do sistema') 
WHERE TEX_CHAVE IN ('mensagem.erro.arquivos.conciliacao.nao.encontrados.servidor', 'mensagem.erro.lote.arquivo.nao.encontrado', 'mensagem.erro.sistema.arquivo.falecido.nao.encontrado', 'mensagem.erro.sistema.arquivo.inconsistencia.nao.encontrado');
