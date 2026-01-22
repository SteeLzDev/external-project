-- DESENV-13932
INSERT INTO tb_agendamento (AGD_CODIGO, TAG_CODIGO, SAG_CODIGO, USU_CODIGO, AGD_DESCRICAO, AGD_JAVA_CLASS_NAME, AGD_DATA_CADASTRO, AGD_DATA_PREVISTA, REL_CODIGO) 
VALUES ('42', '1', '1', '1', 'Arquivo de movimento de rescisão', 'com.zetra.timer.econsig.job.ModuloRescisaoJob', NOW(), NOW(), NULL);

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('780', 'Nome do Arquivo de configuração da entrada na exportação de contratos de verba rescisória', 'ALFA', 'rescisao_entrada.xml', 'N', 'N', 'N', 'N', '2');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('781', 'Nome do Arquivo de configuração da saída na exportação de contratos de verba rescisória', 'ALFA', 'rescisao_saida.xml', 'N', 'N', 'N', 'N', '2');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('782', 'Nome do Arquivo de configuração do tradutor na exportação de contratos de verba rescisória', 'ALFA', 'rescisao_tradutor.xml', 'N', 'N', 'N', 'N', '2');


INSERT INTO tb_item_menu (ITM_CODIGO, MNU_CODIGO, TEX_CHAVE, ITM_CODIGO_PAI, ITM_DESCRICAO, ITM_ATIVO, ITM_SEQUENCIA, ITM_SEPARADOR, ITM_CENTRALIZADOR, ITM_IMAGEM)
VALUES ('243', '7', NULL, NULL, 'Download arquivos de rescisão', 1, 3, 'N', 'S', NULL);


INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15983', '1', '485', '/v3/listarArquivosDownloadRescisao', 'acao', 'iniciar', 1, 'S', 'S', '243', 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15984', '3', '485', '/v3/listarArquivosDownloadRescisao', 'acao', 'iniciar', 1, 'S', 'S', '243', 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15985', '7', '485', '/v3/listarArquivosDownloadRescisao', 'acao', 'iniciar', 1, 'S', 'S', '243', 'N', '2');


INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15986', '1', '485', '/v3/downloadArquivo', 'tipo', 'rescisao', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15987', '3', '485', '/v3/downloadArquivo', 'tipo', 'rescisao', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15988', '7', '485', '/v3/downloadArquivo', 'tipo', 'rescisao', 1, 'S', 'S', NULL, 'N', '2');
