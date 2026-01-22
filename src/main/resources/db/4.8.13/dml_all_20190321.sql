-- DESENV-10947
update tb_texto_sistema set tex_texto = 'A data de casamento deve ser informada.' where TEX_CHAVE = 'mensagem.erro.data.casamento.nao.informada.para.grau.parentesco.selecionado';
