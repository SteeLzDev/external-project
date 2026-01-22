-- DESENV-23307
UPDATE tb_relatorio_filtro SET RFI_EXIBE_SER = '2' WHERE TFR_CODIGO = 'campo_formato_relatorio' AND REL_CODIGO = 'taxas';

UPDATE tb_relatorio_filtro SET RFI_EXIBE_SER = '2' WHERE TFR_CODIGO = 'campo_svc_taxas' AND REL_CODIGO = 'taxas';

UPDATE tb_relatorio_filtro SET RFI_EXIBE_SER = '1' WHERE TFR_CODIGO = 'campo_agendado' AND REL_CODIGO = 'taxas';

UPDATE tb_relatorio_filtro SET RFI_EXIBE_SER = '1' WHERE TFR_CODIGO = 'campo_data_execucao' AND REL_CODIGO = 'taxas';

UPDATE tb_relatorio_filtro SET RFI_EXIBE_SER = '1' WHERE TFR_CODIGO = 'campo_data_vigencia' AND REL_CODIGO = 'taxas';

UPDATE tb_relatorio_filtro SET RFI_EXIBE_SER = '1' WHERE TFR_CODIGO = 'campo_envio_email' AND REL_CODIGO = 'taxas';

UPDATE tb_relatorio_filtro SET RFI_EXIBE_SER = '1' WHERE TFR_CODIGO = 'campo_periodicidade' AND REL_CODIGO = 'taxas';

UPDATE tb_relatorio_filtro SET RFI_EXIBE_SER = '1' WHERE TFR_CODIGO = 'campo_prazo' AND REL_CODIGO = 'taxas';

UPDATE tb_relatorio_filtro SET RFI_EXIBE_SER = '1' WHERE TFR_CODIGO = 'campo_status_csa' AND REL_CODIGO = 'taxas';

UPDATE tb_relatorio_filtro SET RFI_EXIBE_SER = '1' WHERE TFR_CODIGO = 'campo_taxas' AND REL_CODIGO = 'taxas';

UPDATE tb_relatorio_filtro SET RFI_EXIBE_SER = '1' WHERE TFR_CODIGO = 'campo_tipo_agendamento' AND REL_CODIGO = 'taxas';

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('17088', '6', '134', '/v3/excluirArquivo', 'subtipo', 'taxas', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('17089', '6', '134', '/v3/downloadArquivo', 'subtipo', 'taxas', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('17090', '6', '134', '/v3/listarRelatorio', 'tipo', 'taxas', 1, 'S', 'S', '68', 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('17091', '6', '134', '/v3/executarRelatorio', 'tipoRelatorio', 'taxas', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('17092', '6', '134', '/v3/agendarRelatorio', 'tipo', 'taxas', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('17093', '6', '134', '/v3/cancelarAgendamentoRelatorio', 'tipo', 'taxas', 1, 'S', 'S', NULL, 'N', '2');

