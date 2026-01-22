-- DESENV-14268
-- 1) ATUALIZAR O GRUPO DE PARAMETROS NOS NOVOS PARAMETROS: 

update tb_tipo_param_sist_consignante set  gps_codigo = '1' where tpc_codigo in ('759');
update tb_tipo_param_sist_consignante set  gps_codigo = '2' where tpc_codigo in ('749','750','751','760','761','762');


-- 2) CONCEDER A  PERMISSAO DE VISUALIZAR E ALTERAR PARA USUÁRIOS SUP: 

update tb_tipo_param_sist_consignante set tpc_sup_consulta = 'S', tpc_sup_altera = 'S' where tpc_codigo in ('749','750','751','759','760','761','762');


-- 3) VINCULAR OS PARAMETROS AOS PERFIS: 

insert ignore into tb_perfil_param_sist_cse (tpc_codigo, per_codigo)
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-IMP_AVANCADO')
where tpc.tpc_codigo in ('749','750','751','759','760','761','762');

insert ignore into tb_perfil_param_sist_cse (tpc_codigo, per_codigo)
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-MANUTENCAO')
where tpc.tpc_codigo in ('749','750','751','759','760','761','762');

insert ignore into tb_perfil_param_sist_cse (tpc_codigo, per_codigo)
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-SEGURANCA_BASICO')
where tpc.tpc_codigo in ('759');

insert into tb_perfil_param_sist_cse (tpc_codigo, per_codigo)
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-PRODUCAO')
where tpc.tpc_codigo in ('749','750','751','760','761','762');
