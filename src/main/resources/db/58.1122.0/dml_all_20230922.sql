-- DESENV-20220
INSERT INTO tb_funcao (FUN_CODIGO, GRF_CODIGO, FUN_DESCRICAO, FUN_PERMITE_BLOQUEIO, FUN_EXIGE_TMO, FUN_AUDITAVEL, FUN_RESTRITA_NCA, FUN_EXIGE_SEGUNDA_SENHA_CSE, FUN_EXIGE_SEGUNDA_SENHA_SUP, FUN_EXIGE_SEGUNDA_SENHA_ORG, FUN_EXIGE_SEGUNDA_SENHA_CSA, FUN_EXIGE_SEGUNDA_SENHA_COR)
VALUES ('550', '4', 'Relatório de Parcelas Processadas e Futuras', 'N', 'N', 'S', 'N', 'N', 'N', 'N', 'N', 'N');

-- INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('2', '550');
-- INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('4', '550');

INSERT INTO tb_item_menu (ITM_CODIGO, MNU_CODIGO, ITM_CODIGO_PAI, ITM_DESCRICAO, ITM_ATIVO, ITM_SEQUENCIA)
VALUES ('283', '2', NULL, 'Parcelas Processadas e Futuras', 1, 999);


INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16821', '1', '550', '/v3/listarRelatorio', 'tipo', 'parcelas_processadas_futuras', 1, 'S', 'S', '283', 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16822', '1', '550', '/v3/executarRelatorio', 'tipoRelatorio', 'parcelas_processadas_futuras', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16823', '1', '550', '/v3/excluirArquivo', 'subtipo', 'parcelas_processadas_futuras', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16824', '1', '550', '/v3/downloadArquivo', 'subtipo', 'parcelas_processadas_futuras', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16825', '1', '550', '/v3/agendarRelatorio', 'tipo', 'parcelas_processadas_futuras', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16826', '1', '550', '/v3/cancelarAgendamentoRelatorio', 'tipo', 'parcelas_processadas_futuras', 1, 'S', 'S', NULL, 'N', '2');


INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16827', '2', '550', '/v3/listarRelatorio', 'tipo', 'parcelas_processadas_futuras', 1, 'S', 'S', '283', 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16828', '2', '550', '/v3/executarRelatorio', 'tipoRelatorio', 'parcelas_processadas_futuras', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16829', '2', '550', '/v3/excluirArquivo', 'subtipo', 'parcelas_processadas_futuras', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16830', '2', '550', '/v3/downloadArquivo', 'subtipo', 'parcelas_processadas_futuras', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16831', '2', '550', '/v3/agendarRelatorio', 'tipo', 'parcelas_processadas_futuras', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16832', '2', '550', '/v3/cancelarAgendamentoRelatorio', 'tipo', 'parcelas_processadas_futuras', 1, 'S', 'S', NULL, 'N', '2');


INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16833', '7', '550', '/v3/listarRelatorio', 'tipo', 'parcelas_processadas_futuras', 1, 'S', 'S', '283', 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16834', '7', '550', '/v3/executarRelatorio', 'tipoRelatorio', 'parcelas_processadas_futuras', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16835', '7', '550', '/v3/excluirArquivo', 'subtipo', 'parcelas_processadas_futuras', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16836', '7', '550', '/v3/downloadArquivo', 'subtipo', 'parcelas_processadas_futuras', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16837', '7', '550', '/v3/agendarRelatorio', 'tipo', 'parcelas_processadas_futuras', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16838', '7', '550', '/v3/cancelarAgendamentoRelatorio', 'tipo', 'parcelas_processadas_futuras', 1, 'S', 'S', NULL, 'N', '2');


INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16839', '4', '550', '/v3/listarRelatorio', 'tipo', 'parcelas_processadas_futuras', 1, 'S', 'S', '283', 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16840', '4', '550', '/v3/executarRelatorio', 'tipoRelatorio', 'parcelas_processadas_futuras', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16841', '4', '550', '/v3/excluirArquivo', 'subtipo', 'parcelas_processadas_futuras', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16842', '4', '550', '/v3/downloadArquivo', 'subtipo', 'parcelas_processadas_futuras', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16843', '4', '550', '/v3/agendarRelatorio', 'tipo', 'parcelas_processadas_futuras', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16844', '4', '550', '/v3/cancelarAgendamentoRelatorio', 'tipo', 'parcelas_processadas_futuras', 1, 'S', 'S', NULL, 'N', '2');


