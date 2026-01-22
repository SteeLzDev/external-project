-- DESENV-10088
UPDATE tb_texto_sistema SET tex_chave = 'rotulo.integracao.orientada.servidor.nome.manual' WHERE tex_chave = 'rotulo.integracao.orientada.colaborador.nome.manual';

UPDATE tb_texto_sistema SET tex_chave = 'rotulo.integracao.orientada.servidor.tooltip.manual.ajuda' WHERE tex_chave = 'rotulo.integracao.orientada.colaborador.tooltip.manual.ajuda';

UPDATE tb_texto_sistema SET tex_chave = 'rotulo.integracao.orientada.servidor.tooltip.manual.ajuda.link' WHERE tex_chave = 'rotulo.integracao.orientada.colaborador.tooltip.manual.ajuda.link';

UPDATE tb_texto_sistema SET tex_chave = 'rotulo.integracao.orientada.servidor.dashboard.download.manual' WHERE tex_chave = 'rotulo.integracao.orientada.colaborador.dashboard.download.manual';
