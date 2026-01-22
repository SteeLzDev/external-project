-- DESENV-10326
update tb_tipo_param_consignataria set tpa_dominio = concat('ESCOLHA[C=Data Atual', 0x3b, 'M=Mês Anterior', 0x3b, 'P=Período', 0x3b, 'A=Período Anterior]') where tpa_codigo = '52';
