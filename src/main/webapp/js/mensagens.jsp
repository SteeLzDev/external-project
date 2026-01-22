<%@page import="com.zetra.econsig.helper.texto.LocaleHelper"%>
<%@page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page contentType="text/javascript; charset=iso-8859-1" language="java" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
var map = new Object();

map['rotulo.campo.selecione'] = '<hl:message key="rotulo.campo.selecione"/>';

map['rotulo.nivel.senha.muito.baixo'] = '<hl:message key="rotulo.nivel.senha.muito.baixo"/>';
map['rotulo.nivel.senha.baixo'] = '<hl:message key="rotulo.nivel.senha.baixo"/>';
map['rotulo.nivel.senha.medio'] = '<hl:message key="rotulo.nivel.senha.medio"/>';
map['rotulo.nivel.senha.alto'] = '<hl:message key="rotulo.nivel.senha.alto"/>';
map['rotulo.nivel.senha.muito.alto'] = '<hl:message key="rotulo.nivel.senha.muito.alto"/>';

map['mensagem.informe.campo'] = '<hl:message key="mensagem.informe.campo"/>';
map['mensagem.informe.ambos.campos.data'] = '<hl:message key="mensagem.informe.ambos.campos.data"/>';
map['mensagem.informe.consignataria'] = '<hl:message key="mensagem.informe.consignataria"/>';
map['mensagem.informe.servico'] = '<hl:message key="mensagem.informe.servico"/>';
map['mensagem.informe.plano'] = '<hl:message key="mensagem.informe.plano"/>';
map['mensagem.informe.estabelecimento'] = '<hl:message key="mensagem.informe.estabelecimento"/>';
map['mensagem.informe.orgao'] = '<hl:message key="mensagem.informe.orgao"/>';
map['mensagem.informe.matricula'] = '<hl:message key="mensagem.informe.matricula"/>';
map['mensagem.informe.cpf'] = '<hl:message key="mensagem.informe.cpf"/>';
map['mensagem.informe.ade.numero'] = '<hl:message key="mensagem.informe.ade.numero"/>';
map['mensagem.informe.ade.identificador'] = '<hl:message key="mensagem.informe.ade.identificador"/>';
map['mensagem.informe.ade.valor'] = '<hl:message key="mensagem.informe.ade.valor"/>';
map['mensagem.informe.ade.prazo'] = '<hl:message key="mensagem.informe.ade.prazo"/>';
map['mensagem.informe.ade.carencia'] = '<hl:message key="mensagem.informe.ade.carencia"/>';
map['mensagem.informe.cse.identificador'] = '<hl:message key="mensagem.informe.cse.identificador"/>';
map['mensagem.informe.cse.nome'] = '<hl:message key="mensagem.informe.cse.nome"/>';
map['mensagem.informe.est.identificador'] = '<hl:message key="mensagem.informe.est.identificador"/>';
map['mensagem.informe.est.nome'] = '<hl:message key="mensagem.informe.est.nome"/>';
map['mensagem.informe.est.cnpj'] = '<hl:message key="mensagem.informe.est.cnpj"/>';
map['mensagem.informe.org.identificador'] = '<hl:message key="mensagem.informe.org.identificador"/>';
map['mensagem.informe.org.nome'] = '<hl:message key="mensagem.informe.org.nome"/>';
map['mensagem.informe.org.estabelecimento'] = '<hl:message key="mensagem.informe.org.estabelecimento"/>';
map['mensagem.informe.csa.identificador'] = '<hl:message key="mensagem.informe.csa.identificador"/>';
map['mensagem.informe.csa.nome'] = '<hl:message key="mensagem.informe.csa.nome"/>';
map['mensagem.informe.csa.natureza'] = '<hl:message key="mensagem.informe.csa.natureza"/>';
map['mensagem.informe.cor.identificador'] = '<hl:message key="mensagem.informe.cor.identificador"/>';
map['mensagem.informe.cor.nome'] = '<hl:message key="mensagem.informe.cor.nome"/>';
map['mensagem.informe.svc.identificador'] = '<hl:message key="mensagem.informe.svc.identificador"/>';
map['mensagem.informe.svc.descricao'] = '<hl:message key="mensagem.informe.svc.descricao"/>';
map['mensagem.informe.svc.natureza'] = '<hl:message key="mensagem.informe.svc.natureza"/>';
map['mensagem.informe.ccr.descricao'] = '<hl:message key="mensagem.informe.ccr.descricao"/>';
map['mensagem.informe.ccr.valor'] = '<hl:message key="mensagem.informe.ccr.valor"/>';
map['mensagem.informe.ccr.mes'] = '<hl:message key="mensagem.informe.ccr.mes"/>';
map['mensagem.informe.ccr.ano'] = '<hl:message key="mensagem.informe.ccr.ano"/>';
map['mensagem.informe.rse.status'] = '<hl:message key="mensagem.informe.rse.status"/>';
map['mensagem.informe.rse.margem'] = '<hl:message key="mensagem.informe.rse.margem"/>';
map['mensagem.informe.ech.identificador'] = '<hl:message key="mensagem.informe.ech.identificador"/>';
map['mensagem.informe.ech.descricao'] = '<hl:message key="mensagem.informe.ech.descricao"/>';
map['mensagem.informe.ech.qtd.unidade'] = '<hl:message key="mensagem.informe.ech.qtd.unidade"/>';
map['mensagem.informe.men.titulo'] = '<hl:message key="mensagem.informe.men.titulo"/>';
map['mensagem.informe.funcao.mensagem.apenas.csa'] = '<hl:message key="mensagem.informe.funcao.mensagem.apenas.csa"/>';
map['mensagem.informe.faq.titulo1'] = '<hl:message key="mensagem.informe.faq.titulo1"/>';
map['mensagem.informe.faq.titulo2'] = '<hl:message key="mensagem.informe.faq.titulo2"/>';
map['mensagem.informe.faq.conteudo'] = '<hl:message key="mensagem.informe.faq.conteudo"/>';
map['mensagem.informe.data.dia'] = '<hl:message key="mensagem.informe.data.dia"/>';
map['mensagem.informe.data.mes'] = '<hl:message key="mensagem.informe.data.mes"/>';
map['mensagem.informe.data.ano'] = '<hl:message key="mensagem.informe.data.ano"/>';
map['mensagem.informe.login.usuario'] = '<hl:message key="mensagem.informe.login.usuario"/>';
map['mensagem.informe.login.senha'] = '<hl:message key="mensagem.informe.login.senha"/>';
map['mensagem.informe.login.captcha'] = '<hl:message key="mensagem.informe.login.captcha"/>';
map['mensagem.informe.arquivo.upload.ext'] = '<hl:message key="mensagem.informe.arquivo.upload.ext"/>';
map['mensagem.informe.token.leitor'] = '<hl:message key="mensagem.informe.token.leitor"/>';
map['mensagem.informe.texto.sistema.texto'] = '<hl:message key="mensagem.informe.texto.sistema.texto"/>';

