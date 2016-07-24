(function() {
    'use strict';

    angular
        .module('rentitApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('app-settings', {
            parent: 'entity',
            url: '/app-settings',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'rentitApp.appSettings.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/app-settings/app-settings.html',
                    controller: 'AppSettingsController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('appSettings');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('app-settings-detail', {
            parent: 'entity',
            url: '/app-settings/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'rentitApp.appSettings.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/app-settings/app-settings-detail.html',
                    controller: 'AppSettingsDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('appSettings');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'AppSettings', function($stateParams, AppSettings) {
                    return AppSettings.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('app-settings.new', {
            parent: 'app-settings',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/app-settings/app-settings-dialog.html',
                    controller: 'AppSettingsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                value: null,
                                type: null,
                                description: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('app-settings', null, { reload: true });
                }, function() {
                    $state.go('app-settings');
                });
            }]
        })
        .state('app-settings.edit', {
            parent: 'app-settings',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/app-settings/app-settings-dialog.html',
                    controller: 'AppSettingsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['AppSettings', function(AppSettings) {
                            return AppSettings.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('app-settings', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('app-settings.delete', {
            parent: 'app-settings',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/app-settings/app-settings-delete-dialog.html',
                    controller: 'AppSettingsDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['AppSettings', function(AppSettings) {
                            return AppSettings.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('app-settings', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
