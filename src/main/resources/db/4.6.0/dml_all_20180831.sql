UPDATE tb_texto_sistema SET TEX_TEXTO = '512' WHERE TEX_CHAVE = 'mensagem.informacao.simulacao.informar.confirmacao.leitura.xml';
UPDATE tb_texto_sistema SET TEX_TEXTO = '<br><b>Resumo</b> do processamento do arquivo:' WHERE TEX_CHAVE = 'mensagem.integracao.orientada.resumo.processamento.titulo';
UPDATE tb_texto_sistema SET TEX_TEXTO = 'Clique aqui para fazer o download do manual de uso e com os leiautes dos arquivos da Integração Orientada.' WHERE TEX_CHAVE = 'rotulo.integracao.orientada.dashboard.download.manual.ajuda';
UPDATE tb_texto_sistema SET TEX_TEXTO = 'Download do Manual de <b>Orientações</b>' WHERE TEX_CHAVE = 'rotulo.integracao.orientada.dashboard.download.manual';
UPDATE tb_texto_sistema SET TEX_TEXTO = 'O órgão deve ser informado.' WHERE TEX_CHAVE = 'mensagem.beneficio.orgao.informar';

delete from tb_texto_sistema where tex_chave in ('mensagem.confirmacao.validacao.arquivo');
