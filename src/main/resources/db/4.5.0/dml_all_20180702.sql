-- DESENV-8930

update tb_acesso_recurso set ACR_RECURSO = '/v3/listarOrgao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' where ACR_RECURSO = '/orgao/lst_orgao.jsp';
update tb_acesso_recurso set ACR_RECURSO = '/v3/editarOrgao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'bloquearOrgao' where ACR_RECURSO = '/orgao/modifica_orgao.jsp' and ACR_OPERACAO = 'editar';
update tb_acesso_recurso set ACR_RECURSO = '/v3/editarOrgao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'excluirOrgao' where ACR_RECURSO = '/orgao/modifica_orgao.jsp' and ACR_PARAMETRO = 'excluir';
update tb_acesso_recurso set ACR_RECURSO = '/v3/editarOrgao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'consultarOrgao' where ACR_RECURSO = '/orgao/edt2_orgao.jsp' and ACR_OPERACAO = 'consultar';
update tb_acesso_recurso set ACR_RECURSO = '/v3/editarOrgao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'editarOrgao' where ACR_RECURSO = '/orgao/edt2_orgao.jsp' and ACR_OPERACAO = 'editar';
update tb_acesso_recurso set ACR_RECURSO = '/v3/editarOrgao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'editarIp' where ACR_RECURSO = '/orgao/edt2_orgao.jsp' and ACR_OPERACAO = 'edt_ip';

update tb_acesso_recurso set ACR_RECURSO = '/v3/listarServicoOrgao', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' where ACR_RECURSO = '/orgao/lst_servico.jsp' and ACR_OPERACAO = 'consultar';
update tb_acesso_recurso set ACR_RECURSO = '/v3/listarServicoCorrespondente', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' where ACR_RECURSO = '/orgao/lst_servico_correspondente.jsp' and ACR_OPERACAO = 'consultar';
update tb_acesso_recurso set ACR_RECURSO = '/v3/listarServicoConvenios', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'consultarConvenio' where ACR_RECURSO = '/orgao/lst_convenios.jsp' and FUN_CODIGO = '94';
update tb_acesso_recurso set ACR_RECURSO = '/v3/listarServicoConvenios', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'editarConvenio' where ACR_RECURSO = '/orgao/lst_convenios.jsp' and FUN_CODIGO = '93';
