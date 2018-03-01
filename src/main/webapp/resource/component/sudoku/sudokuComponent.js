'use strict'
var sudokuModule = angular.module('sudokuComponent', []);

sudokuModule.component('sudoku', {
	controller: 'sudokuController as sCtrl',
	templateUrl: 'resource/component/sudoku/sudoku.html',
	bindings: {

	}
});

sudokuModule.controller('sudokuController', sudokuController);