function safe_tags( str ) {
    return String( str ).replace( /&/g, '&amp;' ).replace( /"/g, '&quot;' ).replace( /'/g, '&#39;' ).replace( /</g, '&lt;' ).replace( />/g, '&gt;' );
}

function uploadAnexo(fieldId, maxFileSize, allowedExtensions, typeOfFile, multiple, minQuantFile) {
	uploadAnexo(fieldId, maxFileSize, allowedExtensions, typeOfFile, multiple, false, minQuantFile);
}

function uploadAnexo(fieldId, maxFileSize, allowedExtensions, typeOfFile, multiple, botaoVisualizarRemover, minQuantFile) {	
    var file1 = document.getElementById(fieldId),
        btn = document.getElementById('upload-btn-' + fieldId),
        wrap = document.getElementById('pic-progress-wrap-' + fieldId),
        picBox = document.getElementById('picbox-' + fieldId),
        errBox = document.getElementById('errormsg-' + fieldId),
        multiWrapper = document.getElementById('multiWrapper-' + fieldId),
        maxFileSize = maxFileSize,
        fileSize = 0;
    
    if (file1 == null || typeof file1 === "undefined") {
    	return true;
    }
    
    var numAnexosEnviados = 0;
    var quantidadeMinima = minQuantFile;

    var uploader = new ss.SimpleUpload({
                button: btn,
                url: '../arquivos/upload_anexo.jsp',
                name: 'input-' + fieldId,
                multiple: multiple, //multipart
                maxUploads: (multiple ? 10 : 1),
                maxSize: maxFileSize,
                allowedExtensions: allowedExtensions,
                //accept: 'image/*',
                hoverClass: 'btn-hover',
                focusClass: 'active',
                disabledClass: 'disabled',
                responseType: 'json',
                data: {'tipo': typeOfFile},
                contentType: false,
                multipart: true,
                onExtError: function(filename, extension) {
                  alert(mensagem('mensagem.erro.upload.extensao.invalida').replace('{0}', filename).replace('{1}', allowedExtensions));
                },
                onSizeError: function(filename, fileSize) {
                  alert(mensagem('mensagem.erro.upload.tamanho.invalido').replace('{0}', filename).replace('{1}', maxFileSize));
                },        
                onSubmit: function(filename, ext) {            
                   var prog = document.createElement('div'),
                       outer = document.createElement('div'),
                       bar = document.createElement('div'),
                       size = document.createElement('div');
					   
				   if (wrap) {
					 wrap.style.display = 'block'; 
				   }
                   
				   prog.className = 'prog';
                   size.className = 'size';
                   outer.className = 'progress progress-striped active';
                   bar.className = 'progress-bar progress-bar-success';
                    
                   outer.appendChild(bar);
                   prog.innerHTML = '<span style="vertical-align:middle;">'+safe_tags(filename)+' - </span>';
                   prog.appendChild(size);
                   prog.appendChild(outer);
                   wrap.appendChild(prog);
                    
                   this.setProgressBar(bar);
                   this.setProgressContainer(prog);
                   this.setFileSizeBox(size);      
                    
                   errBox.innerHTML = '';
                },
                  
                startXHR: function() {
                    var abort = document.createElement('button');
                    wrap.appendChild(abort);
                    abort.className = 'btn btn-sm btn-info';
                    abort.innerHTML = 'Cancel';
                    this.setAbortBtn(abort, true);              
                },
                
                onComplete: function(filename, response) {
                    var prog = document.createElement('div');
                    var numero = 0;
                    
                    if(botaoVisualizarRemover && document.getElementById("posicao") != null){	                    
                    	numero = document.getElementById("posicao").value;
                    }
                    
                    prog.className = 'prog';
                	
                    if (!response) {
                        errBox.innerHTML = mensagem('mensagem.erro.upload.arquivo');
                        return;
                    } else {
                    	if (response.mensagem) {
                        	alert(response.mensagem);
                            errBox.innerHTML = response.mensagem;
                        } else {
                        	if (!multiple) {
                        		wrap.innerHTML = '';
                        		file1.value = '';
                        	}
                        	if (botaoVisualizarRemover) {
                        		prog.innerHTML = '<div id="'+ response.fileName + '" class="d-flex justify-content-between align-items-center mb-1 mt-1">' + response.fileName + ' - ' + response.fileSize + 'K <span><a id="botaoRemoveAnexoTemp" class="btn btn-primary pr-0 mr-2" title="Download deste anexo" href="#" onclick="downloadAnexoVisualizacao(' + numero + ')" aria-label="Clique aqui para fazer download deste anexo"><svg width="15"><use xlink:href="../img/sprite.svg#i-download"></use></svg></a><a id="botaoRemoveAnexoTemp" class="btn btn-danger pr-0" title="Remover anexo" href="#" onclick="removeAnexoVisualizacao(' + numero + ')" aria-label="Clique aqui para remover este anexo"><svg width="15"><use xlink:href="../img/sprite.svg#i-excluir"></use></svg></a></span></div>';
                        	} else {
                        		prog.innerHTML = '<div class="d-flex justify-content-center align-items-center">' + response.fileName + ' - ' + response.fileSize + 'K </div>';
                            }
                            wrap.appendChild(prog);
                            wrap.style.display = "block";
                            file1.value = file1.value ? file1.value + ';' + response.fileName : response.fileName;
                            
                            if (multiple && (novoLeiaute != null && !novoLeiaute)) {
                                var multipleFiles = document.getElementById('multipleFiles');
                                multipleFiles.value += response.fileName + ";";
                                multiWrapper.innerHTML += '<div style="vertical-align:middle; padding: 0.5rem;">' + response.fileName + ' - ' + response.fileSize + 'K </div>';
                                prog.innerHTML = '';
                            }

                        }
                    }
                    
                    numAnexosEnviados++;

                    if (botaoVisualizarRemover && document.getElementById("posicao") != null) {                    
                    	document.getElementById("posicao").value = Number(numero) + 1;
                    }
                    
                    var inputFILE1 = document.getElementById("input-" + fieldId);
                    inputFILE1.title=mensagem('mensagem.informe.arquivo.upload.ext').replace('{0}', allowedExtensions).replace('{1}', maxFileSize);
                }
     });
     
     if (quantidadeMinima > 0 && numAnexosEnviados < quantidadeMinima) {
	    errBox.innerHTML = mensagem('mensagem.erro.upload.arquivo.qunt.min').replace('{0}', quantidadeMinima);
        return;
	 }

     var inputFILE1 = document.getElementById("input-" + fieldId);
     inputFILE1.title=mensagem('mensagem.informe.arquivo.upload.ext').replace('{0}', allowedExtensions).replace('{1}', maxFileSize);
}