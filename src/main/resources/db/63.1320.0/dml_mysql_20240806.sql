-- DESENV-22002
-- Remove acentuação das chaves
UPDATE tb_texto_sistema SET TEX_CHAVE = 'mensagem.erro.consignataria.bloqueio.pendente.aprovacao' WHERE TEX_CHAVE = 'mensagem.erro.consignataria.bloqueio.pendente.aprovação';
UPDATE tb_texto_sistema SET TEX_CHAVE = 'mensagem.info.reimplantar.parcela.manual.reimplante.nao.existe' WHERE TEX_CHAVE = 'mensagem.info.reimplantar.parcela.manual.reimplante.não.existe';
UPDATE tb_texto_sistema SET TEX_CHAVE = 'mensagem.informacao.atualizando.bloqueio.consignataria' WHERE TEX_CHAVE = 'mensagem.informacao.atualizando.bloqueio.consignatária';
UPDATE tb_texto_sistema SET TEX_CHAVE = 'mensagem.informacao.ptf.rever.pontuacao.leilao' WHERE TEX_CHAVE = 'mensagem.informacao.ptf.rever.pontuação.leilao';
UPDATE tb_texto_sistema SET TEX_CHAVE = 'rotulo.beneficio.tipo.beneficiario' WHERE TEX_CHAVE = 'rotulo.beneficio.tipo.beneficiário';
UPDATE tb_texto_sistema SET TEX_CHAVE = 'rotulo.listar.bloqueios.solicitacao.liquidacao.nao.atendida' WHERE TEX_CHAVE = 'rotulo.listar.bloqueios.solicitação.liquidacao.nao.atendida';
UPDATE tb_texto_sistema SET TEX_CHAVE = 'rotulo.paginacao.titulo.bloqueio.pela.solicitacao.liquidacao.nao.atendida' WHERE TEX_CHAVE = 'rotulo.paginacao.titulo.bloqueio.pela.solicitação.liquidacao.nao.atendida';
UPDATE tb_texto_sistema SET TEX_CHAVE = 'rotulo.relatorio.consignatariascsa.data.expiracao.cadastral' WHERE TEX_CHAVE = 'rotulo.relatorio.consignatariascsa.data.expiração.cadastral';
UPDATE tb_texto_sistema SET TEX_CHAVE = 'rotulo.relatorio.sinteticogerencialconsignataria.ultimo.movimento.inadimplencia' WHERE TEX_CHAVE = 'rotulo.relatorio.sinteticogerencialconsignataria.ultimo.movimento.inadimplência';

-- Remove espaços dos textos das chaves
UPDATE tb_texto_sistema SET TEX_TEXTO = TRIM(TEX_TEXTO) WHERE LENGTH(TEX_TEXTO) <> LENGTH(TRIM(TEX_TEXTO));

-- Remove chaves que contém caracteres especiais (provavelmente alguma inconsistência de codificação de caracteres)
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE REGEXP '[^\\p{ASCII}]';