map['mensagem.erro.valor.parcela.maior.margem'] = '<hl:message key="mensagem.erro.valor.parcela.maior.margem"/>';
map['mensagem.erro.valor.parcela.incorreto'] = '<hl:message key="mensagem.erro.valor.parcela.incorreto"/>';
map['mensagem.erro.valor.parcela.negativo'] = '<hl:message key="mensagem.erro.valor.parcela.negativo"/>';
map['mensagem.erro.prazo.incorreto'] = '<hl:message key="mensagem.erro.prazo.incorreto"/>';
map['mensagem.erro.prazo.negativo'] = '<hl:message key="mensagem.erro.prazo.negativo"/>';
map['mensagem.erro.prazo.maior.svc'] = '<hl:message key="mensagem.erro.prazo.maior.svc"/>';
map['mensagem.erro.prazo.maior.ser'] = '<hl:message key="mensagem.erro.prazo.maior.ser"/>';
map['mensagem.erro.valor.margem.incorreto'] = '<hl:message key="mensagem.erro.valor.margem.incorreto"/>';
map['mensagem.erro.valor.margem.maior.atual'] = '<hl:message key="mensagem.erro.valor.margem.maior.atual"/>';
map['mensagem.erro.carencia.incorreta'] = '<hl:message key="mensagem.erro.carencia.incorreta"/>';
map['mensagem.erro.email.csa.invalido'] = '<hl:message key="mensagem.erro.email.csa.invalido"/>';
map['mensagem.erro.email.cor.invalido'] = '<hl:message key="mensagem.erro.email.cor.invalido"/>';
map['mensagem.erro.lista.valor.existe'] = '<hl:message key="mensagem.erro.lista.valor.existe"/>';
map['mensagem.erro.lista.endereco.existe'] = '<hl:message key="mensagem.erro.lista.endereco.existe"/>';
map['mensagem.erro.lista.qtd.max.endereco'] = '<hl:message key="mensagem.erro.lista.qtd.max.endereco"/>';
map['mensagem.erro.cnpj.invalido'] = '<hl:message key="mensagem.erro.cnpj.invalido"/>';
map['mensagem.erro.cpf.invalido'] = '<hl:message key="mensagem.erro.cpf.invalido"/>';
map['mensagem.erro.data.invalida.mes.ano'] = '<hl:message key="mensagem.erro.data.invalida.mes.ano"/>';
map['mensagem.erro.ano.invalido'] = '<hl:message key="mensagem.erro.ano.invalido"/>';
map['mensagem.erro.mes.invalido'] = '<hl:message key="mensagem.erro.mes.invalido"/>';
map['mensagem.erro.quinzena.invalida'] = '<hl:message key="mensagem.erro.quinzena.invalida"/>';
map['mensagem.erro.quatorzena.invalida'] = '<hl:message key="mensagem.erro.quatorzena.invalida"/>';
map['mensagem.erro.semana.invalida'] = '<hl:message key="mensagem.erro.semana.invalida"/>';
map['mensagem.erro.dia.invalido'] = '<hl:message key="mensagem.erro.dia.invalido"/>';
map['mensagem.erro.data.invalida'] = '<hl:message key="mensagem.erro.data.invalida"/>';
map['mensagem.erro.horario.invalido'] = '<hl:message key="mensagem.erro.horario.invalido"/>';
map['mensagem.erro.hora.invalida'] = '<hl:message key="mensagem.erro.hora.invalida"/>';
map['mensagem.erro.minuto.invalido'] = '<hl:message key="mensagem.erro.minuto.invalido"/>';
map['mensagem.erro.segundo.invalido'] = '<hl:message key="mensagem.erro.segundo.invalido"/>';
map['mensagem.erro.verifique.campos'] = '<hl:message key="mensagem.erro.verifique.campos"/>';
map['mensagem.erro.ip.interno'] = '<hl:message key="mensagem.erro.ip.interno"/>';
map['mensagem.erro.ip.invalido'] = '<hl:message key="mensagem.erro.ip.invalido"/>';
map['mensagem.erro.browser.sem.audio'] = '<hl:message key="mensagem.erro.browser.sem.audio"/>';
map['mensagem.erro.data.final.menor.inicial'] = '<hl:message key="mensagem.erro.data.final.menor.inicial"/>';
map['mensagem.erro.dias.periodo.invalido'] = '<hl:message key="mensagem.erro.dias.periodo.invalido"/>';
map['mensagem.erro.banco.invalido'] = '<hl:message key="mensagem.erro.banco.invalido"/>';
map['mensagem.erro.upload.arquivo'] = '<hl:message key="mensagem.erro.upload.arquivo"/>';
map['mensagem.erro.upload.extensao.invalida'] = '<hl:message key="mensagem.erro.upload.extensao.invalida"/>';
map['mensagem.erro.upload.tamanho.invalido'] = '<hl:message key="mensagem.erro.upload.tamanho.invalido"/>';
map['mensagem.erro.recalcula.margem.servidores.valor'] = "<hl:message key="mensagem.erro.recalcula.margem.servidores.valor"/>";
map['mensagem.erro.filtros.origem.termino.contrato.nao.podem.ser.aplicados.simultaneamente'] = '<hl:message key="mensagem.erro.filtros.origem.termino.contrato.nao.podem.ser.aplicados.simultaneamente"/>';
map['mensagem.erro.texto.sistema.edicao.bloqueada'] = '<hl:message key="mensagem.erro.texto.sistema.edicao.bloqueada"/>';

