function picCBController() {
    var self = this;
    var h = 200;
    var w = 300;
    self.h = "200px;";
    self.w = "300px;";
    self.inputW = "width: 80px;";
    self.showImages = false;
    self.showSCs = false;
    
    self.arbitraryFilter = true;
    
    self.R = 0, self.G = 0, self.B = 0, self.A = 1;
    self.outR = 0, self.outG = 0, self.outB = 0, self.outA = 1;
    var scOrigCanvas, scAltCanvas;
    
    
    var imageFile;
    var origCanvas, altCanvas, secondCanvas;
    var origImage;

    // filters credit to http://web.archive.org/web/20081014161121/http://www.colorjack.com/labs/colormatrix/
    self.filterList = [
        {name: "User-defined", value: [[1, 0, 0], [0, 1, 0], [0, 0 ,1]]},
        {name: "Protanopia", value: [[0.56667, 0.43333, 0], [0.55833, 0.44167, 0], [0, 0.24167, 0.75833]]},
        {name: "Protanomaly", value: [[0.81667, 0.18333, 0], [0.33333, 0.66667, 0], [0, 0.125, 0.875]]},
        {name: "Deuteranopia", value: [[0.625, 0.375, 0], [0.7, 0.3, 0], [0, 0.3, 0.7]]},
        {name: "Deuteranomaly", value: [[0.8, 0.2, 0], [0.25833, 0.74167, 0], [0, 0.14167, 0.85833]]},
        {name: "Tritanopia", value: [[0.95, 0.05, 0], [0, 0.43333, 0.56667], [0, 0.475, 0.525]]},
        {name: "Tritanomaly", value: [[0.96667, 0.03333, 0], [0, 0.73333, 0.26667], [0, 0.18333, 0.81667]]},
        {name: "Achromatopsia", value: [[0.299, 0.587, 0.114], [0.299, 0.587, 0.114], [0.299, 0.587, 0.114]]},
        {name: "Achromatomaly", value: [[0.618, 0.32, 0.062], [0.163, 0.775, 0.062], [0.163, 0.32, 0.516]]}
            ];
    self.filterSelect = self.filterList[1];
    self.userFilter = self.filterList[0];
    
    self.toggleImages = function() {
    	self.showImages = !self.showImages;
    };
    
    self.toggleSCs = function() {
    	self.showSCs = !self.showSCs;
    };
    
    self.uploadImage = function() {
    	var input = document.getElementById("imageFile");
    	if (!input.files || !input.files[0]) {
    	    alert("Error");
    	    return;
    	}
    	    	
    	imageFile = input.files[0];
    	readFile(imageFile);
    };
    
    self.renderImage = function() {
        processOrigImage();
    };
    
    self.onChangeFilter = function() {
    	return;
        if (self.filterSelect == self.userFilter)
            self.arbitraryFilter = true;
        else
            self.arbitraryFilter = false;
    };
    
    var singleColorRender = function() {
    	var ctx = scOrigCanvas.getContext("2d");
    	ctx.fillStyle = "rgba(" + self.R + "," + self.G + "," + self.B + "," + self.A + ")";
    	ctx.fillRect(0, 0, w, h);
    	
    	cbColor = transformColor([self.R, self.G, self.B, self.A], self.filterSelect.value);
    	self.outR = cbColor[0];
    	self.outG = cbColor[1];
    	self.outB = cbColor[2];
    	self.outA = cbColor[3];
    	ctx = scAltCanvas.getContext("2d");
    	ctx.fillStyle = "rgba(" + cbColor[0] + "," + cbColor[1] + "," + cbColor[2] + "," + cbColor[3] + ")";
    	ctx.fillRect(0, 0, w, h);
    };
    
    var transformColor = function(color, matrix) {
    	var r,g,b,a;
    	r = uint8ify(matrix[0][0] * color[0] + matrix[0][1] * color[1] + matrix[0][2] * color[2]);
    	g = uint8ify(matrix[1][0] * color[0] + matrix[1][1] * color[1] + matrix[1][2] * color[2]);
    	b = uint8ify(matrix[2][0] * color[0] + matrix[2][1] * color[1] + matrix[2][2] * color[2]);
    	a = color[3];
    	return [r,g,b,a];
    };
    
    var uint8ify = function(num) {
        if (num < 0)
            return 0;
        else if (num > 255)
            return 255;
        return Math.floor(num);
    };
    
    var transformImage = function(data, filter) {
        var len = data.length;
        var r,g,b;
        for (var i=0; i<len-3; i+=4) {
            r = filter[0][0] * data[i] + filter[0][1] * data[i+1] + filter[0][2] * data[i+2];
            g = filter[1][0] * data[i] + filter[1][1] * data[i+1] + filter[1][2] * data[i+2];
            b = filter[2][0] * data[i] + filter[2][1] * data[i+1] + filter[2][2] * data[i+2];
            data[i] = uint8ify(r);
            data[i+1] = uint8ify(g);
            data[i+2] = uint8ify(b);
        }
    };
    
    self.onRenderSC = function() {
    	singleColorRender();
    };
    
    var initSingleColor = function() {
    	scOrigCanvas = document.getElementById("scOrigCanvas");
    	scAltCanvas = document.getElementById("scAltCanvas");
    	self.onRenderSC();
    };
    
    var init = function() {
        origCanvas = document.getElementById("origCanvas");
        altCanvas = document.getElementById("altCanvas");
        secondCanvas = document.getElementById("secondCanvas");
        origImage = document.getElementById("origImage");
    };
    
    var processOrigImage = function() {
        altCanvas.width = w;
        altCanvas.height = h;
        secondCanvas.width = w;
        secondCanvas.height = h;

        origCanvas.getContext("2d").drawImage(origImage, 0, 0, w, h);

        var imageData = origCanvas.getContext("2d").getImageData(0, 0, w, h);
      	transformImage(imageData.data, self.filterSelect.value);
        altCanvas.getContext("2d").putImageData(imageData, 0, 0);

    	// apply the filter a second time
    	transformImage(imageData.data, self.filterSelect.value);
        secondCanvas.getContext("2d").putImageData(imageData, 0, 0);
    };
    
    var readFile = function(file) {
        var reader = new FileReader();
	
        reader.onload = function (e) {
            origImage.src = e.target.result;
        };
	
        reader.readAsDataURL(file);
    };
    
    init();
    initSingleColor();
}