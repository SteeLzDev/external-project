-- DESENV-9754
INSERT INTO tb_funcao (fun_codigo, grf_codigo, fun_descricao, fun_permite_bloqueio, fun_exige_tmo, fun_exige_segunda_senha_cse, fun_auditavel, fun_restrita_nca, fun_exige_segunda_senha_sup, fun_exige_segunda_senha_org, fun_exige_segunda_senha_csa, fun_exige_segunda_senha_cor)
VALUES ('433', '4', 'Relatório de Contratos de Benefícios', 'N','N','N','N','N','N','N','N','N');

-- INSERT INTO tb_papel_funcao (pap_codigo, fun_codigo)
-- VALUES ('7', '433');

INSERT INTO tb_relatorio (rel_codigo, fun_codigo, tag_codigo, rel_titulo, rel_ativo, rel_agendado, rel_classe_relatorio, rel_classe_processo, rel_classe_agendamento,rel_template_jasper, rel_template_dinamico, rel_template_subrelatorio, rel_template_sql, rel_qtd_dias_limpeza,rel_customizado, rel_agrupamento)
VALUES ('contratos_beneficios', '433', NULL, 'Relatório de Contratos de Benefícios', '1', 'N', 'com.zetra.report.reports.RelatorioContratosBeneficios', 'com.zetra.processamento.ProcessaRelatorioContratosBeneficios', NULL, 'ContratosBeneficios.jasper', NULL, NULL, NULL, 30, 'N', NULL);

INSERT INTO tb_item_menu (itm_codigo,mnu_codigo,itm_codigo_pai,itm_descricao,itm_ativo,itm_sequencia,itm_separador,itm_centralizador)
VALUES ('210', '2', NULL, 'Contratos de Benefícios', 1, 69, 'N', 'S');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14996', '7', '433', '/v3/excluirArquivo', 'subtipo', 'contratos_beneficios', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14997', '7', '433', '/v3/executarRelatorio', 'tipoRelatorio', 'contratos_beneficios', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14998', '7', '433', '/v3/listarRelatorio', 'tipo', 'contratos_beneficios', 1, 'S', 'S', '210', 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14999', '7', '433', '/v3/downloadArquivo', 'subtipo', 'contratos_beneficios', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15000', '7', '433', '/v3/agendarRelatorio', 'tipo', 'contratos_beneficios', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15001', '7', '433', '/v3/cancelarAgendamentoRelatorio', 'tipo', 'contratos_beneficios', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_tipo_filtro_relatorio (tfr_codigo,tfr_descricao,tfr_recurso,tfr_exibe_edicao)
VALUES ('campo_status_contrato_beneficio', 'Situação do Benefício', '/relatorios/campos_relatorio/campo_status_contrato_beneficio.jsp', 'S');

INSERT INTO tb_relatorio_filtro (rel_codigo, tfr_codigo, rfi_exibe_cse, rfi_exibe_csa, rfi_exibe_cor, rfi_exibe_org, rfi_exibe_ser, rfi_sequencia, rfi_parametro, rfi_exibe_sup)
VALUES ('contratos_beneficios', 'campo_data_inicio_fim', '0', '0', '0', '0', '0', 1, '90', '2');

INSERT INTO tb_relatorio_filtro (rel_codigo, tfr_codigo, rfi_exibe_cse, rfi_exibe_csa, rfi_exibe_cor, rfi_exibe_org, rfi_exibe_ser, rfi_sequencia, rfi_parametro, rfi_exibe_sup)
VALUES ('contratos_beneficios', 'campo_status_contrato_beneficio', '0', '0', '0', '0', '0', 2, NULL, '1');

INSERT INTO tb_relatorio_filtro (rel_codigo, tfr_codigo, rfi_exibe_cse, rfi_exibe_csa, rfi_exibe_cor, rfi_exibe_org, rfi_exibe_ser, rfi_sequencia, rfi_parametro, rfi_exibe_sup)
VALUES ('contratos_beneficios', 'campo_formato_relatorio', '0', '0', '0', '0', '0', 3, NULL, '2');

INSERT INTO tb_relatorio_filtro (rel_codigo, tfr_codigo, rfi_exibe_cse, rfi_exibe_csa, rfi_exibe_cor, rfi_exibe_org, rfi_exibe_ser, rfi_sequencia, rfi_parametro, rfi_exibe_sup)
VALUES ('contratos_beneficios', 'campo_agendado', '0', '0', '0', '0', '0', 4, NULL, '1');

INSERT INTO tb_relatorio_filtro (rel_codigo, tfr_codigo, rfi_exibe_cse, rfi_exibe_csa, rfi_exibe_cor, rfi_exibe_org, rfi_exibe_ser, rfi_sequencia, rfi_parametro, rfi_exibe_sup)
VALUES ('contratos_beneficios', 'campo_data_execucao', '0', '0', '0', '0', '0', 5, NULL, '1');

INSERT INTO tb_relatorio_filtro (rel_codigo, tfr_codigo, rfi_exibe_cse, rfi_exibe_csa, rfi_exibe_cor, rfi_exibe_org, rfi_exibe_ser, rfi_sequencia, rfi_parametro, rfi_exibe_sup)
VALUES ('contratos_beneficios', 'campo_tipo_agendamento', '0', '0', '0', '0', '0', 6, NULL, '1');

INSERT INTO tb_relatorio_filtro (rel_codigo, tfr_codigo, rfi_exibe_cse, rfi_exibe_csa, rfi_exibe_cor, rfi_exibe_org, rfi_exibe_ser, rfi_sequencia, rfi_parametro, rfi_exibe_sup)
VALUES ('contratos_beneficios', 'campo_periodicidade', '0', '0', '0', '0', '0', 7, NULL, '1');