map['mensagem.confirmacao.bloqueio.entidade'] = '<hl:message key="mensagem.confirmacao.bloqueio.entidade"/>';
map['mensagem.confirmacao.desbloqueio.entidade'] = '<hl:message key="mensagem.confirmacao.desbloqueio.entidade"/>';
map['mensagem.confirmacao.exclusao.entidade'] = '<hl:message key="mensagem.confirmacao.exclusao.entidade"/>';
map['mensagem.confirmacao.exclusao.usuario'] = '<hl:message key="mensagem.confirmacao.exclusao.usuario"/>';
map['mensagem.confirmacao.bloqueio.usuario'] = '<hl:message key="mensagem.confirmacao.bloqueio.usuario"/>';
map['mensagem.confirmacao.desbloqueio.usuario'] = '<hl:message key="mensagem.confirmacao.desbloqueio.usuario"/>';
map['mensagem.confirmacao.reinicializar.senha.usuario'] = '<hl:message key="mensagem.confirmacao.reinicializar.senha.usuario"/>';

map['rotulo.markdown.alinhamento.esq'] = '<hl:message key="rotulo.markdown.alinhamento.esq"/>';
map['rotulo.markdown.alinhamento.dir'] = '<hl:message key="rotulo.markdown.alinhamento.dir"/>';
map['rotulo.markdown.alinhamento.centro'] = '<hl:message key="rotulo.markdown.alinhamento.centro"/>';
map['rotulo.markdown.negrito'] = '<hl:message key="rotulo.markdown.negrito"/>';
map['rotulo.markdown.italico'] = '<hl:message key="rotulo.markdown.italico"/>';
map['rotulo.markdown.sobrescrito'] = '<hl:message key="rotulo.markdown.sobrescrito"/>';
map['rotulo.markdown.lista'] = '<hl:message key="rotulo.markdown.lista"/>';
map['rotulo.markdown.lista.numerada'] = '<hl:message key="rotulo.markdown.lista.numerada"/>';
map['rotulo.markdown.fonte.vermelha'] = '<hl:message key="rotulo.markdown.fonte.vermelha"/>';
map['rotulo.markdown.fonte.amarela'] = '<hl:message key="rotulo.markdown.fonte.amarela"/>';
map['rotulo.markdown.fonte.verde'] = '<hl:message key="rotulo.markdown.fonte.verde"/>';
map['rotulo.markdown.fonte.azul'] = '<hl:message key="rotulo.markdown.fonte.azul"/>';
map['rotulo.markdown.estilo.titulo'] = '<hl:message key="rotulo.markdown.estilo.titulo"/>';
map['rotulo.markdown.estilo.subtitulo'] = '<hl:message key="rotulo.markdown.estilo.subtitulo"/>';
map['rotulo.markdown.estilo.texto'] = '<hl:message key="rotulo.markdown.estilo.texto"/>';
map['rotulo.markdown.imagem'] = '<hl:message key="rotulo.markdown.imagem"/>';
map['rotulo.markdown.desfazer'] = '<hl:message key="rotulo.markdown.desfazer"/>';
map['rotulo.markdown.refazer'] = '<hl:message key="rotulo.markdown.refazer"/>';
map['rotulo.markdown.ajuda'] = '<hl:message key="rotulo.markdown.ajuda"/>';
map['rotulo.markdown.aumentar.fonte'] = '<hl:message key="rotulo.markdown.aumentar.fonte"/>';
map['rotulo.markdown.reduzir.fonte'] = '<hl:message key="rotulo.markdown.reduzir.fonte"/>';

