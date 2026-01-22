-- DESENV-9232

-- Delete agendamento, relatórios filtros e novos inserts
DELETE FROM tb_agendamento WHERE REL_CODIGO = 'rel_inc_ben_recem_nascidos';

DELETE FROM tb_relatorio_filtro WHERE REL_CODIGO = 'rel_inc_ben_recem_nascidos' AND TFR_CODIGO = 'campo_agendado';
DELETE FROM tb_relatorio_filtro WHERE REL_CODIGO = 'rel_inc_ben_recem_nascidos' AND TFR_CODIGO = 'campo_beneficio';
DELETE FROM tb_relatorio_filtro WHERE REL_CODIGO = 'rel_inc_ben_recem_nascidos' AND TFR_CODIGO = 'campo_csa_natureza_beneficio';
DELETE FROM tb_relatorio_filtro WHERE REL_CODIGO = 'rel_inc_ben_recem_nascidos' AND TFR_CODIGO = 'campo_data_execucao';
DELETE FROM tb_relatorio_filtro WHERE REL_CODIGO = 'rel_inc_ben_recem_nascidos' AND TFR_CODIGO = 'campo_data_inclusao';
DELETE FROM tb_relatorio_filtro WHERE REL_CODIGO = 'rel_inc_ben_recem_nascidos' AND TFR_CODIGO = 'campo_formato_relatorio';
DELETE FROM tb_relatorio_filtro WHERE REL_CODIGO = 'rel_inc_ben_recem_nascidos' AND TFR_CODIGO = 'campo_periodicidade';
DELETE FROM tb_relatorio_filtro WHERE REL_CODIGO = 'rel_inc_ben_recem_nascidos' AND TFR_CODIGO = 'campo_tipo_agendamento';

DELETE FROM tb_relatorio WHERE REL_CODIGO = 'rel_inc_ben_recem_nascidos';

