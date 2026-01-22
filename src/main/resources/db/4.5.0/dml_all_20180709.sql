-- DESENV-9031

-- Apaga a mensagem caso esteja com valor incorreto
delete from tb_texto_sistema where tex_chave in ('mensagem.informe.servidor.cidade.xml', 'mensagem.informe.servidor.bairro.xml', 'mensagem.informe.servidor.cep.xml', 'mensagem.informe.servidor.estado.xml', 'mensagem.informe.servidor.logradouro.xml', 'mensagem.informe.servidor.telefone.xml', 'mensagem.informe.servidor.municipio.lotacao.xml') and tex_texto = '494';

-- Corrige mensagens incorretas
update tb_texto_sistema set tex_chave = replace(tex_chave, 'mensage.erro', 'mensagem.erro') where tex_chave like 'mensage.erro.%';
update tb_texto_sistema set tex_chave = 'mensagem.erro.nenhuma.mensagem.encontrada' where tex_chave = 'mensagem.erro.nenhuma.mensagem.encotrada';

-- NOVOS
update tb_texto_sistema set tex_chave = replace(tex_chave, 'adequacao.magem', 'adequacao.margem') where tex_chave like '%adequacao.magem%';
update tb_texto_sistema set tex_chave = 'mensagem.erro.nao.possivel.bloquear.usuarios.por.fim.vigencia' where tex_chave = 'mensagem.erro.nao.possivel.bloqear.usuarios.por.fim.vigencia';
update tb_texto_sistema set tex_chave = 'rotulo.validacao.modulo.beneficio.regra.verifica.subsidio.para.mensalidade.e.odontologico' where tex_chave = 'rotulo.validacao.modulo.beneficio.regra.verifica.subsidio.para.mensalidad.e.eodontologico';
