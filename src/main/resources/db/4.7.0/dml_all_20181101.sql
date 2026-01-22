-- DESENV-9665
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, TPC_VLR_DEFAULT)
VALUES ('614', 'Nome do Arquivo de configuração da entrada na importação do arquivo operadora beneficio.', 'ALFA', 'N', 'N', 'N', 'S', 'imp_retorno_operadora_beneficio_entrada.xml');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, TPC_VLR_DEFAULT)
VALUES ('615', 'Nome do Arquivo de configuração da tradutor na importação do arquivo operadora beneficio.', 'ALFA', 'N', 'N', 'N', 'S', 'imp_retorno_operadora_beneficio_tradutor.xml');

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('614', '1', 'imp_retorno_operadora_beneficio_entrada.xml');

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('615', '1', 'imp_retorno_operadora_beneficio_tradutor.xml');
