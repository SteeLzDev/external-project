-- DESENV-9298
update tb_acesso_recurso set acr_recurso = '/v3/mantemConvenio', acr_parametro = 'acao', acr_operacao = 'iniciar' where acr_recurso = '/convenio/lst_convenios.jsp' and acr_parametro = 'tipo' and acr_operacao = 'consultar';
update tb_acesso_recurso set acr_recurso = '/v3/mantemConvenio', acr_parametro = 'acao', acr_operacao = 'editar' where acr_recurso = '/convenio/edt_convenio.jsp';
update tb_acesso_recurso set acr_recurso = '/v3/mantemConvenio', acr_parametro = 'acao', acr_operacao = 'edtPrioridadeCnv' where acr_recurso = '/convenio/edt_prioridade_convenios.jsp' and acr_parametro = 'tipo' and acr_operacao = 'consultar'; 

delete from tb_acesso_recurso where fun_codigo = '65' and acr_recurso = '/convenio/lst_convenios.jsp' and acr_operacao = 'editar';
delete from tb_acesso_recurso where fun_codigo = '65' and acr_recurso = '/convenio/edt_prioridade_convenios.jsp' and acr_operacao = 'editar';
