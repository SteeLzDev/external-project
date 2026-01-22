-- DESENV-14183
UPDATE tb_texto_sistema SET TEX_TEXTO = 'O eConsig possui {0} ${lower(rotulo.servidor.plural)} cadastrados, sendo {1} ativos, {2} bloqueados, {3} transferidos e {4} excluídos.' WHERE tex_chave = 'mensagem.relatorio.gerencialgeralservidores.quantidade.servidores';
