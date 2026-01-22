-- DESENV-9432

delete from tb_texto_sistema where tex_chave in ('recaptcha.chave.privada', 'recaptcha.chave.publica', 'rotulo.nome.arquivo.relatorio.inc.beneficiarios.recem.nascidos', 'rotulo.relatorio.inclusao.beneficiarios.recem.nascidos.beneficio', 'rotulo.relatorio.inclusao.beneficiarios.recem.nascidos.consginataria', 'rotulo.relatorio.inclusao.beneficiarios.recem.nascidos.data.inclusao', 'rotulo.relatorio.inclusao.beneficiarios.recem.nascidos.data.nascimento', 'rotulo.relatorio.inclusao.beneficiarios.recem.nascidos.matricula', 'rotulo.relatorio.inclusao.beneficiarios.recem.nascidos.nome.beneficiario', 'rotulo.relatorio.inclusao.beneficiarios.recem.nascidos.nome.titular', 'rotulo.relatorio.inclusao.beneficiarios.recem.nascidos.numero.contrato', 'rotulo.validacao.modulo.beneficio.regra.verifica.contratos.mensalida.de.duplicados.por.cpf', 'rotulo.validacao.modulo.beneficio.regra.verifica.subsidio.para.mensalidad.e.eodontologico', 'tb_beneficiario.bfc_grau_parentesco');

update tb_texto_sistema set tex_texto = '${rotulo.posto.singular} do ${rotulo.servidor.singular} alterado de \'<DE>\' para \'<PARA>\'.' where tex_chave = 'mensagem.cadMargem.posto.servidor.alterado.de.para';
update tb_texto_sistema set tex_texto = '${rotulo.relatorio.inclusao.beneficiarios.periodo.beneficio}: {0}' where tex_chave = 'rotulo.beneficio.arg0';
update tb_texto_sistema set tex_texto = '${rotulo.relatorio.inclusao.beneficiarios.periodo.consginataria}: {0}' where tex_chave = 'rotulo.operadora.arg0';
update tb_texto_sistema set tex_texto = 'Cpf do Beneficiário' where tex_chave = 'tb_beneficiario.bfc_cpf';
update tb_texto_sistema set tex_texto = 'Nome da Mãe do Beneficiário' where tex_chave = 'tb_beneficiario.bfc_nome_mae';
update tb_texto_sistema set tex_texto = 'Filtrar por' where tex_chave = 'rotulo.acao.filtrar.por';
update tb_texto_sistema set tex_texto = 'O orgão deve ser inforrmado.' where tex_chave = 'mensagem.beneficio.orgao.informar';
