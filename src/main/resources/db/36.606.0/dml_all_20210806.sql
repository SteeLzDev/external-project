-- DESENV-16380
UPDATE tb_tipo_param_sist_consignante SET TPC_DOMINIO = CONCAT('ESCOLHA[S=Opcional', CAST(0x3b AS CHAR), 'N=Não', CAST(0x3b AS CHAR), 'O=Obrigatório]') WHERE TPC_CODIGO = '232';