map['rotulo.botao.confirmar'] = '<hl:message key="rotulo.botao.confirmar"/>';
map['rotulo.botao.cancelar'] = '<hl:message key="rotulo.botao.cancelar"/>';

map['ajuda.campo.captcha'] = '<hl:message key="ajuda.campo.captcha"/>';

map['rotulo.botao.teclado.virtual.aceitar'] =  '<hl:message key="rotulo.botao.teclado.virtual.aceitar"/>';
map['rotulo.botao.teclado.virtual.altgr'] = '<hl:message key="rotulo.botao.teclado.virtual.altgr"/>';
map['rotulo.botao.teclado.virtual.retroceder'] = '<hl:message key="rotulo.botao.teclado.virtual.retroceder"/>';
map['rotulo.botao.teclado.virtual.apagar'] = '<hl:message key="rotulo.botao.teclado.virtual.apagar"/>';
map['rotulo.botao.teclado.virtual.cancelar'] = '<hl:message key="rotulo.botao.teclado.virtual.cancelar"/>';
map['rotulo.botao.teclado.virtual.limpar'] = '<hl:message key="rotulo.botao.teclado.virtual.limpar"/>';
map['rotulo.botao.teclado.virtual.shift'] = '<hl:message key="rotulo.botao.teclado.virtual.shift"/>';
map['rotulo.botao.teclado.virtual.space'] = '<hl:message key="rotulo.botao.teclado.virtual.space"/>';
map['rotulo.botao.teclado.virtual.scroll'] = '<hl:message key="rotulo.botao.teclado.virtual.scroll"/>';

