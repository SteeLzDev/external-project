-- DESENV-10287
update tb_acesso_recurso set acr_recurso = '/v3/downloadArquivo' where acr_codigo in ('11487', '11488', '11489', '11490', '11491', '12436');
update tb_acesso_recurso set acr_operacao = 'editar_anexo' where acr_codigo in ('14758', '14759', '14760', '14761', '14762', '14763');
