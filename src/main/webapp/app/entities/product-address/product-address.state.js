(function() {
    'use strict';

    angular
        .module('rentitApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('product-address', {
            parent: 'entity',
            url: '/product-address',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'rentitApp.productAddress.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/product-address/product-addresses.html',
                    controller: 'ProductAddressController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('productAddress');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('product-address-detail', {
            parent: 'entity',
            url: '/product-address/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'rentitApp.productAddress.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/product-address/product-address-detail.html',
                    controller: 'ProductAddressDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('productAddress');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ProductAddress', function($stateParams, ProductAddress) {
                    return ProductAddress.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('product-address.new', {
            parent: 'product-address',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/product-address/product-address-dialog.html',
                    controller: 'ProductAddressDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('product-address', null, { reload: true });
                }, function() {
                    $state.go('product-address');
                });
            }]
        })
        .state('product-address.edit', {
            parent: 'product-address',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/product-address/product-address-dialog.html',
                    controller: 'ProductAddressDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ProductAddress', function(ProductAddress) {
                            return ProductAddress.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('product-address', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('product-address.delete', {
            parent: 'product-address',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/product-address/product-address-delete-dialog.html',
                    controller: 'ProductAddressDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ProductAddress', function(ProductAddress) {
                            return ProductAddress.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('product-address', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
