-- DESENV-12394
-- RELATÓRIO DE CONF. CAD. MARGEM
-- INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('6', '187');
-- INSERT INTO tb_funcao_perfil_ser (SER_CODIGO, USU_CODIGO, FUN_CODIGO) SELECT SER_CODIGO, USU_CODIGO, '187' FROM tb_usuario_ser; 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15223', '6', '187', '/v3/listarRelatorio', 'tipo', 'conf_cad_margem', 1, 'S', 'S', '47', 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15224', '6', '187', '/v3/executarRelatorio', 'tipoRelatorio', 'conf_cad_margem', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15225', '6', '187', '/v3/downloadArquivo', 'subtipo', 'conf_cad_margem', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15226', '6', '187', '/v3/excluirArquivo', 'subtipo', 'conf_cad_margem', 1, 'S', 'S', NULL, 'N', '2');

UPDATE tb_relatorio_filtro SET RFI_EXIBE_SER = '2' WHERE REL_CODIGO = 'conf_cad_margem' AND TFR_CODIGO = 'campo_margens';
UPDATE tb_relatorio_filtro SET RFI_EXIBE_SER = '2' WHERE REL_CODIGO = 'conf_cad_margem' AND TFR_CODIGO = 'campo_formato_relatorio';


-- RELATÓRIO DE CONSIGNAÇÕES
-- INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('6', '39');
-- INSERT INTO tb_funcao_perfil_ser (SER_CODIGO, USU_CODIGO, FUN_CODIGO) SELECT SER_CODIGO, USU_CODIGO, '39' FROM tb_usuario_ser; 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15227', '6', '39', '/v3/listarRelatorio', 'tipo', 'consignacoes', 1, 'S', 'S', '58', 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15228', '6', '39', '/v3/executarRelatorio', 'tipoRelatorio', 'consignacoes', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15229', '6', '39', '/v3/downloadArquivo', 'subtipo', 'consignacoes', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15230', '6', '39', '/v3/excluirArquivo', 'subtipo', 'consignacoes', 1, 'S', 'S', NULL, 'N', '2');

UPDATE tb_relatorio_filtro SET RFI_EXIBE_SER = '2' WHERE REL_CODIGO = 'consignacoes' AND TFR_CODIGO = 'campo_periodo_data_inclusao';
UPDATE tb_relatorio_filtro SET RFI_EXIBE_SER = '2' WHERE REL_CODIGO = 'consignacoes' AND TFR_CODIGO = 'campo_formato_relatorio';
UPDATE tb_relatorio_filtro SET RFI_EXIBE_SER = '1' WHERE REL_CODIGO = 'consignacoes' AND TFR_CODIGO = 'campo_status_contrato';
UPDATE tb_relatorio_filtro SET RFI_EXIBE_SER = '1' WHERE REL_CODIGO = 'consignacoes' AND TFR_CODIGO = 'campo_csa';
UPDATE tb_relatorio_filtro SET RFI_EXIBE_SER = '1' WHERE REL_CODIGO = 'consignacoes' AND TFR_CODIGO = 'campo_svc';
