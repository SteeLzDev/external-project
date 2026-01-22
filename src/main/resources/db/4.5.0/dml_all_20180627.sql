-- DESENV-8918

update tb_acesso_recurso set ACR_RECURSO = '/v3/visualizarHistorico', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' where ACR_RECURSO = '/margem/historico.jsp'; 
update tb_acesso_recurso set ACR_RECURSO = '/v3/listarOcorrenciaRegistroServidor', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' where FUN_CODIGO = '146' and ACR_RECURSO = '/servidor/lst_ocorrencia_rse.jsp'; 

delete from tb_ajuda where acr_codigo in (select acr_codigo from tb_acesso_recurso where FUN_CODIGO = '146' and ACR_PARAMETRO = 'tipo' and ACR_OPERACAO = 'historico');
delete from tb_acesso_recurso where FUN_CODIGO = '146' and ACR_PARAMETRO = 'tipo' and ACR_OPERACAO = 'historico';
