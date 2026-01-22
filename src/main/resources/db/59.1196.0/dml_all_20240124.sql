-- DESENV-21033
INSERT INTO tb_tipo_param_consignataria (TPA_CODIGO, TPA_DESCRICAO, TPA_DOMINIO, TPA_CSE_ALTERA, TPA_SUP_ALTERA, TPA_CSA_ALTERA)
VALUES ('90', 'Pesquisa a matrícula exata do servidor nas operações SOAP.', 'SN', 'N', 'N', 'N');

-- REMOCAO PARAMETRO DE SISTEMA CRIADO ANTERIORMENTE
DELETE FROM tb_nivel_seguranca_param_sist WHERE TPC_CODIGO = '939';
DELETE FROM tb_ocorrencia_param_sist_cse WHERE TPC_CODIGO = '939';
DELETE FROM tb_perfil_param_sist_cse WHERE TPC_CODIGO = '939';
DELETE FROM tb_param_sist_consignante WHERE TPC_CODIGO = '939';
DELETE FROM tb_tipo_param_sist_consignante WHERE TPC_CODIGO = '939';

