-- DESENV-21046
-- 1) ATUALIZAR O GRUPO DE PARAMETROS NOS NOVOS PARAMETROS:
update tb_tipo_param_sist_consignante set gps_codigo = '2' where tpc_codigo in ('940','945');
update tb_tipo_param_sist_consignante set gps_codigo = '3' where tpc_codigo in ('939','941','942','943','944','946');

-- 2) CONCEDER A  PERMISSAO DE VISUALIZAR E ALTERAR PARA USUÁRIOS SUP:
update tb_tipo_param_sist_consignante set tpc_sup_consulta = 'S', tpc_sup_altera ='S' where tpc_codigo in ('939','940','941','942','943','944','945','946');

-- 3) VINCULAR OS PARAMETROS AOS PERFIS:
insert into tb_perfil_param_sist_cse
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-IMP_AVANCADO')
where tpc.tpc_codigo in ('939', '940', '941', '942', '943', '944', '945', '946')
and not exists (select 1 from tb_perfil_param_sist_cse pcc where tpc.tpc_codigo = pcc.tpc_codigo and per.per_codigo = pcc.per_codigo);

insert into tb_perfil_param_sist_cse
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-MANUTENCAO')
where tpc.tpc_codigo in ('939', '940', '941', '942', '943', '944', '945', '946')
and not exists (select 1 from tb_perfil_param_sist_cse pcc where tpc.tpc_codigo = pcc.tpc_codigo and per.per_codigo = pcc.per_codigo);

insert into tb_perfil_param_sist_cse 
select tpc.tpc_codigo, per.per_codigo 
from tb_tipo_param_sist_consignante tpc 
inner join tb_perfil per on (per.per_codigo = 'ZETRA-PRODUCAO') 
where tpc.tpc_codigo in ('940', '945')
and not exists (select 1 from tb_perfil_param_sist_cse pcc where tpc.tpc_codigo = pcc.tpc_codigo and per.per_codigo = pcc.per_codigo);

