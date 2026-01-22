-- DESENV-10028
INSERT INTO tb_tipo_param_consignataria (TPA_CODIGO, TPA_DESCRICAO, TPA_DOMINIO, TPA_CSE_ALTERA, TPA_SUP_ALTERA, TPA_CSA_ALTERA)
VALUES ('55', 'Valor mínimo para envio no boleto de faturamento benefício', 'INT', 'N', 'N', 'N');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, TPC_VLR_DEFAULT)
VALUES ('621', 'Nome do Arquivo de configuração da tradução na geração de arquivo de faturamento benefício', 'SN', 'N', 'N', 'N', 'N', NULL); 

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR) 
VALUES ('621', '1', 'exp_arq_faturamento_beneficio_tradutor.xml');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, TPC_VLR_DEFAULT)
VALUES ('622', 'Nome do Arquivo de configuração da saída na geração de arquivo de faturamento benefício', 'SN', 'N', 'N', 'N', 'N', NULL); 

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR) 
VALUES ('622', '1', 'exp_arq_faturamento_beneficio_saida.xml');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, TPC_VLR_DEFAULT)
VALUES ('623', 'Nome do Arquivo de configuração tradutor na geração de arquivo de resíduos do faturamento benefício', 'SN', 'N', 'N', 'N', 'N', NULL); 

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR) 
VALUES ('623', '1', 'exp_arq_residuo_faturamento_beneficio_tradutor.xml');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, TPC_VLR_DEFAULT)
VALUES ('624', 'Nome do Arquivo de configuração da saída na geração de arquivo de resíduos do faturamento benefício', 'SN', 'N', 'N', 'N', 'N', NULL); 

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR) 
VALUES ('624', '1', 'exp_arq_residuo_faturamento_beneficio_saida.xml');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15041', '7', '439', '/v3/downloadArquivo', 'tipo', 'fatura', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15042', '7', '439', '/v3/excluirArquivo', 'tipo', 'fatura', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15043', '7', '439', '/v3/consultarFaturamentos', 'acao', 'gerarFaturamento', 1, 'S', 'S', NULL, 'N', '2');
