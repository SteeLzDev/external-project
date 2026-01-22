-- DESENV-11427

-- 1) ATUALIZAR O GRUPO DE PARAMETROS NOS NOVOS PARAMETROS:

update tb_tipo_param_sist_consignante set gps_codigo = '1' where tpc_codigo in ('632');

update tb_tipo_param_sist_consignante set gps_codigo = '2' where tpc_codigo in ('628','631');

update tb_tipo_param_sist_consignante set gps_codigo = '3' where tpc_codigo in ('629','630','633','634','635','636','637');

-- 2) CONCEDER A PERMISSAO DE VISUALIZAR E ALTERAR PARA USUÁRIOS SUP:

update tb_tipo_param_sist_consignante set tpc_sup_consulta = 'S', tpc_sup_altera = 'S' where tpc_codigo in ('628','629','630','631','632','633','634','635','636','637');

-- 3) VINCULAR OS PARAMETROS AOS PERFIS:

insert ignore into tb_perfil_param_sist_cse
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-SEGURANCA_BASICO')
where tpc.tpc_codigo in ('632');

insert ignore into tb_perfil_param_sist_cse
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-IMP_AVANCADO')
where tpc.tpc_codigo in ('628','629','630','631','632','633','634','635','636','637');

insert ignore into tb_perfil_param_sist_cse
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-MANUTENCAO')
where tpc.tpc_codigo in ('628','629','630','631','632','633','634','635','636','637');

insert ignore into tb_perfil_param_sist_cse
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-PRODUCAO')
where tpc.tpc_codigo in ('628');

insert ignore into tb_perfil_param_sist_cse
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-PRODUCAO_AVANCADO')
where tpc.tpc_codigo in ('628');

