-- DESENV-16139
-- Remoção de acesso recurso de consulta de margem desnecessário
DELETE FROM tb_ajuda WHERE ACR_CODIGO IN ('14266', '14268', '14270', '14272', '14274');
DELETE FROM tb_acesso_usuario WHERE ACR_CODIGO IN ('14266', '14268', '14270', '14272', '14274');
DELETE FROM tb_acesso_recurso WHERE ACR_CODIGO IN ('14266', '14268', '14270', '14272', '14274');

-- Remoção de acesso recurso para /arquivos/delete.jsp que não existe mais
DELETE FROM tb_ajuda WHERE ACR_CODIGO IN ('12755', '12756');
DELETE FROM tb_acesso_usuario WHERE ACR_CODIGO IN ('12755', '12756');
DELETE FROM tb_acesso_recurso WHERE ACR_CODIGO IN ('12755', '12756');

-- Remoção de acesso recurso para /indice/edt_indice.jsp que não existe mais
DELETE FROM tb_ajuda WHERE ACR_CODIGO IN ('10281', '10282', '12049');
DELETE FROM tb_acesso_usuario WHERE ACR_CODIGO IN ('10281', '10282', '12049');
DELETE FROM tb_acesso_recurso WHERE ACR_CODIGO IN ('10281', '10282', '12049');

-- Correção de acesso recurso para edição de índices
UPDATE tb_acesso_recurso SET FUN_CODIGO = '164' WHERE FUN_CODIGO = '165' AND ACR_OPERACAO IN ('editar', 'modificar');

-- Correção de textos do sistema
UPDATE tb_texto_sistema SET TEX_TEXTO = 'Importar arquivo de conciliação' WHERE TEX_CHAVE = 'rotulo.importar.arquivo.conciliacao.titulo';
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE = CONCAT('rotulo', CAST(0x3b AS CHAR), 'conf.banner.tabela.titulo');
