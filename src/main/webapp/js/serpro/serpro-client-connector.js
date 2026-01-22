(function () {
	checkEnvironment();
    
    window.initSerproSignerClient = function () {
		var timeoutDefault = 3000;
		var tryAgainTimeoutWebSocket;
		var tryAgainTimeoutVerify;

		// Configure SerproSigner
		configureDesktopClient();

		// Verify if is installed AND running
		verifyDesktopClientInstallation();

		function configureDesktopClient() {
			window.SerproSignerClient.setDebug(false);
			window.SerproSignerClient.setUriServer("wss", "127.0.0.1", 65156, "/signer");
		}

		function verifyDesktopClientInstallation() {
			window.SerproSignerClient.verifyIsInstalledAndRunning()
				.success(function (response) {
					clearInterval(tryAgainTimeoutVerify);
					connectToWebSocket();
				}).error(function (response) {
					alert(mensagem('rotulo.dashboard.credenciamento.acao.ass.termo.aditivo.serpro'));
				});
		}
		
		function connectToWebSocket() {
			window.SerproSignerClient.connect(callbackOpenClose, callbackOpenClose, callbackError);
		}

		function callbackOpenClose(connectionStatus) {

			if (connectionStatus === 1) {
				console.debug('Connected on Server');
				clearInterval(tryAgainTimeoutWebSocket);
			} else {
				clearInterval(tryAgainTimeoutWebSocket);
				tryAgainTimeoutWebSocket = setTimeout(verifyDesktopClientInstallation, timeoutDefault);
			}
		}

		function callbackError(event) {
			var serverAuthorizarion = $('.js-server-authorization');
			serverAuthorizarion.show();

			if (event.error !== undefined) {
				if (event.error !== null && event.error !== 'null') {
					console.error({ message: event.error });
				} else {
					console.error({ message: 'Unknown error' });
				}
			}
		}
	}

	function sign(params) {
    return new Promise(function (resolve, reject) {
        // Valida os parâmetros obrigatórios
        if (!params.type) {
            reject(new Error('Sign type is not defined.'));
            return;
        }
        if (!params.data && params.type !== 'file') {
            reject(new Error('Sign data is not defined.'));
            return;
        }

        // Antes de assinar
        params.beforeSign && params.beforeSign();

        // Sign - Chama o assinador
        window.SerproSignerClient.sign(params.type, params.data, params.textEncoding, params.outputDataType, params.attached)
            .success(function (response) {
                if (response.actionCanceled) {
                    console.debug('Action canceled by User.');
                    params.onCancel && params.onCancel(response);
                    reject(new Error('Action canceled by User'));
                } else {
                    if (params.type == 'file') {
                        params.onSuccess && params.onSuccess({
                            original: {
                                size: response.original.length,
                                base64: response.original
                            },
                            signature: {
                                size: response.signature.length,
                                base64: response.signature
                            },
                            fileName: response.originalFileName
                        });
                    } else {
                        params.onSuccess && params.onSuccess({
                            original: {
                                size: response.original.length,
                                base64: response.original
                            },
                            signature: {
                                size: response.signature.length,
                                base64: response.signature
                            }
                        });
                    }

                    resolve(response.signature);
                }
                params.afterSign && params.afterSign(response);
            })
            .error(function (error) {
                console.debug('Error:', error);
                params.onError && params.onError(error);
                params.afterSign && params.afterSign(error);
                reject(error);
            });
    });
}

	function verify(params) {
	    return new Promise(function (resolve, reject) {
	        // Verify - Chama o assinador
	        window.SerproSignerClient.verify(params.type, params.inputData, params.inputSignature, null, params.algorithmOIDHash)
	            .success(function (response) {
	                if (response.actionCanceled) {
	                    console.debug('Action canceled by User.');
	                    params.onCancel && params.onCancel(response);
	                } else {
	 					params.onSuccess && params.onSuccess({
							signerSignatureValidations: {
								value: response.signerSignatureValidations
							}
                        });
	                }
                    resolve(response.signerSignatureValidations);
	                params.afterSign && params.afterSign(response);
	            })
	            .error(function (error) {
	                console.debug('Error:', error);
	                if (error.error) {
	                    alert(error.error);
	                }
	                console.debug('Error:', error);
	                params.onError && params.onError(error);
	                params.afterSign && params.afterSign(error);
	                reject(error);
	            });
	    });
	}

    // ---------- Sign PDF ----------
	window.signPdf = function(base64){
	    return new Promise(function(resolve, reject) {
	        sign({
	            type: 'pdf',
	            data: base64,
	            onSuccess: function(data) {
	                resolve(data.signature.base64);
	            },
	            onError: function(error) {
	                console.debug('ERRO: ', error);
	                reject(error);
	            }
	        });
	    });
	}

	// ---------- Validate PDF Signature ----------
	window.validatePdfSign = function(base64) {
	    return new Promise(function(resolve, reject) {
	        verify({
	            type: 'pdf',
	            inputData: base64,
	            onSuccess: function(validationSigner) {
	                resolve(validationSigner.signerSignatureValidations.value);
	            },
	            onError: function(error) {
	                console.debug('ERRO: ', error);
	                reject(error);
	            }
	        });
	    });
	};

	function checkEnvironment() {
		var env = {};
		// Browser
		env.ie = is.ie();
		env.edge = is.edge();
		env.chrome = is.chrome();
		env.firefox = is.firefox();
		env.opera = is.opera();
		env.safari = is.safari();

		// OS
		env.windows = is.windows();
		env.mac = is.mac();
		env.linux = is.linux();

		// Type
		env.desktop = is.desktop();
		env.mbile = is.mobile();
		env.blackberry = is.blackberry();

		// hide all
		$('.js-is-system > *').hide();
		$('.js-is-browser > *').hide();
		for (var key in env) {
			var value = env[key];
			if (value === true) {
				$('.js-is-' + key).show();
			}
		}
	}

})();
