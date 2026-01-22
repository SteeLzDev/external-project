-- DESENV-14506
UPDATE tb_texto_sistema
SET TEX_TEXTO = 'Criar nova mensagem de portabilidade', TEX_DATA_ALTERACAO = NULL
WHERE TEX_CHAVE = 'rotulo.criar.email.csa.portabilidade.titulo';

UPDATE tb_texto_sistema
SET TEX_TEXTO = 'Mensagem enviada com sucesso.', TEX_DATA_ALTERACAO = NULL
WHERE TEX_CHAVE = 'mensagem.email.enviado.sucesso';

UPDATE tb_texto_sistema
SET TEX_TEXTO = 'Clique aqui para enviar uma mensagem para a consignatária da portabilidade.', TEX_DATA_ALTERACAO = NULL
WHERE TEX_CHAVE = 'mensagem.acao.enviar.mensagem.csa.destino.clique.aqui';

UPDATE tb_texto_sistema
SET TEX_TEXTO = 'Enviar mensagem de portabilidade', TEX_DATA_ALTERACAO = NULL
WHERE TEX_CHAVE = 'mensagem.acao.enviar.mensagem.csa.destino';
