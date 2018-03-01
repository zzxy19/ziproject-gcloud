var myApp = angular.module('myApp', ['ui.router', 'sudokuComponent', 'picCBComponent']);

myApp.controller('myAppController', function myAppController($scope) {
	$scope.title = "Hello world!";
	$scope.prompt = "Welcome to my testing page.";
});

myApp.config(function($stateProvider, $urlRouterProvider) {
    
    $urlRouterProvider.otherwise('/home');
    
    $stateProvider
        
        // HOME STATES AND NESTED VIEWS ========================================
        .state('home', {
            url: '/home',
            templateUrl: 'page/home.html'
        })
        
        .state('home.picCB', {
            url: '/tool/picCB',
            templateUrl: 'resource/component/picCB/picCBFrame.html'
        })
        
        .state('home.sudoku', {
            url: '/game/sudoku',
            templateUrl: 'resource/component/sudoku/sudokuFrame.html'
        });
        
});