map['rotulo.markdown.url.imagem.label'] = '<hl:message key="rotulo.markdown.url.imagem.label"/>';
map['rotulo.markdown.url.imagem.exemplo'] = '<hl:message key="rotulo.markdown.url.imagem.exemplo"/>';
map['rotulo.markdown.titulo.imagem'] = '<hl:message key="rotulo.markdown.titulo.imagem"/>';
map['rotulo.markdown.titulo.imagem.exemplo'] = '<hl:message key="rotulo.markdown.titulo.imagem.exemplo"/>';
map['rotulo.markdown.texto.alt.imagem'] = '<hl:message key="rotulo.markdown.texto.alt.imagem"/>';
map['rotulo.markdown.texto.alt.imagem.exemplo'] = '<hl:message key="rotulo.markdown.texto.alt.imagem.exemplo"/>';
map['rotulo.markdown.inserir'] = '<hl:message key="rotulo.markdown.inserir"/>';
map['rotulo.markdown.video'] = '<hl:message key="rotulo.markdown.video"/>';
map['rotulo.markdown.video.placeholder'] = '<hl:message key="rotulo.markdown.video.placeholder"/>';
map['rotulo.markdown.video.link'] = '<hl:message key="rotulo.markdown.video.link"/>';

map['rotulo.acoes'] = '<hl:message key="rotulo.acoes"/>';
map['rotulo.datatables.ocultar.colunas'] = '<hl:message key="rotulo.datatables.ocultar.colunas"/>';
map['rotulo.dashboard.credenciamento.acao.ass.termo.aditivo.serpro'] = '<hl:message key="rotulo.dashboard.credenciamento.acao.ass.termo.aditivo.serpro"/>';
map['mensagem.credenciamento.serpro.nao.esta.executando'] = '<hl:message key="mensagem.credenciamento.serpro.nao.esta.executando"/>';

map['mensagem.confirmacao.endereco.consignataria.exclusao'] = '<hl:message key="mensagem.confirmacao.endereco.consignataria.exclusao"/>';

map['mensagem.confirmacao.endereco.correspondente.exclusao'] = '<hl:message key="mensagem.confirmacao.endereco.correspondente.exclusao"/>';

map['mensagem.confirmacao.ocultar.rse.csa.arg'] = '<hl:message key="mensagem.confirmacao.ocultar.rse.csa.arg"/>';

function mensagem(k) {
    return map[k].replace(/<br\s*[\/]?>/gi, "\n");
}

function locale(){
    return '<%=(String)LocaleHelper.getLocale()%>';
}

function getDecimalSeparator(){
    return '<%=(char)LocaleHelper.getDecimalFormat().getDecimalFormatSymbols().getDecimalSeparator()%>';
}

function getGroupingSeparator(){
    return '<%=(char)LocaleHelper.getDecimalFormat().getDecimalFormatSymbols().getGroupingSeparator()%>';
}
