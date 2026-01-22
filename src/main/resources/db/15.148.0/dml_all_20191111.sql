-- DESENV-9275
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/consultarExtratoDivida', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE FUN_CODIGO = '150' AND ACR_RECURSO = '/margem/consignacao.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/consultarExtratoDivida', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'pesquisarServidor' WHERE FUN_CODIGO = '150' AND ACR_RECURSO = '/margem/seleciona_servidor.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/consultarExtratoDivida', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'consultarExtrato' WHERE FUN_CODIGO = '150' AND ACR_RECURSO = '/servidor/extrato_divida.jsp';

DELETE FROM tb_ajuda WHERE ACR_CODIGO IN (SELECT ACR_CODIGO FROM tb_acesso_recurso WHERE FUN_CODIGO = '150' AND ACR_RECURSO = '/margem/pesquisa.jsp');
DELETE FROM tb_acesso_usuario WHERE ACR_CODIGO IN (SELECT ACR_CODIGO FROM tb_acesso_recurso WHERE FUN_CODIGO = '150' AND ACR_RECURSO = '/margem/pesquisa.jsp');
DELETE FROM tb_acesso_recurso WHERE FUN_CODIGO = '150' AND ACR_RECURSO = '/margem/pesquisa.jsp';
