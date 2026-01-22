function placeWatermark() {
    if (document.layers) {
        document.watermark.pageX = (window.innerWidth - document.watermark.document.myImage.width)/2;
        document.watermark.pageY = (window.innerHeight - document.watermark.document.myImage.height)/2;
        document.watermark.visibility = 'visible';
    }
}

function closeWatermark() {
    var watermark = getStyleObject("watermark");
    var hideButton = document.getElementById("hideButton");
    watermark.visibility = "hidden";
    hideButton.style.visibility = "hidden";
}

function getStyleObject(objectId) {
    if(document.getElementById && document.getElementById(objectId)) {
	// W3C DOM
	return document.getElementById(objectId).style;
    } else if (document.all && document.all(objectId)) {
	// MSIE 4 DOM
	return document.all(objectId).style;
    } else if (document.layers && document.layers[objectId]) {
	// NN 4 DOM.. note: this won't find nested layers
	return document.layers[objectId];
    } else {
	return false;
    }
}