-- DESENV-10046
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/downloadArquivo', ACR_PARAMETRO = 'tipo', ACR_OPERACAO = 'manualFolha' WHERE ACR_CODIGO IN ('14929', '15797');

DELETE FROM tb_ajuda WHERE ACR_CODIGO IN ('14904', '14908', '15115', '15118');
DELETE FROM tb_acesso_usuario WHERE ACR_CODIGO IN ('14904', '14908', '15115', '15118');
DELETE FROM tb_acesso_recurso WHERE ACR_CODIGO IN ('14904', '14908', '15115', '15118');

UPDATE tb_texto_sistema SET TEX_TEXTO = 'manual_eventos.pdf' WHERE TEX_CHAVE = 'rotulo.download.arquivos.integracao.nome.manual';

DELETE FROM tb_ajuda WHERE ACR_CODIGO IN ('14907', '15117');
DELETE FROM tb_acesso_usuario WHERE ACR_CODIGO IN ('14907', '15117');
DELETE FROM tb_acesso_recurso WHERE ACR_CODIGO IN ('14907', '15117');

UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/excluirArquivo' WHERE FUN_CODIGO = '32' AND ACR_RECURSO = '/arquivos/delete.jsp';
DELETE FROM tb_ajuda WHERE ACR_CODIGO IN (SELECT ACR_CODIGO FROM tb_acesso_recurso WHERE FUN_CODIGO = '32' AND ACR_OPERACAO = 'excluirArquivo');
DELETE FROM tb_acesso_usuario WHERE ACR_CODIGO IN (SELECT ACR_CODIGO FROM tb_acesso_recurso WHERE FUN_CODIGO = '32' AND ACR_OPERACAO = 'excluirArquivo');
DELETE FROM tb_acesso_recurso WHERE FUN_CODIGO = '32' AND ACR_OPERACAO = 'excluirArquivo';

UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/excluirArquivo', ACR_PARAMETRO = 'tipo', ACR_OPERACAO = 'duplicacao' WHERE FUN_CODIGO = '114' AND ACR_RECURSO = '/v3/duplicarParcela' AND ACR_OPERACAO = 'excluirArquivo';
