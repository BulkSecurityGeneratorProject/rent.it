(function () {
    'use strict';

    angular
        .module('rentitApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];


    function stateConfig($stateProvider) {
        $stateProvider.state('home', {
                parent: 'app',
                url: '/',
                data: {
                    authorities: []
                },
                views: {
                    'content@': {
                        templateUrl: 'app/home/home.html',
                        controller: 'HomeController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('home');
                        return $translate.refresh();
                    }]
                }
            })
            .state('aboutus', {
                parent: 'app',
                url: '/aboutus',
                data: {
                    authorities: []
                },
                views: {
                    'content@': {
                        templateUrl: 'pages/aboutus.html'
                    }
                }
            })
            .state('help', {
                parent: 'app',
                url: '/help',
                data: {
                    authorities: []
                },
                views: {
                    'content@': {
                        templateUrl: 'pages/help.html'
                    }
                }
            })
            .state('careers', {
                parent: 'app',
                url: '/careers',
                views: {
                    'content@': {
                        templateUrl: 'pages/careers.html'
                    }
                }
            })
            .state('press', {
                parent: 'app',
                url: '/press',
                data: {
                    authorities: []
                },
                views: {
                    'content@': {
                        templateUrl: 'pages/press.html'
                    }
                }
            })
            .state('terms', {
                parent: 'app',
                url: '/terms',
                data: {
                    authorities: []
                },
                views: {
                    'content@': {
                        templateUrl: 'pages/terms.html'
                    }
                }
            })
            .state('howto', {
                parent: 'app',
                url: '/howto',
                data: {
                    authorities: []
                },
                views: {
                    'content@': {
                        templateUrl: 'pages/howto.html'
                    }
                }
            });
    }
})();
