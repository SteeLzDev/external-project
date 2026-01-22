<%@page import="com.zetra.econsig.values.CodedValues"%>
<%@page
	import="com.zetra.econsig.helper.markdown.Markdown4jProcessorExtended"%>
<%@page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@page import="com.zetra.econsig.dto.entidade.MensagemTO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@ page import="com.zetra.econsig.values.CodedValues"%>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants"%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib uri="/showfield-lib" prefix="show"%>

<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
boolean possuiMensagemSessao = !TextHelper.isNull(request.getAttribute("possuiMensagemSessao"));
%>

<c:set var="imageHeader">
	<use xlink:href="#i-mensagem"></use>
</c:set>
<c:set var="title">
	<hl:message key="mensagem.info.reconhecimento.facial.titulo.primeiro.acesso" />
</c:set>
<c:set var="bodyContent">
	<% if(possuiMensagemSessao){ %>
		<div class="alert alert-warning" role="alert">
			<p class="mb-0">
				<hl:message key="mensagem.info.reconhecimento.facial.avancar" />
			</p>
		</div>
	<% } %>

	<div class="modal fade modal-top -modalReconhecimentoFacial-35" id="modalInformativoReconhecimentoFacial" tabindex="-1" aria-labelledby="faceModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered">
			<div class="modal-content-ReconhcimentoFacial">
				<div class="modal-body">
					<div class="row">
						<div class="col d-flex flex-column align-items-center">
							<div class="modal-ReconhecimentoFacial-titulo">
								<img class="img-fluid" src="../img/reconhecimentoFacial/icon_reconhecimento_facial.png">
								<h2 class="modal-grid-title"><hl:message key="mensagem.info.reconhecimento.facial.titulo.primeiro.acesso" /></h2>
							</div>
							<div class="modal-ReconhecimentoFacial-recomendacoes">
								<h5><hl:message key="mensagem.info.reconhecimento.facial.instrucoes"/></h5>
								<p><i class="fa fa-sun-o fa-stack" aria-hidden="true"></i><hl:message key="mensagem.info.reconhecimento.facial.primeira.instrucao" /></p>
								<p><i class="fa fa-smile-o fa-stack" aria-hidden="true"></i><hl:message key="mensagem.info.reconhecimento.facial.segunda.instrucao" /></p>
							</div>
							<div class="btn-action">
								<a class="btn btn-primary" href="#no-back" onClick="iniciarCaptura()"><hl:message key="rotulo.reconhecimento.facial.botao.iniciar" /></a>
								<a class="btn btn-outline-danger" href="#no-back" onClick="sairSistema();" alt="<hl:message key="rotulo.botao.voltar"/>" title="<hl:message key="rotulo.botao.voltar"/>"><hl:message key="rotulo.botao.voltar" /></a>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="modal fade modal-top -modalReconhecimentoFacial-65" id="faceModal" tabindex="-1" aria-labelledby="faceModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered">
			<div class="modal-content-ReconhcimentoFacial">
				<div class="modal-body">
					<div class="row">
						<div class="col d-flex flex-column align-items-center">
							<div class="my-auto">
								<h1 class="modal-grid-title"><hl:message key="mensagem.info.reconhecimento.facial.titulo.primeiro.acesso" /></h1>
								<h5><p id="posicaoDoRosto" class="modal-grid-message"><hl:message key="mensagem.info.reconhecimento.facial.posicao.rosto.frontal" /></p></h5>
								<h5><p id="textoInformativo" class="modal-grid-message"><hl:message key="mensagem.info.reconhecimento.facial.posicionamento.rosto" /></p></h5>
								<h5><p id="proximaFoto" class="modal-grid-message"><hl:message key="mensagem.info.reconhecimento.facial.aguarde.proxima.foto" /></p></h5>
								<div id="countdown"><h2></h2></div>
								<div id="loading-icon" class="text-center"></div>
								<div class="btn-action text-center" id="novaTentativaCaptura" style="display:none; margin-top:20px">
                     				<a class="btn btn-outline-danger" href="#no-back" onClick="sairSistema();" alt="<hl:message key="rotulo.botao.sair"/>" title="<hl:message key="rotulo.botao.sair"/>"><hl:message key="rotulo.botao.sair"/></a>
                     				<a class="btn btn-primary" id="botaoTentarNovamente" href="#no-back" onClick="reiniciarCaptura()"><hl:message key="rotulo.reconhecimento.facial.botao.tentar.novamente"/></a>
                  				</div>
								<img id="foto-instrucaoReconhecimento" class="img-fluid" src="../img/reconhecimentoFacial/frontal.png">
							</div>
						</div>
						<div class="col">
							<div id="video-container-modalReconhecimentoFacial">
								<video id="video-modalReconhecimentoFacial" autoplay></video>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="btn-action">
		<%if(possuiMensagemSessao){ %>
			<a class="btn btn-primary" href="#no-back" onClick="iniciarReconhecimentoFacial()"><hl:message key="rotulo.botao.avancar" /></a>
		<% } %>
		<a class="btn btn-outline-danger" href="#no-back" onClick="sairSistema();" alt="<hl:message key="rotulo.botao.voltar"/>" title="<hl:message key="rotulo.botao.voltar"/>"><hl:message key="rotulo.botao.voltar" /></a>
	</div>
