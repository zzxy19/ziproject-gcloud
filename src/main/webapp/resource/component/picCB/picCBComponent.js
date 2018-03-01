'use strict'
var picCBModule = angular.module('picCBComponent', []);

sudokuModule.component('picCB', {
	controller: 'picCBController as cCtrl',
	templateUrl: 'resource/component/picCB/picCB.html',
	bindings: {

	}
});

picCBModule.controller('picCBController', picCBController);