INSERT INTO tb_relatorio (REL_CODIGO, FUN_CODIGO, TAG_CODIGO, REL_TITULO, REL_ATIVO, REL_AGENDADO, REL_CLASSE_RELATORIO, REL_CLASSE_PROCESSO, REL_CLASSE_AGENDAMENTO, REL_TEMPLATE_JASPER, REL_TEMPLATE_DINAMICO, REL_TEMPLATE_SUBRELATORIO, REL_TEMPLATE_SQL, REL_QTD_DIAS_LIMPEZA, REL_CUSTOMIZADO, REL_AGRUPAMENTO)
VALUES ('parcelas_processadas_futuras', '550', NULL, 'Relatório de Parcelas Processadas e Futuras', 1, 'N', 'com.zetra.econsig.report.reports.RelatorioParcelasProcessadasFuturas', 'com.zetra.econsig.job.process.ProcessaRelatorioParcelasProcessadasFuturas', NULL, 'ParcelasProcessadasFuturas.jasper', NULL, NULL, NULL, 30, 'N', NULL);

INSERT INTO tb_tipo_filtro_relatorio (TFR_CODIGO, TFR_DESCRICAO, TFR_RECURSO, TFR_EXIBE_EDICAO)
VALUES ('campo_periodo_inicio_fim', 'Período', '/relatorios/campos_relatorio/campo_periodo_ini_fim.jsp', 'N');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('parcelas_processadas_futuras', 'campo_periodo_inicio_fim', '2', '2', '0', '2', '2', '0', 1, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('parcelas_processadas_futuras', 'campo_matricula', '1', '1', '0', '1', '1', '0', 2, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('parcelas_processadas_futuras', 'campo_cpf', '1', '1', '0', '1', '1', '0', 3, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('parcelas_processadas_futuras', 'campo_csa', '1', '1', '0', '1', '0', '0', 4, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('parcelas_processadas_futuras', 'campo_cor', '0', '0', '0', '1', '1', '0', 5, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('parcelas_processadas_futuras', 'campo_cor_multiplo', '1', '1', '0', '0', '0', '0', 6, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('parcelas_processadas_futuras', 'campo_est', '1', '1', '0', '1', '1', '0', 7, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('parcelas_processadas_futuras', 'campo_org', '1', '1', '0', '1', '1', '0', 8, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('parcelas_processadas_futuras', 'campo_sub_orgao', '1', '1', '0', '1', '0', '0', 9, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('parcelas_processadas_futuras', 'campo_unidade', '1', '1', '0', '1', '0', '0', 10, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('parcelas_processadas_futuras', 'campo_natureza_svc', '1', '1', '0', '0', '0', '0', 11, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('parcelas_processadas_futuras', 'campo_svc', '1', '1', '0', '1', '1', '0', 12, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('parcelas_processadas_futuras', 'campo_status_servidor', '1', '1', '0', '1', '1', '0', 13, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('parcelas_processadas_futuras', 'campo_origem_contrato', '1', '1', '0', '1', '1', '0', 14, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('parcelas_processadas_futuras', 'campo_termino_contrato', '1', '1', '0', '1', '1', '0', 15, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('parcelas_processadas_futuras', 'campo_judicial', '1', '1', '0', '0', '0', '0', 16, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('parcelas_processadas_futuras', 'campo_agendado', '1', '1', '0', '0', '0', '0', 17, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('parcelas_processadas_futuras', 'campo_data_execucao', '1', '1', '0', '0', '0', '0', 18, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('parcelas_processadas_futuras', 'campo_tipo_agendamento', '1', '1', '0', '0', '0', '0', 19, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('parcelas_processadas_futuras', 'campo_periodicidade', '1', '1', '0', '0', '0', '0', 20, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('parcelas_processadas_futuras', 'campo_envio_email', '1', '1', '0', '0', '0', '0', 21, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('parcelas_processadas_futuras', 'campo_formato_relatorio', '2', '2', '0', '2', '2', '0', 22, NULL);

