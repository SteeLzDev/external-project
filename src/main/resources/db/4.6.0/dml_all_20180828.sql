-- DESENV-9543

update tb_acesso_recurso set ACR_RECURSO = '/v3/processarLote', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'listarLoteMultiplo' where ACR_RECURSO = '/lote/lst_xml.jsp' and FUN_CODIGO = '314' and ACR_OPERACAO = 'processar' and ACR_PARAMETRO = 'tipo';
