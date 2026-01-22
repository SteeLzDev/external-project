-- DESENV-15975
UPDATE tb_texto_sistema SET tex_texto = REPLACE(tex_texto,'Caro ','Caro(a) ') WHERE tex_chave = 'mensagem.informacao.visualizar.comunicado.rescisao.topo'; 

