-- DESENV-8168

update tb_texto_sistema set tex_texto = 'A solicitação do ${lower(rotulo.saldo.devedor.singular)} para todas ${lower(rotulo.consignataria.plural)} somente pode ser feita para ${lower(rotulo.servidor.plural)} bloqueados ou excluídos ou ativos.' where tex_chave = 'mensagem.erro.saldo.devedor.exclusao.servidor.status';