INSERT INTO tb_relatorio (REL_CODIGO, FUN_CODIGO, TAG_CODIGO, REL_TITULO, REL_ATIVO, REL_AGENDADO, REL_CLASSE_RELATORIO, REL_CLASSE_PROCESSO, REL_CLASSE_AGENDAMENTO, REL_TEMPLATE_JASPER, REL_TEMPLATE_DINAMICO, REL_TEMPLATE_SUBRELATORIO, REL_TEMPLATE_SQL, REL_QTD_DIAS_LIMPEZA, REL_CUSTOMIZADO, REL_AGRUPAMENTO)
VALUES ('rel_inc_ben_por_periodo', '411', NULL, 'Relatório de Inclusão de Beneficiários por Período', 1, 'N', 'com.zetra.report.reports.RelatorioIncBeneficiariosPorPeriodo', 'com.zetra.processamento.ProcessaRelatorioIncBeneficiariosPorPeriodo', NULL, 'IncBeneficiariosPorPeriodo.jasper', NULL, NULL, NULL, 30, 'N', NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('rel_inc_ben_por_periodo', 'campo_agendado', '0', '0', '0', '0', '0', 5, NULL, '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('rel_inc_ben_por_periodo', 'campo_beneficio', '0', '0', '0', '0', '0', 3, NULL, '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('rel_inc_ben_por_periodo', 'campo_csa_natureza_beneficio', '0', '0', '0', '0', '0', 2, NULL, '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('rel_inc_ben_por_periodo', 'campo_data_execucao', '0', '0', '0', '0', '0', 6, NULL, '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('rel_inc_ben_por_periodo', 'campo_data_inclusao', '0', '0', '0', '0', '0', 1, '90', '2');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('rel_inc_ben_por_periodo', 'campo_formato_relatorio', '0', '0', '0', '0', '0', 4, NULL, '2');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('rel_inc_ben_por_periodo', 'campo_periodicidade', '0', '0', '0', '0', '0', 8, NULL, '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('rel_inc_ben_por_periodo', 'campo_tipo_agendamento', '0', '0', '0', '0', '0', 7, NULL, '1');

-- item menu
UPDATE tb_item_menu SET MNU_CODIGO = '2', ITM_CODIGO_PAI = NULL, ITM_DESCRICAO = 'Inclusão de Beneficiários por Período', ITM_ATIVO = 1, ITM_SEQUENCIA = 58, ITM_SEPARADOR = 'N' WHERE ITM_CODIGO = '196';

-- Função
UPDATE tb_funcao SET grf_codigo = '4', FUN_DESCRICAO = 'Relatório de Inclusão de Beneficiários por Período', FUN_PERMITE_BLOQUEIO = 'N', FUN_EXIGE_TMO = 'N', FUN_EXIGE_SEGUNDA_SENHA_CSE = 'N', FUN_AUDITAVEL = 'N', FUN_RESTRITA_NCA = 'N', FUN_EXIGE_SEGUNDA_SENHA_SUP = 'N', FUN_EXIGE_SEGUNDA_SENHA_ORG = 'N', FUN_EXIGE_SEGUNDA_SENHA_CSA = 'N', FUN_EXIGE_SEGUNDA_SENHA_COR = 'N' WHERE fun_codigo = '411';

-- PAPEL FUNÇÃO
-- INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('7', '411');

-- Acessos recursos
UPDATE tb_acesso_recurso SET PAP_CODIGO = '7', FUN_CODIGO = '411', ACR_RECURSO = '/v3/executarRelatorio', ACR_PARAMETRO = 'tipoRelatorio', ACR_OPERACAO = 'rel_inc_ben_por_periodo', ACR_ATIVO = 1, ACR_BLOQUEIO = 'S', ACR_SESSAO = 'S', ITM_CODIGO = NULL, ACR_FIM_FLUXO = 'N', ACR_METODO_HTTP = '2' WHERE ACR_CODIGO = '14823';
UPDATE tb_acesso_recurso SET PAP_CODIGO = '7', FUN_CODIGO = '411', ACR_RECURSO = '/v3/listarRelatorio', ACR_PARAMETRO = 'tipo', ACR_OPERACAO = 'rel_inc_ben_por_periodo', ACR_ATIVO = 1, ACR_BLOQUEIO = 'S', ACR_SESSAO = 'S', ITM_CODIGO = '196', ACR_FIM_FLUXO = 'N', ACR_METODO_HTTP = '2' WHERE ACR_CODIGO = '14824';
UPDATE tb_acesso_recurso SET PAP_CODIGO = '7', FUN_CODIGO = '411', ACR_RECURSO = '/v3/agendarRelatorio', ACR_PARAMETRO = 'tipo', ACR_OPERACAO = 'rel_inc_ben_por_periodo', ACR_ATIVO = 1, ACR_BLOQUEIO = 'S', ACR_SESSAO = 'S', ITM_CODIGO = NULL, ACR_FIM_FLUXO = 'N', ACR_METODO_HTTP = '2' WHERE ACR_CODIGO = '14825';
UPDATE tb_acesso_recurso SET PAP_CODIGO = '7', FUN_CODIGO = '411', ACR_RECURSO = '/v3/downloadArquivo', ACR_PARAMETRO = 'subtipo', ACR_OPERACAO = 'rel_inc_ben_por_periodo', ACR_ATIVO = 1, ACR_BLOQUEIO = 'S', ACR_SESSAO = 'S', ITM_CODIGO = NULL, ACR_FIM_FLUXO = 'N', ACR_METODO_HTTP = '2' WHERE ACR_CODIGO = '14826';
UPDATE tb_acesso_recurso SET PAP_CODIGO = '7', FUN_CODIGO = '411', ACR_RECURSO = '/v3/excluirArquivo', ACR_PARAMETRO = 'subtipo', ACR_OPERACAO = 'rel_inc_ben_por_periodo', ACR_ATIVO = 1, ACR_BLOQUEIO = 'S', ACR_SESSAO = 'S', ITM_CODIGO = NULL, ACR_FIM_FLUXO = 'N', ACR_METODO_HTTP = '2' WHERE ACR_CODIGO = '14827';
UPDATE tb_acesso_recurso SET PAP_CODIGO = '7', FUN_CODIGO = '411', ACR_RECURSO = '/v3/cancelarAgendamentoRelatorio', ACR_PARAMETRO = 'tipo', ACR_OPERACAO = 'rel_inc_ben_por_periodo', ACR_ATIVO = 1, ACR_BLOQUEIO = 'S', ACR_SESSAO = 'S', ITM_CODIGO = NULL, ACR_FIM_FLUXO = 'N', ACR_METODO_HTTP = '2' WHERE ACR_CODIGO = '14828';