</c:set>
<c:set var="javascript">
	<script src="<c:url value='/js/faceapi/face-api.min.js'/>"></script>
	<script type="text/JavaScript">
		const localModels = '<%=request.getContextPath()%>' + '/js/faceapi/models';
	
		const constanteLimiteSuperiorIEPD = 41
		const constanteLimiteInferiorIEPD = 25
		const constanteLimiteSuperiorIDPD = 30
		const constanteLimiteInferiorIDPD = 24
	
		const constanteLimiteSuperiorIEPE = 50
		const constanteLimiteInferiorIEPE = 40
		const constanteLimiteSuperiorIDPE = 22
		const constanteLimiteInferiorIDPE = 10

	
		const video = document.getElementById('video-modalReconhecimentoFacial');
		const videoContainer = document.getElementById('video-container-modalReconhecimentoFacial');
		const countdownElement = document.querySelector('#countdown h2');
		const posicaoDoRosto = document.getElementById("posicaoDoRosto");
		const proximaFoto = document.getElementById("proximaFoto")
		const fotoInstrucaoReconhecimento = document.getElementById("foto-instrucaoReconhecimento");
		const textoInformativo = document.getElementById("textoInformativo");
		const novaTentativaCaptura = document.getElementById("novaTentativaCaptura");
		const botaoTentarNovamente = document.getElementById("botaoTentarNovamente");
	
		
		let intervaloContagemRegressiva; 
		let contagemRegressivaAtiva = true; 
		let contagemRegressivaIniciada = false;
		let stream; 
		let base64DataRecortada = null;
		let fotoTirada = false;
		let capturaPerfilEsquerdo = false; 
		let fotoEnviadaPD = false; 
		let fotoEnviadaPE = false; 
		let fotoEnviadaFrontal = false;
		let fotosCapturadas = {
				fotoFrontal: null,
				fotoPerfilDireito: null,
				fotoPerfilEsquerdo: null
		};
		let quantidadeTentativasFrontal = 0;
		let quantidadeTentativasPD = 0;
		let quantidadeTentativasPE = 0;
		let quantidadeTentativasAjax = 0;
		let tempoAteFoto = 5000;

		forcaFechamentoModal = function(event) {
			event.preventDefault();
		};
		document.getElementById("faceModal").addEventListener('hide.bs.modal', forcaFechamentoModal);
		
		
		forcaFechamentoModalInstrucao = function(event) {
			event.preventDefault();
		};
		document.getElementById("modalInformativoReconhecimentoFacial").addEventListener('hide.bs.modal', forcaFechamentoModalInstrucao);
	
		proximaFoto.style.display = 'none';
	
		<% if(possuiMensagemSessao){ %>
			function iniciarReconhecimentoFacial(){
				$('#modalInformativoReconhecimentoFacial').modal('show');
			}
		<% } else { %>
			$(document).ready(function() {
				$('#modalInformativoReconhecimentoFacial').modal('show');
			});
		<% } %>
		function iniciarCaptura() {
			$('#modalInformativoReconhecimentoFacial').modal('hide');
			$('#faceModal').modal('show');
			carregarModels();
		}
		
		function reiniciarCaptura() {
			location.reload();
		}
	
		function carregarModels() {
			Promise.all([
				faceapi.nets.tinyFaceDetector.loadFromUri(localModels),
				faceapi.nets.faceLandmark68Net.loadFromUri(localModels),
				faceapi.nets.faceRecognitionNet.loadFromUri(localModels),
				faceapi.nets.faceExpressionNet.loadFromUri(localModels)
			]).then(iniciarVideo(rastrearRostoFrontal));
		}
	
		async function iniciarVideo(rastrear) {
			stream = await navigator.mediaDevices.getUserMedia({ video: true });
	
			video.srcObject = stream;
			video.addEventListener('loadedmetadata', () => {
				video.play();
				rastrear();
			});
		}

		async function rastrearRostoFrontal() {
			try{
				if(verificaQuantidadeTentativas()){
					return;
				}

				const faceDetector = new faceapi.TinyFaceDetectorOptions();
				const canvas = faceapi.createCanvasFromMedia(video);
		
				const displaySize = { width: video.videoWidth, height: video.videoHeight };
				videoContainer.style.width = `${displaySize.width}px`;
				videoContainer.style.height = `${displaySize.height}px`;
		
				faceapi.matchDimensions(canvas, displaySize);
		
				let fotoTirada = false;
		
				intervaloContagemRegressiva = setInterval(async () => {
					if (!contagemRegressivaAtiva) {
						clearInterval(intervaloContagemRegressiva);
						return;
					}
		
					const detections = await faceapi.detectAllFaces(video, faceDetector).withFaceLandmarks().withFaceExpressions();
		
					if (detections.length > 0) {
						const landmarks = detections[0].landmarks;
						const olhosEsquerdo = landmarks.getLeftEye();
						const olhosDireito = landmarks.getRightEye();
						const boca = landmarks.getMouth();
						const nariz = landmarks.getNose();
		
						if (olhosEsquerdo && olhosDireito && boca && nariz && !fotoTirada) {
							if (tempoAteFoto <= 0 && !fotoEnviadaFrontal) {
								const fotoDataUrl = await capturarFoto();
		
								if (fotoDataUrl == null) {
									textoInformativo.innerText = '<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.retentativa", responsavel)%>'
									video.style.display = 'block';
		
									if (stream) {
										stream.getTracks().forEach(track => track.stop());
									}
		
									clearInterval(intervaloContagemRegressiva);
									contagemRegressivaAtiva = true;
									contagemRegressivaIniciada = true;
									base64DataRecortada = null;
									tempoAteFoto = 5000;
									quantidadeTentativasFrontal++;
									iniciarVideo(rastrearRostoFrontal);
								} else {
	 								fotoTirada = true;
									contagemRegressivaAtiva = false;
									if (base64DataRecortada != null && !fotoEnviadaFrontal) {
										clearInterval(intervaloContagemRegressiva);
										fotosCapturadas.fotoFrontal = fotoDataUrl.replace(/^data:image\/[a-z]+;base64,/, '');
										textoInformativo.innerText = '<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.aprovado.primeiro.acesso", responsavel)%>';
										proximaFoto.style.display = 'block';
										posicaoDoRosto.style.display = 'none';
										countdownElement.style.display = 'none';
										fotoInstrucaoReconhecimento.style.display = 'none';
										addLoadingIcon();
										setTimeout(iniciarVideo, 4000, rostoPerfilDireito);
										fotoEnviadaFrontal = true;
										fotoInstrucaoReconhecimento.src = "../img/reconhecimentoFacial/direita.png";
									}
									
								}
							} else {
								countdownElement.textContent = Math.ceil(tempoAteFoto / 1000);
								tempoAteFoto -= 100;
							}
						}
						videoContainer.classList.remove('red-border-modalReconhecimentoFacial');
						videoContainer.classList.add('green-border-modalReconhecimentoFacial');
					} else {
						videoContainer.classList.remove('green-border-modalReconhecimentoFacial');
						videoContainer.classList.add('red-border-modalReconhecimentoFacial');
					}
				}, 100);
			}catch(error){
				quantidadeTentativasFrontal++;
			}
		}
	
		async function rostoPerfilDireito() {
			try{
				if(verificaQuantidadeTentativas()){
					return;
				}
				removeLoadingIcon();
				clearInterval(intervaloContagemRegressiva);
				videoContainer.classList.remove('green-border-modalReconhecimentoFacial');
				videoContainer.classList.add('red-border-modalReconhecimentoFacial');
				textoInformativo.innerText = '<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.posicionamento.rosto", responsavel)%>';
				posicaoDoRosto.innerText = '<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.posicao.rosto.perfil.direito", responsavel)%>'
				proximaFoto.style.display = 'none';
				posicaoDoRosto.style.display = 'block';
				video.style.display = 'block';
				fotoInstrucaoReconhecimento.style.display = '';
				contagemRegressivaAtiva = true;
				contagemRegressivaIniciada = true;
		
				const canvas = faceapi.createCanvasFromMedia(video);
		
				const displaySize = { width: video.videoWidth, height: video.videoHeight };
				videoContainer.style.width = `${displaySize.width}px`;
				videoContainer.style.height = `${displaySize.height}px`;
		
				faceapi.matchDimensions(canvas, displaySize);
		
				let fotoTirada = false;
				tempoAteFoto = 5000;
				countdownElement.textContent = Math.ceil(tempoAteFoto / 1000);
				countdownElement.style.display = '';

		
				intervaloContagemRegressiva = setInterval(async () => {
					if (!contagemRegressivaAtiva) {
						clearInterval(intervaloContagemRegressiva);
						return;
					}
		
					const faceDetection = await faceapi.detectSingleFace(video, new faceapi.TinyFaceDetectorOptions()).withFaceLandmarks();
		
					if (faceDetection) {
						const leftEye = faceDetection.landmarks.getLeftEye();
						const rightEye = faceDetection.landmarks.getRightEye();
						const nose = faceDetection.landmarks.getNose();
		
						const inclinacaoEsquerda = calcularInclinacao(leftEye[0], nose[3], nose[0]);
						const inclinacaoDireita = calcularInclinacao(rightEye[0], nose[3], nose[0]);

						if(inclinacaoEsquerda == null || inclinacaoDireita == null){
							quantidadeTentativasPD++;
						}

						if (constanteLimiteInferiorIEPD < Math.abs(inclinacaoEsquerda) && Math.abs(inclinacaoEsquerda) < constanteLimiteSuperiorIEPD &&
						constanteLimiteInferiorIDPD < Math.abs(inclinacaoDireita) && Math.abs(inclinacaoDireita) < constanteLimiteSuperiorIDPD) {
							videoContainer.classList.remove('red-border-modalReconhecimentoFacial');
							videoContainer.classList.add('green-border-modalReconhecimentoFacial');
							if (tempoAteFoto <= 0 && !fotoEnviadaPD) {
								const fotoDataUrl = await capturarFoto();
		
								if (fotoDataUrl == null) {
									textoInformativo.innerText = '<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.retentativa", responsavel)%>'
									video.style.display = 'block';
		
									if (stream) {
										stream.getTracks().forEach(track => track.stop());
									}
		
									clearInterval(intervaloContagemRegressiva);
									contagemRegressivaAtiva = true;
									contagemRegressivaIniciada = true;
									base64DataRecortada = null;
									tempoAteFoto = 5000;
									quantidadeTentativasPD++;
									iniciarVideo(rostoPerfilDireito);
								} else {
	 								fotoTirada = true;
									contagemRegressivaAtiva = false;
									if (base64DataRecortada != null && !fotoEnviadaPD) {
										clearInterval(intervaloContagemRegressiva);
										fotosCapturadas.fotoPerfilDireito = fotoDataUrl.replace(/^data:image\/[a-z]+;base64,/, '');
										textoInformativo.innerText = '<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.aprovado.primeiro.acesso", responsavel)%>';
										proximaFoto.style.display = 'block';
										posicaoDoRosto.style.display = 'none';
										countdownElement.style.display = 'none';
										fotoInstrucaoReconhecimento.style.display = 'none';
										addLoadingIcon();
										setTimeout(iniciarVideo, 4000, rostoPerfilEsquerdo);
										fotoEnviadaPD = true;
										fotoInstrucaoReconhecimento.src = "../img/reconhecimentoFacial/esquerda.png";
									}
								}
							} else {
								countdownElement.textContent = Math.ceil(tempoAteFoto / 1000);
								tempoAteFoto -= 100;
							}						
						}else{
							videoContainer.classList.remove('green-border-modalReconhecimentoFacial');
							videoContainer.classList.add('red-border-modalReconhecimentoFacial');
						}
					} else {
						videoContainer.classList.remove('green-border-modalReconhecimentoFacial');
						videoContainer.classList.add('red-border-modalReconhecimentoFacial');
					}
				}, 100);
			}catch(error){
				quantidadeTentativasPD++;
			}
		}
	
		async function rostoPerfilEsquerdo() {
			try{
				if(verificaQuantidadeTentativas()){
					return;
				}
				removeLoadingIcon();
				clearInterval(intervaloContagemRegressiva);
				videoContainer.classList.remove('green-border-modalReconhecimentoFacial');
				videoContainer.classList.add('red-border-modalReconhecimentoFacial');
				textoInformativo.innerText = '<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.posicionamento.rosto", responsavel)%>';
				posicaoDoRosto.innerText = '<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.posicao.rosto.perfil.esquerdo", responsavel)%>'
				proximaFoto.style.display = 'none';
				posicaoDoRosto.style.display = 'block';
	 			video.style.display = 'block';
	 			fotoInstrucaoReconhecimento.style.display = '';
				contagemRegressivaAtiva = true;
				contagemRegressivaIniciada = true;

		
				const canvas = faceapi.createCanvasFromMedia(video);
		
				const displaySize = { width: video.videoWidth, height: video.videoHeight };
				videoContainer.style.width = `${displaySize.width}px`;
				videoContainer.style.height = `${displaySize.height}px`;
		
				faceapi.matchDimensions(canvas, displaySize);
		
				let fotoTirada = false;
				tempoAteFoto = 5000;
				countdownElement.textContent = Math.ceil(tempoAteFoto / 1000);
				countdownElement.style.display = '';
		
				intervaloContagemRegressiva = setInterval(async () => {
					if (!contagemRegressivaAtiva) {
						clearInterval(intervaloContagemRegressiva);
						return;
					}
		
					const faceDetection = await faceapi.detectSingleFace(video, new faceapi.TinyFaceDetectorOptions()).withFaceLandmarks();
		
					if (faceDetection) {
						const leftEye = faceDetection.landmarks.getLeftEye();
						const rightEye = faceDetection.landmarks.getRightEye();
						const nose = faceDetection.landmarks.getNose();
		
						const inclinacaoEsquerda = calcularInclinacao(leftEye[0], nose[3], nose[0]);
						const inclinacaoDireita = calcularInclinacao(rightEye[0], nose[3], nose[0]);
						
						if(inclinacaoEsquerda == null || inclinacaoDireita == null){
							quantidadeTentativasPE++;
						}
	
						if (constanteLimiteInferiorIEPE < Math.abs(inclinacaoEsquerda) && Math.abs(inclinacaoEsquerda) < constanteLimiteSuperiorIEPE &&
						constanteLimiteInferiorIDPE < Math.abs(inclinacaoDireita) && Math.abs(inclinacaoDireita) < constanteLimiteSuperiorIDPE) {
							videoContainer.classList.remove('red-border-modalReconhecimentoFacial');
							videoContainer.classList.add('green-border-modalReconhecimentoFacial');
							if (tempoAteFoto <= 0 && !fotoEnviadaPE) {
								const fotoDataUrl = await capturarFoto();
		
								if (fotoDataUrl == null) {
									textoInformativo.innerText = '<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.retentativa", responsavel)%>'
									video.style.display = 'block';
		
									if (stream) {
										stream.getTracks().forEach(track => track.stop());
									}
		
									clearInterval(intervaloContagemRegressiva);
									contagemRegressivaAtiva = true;
									contagemRegressivaIniciada = true;
									base64DataRecortada = null;
									quantidadeTentativasPE++;
									tempoAteFoto = 5000;
									iniciarVideo(rostoPerfilEsquerdo);
								} else {
	 								fotoTirada = true;
									contagemRegressivaAtiva = false;
									if (base64DataRecortada != null && !fotoEnviadaPE && !capturaPerfilEsquerdo) {
										clearInterval(intervaloContagemRegressiva);
										capturaPerfilEsquerdo = true;
										fotosCapturadas.fotoPerfilEsquerdo = fotoDataUrl.replace(/^data:image\/[a-z]+;base64,/, '');
										await enviarFotoReconhecimentoPerfil(fotosCapturadas);
									}
									
								}
							} else {
								countdownElement.textContent = Math.ceil(tempoAteFoto / 1000);
								tempoAteFoto -= 100;
							}			
						}else{
							videoContainer.classList.remove('green-border-modalReconhecimentoFacial');
							videoContainer.classList.add('red-border-modalReconhecimentoFacial');}
					} else {
						videoContainer.classList.remove('green-border-modalReconhecimentoFacial');
						videoContainer.classList.add('red-border-modalReconhecimentoFacial');
					}
				}, 100);
			}catch(error){
				quantidadeTentativasPE++;
			}
		}
	
		function calcularInclinacao(ponto1, ponto2, ponto3) {
			try{
				const inclinacaoRad = Math.atan2(ponto3.y - ponto2.y, ponto3.x - ponto2.x) - Math.atan2(ponto1.y - ponto2.y, ponto1.x - ponto2.x);
				const inclinacaoGraus = inclinacaoRad * (180 / Math.PI);
				return inclinacaoGraus;
			}catch(error){
				return null;
			}
		}
	
		async function capturarFoto() {
			try{
				const canvas = document.createElement('canvas');
				const context = canvas.getContext('2d');
				const { videoWidth, videoHeight } = video;
		
				const scaleFactor = 1.2;
		
				const targetWidth = scaleFactor * videoWidth;
				const targetHeight = scaleFactor * videoHeight;
		
				const offsetX = (targetWidth - videoWidth) / 2;
				const offsetY = (targetHeight - videoHeight) / 2;
		
				canvas.width = targetWidth;
				canvas.height = targetHeight;
				context.drawImage(video, -offsetX, -offsetY, targetWidth, targetHeight);
		
				const base64Data = canvas.toDataURL('image/jpeg');
		
				const detections = await faceapi.detectSingleFace(canvas, new faceapi.TinyFaceDetectorOptions({
				})).withFaceLandmarks();
		
				if (detections) {
					const faceBoundingBox = detections.detection.box;
					const { x, y, width, height } = faceBoundingBox;
		
					const centerX = x + width / 2;
					const centerY = y + height / 2;
		
					const recorteWidth = width * scaleFactor;
					const recorteHeight = height * scaleFactor;
		
					const recorteX = centerX - recorteWidth / 2;
					const recorteY = centerY - recorteHeight / 2;
		
					const canvasRecorte = document.createElement('canvas');
					const contextRecorte = canvasRecorte.getContext('2d');
		
					canvasRecorte.width = recorteWidth;
					canvasRecorte.height = recorteHeight;
		
					contextRecorte.drawImage(canvas, recorteX, recorteY, recorteWidth, recorteHeight, 0, 0, recorteWidth, recorteHeight);
		
					base64DataRecortada = canvasRecorte.toDataURL('image/jpeg');
					return base64DataRecortada;
				} else {
					return null;
				}
			}catch(error){
				return null;
			}
		}
	
		function fecharModal() {
			clearInterval(intervaloContagemRegressiva);
			postData('../v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true');
		}

		async function enviarFotoReconhecimentoPerfil(fotoData) {
			if(verificaQuantidadeTentativas()){
				return;
			}
			const validacaoCaptura = false
			countdownElement.textContent = '';
			fotoInstrucaoReconhecimento.style.display = 'none';
			posicaoDoRosto.style.display = 'none';
			addLoadingIcon();
			textoInformativo.innerText = '<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.aguarde.cadastro", responsavel)%>'
			

			for (const foto in fotoData) {
				if (fotoData[foto] == null) {
					validacaoCaptura = true;
					break;
				}
			}

			if (!validacaoCaptura) {
				try {
					const url = '../v3/reconhecimentoFacial?acao=registrarFotosPrimeiroAcesso';

					const response = await fetch(url, {
						method: 'POST',
						headers: {
							'Content-Type': 'application/json'
						},
						body: JSON.stringify(fotoData)
					});

					if (response.status === 200) {
						continuarAguardandoVerificacao = true;
						textoInformativo.innerText = '<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.aprovado.primeiro.acesso", responsavel)%>';
						proximaFoto.innerText = '<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.aguarde.redirecionamento", responsavel)%>';
						proximaFoto.style.display = 'block';
						setTimeout(fecharModal, 5000);
						fotoEnviadaPE = true;
					} else {
						if (stream) {
							stream.getTracks().forEach(track => track.stop());
						}
						postData('../v3/reconhecimentoFacial?acao=erroReconhecimentoFacial');
					}
				} catch (error) {
					quantidadeTentativasAjax++;
				}
			} else {
				quantidadeTentativasAjax++;
			}
		}

	function sairSistema() {
		postData('../v3/sairSistema?acao=sair','_top');
	}
	
	function addLoadingIcon(){
		const loadingIconDiv = document.getElementById("loading-icon");
		if(!loadingIconDiv.hasChildNodes()){
			const spinnerDiv = document.createElement("div");
			spinnerDiv.classList.add("spinner-border");
			spinnerDiv.setAttribute("role", "status");				
			loadingIconDiv.appendChild(spinnerDiv);	
		}
	}
	
	function removeLoadingIcon(){
		const loadingIconDiv = document.getElementById("loading-icon");
		if(loadingIconDiv.hasChildNodes()){
			loadingIconDiv.removeChild(loadingIconDiv.firstElementChild);
		}
	}
	
	function verificaQuantidadeTentativas(){
		if(quantidadeTentativasFrontal >= 3 || quantidadeTentativasPD >= 3 || quantidadeTentativasPE >= 3 || quantidadeTentativasAjax >= 3){
			if (stream) {
				stream.getTracks().forEach(track => track.stop());
			}
			clearInterval(intervaloContagemRegressiva);
			novaTentativaCaptura.style.display = '';
			posicaoDoRosto.style.display = 'none';
			proximaFoto.style.display = 'none';
			countdown.style.display = 'none';
			fotoInstrucaoReconhecimento.style.display = 'none';
			removeLoadingIcon();
			textoInformativo.innerText = '<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.erro.captura", responsavel)%>';
			return true;
		}
		return false;
	}
	</script>
</c:set>
<%-- Leiaute Fixo --%>
<t:page_v4>
	<jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
	<jsp:attribute name="header">${title}</jsp:attribute>
	<jsp:attribute name="javascript">${javascript}</jsp:attribute>
	<jsp:body>${bodyContent}</jsp:body>
</t:page_v4>