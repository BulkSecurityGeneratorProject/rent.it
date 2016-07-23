(function() {
    'use strict';

    angular
        .module('rentitApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('user-address', {
            parent: 'entity',
            url: '/user-address',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'rentitApp.userAddress.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/user-address/user-addresses.html',
                    controller: 'UserAddressController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userAddress');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('user-address-detail', {
            parent: 'entity',
            url: '/user-address/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'rentitApp.userAddress.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/user-address/user-address-detail.html',
                    controller: 'UserAddressDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userAddress');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'UserAddress', function($stateParams, UserAddress) {
                    return UserAddress.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('user-address.new', {
            parent: 'user-address',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-address/user-address-dialog.html',
                    controller: 'UserAddressDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                streetAddress: null,
                                postalCode: null,
                                city: null,
                                stateProvince: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('user-address', null, { reload: true });
                }, function() {
                    $state.go('user-address');
                });
            }]
        })
        .state('user-address.edit', {
            parent: 'user-address',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-address/user-address-dialog.html',
                    controller: 'UserAddressDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['UserAddress', function(UserAddress) {
                            return UserAddress.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-address', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('user-address.delete', {
            parent: 'user-address',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-address/user-address-delete-dialog.html',
                    controller: 'UserAddressDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['UserAddress', function(UserAddress) {
                            return UserAddress.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-address', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
