-- DESENV-11603
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/listarRelatorio' WHERE ACR_RECURSO = '/relatorios/lst_relatorio.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/executarRelatorio' WHERE ACR_RECURSO = '/relatorios/relatorio.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/agendarRelatorio' WHERE ACR_RECURSO = '/relatorios/agenda_relatorio.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/cancelarAgendamentoRelatorio' WHERE ACR_RECURSO = '/relatorios/cancela_agendamento.jsp';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/downloadArquivo' WHERE ACR_RECURSO = '/arquivos/download.jsp' AND ACR_PARAMETRO = 'subtipo';
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/excluirArquivo' WHERE ACR_RECURSO = '/arquivos/delete.jsp' AND ACR_PARAMETRO = 'subtipo' AND ACR_OPERACAO <> 'banner';

UPDATE tb_texto_sistema SET TEX_TEXTO = 'Relatório excluído com sucesso.' WHERE TEX_CHAVE = 'mensagem.excluir.relatorio.sucesso';
