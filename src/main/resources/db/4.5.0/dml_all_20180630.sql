-- DESENV-8922

update tb_acesso_recurso set ACR_OPERACAO = 'listar' where ACR_RECURSO = '/v3/enviarComunicacao' and ACR_OPERACAO = 'iniciar';

update tb_acesso_recurso set ACR_RECURSO = '/v3/enviarComunicacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' where ACR_RECURSO = '/margem/consignacao.jsp' and ACR_OPERACAO = 'sel_servidor' and FUN_CODIGO = '230';

update tb_acesso_recurso set ACR_RECURSO = '/v3/enviarComunicacao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'pesquisarServidor' where ACR_RECURSO = '/margem/pesquisa.jsp' and ACR_OPERACAO = 'sel_servidor' and FUN_CODIGO = '230';

delete from tb_ajuda where acr_codigo in (select acr_codigo from tb_acesso_recurso where ACR_RECURSO = '/margem/seleciona_servidor.jsp' and FUN_CODIGO = '230' and ACR_PARAMETRO = 'tipo' and ACR_OPERACAO = 'sel_servidor');
delete from tb_acesso_recurso where ACR_RECURSO = '/margem/seleciona_servidor.jsp' and FUN_CODIGO = '230' and ACR_PARAMETRO = 'tipo' and ACR_OPERACAO = 'sel_servidor';
